package com.khnumpottr.plantirrigationservice.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.khnumpottr.plantirrigationservice.dao.mongo.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.IrrigationData
import com.khnumpottr.plantirrigationservice.domain.MessageData
import com.khnumpottr.plantirrigationservice.domain.enums.MessageTypes
import mu.KotlinLogging
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.IOException
import java.lang.Integer.parseInt

//https://programmer.help/blogs/simple-message-broadcast-and-unicast-of-websocket-using-spring.html

class MoistureLevelService {

    private val moistureReadingDAO = MoistureReadingDAO()
    private var sessionMap: HashMap<String, WebSocketSession> = HashMap<String, WebSocketSession>()
    private val activeNodes: HashMap<String, String> = HashMap<String, String>()

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
    fun addDataNode(nodeName: String, sessionId: String) {
        LOG.info { "Adding new Node: $nodeName" }
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
    fun reportInitialMoistureLevel(sessionId: String): MessageData? {
        val node = activeNodes[sessionId] ?: return null
        val levels: ArrayList<IrrigationData> = ArrayList()
        val historicLevels = moistureReadingDAO.findAllMoisture(node)
        historicLevels.forEach { levels.add(IrrigationData(parseInt(it.payload.toString()), it.dateReceived)) }
        return MessageData(nodeName = node, messageType = MessageTypes.ARRAY_DATA, payload = levels)
    }

    @Synchronized
    fun reportMoistureLevel(messageData: MessageData) {
        moistureReadingDAO.insert(messageData)
        if (sessionMap.isNotEmpty()) {
            sessionMap.forEach { session ->
                val tm = TextMessage(jacksonObjectMapper().writeValueAsString(messageData))
                try {
                    session.value.sendMessage(tm)
                    println("Sending")
                } catch (e: IOException) {
                    // After sending fails, you need to continue broadcasting to other people, so try to catch exceptions in the loop
                }
            }
        }
    }

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

}