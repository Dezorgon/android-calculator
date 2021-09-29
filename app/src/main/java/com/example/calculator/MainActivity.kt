package com.example.calculator

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.TextView
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn_decimal.setOnClickListener {
            if (formula.text.isBlank() ||
                (!formula.text.last().isDigit() && formula.text.last() != '.')) {
                formula.append("0.")
            }

            if (formula.text.last().isDigit()) {
                var flg = true
                for (i in formula.text.length-1 downTo 0) {
                    if(formula.text[i] == '.'){
                        flg = false
                        break
                    }
                    if (!formula.text[i].isDigit()) {
                        break
                    }
                }
                if (flg)
                    formula.append(".")
            }
        }

        btn_clear.setOnLongClickListener {
            formula.text = ""
            result.text = ""
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(100)
                }
            }
            return@setOnLongClickListener true
        }

        btn_equals.setOnClickListener {
            formula.text = result.text
            result.text = ""
        }

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

    fun appendToInput(txt: CharSequence){
        formula.append(txt)
        showResult()
    }

    private fun showResult() {
        if (formula.text.isBlank() || formula.text.last().isDigit())
            try {
                val input = StringBuilder(
                    formula.text.toString()
                    .replace('÷', '/')
                    .replace('×', '*')
                    .replace("√", "sqrt"))

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
    }

    fun onDigit(view: View) {
        val btn = view as Button
        appendToInput(btn.text)
    }

    fun onOp(view: View) {
        val btn = view as Button

        if (btn.text.last() == '√')
            formula.append(btn.text)

        if (formula.text.isBlank()) {
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

    fun onClear(view: View) {
        if (formula.text.length > 0)
            formula.text = formula.text.subSequence(0, formula.text.length-1)

        showResult()
    }


}