package com.example.turbo_transport

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class SignatureView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private var path = Path()
    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 10f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
            }
            MotionEvent.ACTION_UP -> {
                //Any actions here?
            }
            else -> return false
        }

        invalidate()
        return true
    }

    fun clearSignature() {
        path.reset()
        invalidate()
    }
    fun getSignatureBitmap(): Bitmap {
        //Create a bitmap where the signature will be drawn
        val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        //use canvas
        val canvas = Canvas(bitmap)
        //create background otherwise transparent
        canvas.drawColor(Color.WHITE)
        //Draw other signatureview onto the canvas and return the bitmap created
        this.draw(canvas)
        return bitmap
    }

}
