package com.example.calculator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ResultViewModel: ViewModel() {
    private val _result: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val result: MutableLiveData<String>
        get() {
            if (_result.value == null)
                _result.value = ""
            return _result
        }
    private val _formula: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val formula: MutableLiveData<String>
        get() {
            if (_formula.value == null)
                _formula.value = ""
            return _formula
        }
}