package com.duong.thoi.androidparticles.progams

import android.content.Context
import android.opengl.GLES20.*
import com.duong.thoi.androidparticles.R


/**
 * Created by thoiduong on 11/14/18.
 */
class HeightmapShaderProgram(context: Context) : ShaderProgram(context, R.raw.heightmap_vertex_shader, R.raw.heightmap_fragment_shader) {
    private val uMatrixLocation = glGetUniformLocation(program, ShaderProgram.U_MATRIX)
    private val positionAttributeLocation = glGetAttribLocation(program, ShaderProgram.A_POSITION)

    fun setUniforms(matrix: FloatArray) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
    }

    fun getPositionAttributeLocation() = positionAttributeLocation
}