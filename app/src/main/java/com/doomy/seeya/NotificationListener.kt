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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationListener : NotificationListenerService() {

    private var MANY_YEARS: Long = 10000000000000L
    private var mNotificationListenerBroadcastReceiver: NotificationListenerBroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()
        mNotificationListenerBroadcastReceiver = NotificationListenerBroadcastReceiver()
        val mFilter = IntentFilter()
        mFilter.addAction(getString(R.string.intent_filter))
        registerReceiver(mNotificationListenerBroadcastReceiver, mFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mNotificationListenerBroadcastReceiver != null) {
            unregisterReceiver(mNotificationListenerBroadcastReceiver)
        }
    }

    override fun onNotificationPosted(statusBarNotification: StatusBarNotification?) {
        if (statusBarNotification == null) {
            return
        }

        if (statusBarNotification.packageName == "android") {
            val mKey = statusBarNotification.notification.extras.getString(getString(R.string.intent_key)) ?: return

            val mContentSingular = getString(R.string.single_notification_content)
            val mContentPlural = getString(R.string.multiple_notification_content)

            if (mKey.contains(mContentSingular) || mKey.contains(mContentPlural)) {
                this@NotificationListener.snoozeNotification(statusBarNotification.key, MANY_YEARS)
            }
        }
    }

    internal inner class NotificationListenerBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getStringExtra("command") == "hide") {
                for (statusBarNotification in this@NotificationListener.activeNotifications) {
                    if (statusBarNotification.packageName == "android") {

                        val mKey = statusBarNotification.notification.extras.getString(getString(R.string.intent_key)) ?: return

                        val mContentSingular = getString(R.string.single_notification_content)
                        val mContentPlural = getString(R.string.multiple_notification_content)

                        if (mKey.contains(mContentSingular) || mKey.contains(mContentPlural)) {
                            this@NotificationListener.snoozeNotification(statusBarNotification.key, MANY_YEARS)
                        }
                    }
                }
            }
        }
    }
}
