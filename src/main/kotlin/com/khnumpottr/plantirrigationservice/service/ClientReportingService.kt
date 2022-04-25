package com.khnumpottr.plantirrigationservice.service

import com.khnumpottr.plantirrigationservice.dao.ConnectedNodesDAO
import com.khnumpottr.plantirrigationservice.dao.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.IrrigationData
import com.khnumpottr.plantirrigationservice.domain.MessageData
import com.khnumpottr.plantirrigationservice.domain.NodeData
import com.khnumpottr.plantirrigationservice.domain.enums.MessageTypes
import mu.KotlinLogging
import org.springframework.web.socket.WebSocketSession

class ClientReportingService() {

    private val moistureReadingDAO = MoistureReadingDAO()
    private val connectedNodesDAO = ConnectedNodesDAO()

    companion object {
        val LOG = KotlinLogging.logger {}
    }
}