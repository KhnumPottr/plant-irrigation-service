package com.khnumpottr.plantirrigationservice.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.khnumpottr.plantirrigationservice.dao.CommandQueueDAO
import com.khnumpottr.plantirrigationservice.dao.ConnectedNodesDAO
import com.khnumpottr.plantirrigationservice.dao.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.Command
import com.khnumpottr.plantirrigationservice.domain.MessageData
import com.khnumpottr.plantirrigationservice.domain.PlanterDetails
import com.khnumpottr.plantirrigationservice.domain.PlanterSummaryData
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

    fun getActiveNodes(): List<PlanterSummaryData> = planterService.get()

    fun irrigationThresholdCheck(session: WebSocketSession) {
        val irrigationCheck = planterService.irrigatingSessionTrigger(session.id)
        if(irrigationCheck){
            val irrigationMessage = MessageData(id = "server", messageType = MessageTypes.NODE_TRIGGER_IRRIGATION, payload = true)
            session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(irrigationMessage)))
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
        if(activePlanters.isNotEmpty()) {
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

    fun saveClientCommand(planterId: String, payload: String){
        val commandType = CommandType.get(payload)
        clientService.saveIssuedCommand(planterId, commandType)
    }

    fun retrievePlanterCommand(planterId: String): List<MessageData>{
        val messages: ArrayList<MessageData> = ArrayList()
        val commands = planterService.getQueueCommands(planterId)
        if(commands.isNotEmpty()){
            commands.forEach { command ->
                messages.add(
                    MessageData(
                        id = "SERVER",
                        messageType =  MessageTypes.COMMAND,
                        payload = command
                    )
                )
            }
        }
        return messages
    }

    companion object {
        val LOG = KotlinLogging.logger {}
    }
}