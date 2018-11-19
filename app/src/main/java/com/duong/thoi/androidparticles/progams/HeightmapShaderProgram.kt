package com.duong.thoi.androidparticles.progams

import android.content.Context
import android.opengl.GLES20.*
import com.duong.thoi.androidparticles.R
import com.duong.thoi.androidparticles.util.Vector


/**
 * Created by thoiduong on 11/14/18.
 */
class HeightmapShaderProgram(context: Context) : ShaderProgram(context, R.raw.heightmap_vertex_shader, R.raw.heightmap_fragment_shader) {

    private val positionAttributeLocation    = glGetAttribLocation(program, A_POSITION)
    private val uVectorToLightLocation       = glGetUniformLocation(program, U_VECTOR_TO_LIGHT)
    private val aNormalLocation              = glGetAttribLocation(program, A_NORMAL)
    private val uMVMatrixLocation            = glGetUniformLocation(program, U_MV_MATRIX)
    private val uIT_MVMatrixLocation         = glGetUniformLocation(program, U_IT_MV_MATRIX)
    private val uMVPMatrixLocation           = glGetUniformLocation(program, U_MVP_MATRIX)
    private val uPointLightPositionsLocation = glGetUniformLocation(program, U_POINT_LIGHT_POSITIONS)
    private val uPointLightColorsLocation    = glGetUniformLocation(program, U_POINT_LIGHT_COLORS)

    public fun setUniforms(mvMatrix: FloatArray
                           , it_mvMatrix: FloatArray
                           , mvpMatrix: FloatArray
                           , vectorToDirectionalLight: FloatArray
                           , pointLightPositions: FloatArray
                           , pointLightColors: FloatArray) {

        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0)
        glUniformMatrix4fv(uIT_MVMatrixLocation, 1, false, it_mvMatrix, 0)
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0)

        glUniform3fv(uVectorToLightLocation, 1, vectorToDirectionalLight, 0)
        glUniform4fv(uPointLightPositionsLocation, 3, pointLightPositions, 0)
        glUniform3fv(uPointLightColorsLocation, 3, pointLightColors, 0)
    }

    fun getPositionAttributeLocation(): Int = positionAttributeLocation
    fun getNormalAttributeLocation(): Int = aNormalLocation
}