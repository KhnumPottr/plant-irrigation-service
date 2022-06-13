package com.khnumpottr.plantirrigationservice.service

import com.khnumpottr.plantirrigationservice.dao.CommandQueueDAO
import com.khnumpottr.plantirrigationservice.dao.ConnectedNodesDAO
import com.khnumpottr.plantirrigationservice.dao.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.Command
import com.khnumpottr.plantirrigationservice.domain.PlanterDetails
import com.khnumpottr.plantirrigationservice.domain.enums.CommandType
import com.khnumpottr.plantirrigationservice.domain.enums.MessageTypes
import mu.KotlinLogging
import java.time.LocalDateTime

class ClientReportingService(
    private val moistureReadingDAO: MoistureReadingDAO,
    private val connectedNodesDAO: ConnectedNodesDAO,
    private val commandQueueDAO: CommandQueueDAO
) {

    fun findPlanterDetails(planterId: String): PlanterDetails? {
        return connectedNodesDAO.find(planterId)
    }

    fun updatePlanterDetails(planterData: PlanterDetails){
        connectedNodesDAO.update(planterData)
    }

    fun saveIssuedCommand(planterId: String, commandType: CommandType){
        commandQueueDAO.insert(Command(
            planterId = planterId,
            issuedCommand = commandType,
        ))
    }

    companion object {
        val LOG = KotlinLogging.logger {}
    }
}