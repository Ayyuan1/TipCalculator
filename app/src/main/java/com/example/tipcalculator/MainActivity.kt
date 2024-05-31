package com.example.tipcalculator

import android.animation.ArgbEvaluator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.ceil
import kotlin.math.floor


private  const val TAG = "MainActivity"
private  const val  INITIAL_TIP_PERCENT = 15
private const val INITIAL_SPLIT_NUM = 1
class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var seekbarSplit: SeekBar
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var tvSplitNumber: TextView
    private lateinit var tvSplitTipAmount: TextView
    private lateinit var tvSplitTotalAmount: TextView
    private lateinit var buttonRoundUp: Button
    private lateinit var buttonRoundDown: Button
    private lateinit var btSettings: ImageButton




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercentLabel = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        seekbarSplit = findViewById(R.id.seekBarSplit)
        tvSplitNumber = findViewById(R.id.tvSplitNumber)
        tvSplitTipAmount = findViewById(R.id.tvSplitTipAmount)
        tvSplitTotalAmount = findViewById(R.id.tvSplitTotalAmount)
        buttonRoundUp = findViewById(R.id.buttonRoundUp)
        buttonRoundDown = findViewById(R.id.buttonRoundDown)
        btSettings = findViewById(R.id.btSettings)

        btSettings.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        initializeTheme()

        seekbarSplit.progress = INITIAL_SPLIT_NUM
        tvSplitNumber.text = "1"
        seekbarSplit.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                tvSplitNumber.text = "$progress"
                computeSplitTipAndTotal()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)
        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                tvTipPercentLabel.text = "$progress%"
                computeTipAndTotal()
                computeSplitTipAndTotal()
                updateTipDescription(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        etBaseAmount.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG,"afterTextChanged $s")
                computeTipAndTotal()
                computeSplitTipAndTotal()
            }

        })

        buttonRoundUp.setOnClickListener{
            roundUp()
        }

        buttonRoundDown.setOnClickListener{
            roundDown()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }

    private fun roundDown() {
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }

        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        val splitNum = seekbarSplit.progress
        val tipAmount = baseAmount * tipPercent / 100
        val splitTotalAmount = (baseAmount + tipAmount) / splitNum
        val totalAmount = baseAmount + tipAmount
        val totalRounded = floor(totalAmount)
        val splitTotalRounded = floor(splitTotalAmount)
        tvTotalAmount.text = totalRounded.toString()
        tvSplitTotalAmount.text = splitTotalRounded.toString()
    }

    private fun roundUp() {
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }

        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        val splitNum = seekbarSplit.progress
        val tipAmount = baseAmount * tipPercent / 100
        val splitTotalAmount = (baseAmount + tipAmount) / splitNum
        val totalAmount = baseAmount + tipAmount
        val totalRounded = ceil(totalAmount)
        val splitTotalRounded = ceil(splitTotalAmount)
        tvTotalAmount.text = totalRounded.toString()
        tvSplitTotalAmount.text = splitTotalRounded.toString()
    }

    private fun computeSplitTipAndTotal() {
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }

        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        val splitNum = seekbarSplit.progress
        val tipAmount = tvTipAmount.text.toString().toDouble()
        val splitTipAmount = (baseAmount * tipPercent / 100) / splitNum
        val splitTotalAmount = (baseAmount + tipAmount) / splitNum
        tvSplitTipAmount.text = "%.2f".format(splitTipAmount)
        tvSplitTotalAmount.text = "%.2f".format(splitTotalAmount)
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }
        tvTipDescription.text = tipDescription

        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.color_worst_tip),
            ContextCompat.getColor(this, R.color.color_best_tip)
        ) as Int
        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal(){
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }
        // 1. Get the value of the base and tip percent
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        // 2. Compute the tip and total
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount
        // 3.Update the UI
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
    }

    private fun initializeTheme() {
        val sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        val themePref = sharedPreferences.getString("theme_preference", "follow_system")
        when (themePref) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "follow_system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}