package com.duong.thoi.androidparticles

import android.content.Context
import android.graphics.BitmapFactory
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
import com.duong.thoi.androidparticles.objects.Heightmap
import com.duong.thoi.androidparticles.progams.HeightmapShaderProgram




/**
 * Created by thoiduong on 11/13/18.
 */
class ParticlesRenderer(private val context: Context): Renderer {
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewMatrixForSkybox = FloatArray(16)
    private val projectionMatrix = FloatArray(16)

    private val tempMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

    private var heightmapProgram: HeightmapShaderProgram? = null
    private var heightmap: Heightmap? = null

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
        glClearColor(0f, 0f, 0f, 0.0f)

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)

        heightmapProgram = HeightmapShaderProgram(context)
        heightmap = Heightmap(BitmapFactory.decodeResource(context.resources, R.drawable.heightmap))


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

        particleTexture = TextureHelper.loadTexture(context, R.drawable.particle_texture)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        MatrixHelper.perspectiveM(projectionMatrix, 45f, width.toFloat()/height.toFloat(), 1f, 10f)

        updateViewMatrices()
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        drawHeightmap()
        drawSkybox()
        drawParticles()
    }

    private fun drawHeightmap() {
        setIdentityM(modelMatrix, 0)
        // Expand the heightmap's dimensions, but don't expand the height as
        // much so that we don't get insanely tall mountains.
        scaleM(modelMatrix, 0, 100f, 10f, 100f)
        updateMvpMatrix()
        heightmapProgram!!.useProgram()
        heightmapProgram!!.setUniforms(modelViewProjectionMatrix)
        heightmap!!.bindData(heightmapProgram!!)
        heightmap!!.draw()
    }

    fun handleTouchDrag(deltaX: Float, deltaY: Float) {
        xRotation += deltaX / 32f
        yRotation += deltaY / 32f

        yRotation = if (yRotation < -90) -90f else if (yRotation > 90) 90f else yRotation

        updateViewMatrices()
    }

    private fun updateMvpMatrix() {
        multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0)
    }

    private fun updateMvpMatrixForSkybox() {
        multiplyMM(tempMatrix, 0, viewMatrixForSkybox, 0, modelMatrix, 0)
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0)
    }

    private fun updateViewMatrices() {
        setIdentityM(viewMatrix, 0)

        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f)
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f)
        System.arraycopy(viewMatrix, 0, viewMatrixForSkybox, 0, viewMatrix.size)
        // We want the translation to apply to the regular view matrix, and not // the skybox.
        translateM(viewMatrix, 0, 0f, -1.5f, -5f)
    }

    private fun drawSkybox(){
        setIdentityM(modelMatrix, 0)

        updateMvpMatrixForSkybox()

        glDepthFunc(GL_LEQUAL)

        skyboxProgram!!.useProgram()
        skyboxProgram!!.setUniforms(modelViewProjectionMatrix, skyboxTexture!!)

        skybox!!.bindData(skyboxProgram!!)
        skybox!!.draw()

        glDepthFunc(GL_LESS)
    }
    private fun drawParticles(){
        val currentTime = (System.nanoTime() - globalStartTime) / 1000000000f

        redParticleShooter!!.position = Point(-Math.sin(currentTime.toDouble()).toFloat(), 0.5f, -Math.cos(currentTime.toDouble()).toFloat())
        redParticleShooter!!.addParticles(particleSystem, currentTime, 20)

        greenParticleShooter!!.position = Point(-Math.cos(currentTime.toDouble()).toFloat(), -Math.sin(currentTime.toDouble()).toFloat(), -0.5f)
        greenParticleShooter!!.addParticles(particleSystem, currentTime, 10)

        blueParticleShooter!!.position = Point(Math.sin(currentTime.toDouble()).toFloat(), 0.5f, Math.cos(currentTime.toDouble()).toFloat())
        blueParticleShooter!!.addParticles(particleSystem, currentTime, 20)


        setIdentityM(modelMatrix, 0)
        updateMvpMatrix()

        glDepthMask(false)
        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE)

        particleProgram.useProgram()
        particleProgram.setUniforms(modelViewProjectionMatrix, currentTime, particleTexture)
        particleSystem.bindData(particleProgram)
        particleSystem.draw()

        glDisable(GL_BLEND)
        glDepthMask(true)
    }
}