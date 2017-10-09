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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView

class AwesomeView : CoordinatorLayout, View.OnClickListener {

    private var vNotification: ImageView? = null
    private var vDotsView: DotsView? = null
    private var vCircle: CircleView? = null

    private var isChecked: Boolean = false
    private var mAnimatorSet: AnimatorSet? = null
    private var mPreferences: SharedPreferences? = null

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
        LayoutInflater.from(context).inflate(R.layout.view_awesome, this, true)
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        isChecked = mPreferences!!.getBoolean(context.getString(R.string.access_granted), false)
        vNotification = findViewById(R.id.vNotification)
        vDotsView = findViewById(R.id.vDotsView)
        vCircle = findViewById(R.id.vCircle)
        vNotification!!.setImageResource(if (isChecked) R.drawable.ic_notification_on else R.drawable.ic_notification_off)
        setOnClickListener(this)
    }

    override fun onClick(view: View) {
        isChecked = !isChecked

        vNotification!!.setImageResource(if (isChecked) R.drawable.ic_notification_on else R.drawable.ic_notification_off)

        if (mAnimatorSet != null) {
            mAnimatorSet!!.cancel()
        }

        if (isChecked) {
            vNotification!!.animate().cancel()
            vNotification!!.scaleX = 0f
            vNotification!!.scaleY = 0f
            vCircle!!.innerCircleRadiusProgress = 0f
            vCircle!!.outerCircleRadiusProgress = 0f
            vDotsView!!.currentProgress = 0f

            mAnimatorSet = AnimatorSet()

            val mOuterCircleAnimator = ObjectAnimator.ofFloat(vCircle, CircleView.OUTER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f)
            mOuterCircleAnimator.duration = 250
            mOuterCircleAnimator.interpolator = DECCELERATE_INTERPOLATOR

            val mInnerCircleAnimator = ObjectAnimator.ofFloat(vCircle, CircleView.INNER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f)
            mInnerCircleAnimator.duration = 200
            mInnerCircleAnimator.startDelay = 200
            mInnerCircleAnimator.interpolator = DECCELERATE_INTERPOLATOR

            val mNotificationScaleYAnimator = ObjectAnimator.ofFloat(vNotification, ImageView.SCALE_Y, 0.2f, 1f)
            mNotificationScaleYAnimator.duration = 350
            mNotificationScaleYAnimator.startDelay = 250
            mNotificationScaleYAnimator.interpolator = OVERSHOOT_INTERPOLATOR

            val mNotificationScaleXAnimator = ObjectAnimator.ofFloat(vNotification, ImageView.SCALE_X, 0.2f, 1f)
            mNotificationScaleXAnimator.duration = 350
            mNotificationScaleXAnimator.startDelay = 250
            mNotificationScaleXAnimator.interpolator = OVERSHOOT_INTERPOLATOR

            val mDotsAnimator = ObjectAnimator.ofFloat<DotsView>(vDotsView, DotsView.DOTS_PROGRESS, 0f, 1f)
            mDotsAnimator.duration = 900
            mDotsAnimator.startDelay = 50
            mDotsAnimator.interpolator = ACCELERATE_DECELERATE_INTERPOLATOR

            mAnimatorSet!!.playTogether(
                    mOuterCircleAnimator,
                    mInnerCircleAnimator,
                    mNotificationScaleYAnimator,
                    mNotificationScaleXAnimator,
                    mDotsAnimator
            )

            mAnimatorSet!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    vCircle!!.innerCircleRadiusProgress = 0f
                    vCircle!!.outerCircleRadiusProgress = 0f
                    vDotsView!!.currentProgress = 0f
                    vNotification!!.scaleX = 1f
                    vNotification!!.scaleY = 1f
                }
            })

            mAnimatorSet!!.start()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                vNotification!!.animate().scaleX(0.7f).scaleY(0.7f).setDuration(150).interpolator = DECCELERATE_INTERPOLATOR
                isPressed = true
            }

            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val y = event.y
                val isInside = x > 0 && x < width && y > 0 && y < height
                if (isPressed != isInside) {
                    isPressed = isInside
                }
            }

            MotionEvent.ACTION_UP -> {
                vNotification!!.animate().scaleX(1f).scaleY(1f).interpolator = DECCELERATE_INTERPOLATOR
                if (isPressed) {
                    performClick()
                    isPressed = false
                }
            }
        }
        return true
    }

    companion object {
        private val DECCELERATE_INTERPOLATOR = DecelerateInterpolator()
        private val ACCELERATE_DECELERATE_INTERPOLATOR = AccelerateDecelerateInterpolator()
        private val OVERSHOOT_INTERPOLATOR = OvershootInterpolator(4f)
    }
}
