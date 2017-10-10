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

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.yarolegovich.lovelydialog.LovelyStandardDialog

class MainActivity : AppCompatActivity(), View.OnTouchListener {
    private var mContext: Context? = null
    private var mLogo: ImageView? = null
    private var mAwesome: AwesomeView? = null
    private var mPreferences: SharedPreferences? = null
    private var isNotificationAccessGranted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mContext = applicationContext
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        mLogo = findViewById(R.id.vLogo) as ImageView
        mLogo!!.setOnLongClickListener {
            LovelyStandardDialog(this)
                    .setTopColorRes(R.color.colorPrimary)
                    .setButtonsColorRes(R.color.colorAccent)
                    .setIcon(R.drawable.signature)
                    .setTitle(R.string.about)
                    .setMessage(R.string.info)
                    .setPositiveButton(R.string.okay, null)
                    .setNeutralButton(R.string.github) {
                        val mIntent = Intent(Intent.ACTION_VIEW)
                        mIntent.data = Uri.parse(getString(R.string.url))
                        startActivity(mIntent)
                    }
                    .show()
            false
        }
        mAwesome = findViewById(R.id.vAwesome) as AwesomeView
        mAwesome!!.setOnTouchListener(this)
    }

    public override fun onResume() {
        super.onResume()

        val mInfo = if (isNotificationAccessGranted) mContext!!.getString(R.string.persistent_notification_disabled) else mContext!!.getString(R.string.persistent_notification_enabled)

        if (!isNotificationAccessGranted) {
            val mEditor = mPreferences!!.edit()
            mEditor.putBoolean(mContext!!.getString(R.string.access_granted), false)
            mEditor.apply()

            Utils.makeText(this, mInfo, LENGTH_SECONDS).show()
        } else {
            val mEditor = mPreferences!!.edit()
            mEditor.putBoolean(mContext!!.getString(R.string.access_granted), true)
            mEditor.apply()

            Utils.makeText(this, mInfo, LENGTH_SECONDS).show()

            val mIntent = Intent(getString(R.string.intent_filter))
            mIntent.putExtra("command", "hide")
            sendBroadcast(mIntent)
        }
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_UP -> {

                val mDelay = if (isNotificationAccessGranted) 50 else 900

                val mHandler = Handler()
                mHandler.postDelayed({ startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }, mDelay.toLong())
            }
        }

        return false
    }

    public override fun onStart() {
        super.onStart()

        isNotificationAccessGranted = Utils.hasAccessGranted(mContext!!)
    }

    companion object {

        private val LENGTH_SECONDS = 3000
    }
}
