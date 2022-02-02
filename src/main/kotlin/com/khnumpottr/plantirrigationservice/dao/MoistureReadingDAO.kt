package com.khnumpottr.plantirrigationservice.dao

import com.khnumpottr.plantirrigationservice.domain.DataMessage

interface MoistureReadingDAO {
    fun insert(data: DataMessage)
}