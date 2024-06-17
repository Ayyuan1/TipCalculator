package com.example.tipcalculator

import android.animation.ArgbEvaluator
import android.content.Intent
import android.os.Build
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
import androidx.annotation.RequiresApi
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
private const val SPLIT_MIN = 1
class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var seekbarSplit: SeekBar
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvSplitNumber: TextView
    private lateinit var tvSplitTipAmount: TextView
    private lateinit var tvSplitTotalAmount: TextView
    private lateinit var buttonRoundUp: Button
    private lateinit var buttonRoundDown: Button
    private lateinit var btSettings: ImageButton
    private lateinit var btPreset15: Button
    private lateinit var btPreset18: Button
    private lateinit var btPreset20: Button
    private lateinit var btPreset23: Button




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercentLabel = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)

        seekbarSplit = findViewById(R.id.seekBarSplit)
        tvSplitNumber = findViewById(R.id.tvSplitNumber)
        tvSplitTipAmount = findViewById(R.id.tvSplitTipAmount)
        tvSplitTotalAmount = findViewById(R.id.tvSplitTotalAmount)
        buttonRoundUp = findViewById(R.id.buttonRoundUp)
        buttonRoundDown = findViewById(R.id.buttonRoundDown)
        btSettings = findViewById(R.id.btSettings)
        btPreset15 = findViewById(R.id.btPreset15)
        btPreset18 = findViewById(R.id.btPreset18)
        btPreset20 = findViewById(R.id.btPreset20)
        btPreset23 = findViewById(R.id.btPreset23)

        btSettings.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        btPreset15.setOnClickListener{
            seekBarTip.progress = 15
            computeTipAndTotal()
        }

        btPreset18.setOnClickListener{
            seekBarTip.progress = 18
            computeTipAndTotal()
        }

        btPreset20.setOnClickListener{
            seekBarTip.progress = 20
            computeTipAndTotal()
        }

        btPreset23.setOnClickListener{
            seekBarTip.progress = 23
            computeTipAndTotal()
        }

        initializeTheme()

        seekbarSplit.min = SPLIT_MIN
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
        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                tvTipPercentLabel.text = "$progress%"
                computeTipAndTotal()
                computeSplitTipAndTotal()
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