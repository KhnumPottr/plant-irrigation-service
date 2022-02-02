package com.khnumpottr.plantirrigationservice.websocket

import com.khnumpottr.plantirrigationservice.websocket.handler.DataHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry


@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(DataHandler(), "/nodeData").setAllowedOrigins("*")//.withSockJS()
    }

    @Bean
    fun myHandler(): WebSocketHandler? {
        return DataHandler()
    }
}