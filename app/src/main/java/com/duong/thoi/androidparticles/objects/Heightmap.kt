package com.duong.thoi.androidparticles.objects

import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.GLES20.*
import com.duong.thoi.androidparticles.data.IndexBuffer
import com.duong.thoi.androidparticles.data.VertexBuffer
import com.duong.thoi.androidparticles.progams.HeightmapShaderProgram

/**
 * Created by thoiduong on 11/14/18.
 */
class Heightmap(bitmap: Bitmap) {
    private val width = bitmap.width
    private val height =  bitmap.height
    private val numElements: Int
    private val vertexBuffer: VertexBuffer
    private val indexBuffer: IndexBuffer

    init {
        if (width * height > 65536) throw RuntimeException("Heightmap is too large for the index buffer.")

        if (width * height <= 0) throw RuntimeException("Heightmap is empty")

        numElements = calculateNumElements()
        vertexBuffer = VertexBuffer(loadBitmapData(bitmap))
        indexBuffer = IndexBuffer(createIndexData())
    }
    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
    }

    private fun calculateNumElements(): Int = (width - 1) * (height - 1) * 2 * 3

    private fun loadBitmapData(bitmap: Bitmap): FloatArray {
        val pixels = IntArray(width * height)

        bitmap.getPixels(pixels, 0, width, 0,0, width, height)
        bitmap.recycle()
        val heightmapVertices = FloatArray(width * height * POSITION_COMPONENT_COUNT)
        var offset = 0

        for (row in 0 until height - 1) {
            for (col in 0 until width - 1) {
                val xPosition = (col.toFloat() / (width - 1).toFloat()) - 0.5f
                val yPosition = (Color.red(pixels[row * height + col]).toFloat()) / 255f
                val zPosition = (row.toFloat() / (height - 1).toFloat()) - 0.5f

                heightmapVertices[offset++] = xPosition
                heightmapVertices[offset++] = yPosition
                heightmapVertices[offset++] = zPosition
            }
        }

        return heightmapVertices
    }

    private fun createIndexData(): ShortArray {
        val indexData = ShortArray(numElements)
        var offset = 0

        for (row in 0 until height - 1) {
            for (col in 0 until width - 1) {
                val topLeftIndexNum = (row * width + col).toShort()
                val topRightIndexNum = (row * width + col + 1).toShort()
                val bottomLeftIndexNum = ((row + 1) * width + col).toShort()
                val bottomRightIndexNum = ((row + 1) * width + col + 1).toShort()

                indexData[offset++] = topLeftIndexNum
                indexData[offset++] = bottomLeftIndexNum
                indexData[offset++] = topRightIndexNum

                indexData[offset++] = topRightIndexNum
                indexData[offset++] = bottomLeftIndexNum
                indexData[offset++] = bottomRightIndexNum
            }
        }

        return indexData
    }

    fun bindData(heightmapProgram: HeightmapShaderProgram) {
        vertexBuffer.setVertexAttribPointer(0,
                heightmapProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0)
    }

    fun draw() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBufferId())
        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }
}