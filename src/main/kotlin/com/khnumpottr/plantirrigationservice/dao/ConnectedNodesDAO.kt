package com.khnumpottr.plantirrigationservice.dao

import com.khnumpottr.plantirrigationservice.domain.PlanterDetails
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import org.litote.kmongo.*
import org.springframework.stereotype.Component
import java.time.LocalDate


@Component
class ConnectedNodesDAO {

    //    private val client = KMongo.createClient("mongodb://mongoDatabase:27017")
    private val client = KMongo.createClient("mongodb://127.0.0.1:27017")
    private val database = client.getDatabase("plant-irrigation-service")
    private val collection = database.getCollection<MongoNodeData>("connected_Nodes")

    fun insert(planterDetails: PlanterDetails) {
        val existingNode = findAllNodes().filter { it.planterId == planterDetails.planterId }
        if (existingNode.isEmpty()) {
            collection.insertOne(MongoNodeData(planterDetails))
        }
    }

    fun find(planterId: String): PlanterDetails? {
        val found = collection.find(MongoNodeData::planterId eq planterId)
            .map(MongoNodeData::build)
            .toList()
        return if (found.isEmpty()) null else found[0]
    }

    fun update(planterData: PlanterDetails): Boolean {
        val command = collection.updateOne(
            MongoNodeData::planterId eq planterData.planterId, set(
                MongoNodeData::title setTo planterData.title,
                MongoNodeData::datePlanted setTo planterData.datePlanted,
                MongoNodeData::upperLimit setTo planterData.upperLimit,
                MongoNodeData::lowerLimit setTo planterData.lowerLimit,
                MongoNodeData::plants setTo planterData.plants,
            )
        )
        return command.matchedCount >= 1
    }

    fun findAllNodes(): List<PlanterDetails> {
        return collection.find()
            .map(MongoNodeData::build)
            .toList()
    }

}

class MongoNodeData @BsonCreator constructor(
    @BsonProperty("planterId")
    val planterId: String,
    @BsonProperty("title")
    val title: String?,
    @BsonProperty("datePlanted")
    val datePlanted: LocalDate?,
    @BsonProperty("upperLimit")
    val upperLimit: Int?,
    @BsonProperty("lowerLimit")
    val lowerLimit: Int?,
    @BsonProperty("plants")
    val plants: String?
) {
    constructor(planterDetails: PlanterDetails) : this(
        planterId = planterDetails.planterId,
        title = planterDetails.title,
        datePlanted = planterDetails.datePlanted,
        upperLimit = planterDetails.upperLimit,
        lowerLimit = planterDetails.lowerLimit,
        plants = planterDetails.plants
    )

    fun build(): PlanterDetails = PlanterDetails(
        planterId = planterId,
        title = title,
        datePlanted = datePlanted,
        upperLimit = upperLimit,
        lowerLimit = lowerLimit,
        plants = plants
    )
}