package com.khnumpottr.plantirrigationservice.dao.mongo

import com.khnumpottr.plantirrigationservice.dao.MoistureReadingDAO
import com.khnumpottr.plantirrigationservice.domain.DataMessage
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.springframework.stereotype.Component


@Component
class MongoMoistureReadingDAO : MoistureReadingDAO {

    val client = KMongo.createClient().coroutine
    val database = client.getDatabase("plant-irrigation-service")
    val collection = database.getCollection<DataMessage>("moisture_reading")

    override fun insert(data: DataMessage) {
        runBlocking {
            collection.insertOne(data)
        }
    }
}

//data class MongoDataMessage @BsonCreator constructor(
//    @BsonProperty("nodeName")
//    val nodeName: String,
//    @BsonProperty("messageType")
//    val messageType: String,
//    @BsonProperty("message")
//    val message: String
//) {
//    constructor(data: DataMessage) : this(
//        nodeName = data.nodeName,
//        messageType = data.messageType,
//        message = data.message
//    )
//}