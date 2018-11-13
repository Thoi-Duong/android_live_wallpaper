package com.duong.thoi.androidparticles

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20.*
import com.duong.thoi.androidparticles.objects.ParticleShooter
import com.duong.thoi.androidparticles.objects.ParticleSystem
import com.duong.thoi.androidparticles.progams.ParticleShaderProgram

import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix.*
import com.duong.thoi.androidparticles.util.MatrixHelper
import com.duong.thoi.androidparticles.util.Point
import com.duong.thoi.androidparticles.util.TextureHelper
import com.duong.thoi.androidparticles.util.Vector
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by thoiduong on 11/13/18.
 */
class ParticlesRenderer(private val context: Context): Renderer {
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)

    private var particleProgram = ParticleShaderProgram(context)
    private var particleSystem = ParticleSystem(10000)

    private var redParticleShooter: ParticleShooter? = null
    private var greenParticleShooter: ParticleShooter? = null
    private var blueParticleShooter: ParticleShooter? = null

    private var globalStartTime: Long = 0
    private var texture: Int = 0


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 1.0f)
        // Enable additive blending
        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE)

        particleProgram = ParticleShaderProgram(context)
        particleSystem = ParticleSystem(10000)
        globalStartTime = System.nanoTime()

        val particleDirection = Vector(0f, 2f, -1f)

        val angleVarianceInDegrees = 10f
        val speedVariance = 1f

        redParticleShooter = ParticleShooter(
                Point(-1f, 0f, 0f),
                particleDirection,
                Color.rgb(255, 50, 5),
                angleVarianceInDegrees,
                speedVariance)

        greenParticleShooter = ParticleShooter(
                Point(0f, 0f, 0f),
                particleDirection,
                Color.rgb(25, 255, 25),
                20f,
                speedVariance)
        blueParticleShooter = ParticleShooter(
                Point(1f, 0f, 0f),
                particleDirection,
                Color.rgb(5, 50, 255),
                angleVarianceInDegrees,
                speedVariance)

        texture = TextureHelper.loadTexture(context, R.drawable.particle_texture);
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        MatrixHelper.perspectiveM(projectionMatrix, 45f, width.toFloat()/height.toFloat(), 1f, 10f)

        setIdentityM(viewMatrix, 0)
        translateM(viewMatrix, 0, 0f, -1.4f, -5f)
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        val currentTime = (System.nanoTime() - globalStartTime) /  1000000000f


        greenParticleShooter!!.addParticles(particleSystem, currentTime, 20)

        blueParticleShooter!!.position = Point(Math.sin(currentTime.toDouble()).toFloat(), 0f, Math.cos(currentTime.toDouble()).toFloat())
        blueParticleShooter!!.addParticles(particleSystem, currentTime, 20)

        redParticleShooter!!.position = Point(-Math.sin(currentTime.toDouble()).toFloat(), 0f, -Math.cos(currentTime.toDouble()).toFloat())
        redParticleShooter!!.addParticles(particleSystem, currentTime, 20)

        particleProgram.useProgram()

        particleProgram.setUniforms(viewProjectionMatrix, currentTime, texture)
        particleSystem.bindData(particleProgram)
        particleSystem.draw()
    }
}