package com.example.pengesturetest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory(
    private val reserved_1: Int, private val reserved_2: Int, private val reserved_3: Int,
    private val reserved_4: Int, private val reserved_5: Int, private val reserved_6: Int,
    private val reserved_7: Int, private val reserved_8: Int, private val reserved_9: Int,
    private val reserved_10: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(reserved_1, reserved_2, reserved_3, reserved_4,
        reserved_5, reserved_6, reserved_7, reserved_8, reserved_9, reserved_10) as T
    }
}