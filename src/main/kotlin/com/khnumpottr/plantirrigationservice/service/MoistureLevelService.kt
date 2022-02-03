package com.khnumpottr.plantirrigationservice.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.khnumpottr.plantirrigationservice.domain.IrrigationData
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.IOException




//https://programmer.help/blogs/simple-message-broadcast-and-unicast-of-websocket-using-spring.html

class MoistureLevelService {

    private var sessionMap: HashMap<String, WebSocketSession> = HashMap<String, WebSocketSession>()

    @Synchronized
    fun addWebSocketSession(session: WebSocketSession) {
        val id = session.id // Unique ID obtained from session
        // In session, you can also obtain various properties of http before handshake, such as URL, request header, which can also be used as a unique identifier
        sessionMap[id] = session //Save session
    }

    @Synchronized
    fun removeWebSocketSession(session: WebSocketSession) {
        val id = session.id
        sessionMap.remove(id) // Delete session
    }

    @Synchronized
    fun reportMoistureLevel(irrigationData: IrrigationData){
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