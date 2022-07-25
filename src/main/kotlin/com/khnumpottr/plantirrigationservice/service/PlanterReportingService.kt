package com.khnumpottr.plantirrigationservice.service

import com.khnumpottr.plantirrigationservice.dao.CommandQueueDAO
import com.khnumpottr.plantirrigationservice.dao.ConnectedNodesDAO
import com.khnumpottr.plantirrigationservice.dao.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.Command
import com.khnumpottr.plantirrigationservice.domain.PlanterDetails
import com.khnumpottr.plantirrigationservice.domain.PlanterSummaryData

class PlanterReportingService(
    private val moistureReadingDAO: MoistureReadingDAO,
    private val connectedNodesDAO: ConnectedNodesDAO,
    private val commandQueueDAO: CommandQueueDAO
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

    fun getBySessionID(sessionId: String): PlanterSummaryData {
        val activeNode = activeNodes.toList().first { it.first == sessionId }
        return activeNode.second
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
        connectedNodesDAO.findAllNodes()?.forEach { planter ->
            val moistureReading = moistureReadingDAO.findRecentReporting(planter.planterId)
            if (moistureReading != null) {
                planterSummaryList.add(
                    PlanterSummaryData(
                        planterId = planter.planterId,
                        title = planter.title,
                        moistureLevel = moistureReading.moistureLevel,
                        irrigating = moistureReading.irrigating
                    )
                )
            }
        }
        return planterSummaryList
    }

    fun getPlanterHistoryReport(planterId: String): List<PlanterSummaryData> {
        return moistureReadingDAO.findAllMoistureReports(planterId)
    }

    fun irrigatingSessionTrigger(sessionId: String): Boolean {
        val reportingNode = activeNodes[sessionId]
        if (reportingNode?.moistureLevel != null) {
            val reportingNodeDetails = connectedNodesDAO.find(reportingNode.planterId)
            if (reportingNodeDetails?.lowerLimit != null && reportingNodeDetails.lowerLimit > reportingNode.moistureLevel) {
                return true
            }
        }
        return false
    }


    fun getQueueCommands(planterId: String): List<Command>{
        return commandQueueDAO.findAll(planterId = planterId)
    }
}