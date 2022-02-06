package com.khnumpottr.plantirrigationservice.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.khnumpottr.plantirrigationservice.dao.mongo.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.MessageData
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.IOException




//https://programmer.help/blogs/simple-message-broadcast-and-unicast-of-websocket-using-spring.html

class MoistureLevelService {

    private val moistureReadingDAO = MoistureReadingDAO()
    private var sessionMap: HashMap<String, WebSocketSession> = HashMap<String, WebSocketSession>()
    private val activeNodes:HashMap<Int, String> = HashMap<Int, String>()

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
    fun addDataNode(nodeName: String){
        val id = nodeName.hashCode()
        activeNodes[id] = nodeName
    }

    @Synchronized
    fun removeDataNode(nodeName: String){
        val id = nodeName.hashCode()
        activeNodes.remove(id)
    }

    @Synchronized
    fun reportMoistureLevel(messageData: MessageData){
        moistureReadingDAO.insert(messageData)


        if(sessionMap.isNotEmpty()){
            sessionMap.forEach{ session ->
                val tm = TextMessage(jacksonObjectMapper().writeValueAsString(irrigationData))
                try {
                    session.value.sendMessage(tm)
                    println("Sending")
                } catch (e: IOException) {
                    // After sending fails, you need to continue broadcasting to other people, so try to catch exceptions in the loop
                }
            }
        }
    }

}