package com.example.waveloading

import android.animation.ObjectAnimator
import android.animation.ValueAnimator.INFINITE
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.FloatProperty
import android.util.Log
import android.view.View
import android.view.animation.Animation.RESTART
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {
    private var interpolator = LinearInterpolator()
    private var duration: Long = 0
    private var repeatMode: Int = 0
    private var repeatCount: Int = 0
    private var view: CustomView? = null
    private var seekbar: SeekBar? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    fun createPath(sides: Int, radius: Float): Path {
        Log.i("nacm", "createPath: called")
        val path = Path()
        val angle = 2.0 * Math.PI / sides
        path.moveTo(
            150 + (radius * Math.cos(0.0)).toFloat(),
            150 + (radius * Math.sin(0.0)).toFloat())
        for (i in 1 until sides) {
            path.lineTo(
                150 + (radius * Math.cos(angle * i)).toFloat(),
                150 + (radius * Math.sin(angle * i)).toFloat())
        }
        path.close()
        Log.i("nacm", "createPath: "+ path.isEmpty)
        return path
    }

    private fun initViews() {
        val polygonLapsDrawable = PolygonLapsDrawable()
        view = findViewById(R.id.custom_view)
        seekbar = findViewById(R.id.seekbar)
        /*ObjectAnimator.ofFloat(polygonLaps, PROGRESS, 0f, 1f).apply {
            duration = 4000L
            interpolator = LinearInterpolator()
            repeatCount = INFINITE
            repeatMode = RESTART
        }.start()*/
        seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                polygonLapsDrawable.progress = p1.toFloat()
                view?.setProgress(p1.toFloat())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }
    @RequiresApi(Build.VERSION_CODES.N)
    object PROGRESS : FloatProperty<PolygonLapsDrawable>("progress") {
        override fun setValue(pld: PolygonLapsDrawable, progress: Float) {
            pld.progress = progress
        }
        override fun get(pld: PolygonLapsDrawable) = pld.progress
    }

}