package com.khnumpottr.plantirrigationservice.service

import com.khnumpottr.plantirrigationservice.dao.ConnectedNodesDAO
import com.khnumpottr.plantirrigationservice.dao.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.MessageData
import com.khnumpottr.plantirrigationservice.domain.NodeData
import com.khnumpottr.plantirrigationservice.domain.NodeSummaryData

class NodeReportingService(
    private val moistureReadingDAO: MoistureReadingDAO,
    private val connectedNodesDAO: ConnectedNodesDAO
) {

    private val activeNodes: HashMap<String, NodeSummaryData> = HashMap()

    fun add(nodeName: String, sessionId: String) {
        ClientReportingService.LOG.info { "Adding new Node: $nodeName" }
        val newNode = NodeSummaryData(nodeName = nodeName)
        connectedNodesDAO.insert(NodeData(nodeName))
        activeNodes[sessionId] = newNode
    }

    fun remove(sessionId: String) {
        ClientReportingService.LOG.info { "node Removed new Node: ${activeNodes[sessionId]}" }
        activeNodes.remove(sessionId)
    }

    fun get(): List<NodeSummaryData> {
        return activeNodes.toList().map { it.second }
    }

    fun saveMoistureReading(sessionId: String, moistureReading: MessageData) {
        moistureReadingDAO.insert(moistureReading)
        val reportingNode = activeNodes[sessionId]
        if (reportingNode != null) {
            activeNodes[sessionId] = reportingNode.copy(moistureLevel = moistureReading.payload as Int?)
        }
    }

    //TODO
    // irrigate message
    fun irrigatingSessionTrigger(sessionId: String): Boolean {
        val reportingNode = activeNodes[sessionId]
        if (reportingNode?.moistureLevel != null) {
            val reportingNodeDetails = connectedNodesDAO.find(reportingNode.nodeName)
            if(reportingNodeDetails != null && reportingNodeDetails.lowerLimit!! > reportingNode.moistureLevel){
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