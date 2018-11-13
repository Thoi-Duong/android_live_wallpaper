package com.duong.thoi.androidparticles.progams

import android.content.Context
import android.opengl.GLES20.*
import com.duong.thoi.androidparticles.R


/**
 * Created by thoiduong on 11/12/18.
 */
class ParticleShaderProgram(context: Context): ShaderProgram(context, R.raw.particle_vertex_shader, R.raw.particle_fragment_shader) {
    // Uniform locations
    private val uMatrixLocation: Int = glGetUniformLocation(program, U_MATRIX)
    private val uTimeLocation: Int = glGetUniformLocation(program, U_TIME)

    // Attribute locations
    private val aPositionLocation: Int = glGetAttribLocation(program, A_POSITION)
    private val aColorLocation: Int = glGetAttribLocation(program, A_COLOR)
    private val aDirectionVectorLocation: Int = glGetAttribLocation(program, A_DIRECTION_VECTOR)
    private val aParticleStartTimeLocation: Int = glGetAttribLocation(program, A_PARTICLE_START_TIME)
    private val uTextureUnitLocation: Int = glGetUniformLocation(program, U_TEXTURE_UNIT)

    fun setUniforms(matrix: FloatArray, elapsedTime: Float, textureId: Int) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        glUniform1f(uTimeLocation, elapsedTime)
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        glUniform1i(uTextureUnitLocation, 0)
    }

    fun getPositionAttributeLocation(): Int = aPositionLocation

    fun getColorAttributeLocation(): Int = aColorLocation

    fun getDirectionVectorAttributeLocation(): Int = aDirectionVectorLocation

    fun getParticleStartTimeAttributeLocation(): Int = aParticleStartTimeLocation
}