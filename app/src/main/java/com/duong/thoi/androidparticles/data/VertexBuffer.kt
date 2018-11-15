package com.duong.thoi.androidparticles.data

import android.opengl.GLES20.*
import com.duong.thoi.androidparticles.Constants.Constants.BYTES_PER_FLOAT
import java.nio.ByteBuffer
import java.nio.ByteOrder
/**
 * Created by thoiduong on 11/14/18.
 */
class VertexBuffer(vertexData: FloatArray) {
    private val bufferID: Int

    init {
        val buffers = IntArray(1)
        glGenBuffers(buffers.size, buffers, 0)

        if (buffers[0] == 0) throw RuntimeException("Could not create a new vertex buffer object.")

        bufferID = buffers[0]

        glBindBuffer(GL_ARRAY_BUFFER, buffers[0])

        val vertexArray = ByteBuffer.allocateDirect(vertexData.size * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData)

        vertexArray.position(0)

        glBufferData(GL_ARRAY_BUFFER, vertexArray.capacity() * BYTES_PER_FLOAT, vertexArray, GL_STATIC_DRAW)

        glBindBuffer(GL_ARRAY_BUFFER,0)
    }

    fun setVertexAttribPointer(dataOffset: Int, attributeLocation: Int, componentCount: Int, stride: Int) {
        glBindBuffer(GL_ARRAY_BUFFER, bufferID)

        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, dataOffset)
        glEnableVertexAttribArray(attributeLocation)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }
}