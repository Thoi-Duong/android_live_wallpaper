package com.duong.thoi.androidparticles

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ConfigurationInfo
import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
