package com.duong.thoi.androidparticles.data

import android.opengl.GLES20.*
import com.duong.thoi.androidparticles.Constants.Constants.BYTES_PER_SHORT
import java.nio.ByteBuffer
import java.nio.ByteOrder


/**
 * Created by thoiduong on 11/14/18.
 */
class IndexBuffer(indexData: ShortArray) {
    private val bufferId: Int

    init {
        // Allocate a buffer.
        val buffers = IntArray(1)
        glGenBuffers(buffers.size, buffers, 0)

        if (buffers[0] == 0) {
            throw RuntimeException("Could not create a new index buffer object.")
        }

        bufferId = buffers[0]

        // Bind to the buffer.
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[0])

        // Transfer data to native memory.
        val indexArray = ByteBuffer
                .allocateDirect(indexData.size * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(indexData)

        indexArray.position(0)

        // Transfer data from native memory to the GPU buffer.
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexArray.capacity() * BYTES_PER_SHORT,
                indexArray, GL_STATIC_DRAW)

        // IMPORTANT: Unbind from the buffer when we're done with it.
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)

        // We let the native buffer go out of scope, but it won't be released
        // until the next time the garbage collector is run.
    }

    fun getBufferId(): Int = bufferId
}