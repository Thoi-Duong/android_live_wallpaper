package com.duong.thoi.androidparticles.objects

import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.GLES20.*
import com.duong.thoi.androidparticles.Constants.Constants.BYTES_PER_FLOAT
import com.duong.thoi.androidparticles.data.IndexBuffer
import com.duong.thoi.androidparticles.data.VertexBuffer
import com.duong.thoi.androidparticles.progams.HeightmapShaderProgram
import com.duong.thoi.androidparticles.util.Geometry
import com.duong.thoi.androidparticles.util.Point

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

    private fun calculateNumElements(): Int = (width - 1) * (height - 1) * 2 * 3

    private fun loadBitmapData(bitmap: Bitmap): FloatArray {
        val heightmapVertices = FloatArray(width * height * TOTAL_COMPONENT_COUNT)

        val pixels = IntArray(width * height)

        bitmap.getPixels(pixels, 0, width, 0,0, width, height)
        bitmap.recycle()

        var offset = 0

        for (row in 0 until height) {
            for (col in 0 until width) {
                val point = getPoint(pixels, row, col)

                heightmapVertices[offset++] = point.x
                heightmapVertices[offset++] = point.y
                heightmapVertices[offset++] = point.z

                val top = getPoint(pixels, row - 1, col)
                val left = getPoint(pixels, row, col - 1)
                val right = getPoint(pixels, row, col + 1)
                val bottom = getPoint(pixels, row + 1, col)

                val rightToLeft = Geometry.vectorBetween(right, left)
                val topToBottom = Geometry.vectorBetween(top, bottom)
                val normal = rightToLeft.crossProduct(topToBottom).normalize()

                heightmapVertices[offset++] = normal.x
                heightmapVertices[offset++] = normal.y
                heightmapVertices[offset++] = normal.z
            }
        }

        return heightmapVertices
    }

    private fun getPoint(pixels: IntArray, row: Int, col: Int): Point {
        val x = (col.toFloat() / (width - 1).toFloat()) - 0.5f
        val z = (row.toFloat() / (height - 1).toFloat()) - 0.5f

        val cRow = clamp(row, 0, width - 1)
        val cCol = clamp(col, 0, height - 1)

        val y = Color.red(pixels[(cRow * height) + cCol]).toFloat() / 255f

        return Point(x, y, z)
    }

    private fun clamp(value: Int, min: Int, max: Int): Int = Math.max(min, Math.min(max, value))

    private fun createIndexData(): ShortArray {
        val indexData = ShortArray(numElements)
        var offset = 0

        for (row in 0 until (height - 1)) {
            for (col in 0 until (width - 1)) {
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
                POSITION_COMPONENT_COUNT, STRIDE)
        vertexBuffer.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT * BYTES_PER_FLOAT,
                heightmapProgram.getNormalAttributeLocation(),
                NORMAL_COMPONENT_COUNT, STRIDE)
    }

    fun draw() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBufferId())
        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
        private const val NORMAL_COMPONENT_COUNT = 3
        private const val TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT
        private const val STRIDE = (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * BYTES_PER_FLOAT
    }
}