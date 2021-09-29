package com.example.calculator

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import com.example.calculator.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val resultViewModel: ResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainerView, KeypadFragment())
            .commit()

        resultViewModel.result.observe(this, { binding.result.text = it})
        resultViewModel.formula.observe(this, {
            val formula = binding.formula as EditText
            formula.setText(it)
            formula.setSelection(formula.text.length)
        })

        /*val versionCode = BuildConfig.VERSION_CODE
        val versionName = BuildConfig.VERSION_NAME*/
        val manager = this.packageManager
        val info = manager.getPackageInfo(this.packageName, PackageManager.GET_ACTIVITIES)
        setTitle(info.versionName.subSequence(0,4));
    }
}