package com.khnumpottr.plantirrigationservice.cache

import com.khnumpottr.plantirrigationservice.domain.IrrigationData
import com.khnumpottr.plantirrigationservice.domain.MessageData
import com.khnumpottr.plantirrigationservice.domain.NodeMoistureLevels
import java.lang.Integer.parseInt
import java.time.LocalDateTime


object AllMoistureLevelCache {
    var levels = ArrayList<NodeMoistureLevels>()

    /**
     * Store levels
     *
     * update levels
     *
     * clear levels
     */

    fun update(messageData: MessageData) {
        val contains = levels.filter { it.nodeName == messageData.nodeName }
        if (contains.isEmpty()) {
            levels.add(
                NodeMoistureLevels(
                    nodeName = messageData.nodeName,
                    levels = listOf(
                        IrrigationData(moisturePercentage = parseInt(messageData.payload))
                    )
                )
            )
        } else {
            levels.forEachIndexed { index, data ->
                if (messageData.nodeName == data.nodeName) {
                    levels[index] =
                        data.copy(levels = listOf(IrrigationData(moisturePercentage = parseInt(messageData.payload))))
                }
            }
        }
    }

    fun clear() {
        levels = ArrayList<NodeMoistureLevels>()
    }

}