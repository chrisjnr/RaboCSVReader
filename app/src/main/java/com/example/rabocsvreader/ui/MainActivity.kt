package com.example.rabocsvreader.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rabocsvreader.databinding.ActivityMainBinding
import com.example.rabocsvreader.ui.util.isOnline
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
        if (isOnline()) {
            viewModel.getFileDownload("https://raw.githubusercontent.com/RabobankDev/AssignmentCSV/main/issues.csv")
        } else {
            Snackbar.make(binding.root,"No Internet connection", Snackbar.LENGTH_SHORT).show()

        }
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
                            binding.pbLoading.isVisible = false
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