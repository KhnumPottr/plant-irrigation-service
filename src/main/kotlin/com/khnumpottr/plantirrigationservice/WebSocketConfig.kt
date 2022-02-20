package com.khnumpottr.plantirrigationservice

import com.khnumpottr.plantirrigationservice.handler.WebClientHandler
import com.khnumpottr.plantirrigationservice.handler.NodeDataHandler
import com.khnumpottr.plantirrigationservice.service.MoistureLevelService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry


@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {

    private val service = MoistureLevelService()

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(NodeDataHandler(service), "/nodeData").setAllowedOrigins("*")//.withSockJS()
        registry.addHandler(WebClientHandler(service), "/moistureLevels").setAllowedOrigins("*")
    }

    @Bean
    fun myHandler(): WebSocketHandler? {
        return NodeDataHandler(service)
    }
}