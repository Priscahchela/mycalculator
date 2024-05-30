package com.example.mycalculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class MainViewModel : ViewModel() {

    private val _displayText = MutableLiveData("0")
    val displayText: LiveData<String> get() = _displayText

    private var currentNumber = ""
    private var operation: Operation? = null
    private var firstNumber = ""
    private val history = mutableListOf<String>()

    fun onButtonClick(button: String) {
        when (button) {
            "AC" -> clear()
            "⌫" -> backspace()
            "%" -> setOperation(Operation.MODULO)
            "÷" -> setOperation(Operation.DIVIDE)
            "×" -> setOperation(Operation.MULTIPLY)
            "−" -> setOperation(Operation.MINUS)
            "+" -> setOperation(Operation.PLUS)
            "=" -> calculate()
            "⟳" -> showHistory()
            "sin" -> applyTrigonometricFunction(::sin)
            "cos" -> applyTrigonometricFunction(::cos)
            "tan" -> applyTrigonometricFunction(::tan)
            "." -> appendDot()
            else -> numberClick(button)
        }
    }

    private fun numberClick(number: String) {
        currentNumber += number
        _displayText.value = currentNumber
    }

    private fun setOperation(op: Operation) {
        if (currentNumber.isNotEmpty()) {
            firstNumber = currentNumber
            currentNumber = ""
            operation = op
            _displayText.value = firstNumber + " " + op.symbol
        }
    }

    private fun calculate() {
        if (currentNumber.isNotEmpty() && firstNumber.isNotEmpty() && operation != null) {
            val result = when (operation) {
                Operation.PLUS -> firstNumber.toDouble() + currentNumber.toDouble()
                Operation.MINUS -> firstNumber.toDouble() - currentNumber.toDouble()
                Operation.MULTIPLY -> firstNumber.toDouble() * currentNumber.toDouble()
                Operation.DIVIDE -> {
                    if (currentNumber == "0") {
                        _displayText.value = "Error"
                        return
                    } else {
                        firstNumber.toDouble() / currentNumber.toDouble()
                    }
                }
                Operation.MODULO -> firstNumber.toDouble() % currentNumber.toDouble()
                else -> throw IllegalStateException("Invalid operation")
            }

            val fullComputation = "$firstNumber ${operation!!.symbol} $currentNumber = $result"
            history.add(fullComputation)
            _displayText.value = fullComputation
            firstNumber = result.toString()
            currentNumber = ""
            operation = null
        }
    }

    private fun clear() {
        currentNumber = ""
        firstNumber = ""
        operation = null
        _displayText.value = "0"
    }

    private fun backspace() {
        if (currentNumber.isNotEmpty()) {
            currentNumber = currentNumber.dropLast(1)
            _displayText.value = currentNumber
        }
    }

    private fun showHistory() {
        val historyText = history.joinToString("\n")
        _displayText.value = historyText
    }

    private fun applyTrigonometricFunction(func: (Double) -> Double) {
        if (currentNumber.isNotEmpty()) {
            val result = func(currentNumber.toDouble())
            _displayText.value = "$currentNumber = $result"
            history.add("$func($currentNumber) = $result")
            currentNumber = result.toString()
        }
    }

    private fun appendDot() {
        if (!currentNumber.contains('.')) {
            currentNumber += "."
            _displayText.value = currentNumber
        }
    }

    private enum class Operation(val symbol: String) {
        PLUS("+"),
        MINUS("−"),
        MULTIPLY("×"),
        DIVIDE("÷"),
        MODULO("%")
    }
}
