package com.khnumpottr.plantirrigationservice.dao

import com.khnumpottr.plantirrigationservice.domain.IrrigationData

interface MoistureReadingDAO {
    fun insert(data: IrrigationData)
    fun findAllMoisture(limit: Int): List<IrrigationData>
}