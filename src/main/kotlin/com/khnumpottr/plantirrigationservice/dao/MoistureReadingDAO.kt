package com.khnumpottr.plantirrigationservice.dao

import com.khnumpottr.plantirrigationservice.domain.MessageData
import com.khnumpottr.plantirrigationservice.domain.enums.MessageTypes
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import org.litote.kmongo.*
import org.springframework.stereotype.Component
import java.lang.Integer.parseInt
import java.time.LocalDateTime


@Component

class MoistureReadingDAO {

    private val client = KMongo.createClient("mongodb://127.0.0.1:27017")
    private val database = client.getDatabase("plant-irrigation-service")
    private val collection = database.getCollection<MongoMessageData>("moisture_reading")

    fun insert(messageData: MessageData) {
        collection.insertOne(MongoMessageData(messageData))
    }

    fun findAllMoistureReports(nodeName: String): List<MessageData> {
        return collection.find(
            and(
                MongoMessageData::nodeName eq nodeName,
                MongoMessageData::dateReceived gte LocalDateTime.now().minusDays(5)
            )
        )
            .sort(ascending(MongoMessageData::dateReceived))
            .map(MongoMessageData::build)
            .toList()
    }

    fun findRecentReporting(nodeName: String): MessageData?{
        return collection.find(and(
            MongoMessageData::nodeName eq nodeName,
            )
        )
            .sort(descending(MongoMessageData::dateReceived))
            .map(MongoMessageData::build)
            .first()
    }

}

class MongoMessageData @BsonCreator constructor(
    @BsonProperty("nodeName")
    val nodeName: String,
    @BsonProperty("moisturePercentage")
    val moisturePercentage: Int,
    @BsonProperty("dateReceived")
    val dateReceived: LocalDateTime,
) {
    constructor(messageData: MessageData) : this(
        nodeName = messageData.nodeName,
        moisturePercentage = parseInt(messageData.payload.toString()),
        dateReceived = LocalDateTime.now()
    )

    fun build(): MessageData = MessageData(
        nodeName = nodeName,
        payload = moisturePercentage,
        messageType = MessageTypes.DATA,
        dateReceived = dateReceived
    )
}