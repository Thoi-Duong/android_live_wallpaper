package com.duong.thoi.androidparticles

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ConfigurationInfo
import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var mSurfaceView: GLSurfaceView
    private var mRenderSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSurfaceView = GLSurfaceView(this)

        val  activityManager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo: ConfigurationInfo = activityManager.deviceConfigurationInfo

        val supportsEs2: Boolean = configurationInfo.reqGlEsVersion >= 0x20000
        configurationInfo.toString()

        val particles = ParticlesRenderer(this)

        if (supportsEs2) {
            mSurfaceView.setEGLContextClientVersion(2)
            mSurfaceView.setRenderer(particles)
            mRenderSet = true
        } else {
            Toast.makeText(this, "This device dose not support OpenGL ES 2.0.", Toast.LENGTH_LONG).show()
            return
        }

        mSurfaceView.setOnTouchListener(object : View.OnTouchListener{
            var previousX = 0f
            var previousY = 0f

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event == null) return false

                if (event!!.action == MotionEvent.ACTION_DOWN) {
                    previousX = event!!.x
                    previousY = event!!.y
                } else if (event!!.action == MotionEvent.ACTION_MOVE) {
                    val deltaX = event!!.x - previousX
                    val deltaY = event!!.y - previousY

                    previousX = event!!.x
                    previousY = event!!.y

                    mSurfaceView.queueEvent { particles.handleTouchDrag(deltaX, deltaY) }
                }
                return true
            }
        })

        setContentView(mSurfaceView)
    }

    override fun onPause() {
        super.onPause()

        if (mRenderSet) mSurfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (mRenderSet) mSurfaceView.onResume()
    }
}
