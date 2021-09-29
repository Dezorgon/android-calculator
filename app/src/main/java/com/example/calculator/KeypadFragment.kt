package com.example.calculator

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import com.example.calculator.databinding.FragmentKeypadBinding
import net.objecthunter.exp4j.ExpressionBuilder
import android.content.res.Configuration


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class KeypadFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var binding: FragmentKeypadBinding
    private val resultViewModel: ResultViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeypadBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDecimal.setOnClickListener {
            if (resultViewModel.formula.value!!.isBlank() ||
                (!resultViewModel.formula.value!!.last().isDigit()
                 && resultViewModel.formula.value!!.last() != '.')) {
                resultViewModel.formula.value += "0."
            }

            if (resultViewModel.formula.value!!.last().isDigit()) {
                var flg = true
                for (i in resultViewModel.formula.value!!.length-1 downTo 0) {
                    if(resultViewModel.formula.value!![i] == '.'){
                        flg = false
                        break
                    }
                    if (!resultViewModel.formula.value!![i].isDigit()) {
                        break
                    }
                }
                if (flg)
                    resultViewModel.formula.value += "."
            }
        }

        binding.btnClear.setOnLongClickListener {
            resultViewModel.formula.value = ""
            resultViewModel.result.value = ""
            val vibrator = getActivity()?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
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

        binding.btnEquals.setOnClickListener {
            resultViewModel.formula.value = resultViewModel.result.value
            resultViewModel.result.value = ""
        }



        binding.btn0.setOnClickListener(onDigit)
        binding.btn1.setOnClickListener(onDigit)
        binding.btn2.setOnClickListener(onDigit)
        binding.btn3.setOnClickListener(onDigit)
        binding.btn4.setOnClickListener(onDigit)
        binding.btn5.setOnClickListener(onDigit)
        binding.btn6.setOnClickListener(onDigit)
        binding.btn7.setOnClickListener(onDigit)
        binding.btn8.setOnClickListener(onDigit)
        binding.btn9.setOnClickListener(onDigit)

        binding.btnPlus.setOnClickListener(onOp)
        binding.btnMinus.setOnClickListener(onOp)
        binding.btnMultiply.setOnClickListener(onOp)
        binding.btnDivide.setOnClickListener(onOp)
        binding.btnPower.setOnClickListener(onOp)
        binding.btnPercent.setOnClickListener(onOp)
        binding.btnRoot.setOnClickListener(onOp)

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            binding.btnSin!!.setOnClickListener(onTrigonometric)
            binding.btnCos!!.setOnClickListener(onTrigonometric)
            binding.btnTan!!.setOnClickListener(onTrigonometric)
            binding.btnLn!!.setOnClickListener(onTrigonometric)
            binding.btnLog!!.setOnClickListener(onTrigonometric)

            binding.btnExp!!.setOnClickListener(onConstant)
            binding.btnPi!!.setOnClickListener(onConstant)

            binding.btnLeftBracket!!.setOnClickListener(onBracket)
            binding.btnRightBracket!!.setOnClickListener(onBracket)
        }

        binding.btnClear.setOnClickListener(onClear)
    }


    private val onDigit = View.OnClickListener {
        val btn = it as Button
        appendToInput(btn.text)
    }

    private val onOp = View.OnClickListener {
        val btn = it as Button

        if (btn.text.lastOrNull() == '√')
            resultViewModel.formula.value += btn.text

        if (resultViewModel.formula.value!!.lastOrNull() == ')' ||
            resultViewModel.formula.value!!.lastOrNull() == 'π' ||
            resultViewModel.formula.value!!.lastOrNull() == 'e'){
            resultViewModel.formula.value += btn.text
            return@OnClickListener
        }

        if (resultViewModel.formula.value!!.isBlank() ||
            resultViewModel.formula.value!!.last() == '(' ||
            resultViewModel.formula.value!!.last() == '√') {
            if (btn.text.last() == '-')
                resultViewModel.formula.value += btn.text
            return@OnClickListener
        }

        if (btn.text.last() == '%' &&
            resultViewModel.formula.value!!.last() == '%')
            resultViewModel.formula.value += btn.text

        if (btn.text.last() in listOf('+', '-', '×', '÷', '^'))
            if (resultViewModel.formula.value!!.lastOrNull() in listOf('+', '-', '×', '÷', '^')) {
                if (!(btn.text.last() == '-' && resultViewModel.formula.value!!.lastOrNull() != '-'))
                    resultViewModel.formula.value = resultViewModel.formula.value!!
                        .substring(0, resultViewModel.formula.value!!.length - 1);
                resultViewModel.formula.value += btn.text
                return@OnClickListener
            }


        if (resultViewModel.formula.value!!.last().isDigit())
            resultViewModel.formula.value += btn.text
    }

    private val onTrigonometric = View.OnClickListener {
        val btn = it as Button
        appendToInput(btn.text)
        appendToInput("(")
    }

    private val onConstant = View.OnClickListener {
        val btn = it as Button
        appendToInput(btn.text)
    }

    private val onBracket = View.OnClickListener {
        val btn = it as Button
        appendToInput(btn.text)
    }

    private val onClear = View.OnClickListener {
        if (resultViewModel.formula.value!!.length > 0)
            resultViewModel.formula.value = resultViewModel.formula.value!!
                .substring(0, resultViewModel.formula.value!!.length - 1);

        showResult()
    }

    private fun appendToInput(txt: CharSequence){
        resultViewModel.formula.value += txt
        showResult()
    }

    private fun showResult() {
        if (resultViewModel.formula.value!!.isBlank() ||
            resultViewModel.formula.value!!.last().isDigit() ||
            resultViewModel.formula.value!!.last() == ')' ||
            resultViewModel.formula.value!!.last() == 'π' ||
            resultViewModel.formula.value!!.last() == 'e')
            try {
                val input = StringBuilder(
                    resultViewModel.formula.value!!.toString()
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
                    resultViewModel.result.value = longResult.toString()
                else
                    resultViewModel.result.value = res.toString()

                //result = input.toString()
            } catch (e: Exception) {
                resultViewModel.result.value = ""
            }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            KeypadFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}