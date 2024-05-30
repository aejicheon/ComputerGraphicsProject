package kr.ac.hallym.prac03

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.ac.hallym.prac03.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        initSurfaceView()
        setContentView(binding.root)

        binding.eyeLeft.setOnClickListener {
            cameraRotate(0.174f)
            binding.surfaceView.requestRender()
        }
        binding.eyeRight.setOnClickListener {
            cameraRotate(-0.174f)
            binding.surfaceView.requestRender()
        }
        binding.eyeForward.setOnClickListener {
            cameraMove(1.0f)
            binding.surfaceView.requestRender()
        }
        binding.eyeBackward.setOnClickListener {
            cameraMove(-0.5f)
            binding.surfaceView.requestRender()
        }
    }

    fun initSurfaceView() {
        binding.surfaceView.setEGLContextClientVersion(3)

        val mainRenderer = MainGLRenderer(this)
        binding.surfaceView.setRenderer(mainRenderer)

        binding.surfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        binding.surfaceView.setOnTouchListener { v, event ->
            mainRenderer.onTouchEvent(event)
        }
    }
}