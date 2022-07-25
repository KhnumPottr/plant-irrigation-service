package com.khnumpottr.plantirrigationservice.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.khnumpottr.plantirrigationservice.dao.CommandQueueDAO
import com.khnumpottr.plantirrigationservice.dao.ConnectedNodesDAO
import com.khnumpottr.plantirrigationservice.dao.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.*
import com.khnumpottr.plantirrigationservice.domain.enums.CommandType
import com.khnumpottr.plantirrigationservice.domain.enums.MessageTypes
import mu.KotlinLogging
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.IOException

//https://programmer.help/blogs/simple-message-broadcast-and-unicast-of-websocket-using-spring.html

open class MoistureLevelService {

    private val moistureReadingDAO = MoistureReadingDAO()
    private val connectedNodesDAO = ConnectedNodesDAO()
    private val commandQueueDAO = CommandQueueDAO()

    private val planterService = PlanterReportingService(moistureReadingDAO, connectedNodesDAO, commandQueueDAO)
    private val clientService = ClientReportingService(moistureReadingDAO, connectedNodesDAO, commandQueueDAO)

    private var connectedClients: HashMap<String, WebSocketSession> = HashMap<String, WebSocketSession>()

    fun reportMoistureLevel(sessionId: String, messageData: MessageData) {
        val planterSummaryData = PlanterSummaryData(
            planterId = messageData.id,
            moistureLevel = messageData.payload as Int,
            dateReceived = messageData.dateReceived
        )
        planterService.saveMoistureReading(sessionId, planterSummaryData)
        if (connectedClients.isNotEmpty()) {
            connectedClients.forEach { session ->
                val tm = TextMessage(jacksonObjectMapper().writeValueAsString(messageData))
                try {
                    session.value.sendMessage(tm)
                } catch (e: IOException) {
                    // After sending fails, you need to continue broadcasting to other people, so try to catch exceptions in the loop
                }
            }
        }
    }

    fun addDataNode(planterId: String, sessionId: String) = planterService.add(planterId, sessionId)

    fun removeDataNode(sessionId: String) = planterService.remove(sessionId)

    private fun getActiveNodes(sessionId: String): PlanterSummaryData = planterService.getBySessionID(sessionId)

    fun irrigationThresholdCheck(session: WebSocketSession) {
        val irrigationCheck = planterService.irrigatingSessionTrigger(session.id)
        if (irrigationCheck) {
            val maxIrrigationValue = getPlanterMaxIrrigation(getActiveNodes(session.id).planterId)
            session.sendMessage(
                TextMessage(
                    jacksonObjectMapper().writeValueAsString(
                        CommandMessage(
                            id = "server",
                            commandType = CommandType.IRRIGATE_MAX.commandNumber,
                            payload = maxIrrigationValue
                        )
                    )
                )
            )
            session.sendMessage(
                TextMessage(
                    jacksonObjectMapper().writeValueAsString(
                        CommandMessage(
                            id = "server",
                            commandType = CommandType.IRRIGATE.commandNumber,
                            payload = null
                        )
                    )
                )
            )
        }
    }

    fun addWebSocketSession(session: WebSocketSession) {
        val id = session.id
        connectedClients[id] = session
    }

    fun removeWebSocketSession(session: WebSocketSession) {
        val id = session.id
        connectedClients[id] = session
    }

    fun getPlanterDetails(planterId: String): MessageData? {
        val planterDetails = clientService.findPlanterDetails(planterId)
        if (planterDetails != null) {
            return MessageData(
                id = planterDetails.planterId,
                messageType = MessageTypes.PLANTER_DATA,
                payload = planterDetails
            )
        }
        return null
    }

    fun updatePlanterDetails(planterData: PlanterDetails): MessageData {
        clientService.updatePlanterDetails(planterData)
        return MessageData(id = planterData.planterId, messageType = MessageTypes.PLANTER_DATA, payload = planterData)
    }

    fun getMoistureLevelHistory(planterId: String): MessageData {
        val history = planterService.getPlanterHistoryReport(planterId)
        return MessageData(id = planterId, messageType = MessageTypes.IRRIGATION_ARRAY_DATA, payload = history)
    }


    fun reportPlanterSummary(): List<MessageData> {
        val messages: ArrayList<MessageData> = ArrayList()
        val activePlanters = planterService.getPlanterListSummary()
        if (activePlanters.isNotEmpty()) {
            activePlanters.forEach { planter ->
                messages.add(
                    MessageData(
                        id = planter.planterId,
                        messageType = MessageTypes.PLANTER_DATA,
                        payload = planter
                    )
                )
            }
        }
        return messages
    }

    fun saveClientCommand(planterId: String, payload: String) {
        val commandType = CommandType.get(payload)
        clientService.saveIssuedCommand(planterId, commandType)
    }

    fun retrievePlanterCommand(planterId: String): List<CommandMessage> {
        val messages: ArrayList<CommandMessage> = ArrayList()
        val commands = planterService.getQueueCommands(planterId)
        if (commands.isNotEmpty()) {
            commands.forEach { command ->
                var payload: Any? = null
                if (command.issuedCommand == CommandType.IRRIGATE_MAX) {
                    payload = getPlanterMaxIrrigation(planterId)
                }
                messages.add(
                    CommandMessage(
                        id = "SERVER",
                        commandType = command.issuedCommand.commandNumber,
                        payload = payload
                    )
                )
            }
        }
        return messages
    }

    private fun getPlanterMaxIrrigation(planterId: String): Int {
        val planterDetails = clientService.findPlanterDetails(planterId)
        if (planterDetails?.upperLimit != null) {
            return planterDetails.upperLimit
        }
        return 0
    }

    companion object {
        val LOG = KotlinLogging.logger {}
    }
}