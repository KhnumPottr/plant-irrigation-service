package com.khnumpottr.plantirrigationservice.cache

import com.khnumpottr.plantirrigationservice.domain.IrrigationData


object AllMoistureLevelCache {
    var levels = ArrayList<IrrigationData>()

    /**
     * Store levels
     *
     * update levels
     *
     * clear levels
     */

    fun update(irrigationData: IrrigationData){
        val contains = levels.filter { it.nodeName == irrigationData.nodeName }
        if(contains.isEmpty()){
            levels.add(irrigationData)
        } else {
            levels.forEachIndexed { index, data ->
                if(irrigationData.nodeName == data.nodeName){
                    levels[index] = irrigationData
                }
            }
        }
    }

    fun clear(){
        levels = ArrayList<IrrigationData>()
    }

}