package com.example.waveloading

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

interface TiltListener {
    fun onTilt(pitchRollRad: Pair<Double, Double>)
}

interface TiltSensor{
    fun addListener(tiltListener: TiltListener)

    fun register()

    fun unregister()
}

class StarTiltListener(context: Context): SensorEventListener, TiltSensor {
    private var sensorManager: SensorManager
    private var accSensor: Sensor
    private var magneticSensor: Sensor
    private var listeners = mutableListOf<TiltListener>()

    private val rotationMatrix = FloatArray(9)
    private val accelerometerValues = FloatArray(3)
    private val magneticValues = FloatArray(3)
    private val orientationAngles = FloatArray(3)

    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            System.arraycopy(event.values, 0, accelerometerValues, 0, accelerometerValues.size)
        } else if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD){
            System.arraycopy(event.values, 0, magneticValues, 0, magneticValues.size)
        }

        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magneticValues)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        val pitchInRed = orientationAngles[1].toDouble()
        val rollInRed = orientationAngles[2].toDouble()
        val pair = Pair(pitchInRed, rollInRed)
        listeners.forEach {
            it.onTilt(pair)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun addListener(tiltListener: TiltListener) {
        listeners.add(tiltListener)
    }

    override fun register() {
        sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun unregister() {
        listeners.clear()
        sensorManager.unregisterListener(this, accSensor)
        sensorManager.unregisterListener(this, magneticSensor)
    }


}