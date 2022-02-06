package com.khnumpottr.plantirrigationservice.dao.mongo

import com.khnumpottr.plantirrigationservice.dao.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.IrrigationData
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.*
import org.springframework.stereotype.Component


@Component
class MongoMoistureReadingDAO : MoistureReadingDAO {

    private final val client = KMongo.createClient()
    private final val database = client.getDatabase("plant-irrigation-service")
    private val collection = database.getCollection<IrrigationData>("moisture_reading")

    override fun insert(data: IrrigationData) {
        collection.insertOne(data)
    }

    override fun findAllMoisture(limit: Int): List<IrrigationData> {
        return collection.find().sort(descending(IrrigationData::dateReceived)).limit(limit).toList()
    }


}