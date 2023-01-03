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
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.lukelorusso.verticalseekbar.VerticalSeekBar
import com.prosabdev.fluidmusic.R
import com.prosabdev.fluidmusic.databinding.ActivityEqualizerBinding
import com.prosabdev.fluidmusic.databinding.ComponentDialogEditTextBinding
import com.prosabdev.fluidmusic.databinding.ComponentDialogTitleBinding
import com.prosabdev.fluidmusic.viewmodels.activities.EqualizerActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.models.equalizer.EqualizerPresetBandLevelItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.equalizer.EqualizerPresetItemViewModel
import kotlinx.coroutines.*
import me.tankery.lib.circularseekbar.CircularSeekBar

@BuildCompat.PrereleaseSdkCheck class EqualizerActivity : AppCompatActivity() {
    private lateinit var mDataBidingView : ActivityEqualizerBinding

    private val mEqualizerPresetItemViewModel: EqualizerPresetItemViewModel by viewModels()
    private val mEqualizerPresetBandLevelItemViewModel: EqualizerPresetBandLevelItemViewModel by viewModels()

    private val mEqualizerActivityViewModel: EqualizerActivityViewModel by viewModels()

    private var mSettingsContentObserver: SettingsContentObserver? = null

    class SettingsContentObserver(
        private val mContext: Context,
        Handler: Handler?,
        private val mViewModel: EqualizerActivityViewModel
    ) :
        ContentObserver(Handler) {
        var previousVolume: Int
        var maxVolume: Int

        init {
            val audio = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
            maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            previousVolume = currentVolume
            mViewModel.setVolume(currentVolume)
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
                    mViewModel.setVolume(currentVolume)
                    Log.i(TAG, "AUDIO VOLUME PERCENT INCREASED $currentVolume")
                } else if (delta < 0) {
                    previousVolume = currentVolume
                    mViewModel.setVolume(currentVolume)
                    Log.i(TAG, "AUDIO VOLUME PERCENT INCREASED $currentVolume")
                }
            }catch (error: Throwable){
                error.printStackTrace()
            }
        }
    }

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
    override fun onStop() {
        mEqualizerActivityViewModel.stopLocalMediaPlayer()
        unregisterCustomVolumeEvenListener()
        super.onStop()
    }
    override fun onStart() {
        registerCustomVolumeEvenListener()
        super.onStart()
    }

    private fun registerCustomVolumeEvenListener() {
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
    private fun unregisterCustomVolumeEvenListener() {
        mSettingsContentObserver?.let { observer ->
            applicationContext.contentResolver.unregisterContentObserver(observer)
        }
        mSettingsContentObserver = null
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
        mEqualizerActivityViewModel.getVolume().observe(this, Observer {
            onVolumeChanged(it)
        })
        mEqualizerActivityViewModel.getBassBoostProgress().observe(this, Observer {
            onBassBoostChanged(it)
        })
        mEqualizerActivityViewModel.getVisualizerProgress().observe(this, Observer {
            onVisualizerChanged(it)
        })
    }

    private fun onVisualizerChanged(it: Int?) {
        if(it == null) return
        updateVisualizerUI(it)
    }
    private fun updateVisualizerUI(it: Int) {
        mDataBidingView.seekbarVisualizer.progress = it.toFloat()
        mDataBidingView.textViewVisualizerProgress.text = "${it}%"
    }

    private fun onBassBoostChanged(it: Int?) {
        if(it == null) return
        updateBassBoostUI(it)
    }
    @SuppressLint("SetTextI18n")
    private fun updateBassBoostUI(it: Int) {
        mDataBidingView.seekbarBassBoost.progress = it.toFloat()
        mDataBidingView.textViewBassBoostProgress.text = "${it}%"
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
        mDataBidingView.volumeSlider.value = it
        mDataBidingView.textViewVolumeProgress.text = "${it.toInt()}%"
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
                val maxVolume = mSettingsContentObserver?.maxVolume ?: 0
                val newVolume = value / 100f * maxVolume
                mEqualizerActivityViewModel.setVolume(newVolume.toInt())
            }
        }
        mDataBidingView.seekbarBassBoost.setOnSeekBarChangeListener(object : CircularSeekBar.OnCircularSeekBarChangeListener{
            override fun onProgressChanged(
                circularSeekBar: CircularSeekBar?,
                progress: Float,
                fromUser: Boolean
            ) {
                if(fromUser){
                    mEqualizerActivityViewModel.setBassBoostProgress(progress.toInt())
                }
            }
            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {
            }
        })
        mDataBidingView.seekbarVisualizer.setOnSeekBarChangeListener(object : CircularSeekBar.OnCircularSeekBarChangeListener{
            override fun onProgressChanged(
                circularSeekBar: CircularSeekBar?,
                progress: Float,
                fromUser: Boolean
            ) {
                if(fromUser){
                    mEqualizerActivityViewModel.setVisualizerProgress(progress.toInt())
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
                    com.prosabdev.common.utils.SystemSettingsUtils.SoftInputService(this@EqualizerActivity, dialogDataBidingView.textInputEditText).show(5)
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
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                dialogDataBidingView.buttonSave
            )
        }else{
            MainScope().launch {
                dialogDataBidingView.textInputLayout.error = null
            }
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
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
                mDataBidingView.linearVerticalSeekbars.findViewById<MaterialTextView>(R.id.text_view_top + INDEX_TEXT_VIEW_TOP_ADD + i)
            val verticalSeekBar =
                mDataBidingView.linearVerticalSeekbars.findViewById<VerticalSeekBar>(R.id.vertical_seekbar + INDEX_VERTICAL_SEEKBAR_ADD +i)
            var bandLevel: Short
            runBlocking {
                bandLevel = mEqualizerActivityViewModel.getBandLevel(currentPreset, i.toShort(), mEqualizerPresetBandLevelItemViewModel)
            }
            if(textTop.text == com.prosabdev.common.utils.FormattersAndParsersUtils.formatBandLevelToString(bandLevel)) continue //If view have been already updated, pass
            textTop.text = com.prosabdev.common.utils.FormattersAndParsersUtils.formatBandLevelToString(bandLevel)
            verticalSeekBar.progress = com.prosabdev.common.utils.FormattersAndParsersUtils.formatBandToPercent(
                bandLevel,
                mEqualizerActivityViewModel.getBandLevelRangeMin(),
                mEqualizerActivityViewModel.getBandLevelRangeMax()
            )
        }
    }

    private fun updateEnableEquUI(isChecked: Boolean, animate: Boolean) {
        if(isChecked){
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.buttonChoosePreset,
                animate,
                200,
                1.0f,
                false
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.buttonSavePreset,
                animate,
                200,
                1.0f,
                false
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.volumeSlider,
                animate
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.imageViewVolume,
                animate
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.textViewVolumeProgress,
                animate
            )
        }else{
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.buttonChoosePreset,
                animate,
                200,
                0.35f,
                false
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.buttonSavePreset,
                animate,
                200,
                0.35f,
                false
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.volumeSlider,
                animate
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.imageViewVolume,
                animate
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
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
                com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
                    textTop,
                    animate
                )
                com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
                    textBottom,
                    animate
                )
                com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
                    verticalSeekBar,
                    animate
                )
            }else{
                com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                    textTop,
                    animate
                )
                com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                    textBottom,
                    animate
                )
                com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                    verticalSeekBar,
                    animate
                )
            }
        }
    }
    private fun updateEnableToneUI(isChecked: Boolean, animate: Boolean) {
        if(isChecked){
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.seekbarBassBoost,
                animate
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.seekbarVisualizer,
                animate
            )
            //update text views
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.textViewVisualizer,
                animate
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.textViewVisualizerProgress,
                animate
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.textViewBassBoost,
                animate
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.textViewBassBoostProgress,
                animate
            )
            //update shape background
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.shapeBackgroundBass,
                animate
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeUpClickable(
                mDataBidingView.shapeBackgroundVisualizer,
                animate
            )
        }else{
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.seekbarBassBoost,
                animate
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.seekbarVisualizer,
                animate
            )
            //update text views
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.textViewVisualizer,
                animate
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.textViewVisualizerProgress,
                animate
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.textViewBassBoost,
                animate
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.textViewBassBoostProgress,
                animate
            )
            //update shape background
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.shapeBackgroundBass,
                animate
            )
            com.prosabdev.common.utils.AnimatorsUtils.crossFadeDownClickable(
                mDataBidingView.shapeBackgroundVisualizer,
                animate
            )
        }
    }

    private fun loadSharedPrefsAnInitEqualizer() {
//        val equEnable = SharedPreferenceManagerUtils.AudioEffects.loadEqualizerState(baseContext)
//        val toneEnable = SharedPreferenceManagerUtils.AudioEffects.loadToneState(baseContext)
//        mEqualizerActivityViewModel.initSilentEqualizer(baseContext)
//        initEqualizerUI(equEnable)
//        initToneUI(toneEnable)
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
            textTop.text = com.prosabdev.common.utils.FormattersAndParsersUtils.formatBandLevelToString(bandLevel)
            textBottom.text = com.prosabdev.common.utils.FormattersAndParsersUtils.formatCenterFreqToString(centerFreq)
            verticalSeekBar.progress = com.prosabdev.common.utils.FormattersAndParsersUtils.formatBandToPercent(
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
        textTop.text = com.prosabdev.common.utils.FormattersAndParsersUtils.formatBandLevelToString(bandLevel)
    }
    private fun updateBandLevelOnVerticalSeekBarChange(bandId: Short, progressLevel: Int) {
        val level = com.prosabdev.common.utils.FormattersAndParsersUtils.formatPercentToBandFreq(
            progressLevel,
            mEqualizerActivityViewModel.getBandLevelRangeMin(),
            mEqualizerActivityViewModel.getBandLevelRangeMax()
        )
        mEqualizerActivityViewModel.setBandLevel(bandId, level)
    }

    private fun initViews() {
        com.prosabdev.common.utils.InsetModifiersUtils.updateTopViewInsets(mDataBidingView.coordinatorLayout)
        com.prosabdev.common.utils.InsetModifiersUtils.updateBottomViewInsets(mDataBidingView.constraintContainer)
    }

    companion object {
        const val TAG = "EqualizerActivity"

        const val INDEX_TEXT_VIEW_TOP_ADD = 11347
        const val INDEX_TEXT_VIEW_BOTTOM_ADD = 42324
        const val INDEX_VERTICAL_SEEKBAR_ADD = 71231
    }
}
