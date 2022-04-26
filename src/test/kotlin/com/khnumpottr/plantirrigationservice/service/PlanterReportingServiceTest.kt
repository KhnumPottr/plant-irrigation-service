package com.khnumpottr.plantirrigationservice.service

import com.khnumpottr.plantirrigationservice.dao.ConnectedNodesDAO
import com.khnumpottr.plantirrigationservice.dao.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.PlanterDetails
import com.khnumpottr.plantirrigationservice.domain.PlanterSummaryData
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
class PlanterReportingServiceTest {

    private val mockMoistureReadingDAO = mock<MoistureReadingDAO>()
    private val mockConnectedNodesDAO = mock<ConnectedNodesDAO>()

    private var service: PlanterReportingService = PlanterReportingService(mockMoistureReadingDAO, mockConnectedNodesDAO)

    @BeforeEach
    fun setup() {
        service = PlanterReportingService(mockMoistureReadingDAO, mockConnectedNodesDAO)
    }

    @Test
    fun `Can add a new node to the active node list`() {
        val testNode = PlanterSummaryData(planterId = "testNode")
        val sessionId = "abcd12345"

        service.add(testNode.planterId, sessionId)

        val result = service.get()
        assertEquals(result.size, 1)
        assertEquals(result.contains(testNode), true)
    }

    @Test
    fun `Can remove a node from the active node list`() {
        val testNode = PlanterSummaryData(planterId = "testNode")
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

        val expectedNode = PlanterSummaryData(planterId = "testNode", moistureLevel = moistureLevel)

        service.add("testNode", sessionId)

        service.saveMoistureReading(sessionId, expectedNode)

        val result = service.get()
        assertEquals(result.size, 1)
        assertEquals(result.any { it.planterId == expectedNode.planterId }, true)
        verify(mockMoistureReadingDAO, times(1)).insert(expectedNode)
    }

    @Nested
    @DisplayName("When moistureLevel for an active node")
    inner class Irrigation {
        @Test
        fun `is lower then the lower limit then saveMoistureReading should return TRUE`() {
            val testNode = PlanterSummaryData(planterId = "testNode")
            val sessionId = "abcd12345"
            val moistureLevel = 14
            val moistureReading = PlanterSummaryData(planterId = "testNode", moistureLevel = moistureLevel)
            val lowerLimit = 15

            whenever(mockConnectedNodesDAO.find("testNode")).thenReturn(PlanterDetails(planterId = "testNode", lowerLimit = lowerLimit))

            service.add(testNode.planterId, sessionId)
            service.saveMoistureReading(sessionId, moistureReading)

            val result = service.irrigatingSessionTrigger(sessionId)

            assertEquals(result, true)
        }

        @Test
        fun `is higher then the lower limit then saveMoistureReading should return FALSE`() {
            val testNode = PlanterSummaryData(planterId = "testNode")
            val sessionId = "abcd12345"
            val moistureLevel = 16
            val moistureReading = PlanterSummaryData(planterId = "testNode", moistureLevel = moistureLevel)
            val lowerLimit = 15

            whenever(mockConnectedNodesDAO.find("testNode")).thenReturn(PlanterDetails(planterId = "testNode", lowerLimit = lowerLimit))

            service.add(testNode.planterId, sessionId)
            service.saveMoistureReading(sessionId, moistureReading)

            val result = service.irrigatingSessionTrigger(sessionId)

            assertEquals(result, false)
        }
    }

    @Test
    fun `Can get planter list summary`() {
        val sessionId = "abcd12345"
        val moistureLevel = 50
        val title = "test title"

        val expectedNode = PlanterSummaryData(planterId = "testNode", moistureLevel = moistureLevel)

        whenever(mockConnectedNodesDAO.findAllNodes()).thenReturn(listOf(PlanterDetails(planterId = "testNode", title = title)))

        whenever(mockMoistureReadingDAO.findRecentReporting("testNode")).thenReturn(PlanterSummaryData(planterId = "testNode", moistureLevel = moistureLevel))

        service.add("testNode", sessionId)

        val result = service.getPlanterListSummary()
        assertEquals(result.size, 1)
        assertEquals(result.any { it.planterId == expectedNode.planterId }, true)
        assertEquals(result.any { it.title == title }, true)
    }


}