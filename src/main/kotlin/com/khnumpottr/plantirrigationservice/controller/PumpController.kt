package com.khnumpottr.plantirrigationservice.controller

import com.pi4j.Pi4J
import com.pi4j.io.gpio.digital.DigitalOutput
import com.pi4j.io.gpio.digital.DigitalState
import mu.KotlinLogging


class PumpController {

    private val PIN_PUMP = 4

    val pi4j = Pi4J.newAutoContext()
    val pumpConfig = DigitalOutput.newConfigBuilder(pi4j)
        .id("pump")
        .name("waterPump")
        .address(PIN_PUMP)
        .shutdown(DigitalState.LOW)
        .initial(DigitalState.LOW)
        .provider("pigpio-digital-output")

    val pump = pi4j.create(pumpConfig)
    
    private var pumpIsActive: Boolean = false

    fun powerPump(moistureLevel: Int) {
        if (moistureLevel < 15 && !pumpIsActive) {
            LOG.info { "Powering ON Pump" }
            pumpIsActive = true
            pump.high()
        } else if (moistureLevel > 60 && pumpIsActive) {
            LOG.info { "Powering OFF Pump" }
            pumpIsActive = false
            pump.low()
        }
    }

    companion object {
        private val LOG = KotlinLogging.logger {}
    }
}