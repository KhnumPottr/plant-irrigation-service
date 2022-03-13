package com.khnumpottr.plantirrigationservice.dao

import com.khnumpottr.plantirrigationservice.domain.NodeData
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import org.litote.kmongo.*
import org.springframework.stereotype.Component


@Component

class ConnectedNodesDAO {

    private val client = KMongo.createClient("mongodb://mongoDatabase:27017")
    private val database = client.getDatabase("plant-irrigation-service")
    private val collection = database.getCollection<MongoNodeData>("connected_Nodes")

    fun insert(nodeData: NodeData) {
        val nodes = findAllNodes()
        if(!nodes.contains(nodeData.nodeName)){
            collection.insertOne(MongoNodeData(nodeData))
        }
    }

    fun findAllNodes(): List<String> {
        return collection.find()
            .map(MongoNodeData::build)
            .toList()
    }

}

class MongoNodeData @BsonCreator constructor(
    @BsonProperty("nodeName")
    val nodeName: String
) {
    constructor(nodeData: NodeData) : this(
        nodeName = nodeData.nodeName,
    )

    fun build(): String = nodeName
}