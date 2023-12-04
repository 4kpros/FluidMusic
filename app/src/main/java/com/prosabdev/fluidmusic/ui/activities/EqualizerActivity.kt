package com.prosabdev.fluidmusic.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.media3.common.util.UnstableApi
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.lukelorusso.verticalseekbar.VerticalSeekBar
import com.prosabdev.common.persistence.PersistentStorage
import com.prosabdev.common.utils.Animators
import com.prosabdev.common.utils.FormattersAndParsers
import com.prosabdev.common.utils.InsetModifiers
import com.prosabdev.common.utils.SystemSettings
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ActivityEqualizerBinding
import com.prosabdev.fluidmusic.databinding.ComponentDialogEditTextBinding
import com.prosabdev.fluidmusic.databinding.ComponentDialogTitleBinding
import com.prosabdev.fluidmusic.viewmodels.activities.EqualizerActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.models.equalizer.EqualizerPresetBandLevelItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.equalizer.EqualizerPresetItemViewModel
import kotlinx.coroutines.*
import me.tankery.lib.circularseekbar.CircularSeekBar

@UnstableApi @BuildCompat.PrereleaseSdkCheck class EqualizerActivity : AppCompatActivity() {

    private lateinit var mDataBiding : ActivityEqualizerBinding

    private val mEqualizerPresetItemViewModel: EqualizerPresetItemViewModel by viewModels()
    private val mEqualizerPresetBandLevelItemViewModel: EqualizerPresetBandLevelItemViewModel by viewModels()

    private val mEqualizerActivityViewModel: EqualizerActivityViewModel by viewModels()

    private var mSettingsContentObserver: SettingsContentObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Apply UI settings and dynamics colors
        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        //Set content with data biding util
        mDataBiding = DataBindingUtil.setContentView(this, R.layout.activity_equalizer)

        //Load your UI content
        if(savedInstanceState == null){
            initViews()
            loadSettingsAndInitEqualizer()
            checkInteractions()
            observeLiveData()
            registerOnBackPressedCallback()
        }
    }
    override fun onStop() {
        mEqualizerActivityViewModel.stopEqualizer()
        unregisterEventListeners()
        super.onStop()
    }
    override fun onStart() {
        super.onStart()
        registerEventListeners()
    }

    private fun registerEventListeners() {
        if(mSettingsContentObserver != null) return

        mSettingsContentObserver = SettingsContentObserver(this, Handler(Looper.getMainLooper()), mEqualizerActivityViewModel)
        mSettingsContentObserver?.let { observer ->
            applicationContext.contentResolver.registerContentObserver(
                android.provider.Settings.System.CONTENT_URI,
                true,
                observer
            )
        }
    }
    private fun unregisterEventListeners() {
        mSettingsContentObserver?.let { observer ->
            applicationContext.contentResolver.unregisterContentObserver(observer)
        }
        mSettingsContentObserver = null
    }

    private fun observeLiveData() {
        mEqualizerActivityViewModel.customPresets.observe(this@EqualizerActivity) {
            //
        }
        mEqualizerActivityViewModel.equalizerState.observe(this) {
            if(it != null) {
                updateEnableEquUI(it)
            }
        }
        mEqualizerActivityViewModel.toneState.observe(this) {
            if(it != null) {
                updateEnableToneUI(it)
            }
        }
        mEqualizerActivityViewModel.currentPreset.observe(this) {
            if(it != null) {
                updateEqualizerBands(it)
            }
        }
        mEqualizerActivityViewModel.presetBandsLevels.observe(this) {
            if(it != null) {
                updateEqualizerBands(mEqualizerActivityViewModel.getCurrentPreset().value)
            }
        }
        mEqualizerActivityViewModel.volumeProgress.observe(this) {
            if(it != null) {
                onVolumeChanged(it)
            }
        }
        mEqualizerActivityViewModel.bassBoostProgress.observe(this) {
            if(it != null) {
                updateBassBoostUI(it)
            }
        }
        mEqualizerActivityViewModel.visualizerProgress.observe(this) {
            if(it != null) {
                updateVisualizerUI(it)
            }
        }
    }

    private fun updateVisualizerUI(it: Int) {
        mDataBiding.seekbarVisualizer.progress = it.toFloat()
        mDataBiding.textViewVisualizerProgress.text = "${it}%"
    }
    @SuppressLint("SetTextI18n")
    private fun updateBassBoostUI(it: Int) {
        mDataBiding.seekbarBassBoost.progress = it.toFloat()
        mDataBiding.textViewBassBoostProgress.text = "${it}%"
    }

    private fun onVolumeChanged(it: Int?) {
        if(it == null) return
        val tempMaxVolume: Int = mSettingsContentObserver?.maxVolume ?: 0
        val tempPercentVolume: Float = (it.toFloat() / tempMaxVolume.toFloat()) * 100.0f
        Log.i(TAG, "ON VOLUME PERCENTAGE CHANGED : $tempPercentVolume")
        updateMediaPlayerVolume(it)
        updateVolumeUI(tempPercentVolume)
    }
    private fun updateMediaPlayerVolume(newVolume: Int) {
        try {
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            if(!audioManager.isVolumeFixed && currentVolume != newVolume){
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    newVolume,
                    AudioManager.FLAG_PLAY_SOUND
                )
            }
        }catch (error: Throwable){
            error.printStackTrace()
        }
    }
    private fun updateVolumeUI(it: Float) {
        mDataBiding.volumeSlider.value = it
        mDataBiding.textViewVolumeProgress.text = "${it.toInt()}%"
    }

    private fun onTonalStateChange(it: Boolean?) {
        val animate = it != null
        updateEnableToneUI(
            it ?: false,
            animate
        )
    }

    private fun onEqualizerStateChanged(it: Boolean?) {
        val animate = it != null
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
        mDataBiding.switchEqualizer.setOnCheckedChangeListener { _, isChecked ->
            mEqualizerActivityViewModel.setEqualizerState(isChecked)
            if(mEqualizerActivityViewModel.getCurrentPreset().value == null){
                val defaultActivatedPreset = mEqualizerActivityViewModel.getPresetName(0)
                mEqualizerActivityViewModel.setCurrentPreset(
                    defaultActivatedPreset,
                    mEqualizerPresetBandLevelItemViewModel
                )
            }
        }
        mDataBiding.switchTone.setOnCheckedChangeListener { _, isChecked ->
            mEqualizerActivityViewModel.toneState.value = isChecked
        }
        mDataBiding.buttonChoosePreset.setOnClickListener{
            openPresetSelectorDialog()
        }
        mDataBiding.buttonSavePreset.setOnClickListener{
            openSavePresetDialog()
        }
        mDataBiding.volumeSlider.addOnChangeListener { _, value, fromUser ->
            if(fromUser){
                val maxVolume = mSettingsContentObserver?.maxVolume ?: 0
                val newVolume = value / 100f * maxVolume
                mEqualizerActivityViewModel.volumeProgress.value = newVolume.toInt()
            }
        }
        mDataBiding.seekbarBassBoost.setOnSeekBarChangeListener(object : CircularSeekBar.OnCircularSeekBarChangeListener{
            override fun onProgressChanged(
                circularSeekBar: CircularSeekBar?,
                progress: Float,
                fromUser: Boolean
            ) {
                if(fromUser){
                    mEqualizerActivityViewModel.bassBoostProgress.value = progress.toInt()
                }
            }
            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {
            }
        })
        mDataBiding.seekbarVisualizer.setOnSeekBarChangeListener(object : CircularSeekBar.OnCircularSeekBarChangeListener{
            override fun onProgressChanged(
                circularSeekBar: CircularSeekBar?,
                progress: Float,
                fromUser: Boolean
            ) {
                if(fromUser){
                    mEqualizerActivityViewModel.visualizerProgress.value = progress.toInt()
                }
            }
            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {
            }
        })
    }

    private fun openSavePresetDialog() {
        val dialogDataBidingView : ComponentDialogEditTextBinding = DataBindingUtil.inflate(layoutInflater, R.layout._component_dialog_edit_text, null, false)
        val dialogTitleDataBidingView : ComponentDialogTitleBinding = DataBindingUtil.inflate(layoutInflater, R.layout._component_dialog_title, null, false)
        MaterialAlertDialogBuilder(this)
            .setCustomTitle(dialogTitleDataBidingView.root)
            .setIcon(R.drawable.save)
            .setView(dialogDataBidingView.root)
            .show().apply {
                //Apply title
                dialogTitleDataBidingView.imageViewIcon.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        baseContext.resources,
                        R.drawable.save,
                        null
                    )
                )
                dialogTitleDataBidingView.textTitle.text = baseContext.resources.getString(R.string.save_preset)
                //Show with errors
                updateSavePresetDialogUI(dialogDataBidingView, true)
                //Listen clicks events
                dialogDataBidingView.buttonCancel.setOnClickListener {
                    dismiss()
                }
                dialogDataBidingView.buttonSave.setOnClickListener {
                    savePresetToDatabase(dialogDataBidingView.textInputEditText.text)
                    dismiss()
                }
                if(dialogDataBidingView.textInputEditText.requestFocus()){
                    dialogDataBidingView.textInputEditText.text = null
                    dialogDataBidingView.textInputEditText.hint = baseContext.resources.getString(R.string.preset_name)
                    dialogDataBidingView.textInputEditText.selectAll()
                    SystemSettings.SoftInputService(this@EqualizerActivity, dialogDataBidingView.textInputEditText).show(5)
                }
                dialogDataBidingView.textInputEditText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }
                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        handleOnInputChanges(dialogDataBidingView, s)
                    }
                    override fun afterTextChanged(s: Editable?) {
                    }
                })
            }
    }

    private fun savePresetToDatabase(text: Editable?) {

    }

    private var mCheckerJob: Job? =null
    private fun handleOnInputChanges(
        dialogDataBidingView: ComponentDialogEditTextBinding,
        sequence: CharSequence?
    ) {
        if(mCheckerJob != null && mCheckerJob?.isActive == true)
            mCheckerJob?.cancel()
        mCheckerJob = CoroutineScope(Dispatchers.Default).launch {

            if(sequence == null || sequence.isEmpty()){
                updateSavePresetDialogUI(dialogDataBidingView, true)
            }else{
                val playlistExist = mEqualizerActivityViewModel.checkIfPresetExists(sequence.toString())
                if(playlistExist){
                    updateSavePresetDialogUI(dialogDataBidingView, true)
                }else{
                    updateSavePresetDialogUI(dialogDataBidingView, false)
                }
            }
        }
    }

    private fun updateSavePresetDialogUI(dialogDataBidingView: ComponentDialogEditTextBinding, haveErrors: Boolean) {
        if(haveErrors){
            val inputTextLength = dialogDataBidingView.textInputEditText.text?.length ?: 0
            MainScope().launch {
                if(inputTextLength <= 0){
                    dialogDataBidingView.textInputLayout.error = baseContext.resources.getString(R.string.invalid_name)
                }else{
                    dialogDataBidingView.textInputLayout.error = baseContext.resources.getString(R.string.name_already_exist)
                }
            }
            Animators.crossFadeDownClickable(
                dialogDataBidingView.buttonSave
            )
        }else{
            MainScope().launch {
                dialogDataBidingView.textInputLayout.error = null
            }
            Animators.crossFadeUpClickable(
                dialogDataBidingView.buttonSave
            )
        }
    }

    private fun openPresetSelectorDialog() {
        val tempCurrentPresetName = mEqualizerActivityViewModel.getCurrentPreset().value
        val dialogTitleDataBidingView : ComponentDialogTitleBinding = DataBindingUtil.inflate(layoutInflater, R.layout._component_dialog_title, null, false)
        MaterialAlertDialogBuilder(this)
            .setCustomTitle(dialogTitleDataBidingView.root)
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
                //Apply title
                dialogTitleDataBidingView.imageViewIcon.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        baseContext.resources,
                        R.drawable.tune,
                        null
                    )
                )
                dialogTitleDataBidingView.textTitle.text = baseContext.resources.getString(R.string.choose_preset)
            }
    }

    private fun updateEqualizerBands(currentPreset: CharSequence?) {
        val bandsCount: Short = mEqualizerActivityViewModel.getBandsCount()
        for(i in 0 until bandsCount){
            val textTop =
                mDataBiding.linearVerticalSeekbars.findViewById<MaterialTextView>(R.id.text_view_top + INDEX_TEXT_VIEW_TOP_ADD + i)
            val verticalSeekBar =
                mDataBiding.linearVerticalSeekbars.findViewById<VerticalSeekBar>(R.id.vertical_seekbar + INDEX_VERTICAL_SEEKBAR_ADD +i)
            var bandLevel: Short
            runBlocking {
                bandLevel = mEqualizerActivityViewModel.getBandLevel(currentPreset, i.toShort(), mEqualizerPresetBandLevelItemViewModel)
            }
            if(textTop.text == FormattersAndParsers.formatBandLevelToString(bandLevel)) continue //If view have been already updated, pass
            textTop.text = FormattersAndParsers.formatBandLevelToString(bandLevel)
            verticalSeekBar.progress = FormattersAndParsers.formatBandToPercent(
                bandLevel,
                mEqualizerActivityViewModel.presetBandsLevels.value?.get(0) ?: 0,
                mEqualizerActivityViewModel.presetBandsLevels.value?.get(1) ?: 0
            )
        }
    }

    private fun updateEnableEquUI(isChecked: Boolean, animate: Boolean = true) {
        if(isChecked){
            Animators.crossFadeUpClickable(
                mDataBiding.buttonChoosePreset,
                animate,
                200,
                1.0f,
                false
            )
            Animators.crossFadeUpClickable(
                mDataBiding.buttonSavePreset,
                animate,
                200,
                1.0f,
                false
            )
            Animators.crossFadeUpClickable(
                mDataBiding.volumeSlider,
                animate
            )
            Animators.crossFadeUpClickable(
                mDataBiding.imageViewVolume,
                animate
            )
            Animators.crossFadeUpClickable(
                mDataBiding.textViewVolumeProgress,
                animate
            )
        }else{
            Animators.crossFadeDownClickable(
                mDataBiding.buttonChoosePreset,
                animate,
                200,
                0.35f,
                false
            )
            Animators.crossFadeDownClickable(
                mDataBiding.buttonSavePreset,
                animate,
                200,
                0.35f,
                false
            )
            Animators.crossFadeDownClickable(
                mDataBiding.volumeSlider,
                animate
            )
            Animators.crossFadeDownClickable(
                mDataBiding.imageViewVolume,
                animate
            )
            Animators.crossFadeDownClickable(
                mDataBiding.textViewVolumeProgress,
                animate
            )
        }
        val bandsCount: Short = mEqualizerActivityViewModel.getBandsCount()
        for(i in 0 until bandsCount) {
            val textTop =
                mDataBiding.linearVerticalSeekbars.findViewById<MaterialTextView>(R.id.text_view_top + INDEX_TEXT_VIEW_TOP_ADD + i)
            val textBottom =
                mDataBiding.linearVerticalSeekbars.findViewById<MaterialTextView>(R.id.text_view_bottom + INDEX_TEXT_VIEW_BOTTOM_ADD + i)
            val verticalSeekBar =
                mDataBiding.linearVerticalSeekbars.findViewById<VerticalSeekBar>(R.id.vertical_seekbar + INDEX_VERTICAL_SEEKBAR_ADD +i)
            verticalSeekBar.useThumbToSetProgress = isChecked
            verticalSeekBar.clickToSetProgress = isChecked
            if(isChecked){
                Animators.crossFadeUpClickable(
                    textTop,
                    animate
                )
                Animators.crossFadeUpClickable(
                    textBottom,
                    animate
                )
                Animators.crossFadeUpClickable(
                    verticalSeekBar,
                    animate
                )
            }else{
                Animators.crossFadeDownClickable(
                    textTop,
                    animate
                )
                Animators.crossFadeDownClickable(
                    textBottom,
                    animate
                )
                Animators.crossFadeDownClickable(
                    verticalSeekBar,
                    animate
                )
            }
        }
    }
    private fun updateEnableToneUI(isChecked: Boolean, animate: Boolean = true) {
        if(isChecked){
            Animators.crossFadeUpClickable(
                mDataBiding.seekbarBassBoost,
                animate
            )
            Animators.crossFadeUpClickable(
                mDataBiding.seekbarVisualizer,
                animate
            )
            //update text views
            Animators.crossFadeUpClickable(
                mDataBiding.textViewVisualizer,
                animate
            )
            Animators.crossFadeUpClickable(
                mDataBiding.textViewVisualizerProgress,
                animate
            )
            Animators.crossFadeUpClickable(
                mDataBiding.textViewBassBoost,
                animate
            )
            Animators.crossFadeUpClickable(
                mDataBiding.textViewBassBoostProgress,
                animate
            )
            //update shape background
            Animators.crossFadeUpClickable(
                mDataBiding.shapeBackgroundBass,
                animate
            )
            Animators.crossFadeUpClickable(
                mDataBiding.shapeBackgroundVisualizer,
                animate
            )
        }else{
            Animators.crossFadeDownClickable(
                mDataBiding.seekbarBassBoost,
                animate
            )
            Animators.crossFadeDownClickable(
                mDataBiding.seekbarVisualizer,
                animate
            )
            //update text views
            Animators.crossFadeDownClickable(
                mDataBiding.textViewVisualizer,
                animate
            )
            Animators.crossFadeDownClickable(
                mDataBiding.textViewVisualizerProgress,
                animate
            )
            Animators.crossFadeDownClickable(
                mDataBiding.textViewBassBoost,
                animate
            )
            Animators.crossFadeDownClickable(
                mDataBiding.textViewBassBoostProgress,
                animate
            )
            //update shape background
            Animators.crossFadeDownClickable(
                mDataBiding.shapeBackgroundBass,
                animate
            )
            Animators.crossFadeDownClickable(
                mDataBiding.shapeBackgroundVisualizer,
                animate
            )
        }
    }

    private fun loadSettingsAndInitEqualizer() {
        val equEnable = PersistentStorage.AudioEffects.loadEqualizerState()
        val toneEnable = PersistentStorage.AudioEffects.loadToneState()
        mEqualizerActivityViewModel.initSilentEqualizer(mDataBiding.exoVisualizerBands)
        initEqualizerUI(equEnable)
        initToneUI(toneEnable)
    }
    private fun initToneUI(toneEnable: Boolean) {
        mDataBiding.switchTone.isChecked = toneEnable
        updateEnableToneUI(
            toneEnable,
            false
        )
    }
    private fun initEqualizerUI(equEnable: Boolean) {
        mDataBiding.switchEqualizer.isChecked = equEnable
        setupEqualizerBandsUI()
    }
    private fun setupEqualizerBandsUI() {
        val bandsCount: Short = mEqualizerActivityViewModel.presetBandsLevels.value?.size ?: 0
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
                    mEqualizerActivityViewModel.currentPreset.value,
                    i.toShort(),
                    mEqualizerPresetBandLevelItemViewModel
                )
            }
            val centerFreq: Int = mEqualizerActivityViewModel.getCenterFrequency(i.toShort())
            textTop.text = FormattersAndParsers.formatBandLevelToString(bandLevel)
            textBottom.text = FormattersAndParsers.formatCenterFreqToString(centerFreq)
            verticalSeekBar.progress = FormattersAndParsers.formatBandToPercent(
                bandLevel,
                mEqualizerActivityViewModel.presetBandsLevels.value?.get(0) ?: 0,
                mEqualizerActivityViewModel.presetBandsLevels.value?.get(1) ?: 0
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
            mDataBiding.linearVerticalSeekbars.addView(inflater, mDataBiding.linearVerticalSeekbars.childCount)
        }
    }

    private fun updateCurrentPresetName() {
        mEqualizerActivityViewModel.setCurrentPreset(null, null)
    }

    private fun updateBandTextsOnVerticalSeekBarChange(bandId: Short, textTop: TextView) {
        var bandLevel: Short
        runBlocking {
            bandLevel = mEqualizerActivityViewModel.getBandLevel(
                mEqualizerActivityViewModel.currentPreset.value,
                bandId,
                mEqualizerPresetBandLevelItemViewModel
            )
        }
        textTop.text = FormattersAndParsers.formatBandLevelToString(bandLevel)
    }
    private fun updateBandLevelOnVerticalSeekBarChange(bandId: Short, progressLevel: Int) {
        val level = FormattersAndParsers.formatPercentToBandFreq(
            progressLevel,
            mEqualizerActivityViewModel.presetBandsLevels.value?.get(0) ?: 0,
            mEqualizerActivityViewModel.presetBandsLevels.value?.get(1) ?: 0
        )
        mEqualizerActivityViewModel.setBandLevel(bandId, level)
    }

    private fun initViews() {
        InsetModifiers.updateTopViewInsets(mDataBiding.coordinatorLayout)
        InsetModifiers.updateBottomViewInsets(mDataBiding.constraintContainer)
    }

    class SettingsContentObserver(
        private val mContext: Context,
        private val mHandler: Handler?,
        private val mViewModel: EqualizerActivityViewModel
    ) :
        ContentObserver(mHandler) {
        private var previousVolume: Int
        var maxVolume: Int

        init {
            val audio = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
            maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            previousVolume = currentVolume
            mViewModel.volumeProgress.value = currentVolume
            Log.i(TAG, "AUDIO VOLUME PERCENT SET $currentVolume")
        }

        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            try {
                val audio = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
                val delta = previousVolume - currentVolume
                if (delta > 0) {
                    previousVolume = currentVolume
                    mViewModel.volumeProgress.value = currentVolume
                    Log.i(TAG, "AUDIO VOLUME PERCENT INCREASED $currentVolume")
                } else if (delta < 0) {
                    previousVolume = currentVolume
                    mViewModel.volumeProgress.value = currentVolume
                    Log.i(TAG, "AUDIO VOLUME PERCENT INCREASED $currentVolume")
                }
            }catch (error: Throwable){
                error.printStackTrace()
            }
        }
    }

    companion object {
        const val TAG = "EqualizerActivity"

        const val INDEX_TEXT_VIEW_TOP_ADD = 11347
        const val INDEX_TEXT_VIEW_BOTTOM_ADD = 42324
        const val INDEX_VERTICAL_SEEKBAR_ADD = 71231
    }
}
