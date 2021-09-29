package com.example.calculator

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import com.example.calculator.databinding.ActivityMainBinding
import net.objecthunter.exp4j.ExpressionBuilder


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
        resultViewModel.formula.observe(this, { binding.formula.text = it})

        /*val versionCode = BuildConfig.VERSION_CODE
        val versionName = BuildConfig.VERSION_NAME*/
        val manager = this.packageManager
        val info = manager.getPackageInfo(this.packageName, PackageManager.GET_ACTIVITIES)
        setTitle(info.versionName);
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence("formula", formula.text.toString())
        outState.putCharSequence("result", result.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        formula.text = savedInstanceState.getCharSequence("formula")
        result.text = savedInstanceState.getCharSequence("result")
    }

    private fun appendToInput(txt: CharSequence){
        formula.append(txt)
        showResult()
    }

    private fun showResult() {
        if (formula.text.isBlank() ||
            formula.text.last().isDigit() ||
            formula.text.last() == ')' ||
            formula.text.last() == 'π' ||
            formula.text.last() == 'e')
            try {
                val input = StringBuilder(
                    formula.text.toString()
                        .replace('÷', '/')
                        .replace('×', '*')
                        .replace("√", "sqrt")
                        .replace("ln", "log10"))

                if ("sqrt" in input){
                    var ind = input.indexOf("sqrt") + 4

                    if (input[ind]!='(') {
                        input.insert(ind, '(')
                        for (i in ind + 1 until input.length) {
                            if (!input[i].isDigit()) {
                                input.insert(i, ')')
                                break
                            }
                            if (i == input.length-1) {
                                input.insert(i+1, ')')
                                break
                            }
                        }

                    }
                }

                val res = ExpressionBuilder(input.toString())
                    .build()
                    .evaluate()
                val longResult = res.toLong()

                if (res == longResult.toDouble())
                    result.text = longResult.toString()
                else
                    result.text = res.toString()

                //result.text = input.toString()
            } catch (e: Exception) {
                result.text = ""
            }
        resultViewModel.result.value = result.text as String
    }

    fun onDigit(view: View) {
        val btn = view as Button
        appendToInput(btn.text)
    }

    fun onOp(view: View) {
        val btn = view as Button

        if (btn.text.lastOrNull() == '√')
            formula.append(btn.text)

        if (formula.text.lastOrNull() == ')' ||
            formula.text.lastOrNull() == 'π' ||
            formula.text.lastOrNull() == 'e'){
            formula.append(btn.text)
            return
        }

        if (formula.text.isBlank() ||
            formula.text.last() == '(' ||
            formula.text.last() == '√') {
            if (btn.text.last() == '-')
                formula.append(btn.text)
            return
        }

        if (btn.text.last() == '%' &&
            formula.text.last() == '%')
            formula.append(btn.text)

        if (btn.text.last() in listOf('+', '-', '×', '÷', '^'))
            if (formula.text.lastOrNull() in listOf('+', '-', '×', '÷', '^')) {
                if (!(btn.text.last() == '-' && formula.text.lastOrNull() != '-'))
                    formula.text = formula.text.subSequence(0, formula.text.length - 1)
                formula.append(btn.text)
                return
            }


        if (formula.text.last().isDigit())
            formula.append(btn.text)
    }

    fun onTrigonometric(view: View){
        val btn = view as Button
        appendToInput(btn.text)
        appendToInput("(")
    }

    fun onConstant(view: View){
        val btn = view as Button
        appendToInput(btn.text)
    }

    fun onBracket(view: View){
        val btn = view as Button
        appendToInput(btn.text)
    }

    fun onClear(view: View) {
        if (formula.text.length > 0)
            formula.text = formula.text.subSequence(0, formula.text.length-1)

        showResult()
    }
}