package com.example.rabocsvreader.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.rabocsvreader.R
import com.example.rabocsvreader.ui.vm.MainScreenEffect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

private const val TAG = "RABO_TEST"

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.getFileDownload()
        collectState()
        collectEffect()

    }


    private fun collectEffect() {
        lifecycleScope.launch {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is MainScreenEffect.Error -> {

                    }

                    is MainScreenEffect.EmptyList -> {

                    }
                }
            }
        }
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                }
            }
        }
    }

}