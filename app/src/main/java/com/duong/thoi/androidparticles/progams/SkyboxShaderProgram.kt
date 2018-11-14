package com.duong.thoi.androidparticles.progams

import android.content.Context
import android.opengl.GLES20.*
import com.duong.thoi.androidparticles.R

/**
 * Created by thoiduong on 11/14/18.
 */
class SkyboxShaderProgram(context: Context): ShaderProgram(context, R.raw.skybox_vertex_shader, R.raw.skybox_fragment_shader) {
    private val uMatrixLocation = glGetUniformLocation(program, U_MATRIX)
    private val uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT)
    private val aPositionLocation = glGetAttribLocation(program, A_POSITION)

    fun setUniforms(matrix: FloatArray, textureId: Int) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId)
        glUniform1i(uTextureUnitLocation, 0)
    }

    fun getPositionAttributeLocation(): Int = aPositionLocation
}