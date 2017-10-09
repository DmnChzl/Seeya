/**
 * Copyright (C) 2017 Damien Chazoule
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */

package com.doomy.seeya

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Property
import android.view.View

class CircleView : View {

    private val mArgbEvaluator = ArgbEvaluator()

    private val mCirclePaint = Paint()
    private val mMaskPaint = Paint()

    private var mTempBitmap: Bitmap? = null
    private var mTempCanvas: Canvas? = null

    var outerCircleRadiusProgress = 0f
        set(outerCircleRadiusProgress) {
            field = outerCircleRadiusProgress
            updateCircleColor()
            postInvalidate()
        }
    var innerCircleRadiusProgress = 0f
        set(innerCircleRadiusProgress) {
            field = innerCircleRadiusProgress
            postInvalidate()
        }

    private var mMaxCircleSize: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mCirclePaint.style = Paint.Style.FILL
        mMaskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        mMaxCircleSize = width / 2
        mTempBitmap = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888)
        mTempCanvas = Canvas(mTempBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mTempCanvas!!.drawColor(0xffffff, PorterDuff.Mode.CLEAR)
        mTempCanvas!!.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), outerCircleRadiusProgress * mMaxCircleSize, mCirclePaint)
        mTempCanvas!!.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), innerCircleRadiusProgress * mMaxCircleSize, mMaskPaint)
        canvas.drawBitmap(mTempBitmap!!, 0f, 0f, null)
    }

    private fun updateCircleColor() {
        var mColorProgress = Utils.clamp(outerCircleRadiusProgress.toDouble(), 0.5, 1.0).toFloat()
        mColorProgress = Utils.mapValueFromRangeToRange(mColorProgress.toDouble(), 0.5, 1.0, 0.0, 1.0).toFloat()
        this.mCirclePaint.color = mArgbEvaluator.evaluate(mColorProgress, START_COLOR, END_COLOR) as Int
    }

    companion object {
        private val START_COLOR = 0xFF2979FF.toInt()
        private val END_COLOR = 0xFF1DE9B6.toInt()

        val INNER_CIRCLE_RADIUS_PROGRESS: Property<CircleView, Float> = object : Property<CircleView, Float>(Float::class.java, "innerCircleRadiusProgress") {
            override fun get(`object`: CircleView): Float {
                return `object`.innerCircleRadiusProgress
            }

            override fun set(`object`: CircleView, value: Float?) {
                `object`.innerCircleRadiusProgress = value!!
            }
        }

        val OUTER_CIRCLE_RADIUS_PROGRESS: Property<CircleView, Float> = object : Property<CircleView, Float>(Float::class.java, "outerCircleRadiusProgress") {
            override fun get(`object`: CircleView): Float {
                return `object`.outerCircleRadiusProgress
            }

            override fun set(`object`: CircleView, value: Float?) {
                `object`.outerCircleRadiusProgress = value!!
            }
        }
    }
}
