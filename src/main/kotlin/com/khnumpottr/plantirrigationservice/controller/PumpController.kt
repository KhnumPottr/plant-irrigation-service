package com.khnumpottr.plantirrigationservice.controller

import com.pi4j.io.gpio.*
import com.pi4j.io.gpio.RaspiPin.GPIO_07
import mu.KotlinLogging


class PumpController {

    val gpio = GpioFactory.getInstance()
    val pump: GpioPinDigitalOutput  = gpio.provisionDigitalOutputPin(
        GPIO_07,  // PIN NUMBER
        "waterPump",  // PIN FRIENDLY NAME (optional)
        PinState.LOW
    )

    private var pumpIsActive: Boolean = false;

    fun powerPump(moistureLevel:Int){
        if(moistureLevel < 15 && !pumpIsActive){
            LOG.info { "Powering ON Pump" }
            pumpIsActive = true
            pump.high();
        }
        else if(moistureLevel > 60 && pumpIsActive){
            LOG.info { "Powering OFF Pump" }
            pumpIsActive = false
            pump.low();
        }
    }

    companion object {
        private val LOG = KotlinLogging.logger {}
    }
}