package com.khnumpottr.plantirrigationservice.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.khnumpottr.plantirrigationservice.dao.ConnectedNodesDAO
import com.khnumpottr.plantirrigationservice.dao.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.IrrigationData
import com.khnumpottr.plantirrigationservice.domain.MessageData
import com.khnumpottr.plantirrigationservice.domain.NodeData
import com.khnumpottr.plantirrigationservice.domain.NodeSummaryData
import com.khnumpottr.plantirrigationservice.domain.enums.MessageTypes
import mu.KotlinLogging
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.IOException

//https://programmer.help/blogs/simple-message-broadcast-and-unicast-of-websocket-using-spring.html

open class MoistureLevelService {

    private val moistureReadingDAO = MoistureReadingDAO()
    private val connectedNodesDAO = ConnectedNodesDAO()

    private val nodeService = NodeReportingService(moistureReadingDAO, connectedNodesDAO)
    private val clientService = ClientReportingService(moistureReadingDAO, connectedNodesDAO)

    private var connectedClients: HashMap<String, WebSocketSession> = HashMap<String, WebSocketSession>()

    private var irrigationIsActive: Boolean = false

    //TODO
    fun reportMoistureLevel(sessionId: String, messageData: MessageData) {
        nodeService.saveMoistureReading(sessionId, messageData)
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

    fun addDataNode(nodeName: String, sessionId: String) = nodeService.add(nodeName, sessionId)

    fun removeDataNode(sessionId: String) = nodeService.remove(sessionId)

    fun getActiveNodes(): List<NodeSummaryData> = nodeService.get()

    //TODO
    fun irrigationAutoToggle(moistureLevel: Int) {
        MoistureLevelService.LOG.info { moistureLevel }
        if (moistureLevel < 15 && !irrigationIsActive) {
            MoistureLevelService.LOG.info { "Powering ON Irrigation" }
            irrigationIsActive = true
            irrigationToggle(true)
        } else if (moistureLevel > 60 && irrigationIsActive) {
            MoistureLevelService.LOG.info { "Powering OFF Irrigation" }
            irrigationIsActive = false
            irrigationToggle(false)
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

    fun getPlanterDetails(planterId: String): MessageData?{
        val planterDetails = clientService.findPlanterDetails(planterId)
        if(planterDetails != null){
            return MessageData(nodeName = planterDetails.planterId, messageType = MessageTypes.PLANTER_DATA, payload = planterDetails)
        }
        return null
    }

    fun updatePlanterDetails(planterData: NodeData): MessageData{
        clientService.updatePlanterDetails(planterData)
        return MessageData(nodeName = planterData.planterId, messageType = MessageTypes.PLANTER_DATA, payload = planterData)
    }

    //TODO
    fun getMoistureLevelHistory(nodeName: String): MessageData {
        val levels: ArrayList<IrrigationData> = ArrayList()
        val historicLevels = moistureReadingDAO.findAllMoistureReports(nodeName)
        historicLevels.forEach { levels.add(IrrigationData(Integer.parseInt(it.payload.toString()), it.dateReceived)) }
        return MessageData(nodeName = nodeName, messageType = MessageTypes.ARRAY_DATA, payload = levels)
    }

    //TODO
    fun reportInitialMoistureLevel(): List<MessageData> {
        val messages: ArrayList<MessageData> = ArrayList()
        val nodes = connectedNodesDAO.findAllNodes()
        nodes.forEach { node ->
            val levels = moistureReadingDAO.findRecentReporting(node.planterId)
            if (levels != null) {
                messages.add(MessageData(nodeName = node.planterId, messageType = MessageTypes.DATA, payload = levels.payload))
            }
        }
        return messages
    }

    //TODO
    fun irrigationToggle(toggle:Boolean): Boolean{
        irrigationIsActive = toggle
//        irrigationSession?.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(toggle)))
        return irrigationIsActive
    }

    companion object {
        val LOG = KotlinLogging.logger {}
    }
}