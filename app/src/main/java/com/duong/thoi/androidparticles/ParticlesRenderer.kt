package com.duong.thoi.androidparticles

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20.*
import com.duong.thoi.androidparticles.objects.ParticleShooter
import com.duong.thoi.androidparticles.objects.ParticleSystem
import com.duong.thoi.androidparticles.progams.ParticleShaderProgram

import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix.*
import com.duong.thoi.androidparticles.objects.Skybox
import com.duong.thoi.androidparticles.progams.SkyboxShaderProgram
import com.duong.thoi.androidparticles.util.MatrixHelper
import com.duong.thoi.androidparticles.util.Point
import com.duong.thoi.androidparticles.util.TextureHelper
import com.duong.thoi.androidparticles.util.Vector
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.Matrix.multiplyMM
import android.opengl.Matrix.setIdentityM
import kotlin.math.sin


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
    private var particleTexture: Int = 0

    private var skyboxProgram: SkyboxShaderProgram? = null
    private var skybox: Skybox? = null
    private var skyboxTexture: Int? = null

    private var xRotation = 0f
    private var yRotation = 0f


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 1.0f)

        particleProgram = ParticleShaderProgram(context)
        particleSystem = ParticleSystem(10000)
        globalStartTime = System.nanoTime()

        skyboxProgram = SkyboxShaderProgram(context)
        skybox = Skybox()
        skyboxTexture = TextureHelper.loadCubeMap(context
                , intArrayOf(R.drawable.left
                , R.drawable.right
                , R.drawable.bottom
                , R.drawable.top
                , R.drawable.front
                , R.drawable.back))

        val particleDirection = Vector(0f, 1f, -1f)

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

        particleTexture = TextureHelper.loadTexture(context, R.drawable.particle_texture);
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        MatrixHelper.perspectiveM(projectionMatrix, 45f, width.toFloat()/height.toFloat(), 1f, 10f)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        drawSkybox()
        drawParticles()
    }
    private var isTouch = false

    fun handleTouchDrag(deltaX: Float, deltaY: Float) {
        xRotation += deltaX / 32f
        yRotation += deltaY / 32f

        yRotation = if (yRotation < -90) -90f else if (yRotation > 90) 90f else yRotation
        isTouch = true

    }

    private fun drawSkybox(){
        setIdentityM(viewMatrix, 0)
        if (!isTouch){
            xRotation+=0.1f
            yRotation+= sin(System.nanoTime().toDouble()).toFloat()*0.001f
        }
        isTouch = false
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f)
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f)
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        skyboxProgram!!.useProgram()
        skyboxProgram!!.setUniforms(viewProjectionMatrix, skyboxTexture!!)

        skybox!!.bindData(skyboxProgram!!)
        skybox!!.draw()
    }
    private fun drawParticles(){
        val currentTime = (System.nanoTime() - globalStartTime) / 1000000000f

        redParticleShooter!!.position = Point(-Math.sin(currentTime.toDouble()).toFloat(), 0.5f, -Math.cos(currentTime.toDouble()).toFloat())
        redParticleShooter!!.addParticles(particleSystem, currentTime, 20)

        greenParticleShooter!!.position = Point(-Math.cos(currentTime.toDouble()).toFloat(), -Math.sin(currentTime.toDouble()).toFloat(), -0.5f)
        greenParticleShooter!!.addParticles(particleSystem, currentTime, 10)

        blueParticleShooter!!.position = Point(Math.sin(currentTime.toDouble()).toFloat(), 0.5f, Math.cos(currentTime.toDouble()).toFloat())
        blueParticleShooter!!.addParticles(particleSystem, currentTime, 20)


        setIdentityM(viewMatrix, 0)
        translateM(viewMatrix, 0, 0f, -1.5f, -5f)
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE)

        particleProgram.useProgram()
        particleProgram.setUniforms(viewProjectionMatrix, currentTime, particleTexture)
        particleSystem.bindData(particleProgram)
        particleSystem.draw()

        glDisable(GL_BLEND)
    }
}