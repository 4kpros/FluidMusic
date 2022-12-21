package com.prosabdev.fluidmusic.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.lukelorusso.verticalseekbar.VerticalSeekBar
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ActivityEqualizerBinding
import com.prosabdev.fluidmusic.databinding.DialogSavePresetBinding
import com.prosabdev.fluidmusic.sharedprefs.SharedPreferenceManagerUtils
import com.prosabdev.fluidmusic.utils.AnimatorsUtils
import com.prosabdev.fluidmusic.utils.FormattersAndParsersUtils
import com.prosabdev.fluidmusic.utils.InsetModifiersUtils
import com.prosabdev.fluidmusic.viewmodels.activities.EqualizerActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.models.equalizer.EqualizerPresetBandLevelItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.equalizer.EqualizerPresetItemViewModel
import kotlinx.coroutines.*

@BuildCompat.PrereleaseSdkCheck class EqualizerActivity : AppCompatActivity() {
    private lateinit var mDataBidingView : ActivityEqualizerBinding

    private val mEqualizerPresetItemViewModel: EqualizerPresetItemViewModel by viewModels()
    private val mEqualizerPresetBandLevelItemViewModel: EqualizerPresetBandLevelItemViewModel by viewModels()

    private val mEqualizerActivityViewModel: EqualizerActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        mDataBidingView = DataBindingUtil.setContentView(this, R.layout.activity_equalizer)

        if(savedInstanceState == null) {
            initViews()
            loadSharedPrefsAnInitEqualizer()
            checkInteractions()
            observeLiveData()
            registerOnBackPressedCallback()
        }
    }

    override fun onDestroy() {
        mEqualizerActivityViewModel.stopLocalMediaPlayer()
        super.onDestroy()
    }

    private fun observeLiveData() {
        MainScope().launch {
            mEqualizerActivityViewModel.listenCustomPresets(this@EqualizerActivity, mEqualizerPresetItemViewModel)
        }
        mEqualizerActivityViewModel.getEqualizerState().observe(this, Observer {
            onEqualizerStateChanged(it)
        })
        mEqualizerActivityViewModel.getToneState().observe(this, Observer {
            onTonalStateChange(it)
        })
        mEqualizerActivityViewModel.getCurrentPreset().observe(this, Observer {
            onCurrentPresetChange(it)
        })
        mEqualizerActivityViewModel.getPresetBandsLevels().observe(this, Observer {
            onPresetBandsLevelsChange()
        })
    }

    private fun onPresetBandsLevelsChange() {
        updateEqualizerBands(mEqualizerActivityViewModel.getCurrentPreset().value)
    }

    private fun onTonalStateChange(it: Boolean?) {
        val animate = if(it == null) false else true
        updateEnableToneUI(
            it ?: false,
            animate
        )
    }

    private fun onCurrentPresetChange(it: CharSequence?) {
        updateEqualizerBands(it)
    }

    private fun onEqualizerStateChanged(it: Boolean?) {
        val animate = if(it == null) false else true
        updateEnableEquUI(
            it ?: false,
            animate
        )
    }

    private fun registerOnBackPressedCallback() {
        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                finish()
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            })
        }
    }

    fun checkInteractions() {
        mDataBidingView.switchEqualizer.setOnCheckedChangeListener { _, isChecked ->
            mEqualizerActivityViewModel.setEqualizerState(isChecked)
            if(mEqualizerActivityViewModel.getCurrentPreset().value == null){
                val defaultActivatedPreset = mEqualizerActivityViewModel.getPresetName(0)
                mEqualizerActivityViewModel.setCurrentPreset(
                    defaultActivatedPreset,
                    mEqualizerPresetBandLevelItemViewModel
                )
            }
        }
        mDataBidingView.switchTone.setOnCheckedChangeListener { _, isChecked ->
            mEqualizerActivityViewModel.setToneState(isChecked)
        }
        mDataBidingView.buttonChoosePreset.setOnClickListener{
            openPresetSelectorDialog()
        }
        mDataBidingView.buttonSavePreset.setOnClickListener{
            openSavePresetDialog()
        }
        mDataBidingView.volumeSlider.addOnChangeListener { _, value, fromUser ->
            if(fromUser){
                updateVolume(value)
            }
        }
    }
    private fun updateVolume(value: Float) {
        updateEqualizerVolume(value)
        updateVolumeProgressUI(value)
    }
    private fun updateEqualizerVolume(value: Float) {
        //
    }
    private fun updateVolumeProgressUI(value: Float) {
        mDataBidingView.textViewVolumeProgress.text = "${value.toInt()}%"
    }

    private fun openSavePresetDialog() {
        val dialogDataBidingView : DialogSavePresetBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_save_preset, null, false)
        MaterialAlertDialogBuilder(this)
            .setTitle(baseContext.resources.getString(R.string.save_preset))
            .setIcon(R.drawable.save)
            .setView(dialogDataBidingView.root)
            .setNegativeButton(baseContext.resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(baseContext.resources.getString(R.string.save)) { dialog, which ->
            }
            .show().apply {
            }
    }

    private fun openPresetSelectorDialog() {
        val tempCurrentPresetName = mEqualizerActivityViewModel.getCurrentPreset().value
        MaterialAlertDialogBuilder(this)
            .setTitle(baseContext.resources.getString(R.string.choose_default_preset))
            .setIcon(R.drawable.tune)
            .setSingleChoiceItems(
                mEqualizerActivityViewModel.getPresets(),
                mEqualizerActivityViewModel.getPresetIdFromName(tempCurrentPresetName).toInt()
            ) { _, which ->
                MainScope().launch {
                    mEqualizerActivityViewModel.setCurrentPreset(
                        mEqualizerActivityViewModel.getPresetName(which.toShort()),
                        mEqualizerPresetBandLevelItemViewModel
                    )
                }
            }
            .show().apply {
            }
    }

    private fun updateEqualizerBands(currentPreset: CharSequence?) {
        val bandsCount: Short = mEqualizerActivityViewModel.getBandsCount()
        for(i in 0 until bandsCount){
            val textTop =
                mDataBidingView.linearVerticalSeekbars.findViewById<MaterialTextView>(R.id.text_view_top + INDEX_TEXT_VIEW_TOP_ADD + i)
            val verticalSeekBar =
                mDataBidingView.linearVerticalSeekbars.findViewById<VerticalSeekBar>(R.id.vertical_seekbar + INDEX_VERTICAL_SEEKBAR_ADD +i)
            var bandLevel: Short
            runBlocking {
                bandLevel = mEqualizerActivityViewModel.getBandLevel(currentPreset, i.toShort(), mEqualizerPresetBandLevelItemViewModel)
            }
            if(textTop.text == FormattersAndParsersUtils.formatBandLevelToString(bandLevel)) continue //If view have been already updated, pass
            textTop.text = FormattersAndParsersUtils.formatBandLevelToString(bandLevel)
            verticalSeekBar.progress = FormattersAndParsersUtils.formatBandToPercent(
                bandLevel,
                mEqualizerActivityViewModel.getBandLevelRangeMin(),
                mEqualizerActivityViewModel.getBandLevelRangeMax()
            )
        }
    }

    private fun updateEnableEquUI(isChecked: Boolean, animate: Boolean) {
        if(isChecked){
            AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.buttonChoosePreset,
                animate,
                200,
                1.0f,
                false
            )
            AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.buttonSavePreset,
                animate,
                200,
                1.0f,
                false
            )
            AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.volumeSlider,
                animate
            )
            AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.imageViewVolume,
                animate
            )
            AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.textViewVolumeProgress,
                animate
            )
        }else{
            AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.buttonChoosePreset,
                animate,
                200,
                0.35f,
                false
            )
            AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.buttonSavePreset,
                animate,
                200,
                0.35f,
                false
            )
            AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.volumeSlider,
                animate
            )
            AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.imageViewVolume,
                animate
            )
            AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.textViewVolumeProgress,
                animate
            )
        }
        val bandsCount: Short = mEqualizerActivityViewModel.getBandsCount()
        for(i in 0 until bandsCount) {
            val textTop =
                mDataBidingView.linearVerticalSeekbars.findViewById<MaterialTextView>(R.id.text_view_top + INDEX_TEXT_VIEW_TOP_ADD + i)
            val textBottom =
                mDataBidingView.linearVerticalSeekbars.findViewById<MaterialTextView>(R.id.text_view_bottom + INDEX_TEXT_VIEW_BOTTOM_ADD + i)
            val verticalSeekBar =
                mDataBidingView.linearVerticalSeekbars.findViewById<VerticalSeekBar>(R.id.vertical_seekbar + INDEX_VERTICAL_SEEKBAR_ADD +i)
            verticalSeekBar.useThumbToSetProgress = isChecked
            verticalSeekBar.clickToSetProgress = isChecked
            if(isChecked){
                AnimatorsUtils.crossFadeUpClickable(
                    textTop,
                    animate
                )
                AnimatorsUtils.crossFadeUpClickable(
                    textBottom,
                    animate
                )
                AnimatorsUtils.crossFadeUpClickable(
                    verticalSeekBar,
                    animate
                )
            }else{
                AnimatorsUtils.crossFadeDownClickable(
                    textTop,
                    animate
                )
                AnimatorsUtils.crossFadeDownClickable(
                    textBottom,
                    animate
                )
                AnimatorsUtils.crossFadeDownClickable(
                    verticalSeekBar,
                    animate
                )
            }
        }
    }
    private fun updateEnableToneUI(isChecked: Boolean, animate: Boolean) {
        if(isChecked){
            AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.seekbarBassBoost,
                animate
            )
            AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.seekbarVisualizer,
                animate
            )
            //update text views
            AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.textViewVisualizer,
                animate
            )
            AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.textViewVisualizerProgress,
                animate
            )
            AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.textViewBassBoost,
                animate
            )
            AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.textViewBassBoostProgress,
                animate
            )
            //update shape background
            AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.shapeBackgroundBass,
                animate
            )
            AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.shapeBackgroundVisualizer,
                animate
            )
        }else{
            AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.seekbarBassBoost,
                animate
            )
            AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.seekbarVisualizer,
                animate
            )
            //update text views
            AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.textViewVisualizer,
                animate
            )
            AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.textViewVisualizerProgress,
                animate
            )
            AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.textViewBassBoost,
                animate
            )
            AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.textViewBassBoostProgress,
                animate
            )
            //update shape background
            AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.shapeBackgroundBass,
                animate
            )
            AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.shapeBackgroundVisualizer,
                animate
            )
        }
    }

    private fun loadSharedPrefsAnInitEqualizer() {
        val equEnable = SharedPreferenceManagerUtils.AudioEffects.loadEqualizerState(baseContext)
        val toneEnable = SharedPreferenceManagerUtils.AudioEffects.loadToneState(baseContext)
        mEqualizerActivityViewModel.initSilentEqualizer(baseContext)
        initEqualizerUI(equEnable)
        initToneUI(toneEnable)
    }
    private fun initToneUI(toneEnable: Boolean) {
        mDataBidingView.switchTone.isChecked = toneEnable
        updateEnableToneUI(
            toneEnable,
            false
        )
    }
    private fun initEqualizerUI(equEnable: Boolean) {
        mDataBidingView.switchEqualizer.isChecked = equEnable
        setupEqualizerBandsUI()
    }
    private fun setupEqualizerBandsUI() {
        val bandsCount: Short = mEqualizerActivityViewModel.getBandsCount()
        for(i in 0 until bandsCount){
            val inflater = LayoutInflater.from(this@EqualizerActivity).inflate(R.layout._component_vertical_seek_bar, null)
            val textTop = inflater.findViewById<MaterialTextView>(R.id.text_view_top)
            val textBottom = inflater.findViewById<MaterialTextView>(R.id.text_view_bottom)
            val verticalSeekBar = inflater.findViewById<VerticalSeekBar>(R.id.vertical_seekbar)
            textTop.id = R.id.text_view_top + INDEX_TEXT_VIEW_TOP_ADD + i
            textBottom.id = R.id.text_view_bottom + INDEX_TEXT_VIEW_BOTTOM_ADD + i
            verticalSeekBar.id = R.id.vertical_seekbar + INDEX_VERTICAL_SEEKBAR_ADD + i
            var bandLevel: Short
            runBlocking {
                bandLevel = mEqualizerActivityViewModel.getBandLevel(
                    mEqualizerActivityViewModel.getCurrentPreset().value,
                    i.toShort(),
                    mEqualizerPresetBandLevelItemViewModel
                )
            }
            val centerFreq: Int = mEqualizerActivityViewModel.getCenterFrequency(i.toShort())
            textTop.text = FormattersAndParsersUtils.formatBandLevelToString(bandLevel)
            textBottom.text = FormattersAndParsersUtils.formatCenterFreqToString(centerFreq)
            verticalSeekBar.progress = FormattersAndParsersUtils.formatBandToPercent(
                bandLevel,
                mEqualizerActivityViewModel.getBandLevelRangeMin(),
                mEqualizerActivityViewModel.getBandLevelRangeMax()
            )
            var havePressed = false
            verticalSeekBar.setOnPressListener {
                havePressed = true
            }
            verticalSeekBar.setOnReleaseListener {
                havePressed = false
            }
            verticalSeekBar.setOnProgressChangeListener {
                MainScope().launch {
                    if(havePressed){
                        updateCurrentPresetName()
                        updateBandLevelOnVerticalSeekBarChange(i.toShort(), it)
                        updateBandTextsOnVerticalSeekBarChange(i.toShort(), textTop)
                    }
                }
            }
            mDataBidingView.linearVerticalSeekbars.addView(inflater, mDataBidingView.linearVerticalSeekbars.childCount)
        }
    }

    private fun updateCurrentPresetName() {
        mEqualizerActivityViewModel.setCurrentPreset(null, null)
    }

    private fun updateBandTextsOnVerticalSeekBarChange(bandId: Short, textTop: TextView) {
        var bandLevel: Short
        runBlocking {
            bandLevel = mEqualizerActivityViewModel.getBandLevel(
                mEqualizerActivityViewModel.getCurrentPreset().value,
                bandId,
                mEqualizerPresetBandLevelItemViewModel
            )
        }
        textTop.text = FormattersAndParsersUtils.formatBandLevelToString(bandLevel)
    }
    private fun updateBandLevelOnVerticalSeekBarChange(bandId: Short, progressLevel: Int) {
        val level = FormattersAndParsersUtils.formatPercentToBandFreq(
            progressLevel,
            mEqualizerActivityViewModel.getBandLevelRangeMin(),
            mEqualizerActivityViewModel.getBandLevelRangeMax()
        )
        mEqualizerActivityViewModel.setBandLevel(bandId, level)
    }

    private fun initViews() {
        InsetModifiersUtils.updateTopViewInsets(mDataBidingView.coordinatorLayout)
        InsetModifiersUtils.updateBottomViewInsets(mDataBidingView.constraintContainer)
    }

    companion object {
        const val TAG = "EqualizerActivity"

        const val INDEX_TEXT_VIEW_TOP_ADD = 11347
        const val INDEX_TEXT_VIEW_BOTTOM_ADD = 42324
        const val INDEX_VERTICAL_SEEKBAR_ADD = 71231
    }
}
