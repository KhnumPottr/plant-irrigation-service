package com.khnumpottr.plantirrigationservice.dao.mongo

import com.khnumpottr.plantirrigationservice.domain.IrrigationData
import com.khnumpottr.plantirrigationservice.domain.MessageData
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import org.litote.kmongo.*
import org.springframework.stereotype.Component
import java.lang.Integer.parseInt
import java.time.LocalDateTime


@Component

class MoistureReadingDAO {

    private final val client = KMongo.createClient()
    private final val database = client.getDatabase("plant-irrigation-service")
    private val collection = database.getCollection<MongoMessageData>("moisture_reading")

    fun insert(messageData: MessageData) {
        collection.insertOne(MongoMessageData(messageData))
    }

//    fun findAllMoisture(limit: Int): List<IrrigationData> {
        //TODO

//        return collection.find().sort(descending(IrrigationData::dateReceived)).limit(limit).toList()
//    }

}

class MongoMessageData @BsonCreator constructor(
    @BsonProperty("nodeName")
    val nodeName: String,
    @BsonProperty("moisturePercentage")
    val moisturePercentage: Int,
    @BsonProperty("dateReceived")
    val dateReceived: LocalDateTime,
) {
    constructor(messageData: MessageData) : this (
        nodeName = messageData.nodeName,
        moisturePercentage = parseInt(messageData.payload),
        dateReceived = LocalDateTime.now()
    )
}