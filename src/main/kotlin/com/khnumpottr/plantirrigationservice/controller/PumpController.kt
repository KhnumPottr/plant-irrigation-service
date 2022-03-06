package com.khnumpottr.plantirrigationservice.controller

import com.pi4j.io.gpio.*
import com.pi4j.io.gpio.RaspiPin.GPIO_04
import mu.KotlinLogging

class PumpController {

    private fun digitalOutput(
        pin: Pin,
        name: String = pin.name,
        provider: GpioProvider = GpioFactory.getDefaultProvider(),
        state: PinState = PinState.LOW,
        controller: GpioController = GpioFactory.getInstance(),
        initializer: GpioPinDigitalOutput.() -> Unit = { }
    ): GpioPinDigitalOutput { return controller.provisionDigitalOutputPin(provider, pin, name, state).apply(initializer)}

    private var pumpIsActive: Boolean = false;
    private val output = digitalOutput(GPIO_04)

    fun powerPump(moistureLevel:Int){
        if(moistureLevel < 15 && !pumpIsActive){
            LOG.info { "Powering ON Pump" }
            pumpIsActive = true
            output.high()
        }
        else if(moistureLevel > 60 && pumpIsActive){
            LOG.info { "Powering OFF Pump" }
            pumpIsActive = false
            output.low()
        }
    }

    fun gpioShutdown() = GpioFactory.getInstance().shutdown()

    companion object {
        private val LOG = KotlinLogging.logger {}
    }
}