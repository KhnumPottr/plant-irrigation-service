package com.khnumpottr.plantirrigationservice.service

import com.khnumpottr.plantirrigationservice.dao.ConnectedNodesDAO
import com.khnumpottr.plantirrigationservice.dao.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.PlanterDetails
import com.khnumpottr.plantirrigationservice.domain.PlanterSummaryData

class PlanterReportingService(
    private val moistureReadingDAO: MoistureReadingDAO,
    private val connectedNodesDAO: ConnectedNodesDAO
) {

    private val activeNodes: HashMap<String, PlanterSummaryData> = HashMap()

    fun add(planterId: String, sessionId: String) {
        ClientReportingService.LOG.info { "Adding new Node: $planterId" }
        val newNode = PlanterSummaryData(planterId = planterId)
        connectedNodesDAO.insert(PlanterDetails(planterId))
        activeNodes[sessionId] = newNode
    }

    fun remove(sessionId: String) {
        ClientReportingService.LOG.info { "node Removed new Node: ${activeNodes[sessionId]}" }
        activeNodes.remove(sessionId)
    }

    fun get(): List<PlanterSummaryData> {
        return activeNodes.toList().map { it.second }
    }

    fun saveMoistureReading(sessionId: String, planterSummaryData: PlanterSummaryData) {
        moistureReadingDAO.insert(planterSummaryData)
        val reportingNode = activeNodes[sessionId]
        if (reportingNode != null) {
            activeNodes[sessionId] = reportingNode.copy(moistureLevel = planterSummaryData.moistureLevel)
        }
    }

    fun getPlanterListSummary(): List<PlanterSummaryData> {
        val planterSummaryList = ArrayList<PlanterSummaryData>()
        val planterDetailsList = connectedNodesDAO.findAllNodes()
        planterDetailsList.forEach { planter ->
            val moistureReading = moistureReadingDAO.findRecentReporting(planter.planterId)
            planterSummaryList.add(
                PlanterSummaryData(
                    planterId = planter.planterId,
                    title = planter.title,
                    moistureLevel = moistureReading?.moistureLevel
                )
            )
        }
        return planterSummaryList
    }

    //TODO
    // irrigate message
    fun irrigatingSessionTrigger(sessionId: String): Boolean {
        val reportingNode = activeNodes[sessionId]
        if (reportingNode?.moistureLevel != null) {
            val reportingNodeDetails = connectedNodesDAO.find(reportingNode.planterId)
            if (reportingNodeDetails != null && reportingNodeDetails.lowerLimit!! > reportingNode.moistureLevel) {
                return true
            }
        }
        return false
    }


    //TODO
    // Sleep Method

    //TODO
    // Stay Awake Method
}