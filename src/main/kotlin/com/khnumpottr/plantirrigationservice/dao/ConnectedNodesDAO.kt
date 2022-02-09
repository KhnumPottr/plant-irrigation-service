package com.khnumpottr.plantirrigationservice.dao.mongo

import com.khnumpottr.plantirrigationservice.domain.MessageData
import com.khnumpottr.plantirrigationservice.domain.NodeData
import com.khnumpottr.plantirrigationservice.domain.enums.MessageTypes
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.springframework.stereotype.Component
import java.lang.Integer.parseInt
import java.time.LocalDateTime


@Component

class ConnectedNodesDAO {

    private val client = KMongo.createClient()
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