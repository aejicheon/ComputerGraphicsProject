package kr.ac.hallym.prac03

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class MyLitTexGround (val myContext: Context) {

    private val vertexCoords = floatArrayOf(
        -10.0f, -1.0f, -10.0f,
        -10.0f, -1.0f, 10.0f,
        10.0f, -1.0f, 10.0f,
        -10.0f, -1.0f, -10.0f,
        10.0f, -1.0f, 10.0f,
        10.0f, -1.0f, -10.0f
    )

    private val vertexUVs = floatArrayOf(
        0.0f, 0.0f,
        0.0f, 20.0f,
        20.0f, 20.0f,
        0.0f, 0.0f,
        20.0f, 20.0f,
        20.0f, 0.0f
    )

    private val vertexNormals = floatArrayOf(
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f
    )
    private var vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertexCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexCoords)
                position(0)
            }
        }
    private var uvBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertexUVs.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexUVs)
                position(0)
            }
        }
    private var normalBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertexNormals.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexNormals)
                position(0)
            }
        }
    private val lightDir = floatArrayOf(1.0f, 1.0f, 1.0f)
    private val lightAmbient = floatArrayOf(0.1f, 0.1f, 0.1f)
    private val lightDiffuse = floatArrayOf(1.0f, 1.0f, 1.0f)
    private val lightSpecular = floatArrayOf(1.0f, 1.0f, 1.0f)
    private val matAmbient = floatArrayOf(1.0f, 1.0f, 1.0f)
    private val matSpecular = floatArrayOf(1.0f, 1.0f, 1.0f)
    private val matShininess = 10.0f

    private var mProgram = -1

    private var mEyePosHandle = -1;
    private var mLightDirHandle = -1
    private var mLightAmbiHandle = -1
    private var mLightDiffHandle = -1
    private var mLightSpecHandle = -1
    private var mMatAmbiHandle = -1
    private var mMatSpecHandle = -1
    private var mMatShHandle = -1

    private var mvpMatrixHandle = -1
    private var mWorldMatHandle = -1

    private var textureID = IntArray(1)

    private val vertexCount: Int = vertexCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4

    init {
        val vertexShader:Int = loadShader(GLES30.GL_VERTEX_SHADER, "ground_light_tex_vert.glsl", myContext)
        val fragmentShader: Int = loadShader(GLES30.GL_FRAGMENT_SHADER, "ground_light_tex_frag.glsl", myContext)

        mProgram = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShader)

            GLES30.glAttachShader(it, fragmentShader)

            GLES30.glLinkProgram(it)
        }
        GLES30.glUseProgram(mProgram)

        GLES30.glEnableVertexAttribArray(0)

        GLES30.glVertexAttribPointer(
            0,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        GLES30.glEnableVertexAttribArray(1)

        GLES30.glVertexAttribPointer(
            1,
            2,
            GLES30.GL_FLOAT,
            false,
            0,
            uvBuffer
        )
        GLES30.glEnableVertexAttribArray(2)

        GLES30.glVertexAttribPointer(
            2,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            normalBuffer
        )

        mEyePosHandle = GLES30.glGetUniformLocation(mProgram, "eyePos").also {
            GLES30.glUniform3fv(it, 1, eyePos, 0)
        }

        mLightDirHandle = GLES30.glGetUniformLocation(mProgram, "lightDir").also {
            GLES30.glUniform3fv(it, 1, lightDir, 0)
        }
        mLightAmbiHandle = GLES30.glGetUniformLocation(mProgram, "lightAmbi").also {
            GLES30.glUniform3fv(it, 1, lightAmbient, 0)
        }
        mLightDiffHandle = GLES30.glGetUniformLocation(mProgram, "lightDiff").also {
            GLES30.glUniform3fv(it, 1, lightDiffuse, 0)
        }
        mLightSpecHandle = GLES30.glGetUniformLocation(mProgram, "lightSpec").also {
            GLES30.glUniform3fv(it, 1, lightSpecular, 0)
        }
        mMatAmbiHandle = GLES30.glGetUniformLocation(mProgram, "matAmbi").also {
            GLES30.glUniform3fv(it, 1, matAmbient, 0)
        }
        mMatSpecHandle = GLES30.glGetUniformLocation(mProgram, "matSpec").also {
            GLES30.glUniform3fv(it, 1, matSpecular, 0)
        }
        mMatShHandle = GLES30.glGetUniformLocation(mProgram, "matSh").also {
            GLES30.glUniform1f(it, matShininess)
        }

        mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
        mWorldMatHandle = GLES30.glGetUniformLocation(mProgram, "worldMat")

        GLES30.glGenTextures(1, textureID, 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, loadBitmap("concon.bmp", myContext),0)
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)
    }
    fun draw(mvpMatrix: FloatArray, worldMat: FloatArray) {
        GLES30.glUseProgram(mProgram)

        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES30.glUniformMatrix4fv(mWorldMatHandle, 1, false, worldMat, 0)

        GLES30.glUniform3fv(mLightDirHandle, 1, lightDir, 0)

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID[0])

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)
    }
}