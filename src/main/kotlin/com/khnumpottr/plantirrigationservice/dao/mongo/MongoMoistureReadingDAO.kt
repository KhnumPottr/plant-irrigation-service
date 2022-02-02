package com.khnumpottr.plantirrigationservice.dao.mongo

import com.khnumpottr.plantirrigationservice.dao.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.IrrigationData
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.springframework.stereotype.Component


@Component
class MongoMoistureReadingDAO : MoistureReadingDAO {

    private final val client = KMongo.createClient().coroutine
    private final val database = client.getDatabase("plant-irrigation-service")
    private val collection = database.getCollection<IrrigationData>("moisture_reading")

    override fun insert(data: IrrigationData) {
        runBlocking {
            collection.insertOne(data)
        }
    }
}