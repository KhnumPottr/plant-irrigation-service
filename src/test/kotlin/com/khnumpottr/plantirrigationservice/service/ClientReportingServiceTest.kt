package com.khnumpottr.plantirrigationservice.service

import com.khnumpottr.plantirrigationservice.dao.ConnectedNodesDAO
import com.khnumpottr.plantirrigationservice.dao.MoistureReadingDAO
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

@DisplayName("Client Reporting Service")
class ClientReportingServiceTest {

    private val mockMoistureReadingDAO = mock<MoistureReadingDAO>()
    private val mockConnectedNodesDAO = mock<ConnectedNodesDAO>()

    private var service: ClientReportingService = ClientReportingService(mockMoistureReadingDAO, mockConnectedNodesDAO)

    @BeforeEach
    fun setup() {
        service = ClientReportingService(mockMoistureReadingDAO, mockConnectedNodesDAO)
    }
}