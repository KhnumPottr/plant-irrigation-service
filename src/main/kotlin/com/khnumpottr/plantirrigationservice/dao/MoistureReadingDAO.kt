package com.khnumpottr.plantirrigationservice.dao

import com.khnumpottr.plantirrigationservice.domain.PlanterSummaryData
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import org.litote.kmongo.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime


@Component

class MoistureReadingDAO {

    private val client = KMongo.createClient("mongodb://127.0.0.1:27017")
    private val database = client.getDatabase("plant-irrigation-service")
    private val collection = database.getCollection<MongoMoistureData>("moisture_reading")

    fun insert(planterSummaryData: PlanterSummaryData) {
        collection.insertOne(MongoMoistureData(planterSummaryData))
    }

    fun findAllMoistureReports(planterId: String): List<PlanterSummaryData> {
        return collection.find(
            and(
                MongoMoistureData::planterId eq planterId,
                MongoMoistureData::dateReceived gte LocalDateTime.now().minusDays(5)
            )
        )
            .sort(ascending(MongoMoistureData::dateReceived))
            .map(MongoMoistureData::build)
            .toList()
    }

    fun findRecentReporting(planterId: String): PlanterSummaryData?{
        return collection.find(and(
            MongoMoistureData::planterId eq planterId,
            )
        )
            .sort(descending(MongoMoistureData::dateReceived))
            .map(MongoMoistureData::build)
            .first()
    }

}

class MongoMoistureData @BsonCreator constructor(
    @BsonProperty("planterId")
    val planterId: String,
    @BsonProperty("moisturePercentage")
    val moisturePercentage: Int,
    @BsonProperty("irrigating")
    val irrigating: Boolean,
    @BsonProperty("dateReceived")
    val dateReceived: LocalDateTime,
) {
    constructor(planterSummaryData: PlanterSummaryData) : this(
        planterId = planterSummaryData.planterId,
        moisturePercentage = planterSummaryData.moistureLevel!!,
        irrigating = planterSummaryData.irrigating,
        dateReceived = planterSummaryData.dateReceived
    )

    fun build(): PlanterSummaryData = PlanterSummaryData(
        planterId = planterId,
        moistureLevel = moisturePercentage,
        irrigating = irrigating,
        dateReceived = dateReceived
    )
}