package com.khnumpottr.plantirrigationservice.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.khnumpottr.plantirrigationservice.dao.ConnectedNodesDAO
import com.khnumpottr.plantirrigationservice.dao.mongo.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.IrrigationData
import com.khnumpottr.plantirrigationservice.domain.MessageData
import com.khnumpottr.plantirrigationservice.domain.NodeData
import com.khnumpottr.plantirrigationservice.domain.enums.MessageTypes
import mu.KotlinLogging
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.IOException
import java.lang.Integer.parseInt

//https://programmer.help/blogs/simple-message-broadcast-and-unicast-of-websocket-using-spring.html

class MoistureLevelService {

    private val moistureReadingDAO = MoistureReadingDAO()
    private val connectedNodesDAO = ConnectedNodesDAO()
    private var sessionMap: HashMap<String, WebSocketSession> = HashMap<String, WebSocketSession>()
    private val activeNodes: HashMap<String, String> = HashMap<String, String>()

    private var irrigationIsActive: Boolean = false
    private var irrigationSession: WebSocketSession? = null


    @Synchronized
    fun addWebSocketSession(session: WebSocketSession) {
        val id = session.id // Unique ID obtained from session
        sessionMap[id] = session //Save session
    }

    @Synchronized
    fun removeWebSocketSession(session: WebSocketSession) {
        val id = session.id
        sessionMap.remove(id) // Delete session
    }

    @Synchronized
    fun connectIrrigationSession(session: WebSocketSession) {
        irrigationSession = session
    }

    @Synchronized
    fun disconnectIrrigationSession() {
        irrigationSession = null
    }

    @Synchronized
    fun addDataNode(nodeName: String, sessionId: String) {
        LOG.info { "Adding new Node: $nodeName" }
        connectedNodesDAO.insert(NodeData(nodeName))
        activeNodes[sessionId] = nodeName
    }

    @Synchronized
    fun removeDataNode(sessionId: String) {
        LOG.info { "node Removed new Node: ${activeNodes[sessionId]}" }
        activeNodes.remove(sessionId)
    }

    @Synchronized
    fun getActiveNodes(): List<String> {
        return activeNodes.toList().map { it.second }
    }

    @Synchronized
    fun getMoistureLevelHistory(nodeName: String): MessageData {
        val levels: ArrayList<IrrigationData> = ArrayList()
        val historicLevels = moistureReadingDAO.findAllMoistureReports(nodeName)
        historicLevels.forEach { levels.add(IrrigationData(parseInt(it.payload.toString()), it.dateReceived)) }
        return MessageData(nodeName = nodeName, messageType = MessageTypes.ARRAY_DATA, payload = levels)
    }

    fun reportInitialMoistureLevel(): List<MessageData> {
        val messages: ArrayList<MessageData> = ArrayList()
        val nodes = connectedNodesDAO.findAllNodes()
        nodes.forEach { node ->
            val levels = moistureReadingDAO.findRecentReporting(node)
            if (levels != null) {
                messages.add(MessageData(nodeName = node, messageType = MessageTypes.DATA, payload = levels.payload))
            }
        }
        return messages
    }

    fun reportMoistureLevel(messageData: MessageData) {
        moistureReadingDAO.insert(messageData)
        if (sessionMap.isNotEmpty()) {
            sessionMap.forEach { session ->
                val tm = TextMessage(jacksonObjectMapper().writeValueAsString(messageData))
                try {
                    session.value.sendMessage(tm)
                } catch (e: IOException) {
                    // After sending fails, you need to continue broadcasting to other people, so try to catch exceptions in the loop
                }
            }
        }
    }

    fun irrigationAutoToggle(moistureLevel: Int) {
        LOG.info { moistureLevel }
        if (moistureLevel < 15 && !irrigationIsActive) {
            LOG.info { "Powering ON Irrigation" }
            irrigationIsActive = true
            irrigationToggle(true)
        } else if (moistureLevel > 60 && irrigationIsActive) {
            LOG.info { "Powering OFF Irrigation" }
            irrigationIsActive = false
            irrigationToggle(false)
        }
    }

    fun irrigationToggle(toggle:Boolean): Boolean{
        irrigationIsActive = toggle
        irrigationSession?.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(toggle)))
        return irrigationIsActive
    }

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

}