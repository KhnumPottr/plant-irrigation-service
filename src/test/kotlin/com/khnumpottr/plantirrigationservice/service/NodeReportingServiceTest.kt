package com.khnumpottr.plantirrigationservice.service

import com.khnumpottr.plantirrigationservice.dao.ConnectedNodesDAO
import com.khnumpottr.plantirrigationservice.dao.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.MessageData
import com.khnumpottr.plantirrigationservice.domain.NodeData
import com.khnumpottr.plantirrigationservice.domain.NodeSummaryData
import com.khnumpottr.plantirrigationservice.domain.enums.MessageTypes
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

@DisplayName("Node Reporting Service")
class NodeReportingServiceTest {

    private val mockMoistureReadingDAO = mock<MoistureReadingDAO>()
    private val mockConnectedNodesDAO = mock<ConnectedNodesDAO>()

    private var service: NodeReportingService = NodeReportingService(mockMoistureReadingDAO, mockConnectedNodesDAO)

    @BeforeEach
    fun setup() {
        service = NodeReportingService(mockMoistureReadingDAO, mockConnectedNodesDAO)
    }

    @Test
    fun `Can add a new node to the active node list`() {
        val testNode = NodeSummaryData(nodeName = "testNode")
        val sessionId = "abcd12345"

        service.add(testNode.nodeName, sessionId)

        val result = service.get()
        assertEquals(result.size, 1)
        assertEquals(result.contains(testNode), true)
    }

    @Test
    fun `Can remove a node from the active node list`() {
        val testNode = NodeSummaryData(nodeName = "testNode")
        val sessionId = "abcd12345"

        service.add("testNode", "abcd12345")
        service.add("testNode2", "abcd67890")
        val activeNodes = service.get()
        assertEquals(activeNodes.size, 2)

        service.remove(sessionId)

        val result = service.get()
        assertEquals(result.size, 1)
        assertEquals(result.contains(testNode), false)
    }

    @Test
    fun `Can update active node list with recent moisture reading`() {
        val sessionId = "abcd12345"
        val moistureLevel = 50
        val moistureReading =
            MessageData(nodeName = "testNode", messageType = MessageTypes.DATA, payload = moistureLevel)

        val expectedNode = NodeSummaryData(nodeName = "testNode", moistureLevel = moistureLevel)

        service.add("testNode", sessionId)

        service.saveMoistureReading(sessionId, moistureReading)

        val result = service.get()
        assertEquals(result.size, 1)
        assertEquals(result.contains(expectedNode), true)
        verify(mockMoistureReadingDAO, times(1)).insert(moistureReading)
    }

    @Nested
    @DisplayName("When moistureLevel for an active node")
    inner class Irrigation {
        @Test
        fun `is lower then the lower limit then saveMoistureReading should return TRUE`() {
            val testNode = NodeSummaryData(nodeName = "testNode")
            val sessionId = "abcd12345"
            val moistureLevel = 14
            val moistureReading =
                MessageData(nodeName = "testNode", messageType = MessageTypes.DATA, payload = moistureLevel)
            val lowerLimit = 15

            whenever(mockConnectedNodesDAO.find("testNode")).thenReturn(NodeData(planterId = "testNode", lowerLimit = lowerLimit))

            service.add(testNode.nodeName, sessionId)
            service.saveMoistureReading(sessionId, moistureReading)

            val result = service.irrigatingSessionTrigger(sessionId)

            assertEquals(result, true)
        }

        @Test
        fun `is higher then the lower limit then saveMoistureReading should return FALSE`() {
            val testNode = NodeSummaryData(nodeName = "testNode")
            val sessionId = "abcd12345"
            val moistureLevel = 16
            val moistureReading =
                MessageData(nodeName = "testNode", messageType = MessageTypes.DATA, payload = moistureLevel)
            val lowerLimit = 15

            whenever(mockConnectedNodesDAO.find("testNode")).thenReturn(NodeData(planterId = "testNode", lowerLimit = lowerLimit))

            service.add(testNode.nodeName, sessionId)
            service.saveMoistureReading(sessionId, moistureReading)

            val result = service.irrigatingSessionTrigger(sessionId)

            assertEquals(result, false)
        }
    }




}