package com.example.waveloading

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.hypot

class StarDrawer
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.starDrawableStyle
): View(context, attrs, defStyleAttr), TiltListener {
    private val wavePath = Path()
    private var starGap: Float
    private var wavePaint: Paint
    private val gradientPaint = Paint(ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }
    private val gradientMatrix = Matrix()
    val tiltSensor = StarTiltListener(context)
    private val green = Color.RED
    // solid green in the center, transparent green at the edges
    private val gradientColors =
        intArrayOf(green, modifyAlpha(green, 0.80f),
            modifyAlpha(green, 0.05f))
    private var center = PointF(0f, 0f)
    private var maxRadius = 0f
    private var initialRadius = 0f
    private var waveAnimator: ValueAnimator? = null
    private var waveRadiusOffset = 0f
    set(value) {
        field = value
        postInvalidateOnAnimation()
    }

    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.StarDrawer, defStyleAttr, 0)
        starGap = attr.getDimension(R.styleable.StarDrawer_starGap, 50f)
        wavePaint = Paint(ANTI_ALIAS_FLAG).apply {
            color = attr.getColor(R.styleable.StarDrawer_starColor, 0)
            strokeWidth = attr.getDimension(R.styleable.StarDrawer_starStrokeWidth, 1f)
            style = Paint.Style.STROKE
        }
        attr.recycle()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var currentRadius = initialRadius + waveRadiusOffset
        while (currentRadius < maxRadius){
            val path = createStarPath(currentRadius, wavePath)
            canvas?.drawPath(path, wavePaint)
            currentRadius += starGap
        }
        canvas?.drawPaint(gradientPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        center.set(w/2f, h/2f)
        maxRadius = hypot(center.x.toDouble(), center.y.toDouble()).toFloat() * 1.3f
        initialRadius = w / starGap
        gradientPaint.shader = RadialGradient(
            center.x, center.y, w/2f,
            gradientColors, null, Shader.TileMode.CLAMP
        )
    }

    private fun createStarPath(radius: Float, path: Path = Path(), points: Int = 12): Path {
        path.reset()
        val pointDelta = 0.7f
        val angelInRadians = 2.0 * Math.PI / points
        val startAngelInRadians = 0.0

        path.moveTo(
            center.x + (radius * pointDelta * Math.cos(startAngelInRadians)).toFloat(),
            center.y + (radius * pointDelta * Math.sin(startAngelInRadians)).toFloat()
        )
        for (i in 1 until points){
            val hypotenuse = if (i % 2 == 0){
                pointDelta * radius
            } else {
                radius
            }

            val nextPointX = center.x + (hypotenuse * Math.cos(startAngelInRadians - angelInRadians * i)).toFloat()
            val nextPointY = center.y + (hypotenuse * Math.sin(startAngelInRadians - angelInRadians * i)).toFloat()
            path.lineTo(nextPointX, nextPointY)
        }
        path.close()
        return path
    }

    private fun modifyAlpha(color: Int, alpha: Float): Int {
        return color and 0x00ffffff or ((alpha * 255).toInt() shl 24)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        waveAnimator = ValueAnimator.ofFloat(0f, starGap).apply {
            addUpdateListener {
                waveRadiusOffset = it.animatedValue as Float
            }
            duration = 1500L
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
        tiltSensor.addListener(this)
        tiltSensor.register()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        waveAnimator?.cancel()
        tiltSensor.unregister()
    }

    override fun onTilt(pitchRollRad: Pair<Double, Double>) {
        val pitchRed = pitchRollRad.first
        val rollRed = pitchRollRad.second

        val maxXOffset = center.x.toDouble()
        val maxYOffset = center.y.toDouble()

        val xOffset = (Math.sin(rollRed) * maxXOffset)
        val yOffset = (Math.sin(pitchRed) * maxYOffset)
        updateGradient(
            xOffset.toFloat() + center.x,
            yOffset.toFloat() + center.y
        )
    }

    private fun updateGradient(x: Float, y: Float) {
        gradientMatrix.setTranslate(x - center.x, y - center.y)
        gradientPaint.shader.setLocalMatrix(gradientMatrix)
        postInvalidateOnAnimation()
    }

}