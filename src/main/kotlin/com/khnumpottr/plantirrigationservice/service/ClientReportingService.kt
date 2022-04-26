package com.khnumpottr.plantirrigationservice.service

import com.khnumpottr.plantirrigationservice.dao.ConnectedNodesDAO
import com.khnumpottr.plantirrigationservice.dao.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.PlanterDetails
import mu.KotlinLogging

class ClientReportingService(
    private val moistureReadingDAO: MoistureReadingDAO,
    private val connectedNodesDAO: ConnectedNodesDAO
) {

    fun findPlanterDetails(planterId: String): PlanterDetails? {
        return connectedNodesDAO.find(planterId)
    }

    fun updatePlanterDetails(planterData: PlanterDetails){
        connectedNodesDAO.update(planterData)
    }

    companion object {
        val LOG = KotlinLogging.logger {}
    }
}