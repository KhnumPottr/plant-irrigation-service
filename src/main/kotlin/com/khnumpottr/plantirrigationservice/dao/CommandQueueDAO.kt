package com.khnumpottr.plantirrigationservice.dao

import com.khnumpottr.plantirrigationservice.domain.Command
import com.khnumpottr.plantirrigationservice.domain.enums.CommandType
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import org.litote.kmongo.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime


@Component

class CommandQueueDAO {

    private val client = KMongo.createClient("mongodb://127.0.0.1:27017")
    private val database = client.getDatabase("plant-irrigation-service")
    private val collection = database.getCollection<MongoCommandData>("command_queue")

    fun insert(command: Command) {
        collection.insertOne(MongoCommandData(command))
    }

    fun findAll(planterId: String): List<Command>{
        return collection.find(and(
            MongoCommandData::planterId eq planterId,
            MongoCommandData::instigated eq false,
            )
        )
            .sort(descending(MongoMoistureData::dateReceived))
            .map(MongoCommandData::build)
            .toList()
    }

    fun update(command: Command){
        collection.updateOne(and(
            MongoCommandData::planterId eq command.planterId,
            MongoCommandData::issuedCommand eq command.issuedCommand,
            MongoCommandData::dateReceived eq command.dateReceived,
        )
        )
    }

}

class MongoCommandData @BsonCreator constructor(
    @BsonProperty("planterId")
    val planterId: String,
    @BsonProperty("issuedCommand")
    val issuedCommand: CommandType,
    @BsonProperty("instigated")
    val instigated: Boolean,
    @BsonProperty("dateReceived")
    val dateReceived: LocalDateTime,
) {
    constructor(command: Command) : this(
        planterId = command.planterId,
        issuedCommand = command.issuedCommand,
        instigated = command.instigated,
        dateReceived = command.dateReceived
    )

    fun build(): Command = Command(
        planterId = planterId,
        issuedCommand = issuedCommand,
        instigated = instigated,
        dateReceived = dateReceived
    )
}