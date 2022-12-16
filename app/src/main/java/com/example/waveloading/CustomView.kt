package com.example.waveloading

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.NonNull

class CustomView @JvmOverloads constructor(
    context: Context,
    @NonNull attrs: AttributeSet,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
): View(context, attrs, defStyleAttr, defStyleRes) {

    private var wavePaint: Paint? = null
    private var lightWavePaint: Paint? = null
    private var circlePaint: Paint? = null
    private var textPaint: Paint? = null
    private var path: Path? = null
    private var screenWidth = 0
    private var screenHeight = 0
    private val amplitude = 100
    private var progress = 0f
    private var textProgress = 0f
    private val startPoint = Point()

    fun setProgress(progress: Float){
        textProgress = progress
        if (progress == 100f){
            this.progress = progress + amplitude
        } else {
            this.progress = progress
        }
    }

    init {
        init()
    }

    private fun init(){
        wavePaint = Paint()
        wavePaint?.isAntiAlias = true
        wavePaint?.strokeWidth = 1f
        lightWavePaint = Paint()
        lightWavePaint?.isAntiAlias = true
        lightWavePaint?.strokeWidth = 0.5f
        textPaint = Paint()
        textPaint?.style = Paint.Style.STROKE
        textPaint?.isAntiAlias = true
        textPaint?.color = Color.parseColor("#FFFFFF")
        textPaint?.textSize = 50f
        circlePaint = Paint()
        circlePaint?.isAntiAlias = true
        circlePaint?.color = Color.parseColor("#292929")
        circlePaint?.strokeWidth = 10f
        circlePaint?.style = Paint.Style.STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = Math.min(measureSize(400, widthMeasureSpec),
            measureSize(400, heightMeasureSpec))
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        screenWidth = w
        screenHeight = h
        startPoint.x = -screenHeight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        clipCircle(canvas)
        drawCircle(canvas)
        drawWave(canvas)
//        drawAnotherWave(canvas)
        drawText(canvas)
        postInvalidateDelayed(10)

    }

    private fun drawCircle(canvas: Canvas?){
        val shapeBounds = RectF(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat())
        canvas?.drawRoundRect(shapeBounds, 50f, 50f, circlePaint!!)
        /*canvas?.drawCircle(
            (screenHeight/2).toFloat(),
            (screenHeight/2).toFloat(),
            (screenHeight/2).toFloat(),
            circlePaint!!
        )*/
    }


    private fun drawText(canvas: Canvas?){
        val targetRect = Rect(0, -screenHeight, screenWidth, 0)
        val fontMetrics = textPaint?.fontMetrics
        textPaint?.color = Color.RED
        val baseline = (targetRect.bottom + targetRect.top + fontMetrics!!.bottom - fontMetrics.top) / 2
        textPaint?.textAlign = Paint.Align.CENTER
        canvas?.drawText((textProgress).toString() + "%",
            targetRect.centerX().toFloat(),
            baseline.toFloat(),
            textPaint!!)
    }

    private fun drawWave(canvas: Canvas?){
        val height = (progress / 100 * screenHeight).toInt()
        startPoint.y = -height
        canvas?.translate(0f, screenHeight.toFloat())
        path = Path()
        wavePaint?.style = Paint.Style.FILL
        if (progress < 20)
            wavePaint?.color = Color.parseColor("#AD2E45")
        else if (progress >21 && progress < 50)
            wavePaint?.color = Color.parseColor("#F39F61")
        else if (progress < 70)
            wavePaint?.color = Color.parseColor("#AABF67")
        else
            wavePaint?.color = Color.parseColor("#2D7D7E")
//        wavePaint?.color = Color.parseColor("#3F0FB7")
        val wave = screenWidth / 4
        path?.moveTo(startPoint.x.toFloat(), startPoint.y.toFloat())
        for (i in 0..6){
            val startX = startPoint.x + i * wave * 2
            val endX = startX + 2 * wave
            if (i % 2 == 0){
                /*path?.quadTo(
                    ((startX + endX)/2).toFloat(),
                    (startPoint.y + amplitude).toFloat(),
                    endX.toFloat(),
                    startPoint.y.toFloat()
                )*/
                path?.cubicTo(
                    ((startX + endX)/2).toFloat(),
                    (startPoint.y + amplitude).toFloat(),
                    endX.toFloat(),
                    startPoint.y.toFloat(),
                    (startX+amplitude*2).toFloat(),
                    startPoint.y.toFloat()
                )
            }else{
                path?.quadTo(
                    ((startX + endX)/2).toFloat(),
                    (startPoint.y - amplitude).toFloat(),
                    endX.toFloat(),
                    startPoint.y.toFloat()
                )
            }
        }
        path?.lineTo(screenWidth.toFloat(),(screenHeight / 2).toFloat())
        path?.lineTo(-screenWidth.toFloat(),(screenHeight / 2).toFloat())
        path?.lineTo(-screenWidth.toFloat(), 0f)
        path?.close()
        canvas?.drawPath(path!!, wavePaint!!)
        startPoint.x += 10
        if (startPoint.x >= 0){
            startPoint.x = -screenWidth
        }
        path?.reset()
    }

    private fun drawAnotherWave(canvas: Canvas){
        val height = (progress / 50 * screenHeight).toInt()
        startPoint.y = -height + 10
        canvas.translate(0f, screenHeight.toFloat())
        path = Path()
        lightWavePaint?.style = Paint.Style.FILL
        lightWavePaint?.color = Color.parseColor("#c774df")
        val wave = screenWidth / 4
        path?.moveTo(startPoint.x.toFloat(), startPoint.y.toFloat())
        for (i in 0..3){
            val startX = startPoint.x + i * wave * 2
            val endX = startX + 2 * wave
            if (i % 2 == 0){
                path?.quadTo(
                    ((startX + endX)/2).toFloat(),
                    (startPoint.y + amplitude).toFloat(),
                    endX.toFloat(),
                    startPoint.y.toFloat()
                )
            }else{
                path?.quadTo(
                    ((startX + endX)/2).toFloat(),
                    (startPoint.y - amplitude).toFloat(),
                    endX.toFloat(),
                    startPoint.y.toFloat()
                )
            }
        }
        path?.lineTo(screenWidth.toFloat(),(screenHeight / 2).toFloat())
        path?.lineTo(-screenWidth.toFloat(),(screenHeight / 2).toFloat())
        path?.lineTo(-screenWidth.toFloat(), 0f)
        path?.close()
        canvas.drawPath(path!!, lightWavePaint!!)
        startPoint.x += 5
        if (startPoint.x >= 0){
            startPoint.x = -screenWidth
        }
        path?.reset()
    }

    private fun clipCircle(canvas: Canvas){
        val circlePath = Path()
//        circlePath.addRoundRect(150f, 150f, screenWidth.toFloat(), screenHeight.toFloat(), 150f, 150f, Path.Direction.CCW)
        circlePath.addRoundRect(15f, 15f, screenWidth.toFloat(), screenHeight.toFloat(), 15f, 15f, Path.Direction.CCW)
        canvas.clipPath(circlePath)
        /*circlePath.addCircle(
            (screenWidth/2).toFloat(),
            (screenHeight/2).toFloat(),
            (screenHeight/2).toFloat(),
            Path.Direction.CCW
        )*/
    }

    private fun measureSize(defaultSize: Int, measureSpec: Int): Int {
        var result = defaultSize
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        when (mode){
            MeasureSpec.UNSPECIFIED -> result = mode
            MeasureSpec.AT_MOST, MeasureSpec.EXACTLY -> result = size
            else -> {}
        }
        return result
    }

}