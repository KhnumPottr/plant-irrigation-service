package com.khnumpottr.plantirrigationservice.dao

import com.khnumpottr.plantirrigationservice.domain.NodeData
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import org.litote.kmongo.*
import org.springframework.stereotype.Component
import java.time.LocalDate


@Component
class ConnectedNodesDAO {

    private val client = KMongo.createClient("mongodb://127.0.0.1:27017")
    private val database = client.getDatabase("plant-irrigation-service")
    private val collection = database.getCollection<MongoNodeData>("connected_Nodes")

    fun insert(nodeData: NodeData) {
        val existingNode = findAllNodes().filter { it.nodeName == nodeData.nodeName }
        if (existingNode.isEmpty()) {
            collection.insertOne(MongoNodeData(nodeData))
        }
    }

    fun find(nodeName: String): NodeData? {
        val found = collection.find(MongoNodeData :: nodeName eq nodeName)
            .map(MongoNodeData::build)
            .toList()
        return if(found.isEmpty()) null else found[0]
    }

    fun findAllNodes(): List<NodeData> {
        return collection.find()
            .map(MongoNodeData::build)
            .toList()
    }

}

class MongoNodeData @BsonCreator constructor(
    @BsonProperty("nodeName")
    val nodeName: String,
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
    constructor(nodeData: NodeData) : this(
        nodeName = nodeData.nodeName,
        title = nodeData.title,
        datePlanted = nodeData.datePlanted,
        upperLimit = nodeData.upperLimit,
        lowerLimit = nodeData.lowerLimit,
        plants = nodeData.plants
    )

    fun build(): NodeData = NodeData(
        nodeName = nodeName,
        title = title,
        datePlanted = datePlanted,
        upperLimit = upperLimit,
        lowerLimit = lowerLimit,
        plants = plants
    )
}