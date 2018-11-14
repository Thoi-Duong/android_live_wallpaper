package com.duong.thoi.androidparticles.objects

import android.opengl.GLES20.*
import com.duong.thoi.androidparticles.data.VertexArray
import com.duong.thoi.androidparticles.progams.SkyboxShaderProgram
import java.nio.ByteBuffer


/**
 * Created by thoiduong on 11/14/18.
 */
class Skybox {
    private val vertexArray: VertexArray
    private val indexArray: ByteBuffer

    init {
        // Create a unit cube.
        vertexArray = VertexArray(floatArrayOf(-1f, 1f, 1f, // (0) Top-left near
                1f, 1f, 1f, // (1) Top-right near
                -1f, -1f, 1f, // (2) Bottom-left near
                1f, -1f, 1f, // (3) Bottom-right near
                -1f, 1f, -1f, // (4) Top-left far
                1f, 1f, -1f, // (5) Top-right far
                -1f, -1f, -1f, // (6) Bottom-left far
                1f, -1f, -1f      // (7) Bottom-right far
        ))

        // 6 indices per cube side
        indexArray = ByteBuffer.allocateDirect(6 * 6)
                .put(byteArrayOf(
                        // Front
                        1, 3, 0, 0, 3, 2,

                        // Back
                        4, 6, 5, 5, 6, 7,

                        // Left
                        0, 2, 4, 4, 2, 6,

                        // Right
                        5, 7, 1, 1, 7, 3,

                        // Top
                        5, 1, 4, 4, 1, 0,

                        // Bottom
                        6, 2, 7, 7, 2, 3))
        indexArray.position(0)
    }

    fun bindData(skyboxProgram: SkyboxShaderProgram) {
        vertexArray.setVertexAttribPointer(0, skyboxProgram.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, 0)
    }

    fun draw() {
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, indexArray)
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
    }
}