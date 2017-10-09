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

import android.app.Activity
import android.content.Context
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.TextView

object Utils {
    fun mapValueFromRangeToRange(value: Double, fromLow: Double, fromHigh: Double, toLow: Double, toHigh: Double): Double {
        return toLow + (value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow)
    }

    fun clamp(value: Double, low: Double, high: Double): Double {
        return Math.min(Math.max(value, low), high)
    }

    fun hasAccessGranted(context: Context): Boolean {
        val mContentResolver = context.contentResolver
        val mEnabledNotificationListeners = Settings.Secure.getString(mContentResolver, "enabled_notification_listeners")
        val mPackageName = context.packageName

        // Check To See If The 'mEnabledNotificationListeners' String Contains Our Package Name
        return !(mEnabledNotificationListeners == null || !mEnabledNotificationListeners.contains(mPackageName))
    }

    fun makeText(context: Context, message: String, duration: Int): Snackbar {
        val mActivity = context as Activity
        val mLayout: View
        val mSnackBar = Snackbar.make(mActivity.findViewById(android.R.id.content), message, duration)
        mLayout = mSnackBar.view

        // Customize Colors
        mLayout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark))
        val mTextView = mLayout.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        mTextView.setTextColor(context.getResources().getColor(R.color.colorLight))

        return mSnackBar
    }
}
