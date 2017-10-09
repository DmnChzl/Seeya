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
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Property
import android.view.View

class DotsView : View {

    private val mCirclePaints = arrayOfNulls<Paint>(4)

    private var mCenterX: Int = 0
    private var mCenterY: Int = 0

    private var mMaxOuterDotsRadius: Float = 0.toFloat()
    private var mMaxInnerDotsRadius: Float = 0.toFloat()
    private var mMaxDotSize: Float = 0.toFloat()

    var currentProgress = 0f
        set(currentProgress) {
            field = currentProgress

            updateInnerDotsPosition()
            updateOuterDotsPosition()
            updateDotsPaints()
            updateDotsAlpha()

            postInvalidate()
        }

    private var mCurrentRadiusOne = 0f
    private var mCurrentDotSizeOne = 0f

    private var mCurrentDotSizeTwo = 0f
    private var mCurrentRadiusTwo = 0f

    private val mArgbEvaluator = ArgbEvaluator()

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
        for (i in mCirclePaints.indices) {
            mCirclePaints[i] = Paint()
            mCirclePaints[i]?.style = Paint.Style.FILL
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        mCenterX = width / 2
        mCenterY = height / 2
        mMaxDotSize = 20f
        mMaxOuterDotsRadius = width / 2 - mMaxDotSize * 2
        mMaxInnerDotsRadius = 0.8f * mMaxOuterDotsRadius
    }

    override fun onDraw(canvas: Canvas) {
        drawOuterDotsFrame(canvas)
        drawInnerDotsFrame(canvas)
    }

    private fun drawOuterDotsFrame(canvas: Canvas) {
        for (i in 0..DOTS_COUNT - 1) {
            val cX = (mCenterX + mCurrentRadiusOne * Math.cos(i.toDouble() * OUTER_DOTS_POSITION_ANGLE.toDouble() * Math.PI / 180)).toInt()
            val cY = (mCenterY + mCurrentRadiusOne * Math.sin(i.toDouble() * OUTER_DOTS_POSITION_ANGLE.toDouble() * Math.PI / 180)).toInt()
            canvas.drawCircle(cX.toFloat(), cY.toFloat(), mCurrentDotSizeOne, mCirclePaints[i % mCirclePaints.size])
        }
    }

    private fun drawInnerDotsFrame(canvas: Canvas) {
        for (i in 0..DOTS_COUNT - 1) {
            val cX = (mCenterX + mCurrentRadiusTwo * Math.cos((i * OUTER_DOTS_POSITION_ANGLE - 10) * Math.PI / 180)).toInt()
            val cY = (mCenterY + mCurrentRadiusTwo * Math.sin((i * OUTER_DOTS_POSITION_ANGLE - 10) * Math.PI / 180)).toInt()
            canvas.drawCircle(cX.toFloat(), cY.toFloat(), mCurrentDotSizeTwo, mCirclePaints[(i + 1) % mCirclePaints.size])
        }
    }

    private fun updateInnerDotsPosition() {
        if (currentProgress < 0.3f) {
            this.mCurrentRadiusTwo = Utils.mapValueFromRangeToRange(currentProgress.toDouble(), 0.0, 0.3, 0.0, mMaxInnerDotsRadius.toDouble()).toFloat()
        } else {
            this.mCurrentRadiusTwo = mMaxInnerDotsRadius
        }

        if (currentProgress < 0.2) {
            this.mCurrentDotSizeTwo = mMaxDotSize
        } else if (currentProgress < 0.5) {
            this.mCurrentDotSizeTwo = Utils.mapValueFromRangeToRange(currentProgress.toDouble(), 0.2, 0.5, mMaxDotSize.toDouble(), 0.3 * mMaxDotSize).toFloat()
        } else {
            this.mCurrentDotSizeTwo = Utils.mapValueFromRangeToRange(currentProgress.toDouble(), 0.5, 1.0, (mMaxDotSize * 0.3f).toDouble(), 0.0).toFloat()
        }

    }

    private fun updateOuterDotsPosition() {
        if (currentProgress < 0.3f) {
            this.mCurrentRadiusOne = Utils.mapValueFromRangeToRange(currentProgress.toDouble(), 0.0, 0.3, 0.0, (mMaxOuterDotsRadius * 0.8f).toDouble()).toFloat()
        } else {
            this.mCurrentRadiusOne = Utils.mapValueFromRangeToRange(currentProgress.toDouble(), 0.3, 1.0, (0.8f * mMaxOuterDotsRadius).toDouble(), mMaxOuterDotsRadius.toDouble()).toFloat()
        }

        if (currentProgress < 0.7) {
            this.mCurrentDotSizeOne = mMaxDotSize
        } else {
            this.mCurrentDotSizeOne = Utils.mapValueFromRangeToRange(currentProgress.toDouble(), 0.7, 1.0, mMaxDotSize.toDouble(), 0.0).toFloat()
        }
    }

    private fun updateDotsPaints() {
        if (currentProgress < 0.5f) {
            val mProgress = Utils.mapValueFromRangeToRange(currentProgress.toDouble(), 0.0, 0.5, 0.0, 1.0).toFloat()
            mCirclePaints[0]?.color = mArgbEvaluator.evaluate(mProgress, COLOR_ONE, COLOR_TWO) as Int
            mCirclePaints[1]?.color = mArgbEvaluator.evaluate(mProgress, COLOR_TWO, COLOR_THREE) as Int
            mCirclePaints[2]?.color = mArgbEvaluator.evaluate(mProgress, COLOR_THREE, COLOR_FOUR) as Int
            mCirclePaints[3]?.color = mArgbEvaluator.evaluate(mProgress, COLOR_FOUR, COLOR_ONE) as Int
        } else {
            val mProgress = Utils.mapValueFromRangeToRange(currentProgress.toDouble(), 0.5, 1.0, 0.0, 1.0).toFloat()
            mCirclePaints[0]?.color = mArgbEvaluator.evaluate(mProgress, COLOR_TWO, COLOR_THREE) as Int
            mCirclePaints[1]?.color = mArgbEvaluator.evaluate(mProgress, COLOR_THREE, COLOR_FOUR) as Int
            mCirclePaints[2]?.color = mArgbEvaluator.evaluate(mProgress, COLOR_FOUR, COLOR_ONE) as Int
            mCirclePaints[3]?.color = mArgbEvaluator.evaluate(mProgress, COLOR_ONE, COLOR_TWO) as Int
        }
    }

    private fun updateDotsAlpha() {
        val mProgress = Utils.clamp(currentProgress.toDouble(), 0.6, 1.0).toFloat()
        val mAlpha = Utils.mapValueFromRangeToRange(mProgress.toDouble(), 0.6, 1.0, 255.0, 0.0).toInt()
        mCirclePaints[0]?.alpha = mAlpha
        mCirclePaints[1]?.alpha = mAlpha
        mCirclePaints[2]?.alpha = mAlpha
        mCirclePaints[3]?.alpha = mAlpha
    }

    companion object {
        private val DOTS_COUNT = 7
        private val OUTER_DOTS_POSITION_ANGLE = 360 / DOTS_COUNT

        private val COLOR_ONE = 0xFF1DE9B6.toInt() // Teal
        private val COLOR_TWO = 0xFF00E5FF.toInt() // Cyan
        private val COLOR_THREE = 0xFF00B0FF.toInt() // Light Blue
        private val COLOR_FOUR = 0xFF1DE9B6.toInt() // Blue

        val DOTS_PROGRESS: Property<DotsView, Float> = object : Property<DotsView, Float>(Float::class.java, "dotsProgress") {
            override fun get(`object`: DotsView): Float {
                return `object`.currentProgress
            }

            override fun set(`object`: DotsView, value: Float?) {
                `object`.currentProgress = value!!
            }
        }
    }
}
