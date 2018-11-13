package com.duong.thoi.androidparticles.objects

import android.graphics.Color
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.glDrawArrays
import com.duong.thoi.androidparticles.Constants.Constants.BYTES_PER_FLOAT
import com.duong.thoi.androidparticles.data.VertexArray
import com.duong.thoi.androidparticles.progams.ParticleShaderProgram
import com.duong.thoi.androidparticles.util.Point
import com.duong.thoi.androidparticles.util.Vector

/**
 * Created by thoiduong on 11/12/18.
 */
class ParticleSystem(private val maxParticleCount: Int) {
    private val particles: FloatArray = FloatArray(maxParticleCount * TOTAL_COMPONENT_COUNT)
    private val vertexArray: VertexArray = VertexArray(particles)
    private var currentParticleCount: Int = 0
    private var nextParticle: Int = 0

    fun addParticle(position: Point, color: Int, direction: Vector, particleStartTime: Float) {
        val pacticleOffset = nextParticle * TOTAL_COMPONENT_COUNT
        var currentOffset = pacticleOffset

        nextParticle++

        if (currentParticleCount < maxParticleCount) currentParticleCount++
        if (nextParticle >= maxParticleCount) nextParticle = 0

        particles[currentOffset++] = position.x
        particles[currentOffset++] = position.y
        particles[currentOffset++] = position.z

        particles[currentOffset++] = Color.red(color) / 255f
        particles[currentOffset++] = Color.green(color) / 255f
        particles[currentOffset++] = Color.blue(color) / 255f

        particles[currentOffset++] = direction.x
        particles[currentOffset++] = direction.y
        particles[currentOffset++] = direction.z

        particles[currentOffset++] = particleStartTime

        vertexArray.updateBuffer(particles, pacticleOffset, TOTAL_COMPONENT_COUNT)
    }

    fun bindData(particleProgram: ParticleShaderProgram) {
        var dataOffset = 0
        vertexArray.setVertexAttribPointer(dataOffset, particleProgram.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE)

        dataOffset += POSITION_COMPONENT_COUNT
        vertexArray.setVertexAttribPointer(dataOffset, particleProgram.getColorAttributeLocation(), COLOR_COMPONENT_COUNT, STRIDE)

        dataOffset += COLOR_COMPONENT_COUNT
        vertexArray.setVertexAttribPointer(dataOffset, particleProgram.getDirectionVectorAttributeLocation(), VECTOR_COMPONENT_COUNT, STRIDE)

        dataOffset += VECTOR_COMPONENT_COUNT
        vertexArray.setVertexAttribPointer(dataOffset, particleProgram.getParticleStartTimeAttributeLocation(), PARTICLE_START_TIME_COMPONENT_COUNT, STRIDE)
    }

    fun draw() {
        glDrawArrays(GL_POINTS, 0, currentParticleCount)
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
        private const val COLOR_COMPONENT_COUNT = 3
        private const val VECTOR_COMPONENT_COUNT = 3
        private const val PARTICLE_START_TIME_COMPONENT_COUNT = 1
        private const val TOTAL_COMPONENT_COUNT = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT + VECTOR_COMPONENT_COUNT + PARTICLE_START_TIME_COMPONENT_COUNT)
        private const val STRIDE = TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT
    }
}