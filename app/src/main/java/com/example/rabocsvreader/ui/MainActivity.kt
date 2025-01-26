package com.example.rabocsvreader.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rabocsvreader.databinding.ActivityMainBinding
import com.example.rabocsvreader.ui.vm.MainScreenEffect
import com.example.rabocsvreader.ui.vm.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModel()
    private val profileAdapter = ProfileAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getFileDownload("https://docs.google.com/spreadsheets/d/e/2PACX-1vSjy4ueh-wbIoUIlKu-Sf7ByRyny5tJKocbGdOj1_wQDwRf4vSqGBGdqsPw6Ase1KMEsRgQSJVYhGz3/pub?output=csv")
        setupUI()
        collectState()
        collectEffect()
    }

    private fun setupUI() = binding.rvProfiles.apply {
        layoutManager = LinearLayoutManager(this@MainActivity)
        adapter = profileAdapter
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiListState.collectLatest { uiState ->
                    profileAdapter.submitList(uiState.peopleList)
                    binding.tvNoOfErrors.text = uiState.errorCount.toString()
                }
            }
        }
    }

    private fun collectEffect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiEffect.collect { uiEffect ->
                    when (uiEffect) {
                        is MainScreenEffect.Loading -> {
                            binding.pbLoading.isVisible = uiEffect.loading
                        }

                        is MainScreenEffect.ShowError -> {
                            Snackbar.make(binding.root, uiEffect.message, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}