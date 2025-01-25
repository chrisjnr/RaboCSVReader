package com.example.rabocsvreader.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rabocsvreader.databinding.ActivityMainBinding
import com.example.rabocsvreader.ui.vm.MainScreenState
import com.example.rabocsvreader.ui.vm.MainViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

private const val TAG = "RABO_TEST"

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by inject()
    private val profileAdapter = ProfileAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getFileDownload("https://docs.google.com/spreadsheets/d/e/2PACX-1vSjy4ueh-wbIoUIlKu-Sf7ByRyny5tJKocbGdOj1_wQDwRf4vSqGBGdqsPw6Ase1KMEsRgQSJVYhGz3/pub?output=csv")
        setupUI()
        collectState()
    }

    private fun setupUI() = binding.rvProfiles.apply {
        layoutManager = LinearLayoutManager(this@MainActivity)
        adapter = profileAdapter
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiStateFlow.collect { uiState ->
                    when (uiState) {
                        is MainScreenState.Loading -> {
                            binding.pbLoading.isVisible = uiState.loading
                        }

                        is MainScreenState.ShowError -> {

                        }

                        is MainScreenState.PeopleListUpdated -> {
                            profileAdapter.updateList(uiState.people)
                        }

                        is MainScreenState.ParsingError -> {
                            binding.tvNoOfErrors.text = uiState.errorCount.toString()
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