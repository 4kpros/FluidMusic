package com.prosabdev.fluidmusic.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.TextView
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
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
import com.prosabdev.fluidmusic.utils.InjectorUtils
import com.prosabdev.fluidmusic.viewmodels.activities.EqualizerActivityViewModel
import com.prosabdev.fluidmusic.viewmodels.mediacontroller.MediaControllerViewModel
import com.prosabdev.fluidmusic.viewmodels.models.equalizer.EqualizerPresetBandLevelItemViewModel
import com.prosabdev.fluidmusic.viewmodels.models.equalizer.EqualizerPresetItemViewModel
import kotlinx.coroutines.*
import me.tankery.lib.circularseekbar.CircularSeekBar

@UnstableApi class EqualizerActivity : AppCompatActivity() {

    //Data binding
    private lateinit var mDataBiding : ActivityEqualizerBinding

    //View models
    private val mEqualizerActivityViewModel by viewModels<EqualizerActivityViewModel> {
        InjectorUtils.provideEqualizerActivityViewModel(application)
    }
    private val mMediaControllerViewModel by viewModels<MediaControllerViewModel> {
        InjectorUtils.provideMediaControllerViewModel(mEqualizerActivityViewModel.mediaEventsListener)
    }
    private val mEqualizerPresetItemViewModel: EqualizerPresetItemViewModel by viewModels()
    private val mEqualizerPresetBandLevelItemViewModel: EqualizerPresetBandLevelItemViewModel by viewModels()

    //Observer for volume changes
    private var mSettingsContentObserver: SettingsContentObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Apply UI settings and dynamics colors
        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        //Set binding layout and return binding object
        mDataBiding = DataBindingUtil.setContentView(this, R.layout.activity_equalizer)

        //Load your UI content
        if(savedInstanceState == null) {
            runBlocking {
                initViews()
                loadPreferencesAndInitEqualizer()
                observeLiveData()
                checkInteractions()
                registerOnBackPressedCallback()
            }
        }
    }

    private fun registerOnBackPressedCallback() {
        if (Build.VERSION.SDK_INT >= 33) {
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

    private suspend fun observeLiveData() {
        mEqualizerActivityViewModel.loadPresets(this@EqualizerActivity, mEqualizerPresetItemViewModel)
        mEqualizerActivityViewModel.equalizerState.observe(this) {
            updateUIEnableEqualizer(it, it != null)
        }
        mEqualizerActivityViewModel.toneState.observe(this) {
            onTonalStateChange(it)
        }
        mEqualizerActivityViewModel.currentPreset.observe(this) {
            onCurrentPresetChange(it)
        }
        mEqualizerActivityViewModel.presetBandsLevels.observe(this) {
            onPresetBandsLevelsChange()
        }
        mEqualizerActivityViewModel.volume.observe(this) {
            onVolumeChanged(it)
        }
        mEqualizerActivityViewModel.bassBoost.observe(this) {
            onBassBoostChanged(it)
        }
        mEqualizerActivityViewModel.visualizer.observe(this) {
            onVisualizerChanged(it)
        }
    }

    private fun onVisualizerChanged(it: Int?) {
        if(it == null) return
        updateVisualizerUI(it)
    }
    private fun updateVisualizerUI(it: Int) {
        mDataBiding.seekbarVisualizer.progress = it.toFloat()
        mDataBiding.textViewVisualizerProgress.text = "${it}%"
    }

    private fun onBassBoostChanged(it: Int?) {
        if(it == null) return
        updateBassBoostUI(it)
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

    private fun onPresetBandsLevelsChange() {
        updateEqualizerBands(mEqualizerActivityViewModel.currentPreset.value)
    }

    private fun onTonalStateChange(it: Boolean?) {
        val animate = it != null
        updateEnableToneUI(
            it ?: false,
            animate
        )
    }

    private fun onCurrentPresetChange(it: CharSequence?) {
        updateEqualizerBands(it)
    }

    private fun checkInteractions() {
        mDataBiding.switchEqualizer.setOnCheckedChangeListener { _, isChecked ->
            switchEqualizer(isChecked)
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
                mEqualizerActivityViewModel.volume.value = newVolume.toInt()
            }
        }
        mDataBiding.seekbarBassBoost.setOnSeekBarChangeListener(object : CircularSeekBar.OnCircularSeekBarChangeListener{
            override fun onProgressChanged(
                circularSeekBar: CircularSeekBar?,
                progress: Float,
                fromUser: Boolean
            ) {
                if(fromUser){
                    mEqualizerActivityViewModel.bassBoost.value = progress.toInt()
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
                    mEqualizerActivityViewModel.visualizer.value = progress.toInt()
                }
            }
            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {
            }
        })
    }

    private fun switchEqualizer(checked: Boolean) {
        mEqualizerActivityViewModel.equalizerState.value = checked
        if(mEqualizerActivityViewModel.currentPreset.value == null){
            val defaultActivatedPreset = mEqualizerActivityViewModel.getPresetName(0)
            lifecycleScope.launch(Dispatchers.Default) {
                mEqualizerActivityViewModel.setCurrentPreset(
                    defaultActivatedPreset,
                    mEqualizerPresetBandLevelItemViewModel
                )
            }
        }
    }

    private fun openPresetSelectorDialog() {
        //Inflate with layout resource and return data binding object
        val dataBinding : ComponentDialogTitleBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout._component_dialog_title,
                null,
                false
            )
        //Build dialog
        MaterialAlertDialogBuilder(this)
            .setCustomTitle(dataBinding.root)
            .setIcon(R.drawable.tune)
            .setSingleChoiceItems(
                mEqualizerActivityViewModel.getPresets(),
                mEqualizerActivityViewModel.getPresetIdFromName(
                    mEqualizerActivityViewModel.currentPreset.value
                ).toInt()
            ) { _, which ->
                lifecycleScope.launch {
                    mEqualizerActivityViewModel.setCurrentPreset(
                        mEqualizerActivityViewModel.getPresetName(which.toShort()),
                        mEqualizerPresetBandLevelItemViewModel
                    )
                }
            }
            .show()
            .apply {
                //Apply custom views
                dataBinding.imageViewIcon.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        baseContext.resources,
                        R.drawable.tune,
                        null
                    )
                )
                dataBinding.textTitle.text = baseContext.resources.getString(R.string.choose_preset)
            }
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
            if(inputTextLength <= 0){
                dialogDataBidingView.textInputLayout.error = baseContext.resources.getString(R.string.invalid_name)
            }else{
                dialogDataBidingView.textInputLayout.error = baseContext.resources.getString(R.string.name_already_exist)
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
                mEqualizerActivityViewModel.getBandLevelRangeMin(),
                mEqualizerActivityViewModel.getBandLevelRangeMax()
            )
        }
    }

    private fun updateUIEnableEqualizer(isChecked: Boolean?, animate: Boolean) {
        if(isChecked == true){
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
            verticalSeekBar.useThumbToSetProgress = isChecked ?: false
            verticalSeekBar.clickToSetProgress = isChecked ?: false
            if(isChecked == true){
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
    private fun updateEnableToneUI(isChecked: Boolean?, animate: Boolean) {
        if(isChecked == true){
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

    private fun loadPreferencesAndInitEqualizer() {
        val equEnable = PersistentStorage.AudioEffects.equalizerState
        val toneEnable = PersistentStorage.AudioEffects.toneState
        mEqualizerActivityViewModel.initSilentEqualizer(mDataBiding.exoVisualizerBands)
        initEqualizerUI(equEnable)
        initToneUI(toneEnable)
    }
    private fun initToneUI(toneEnable: Boolean?) {
        mDataBiding.switchTone.isChecked = toneEnable ?: false
        updateEnableToneUI(
            toneEnable,
            false
        )
    }
    private fun initEqualizerUI(equEnable: Boolean?) {
        mDataBiding.switchEqualizer.isChecked = equEnable ?: false
        setupEqualizerBandsUI()
    }
    private fun setupEqualizerBandsUI() {
        lifecycleScope.launch(Dispatchers.Default) {
            val bandsCount: Short = mEqualizerActivityViewModel.getBandsCount()
            for(i in 0 until bandsCount){
                val inflater = LayoutInflater.from(this@EqualizerActivity).inflate(R.layout._component_vertical_seek_bar, null)
                val textTop = inflater.findViewById<MaterialTextView>(R.id.text_view_top)
                val textBottom = inflater.findViewById<MaterialTextView>(R.id.text_view_bottom)
                val verticalSeekBar = inflater.findViewById<VerticalSeekBar>(R.id.vertical_seekbar)

                textTop.id = R.id.text_view_top + INDEX_TEXT_VIEW_TOP_ADD + i
                textBottom.id = R.id.text_view_bottom + INDEX_TEXT_VIEW_BOTTOM_ADD + i
                verticalSeekBar.id = R.id.vertical_seekbar + INDEX_VERTICAL_SEEKBAR_ADD + i

                val bandLevel: Short = mEqualizerActivityViewModel.getBandLevel(
                    mEqualizerActivityViewModel.currentPreset.value,
                    i.toShort(),
                    mEqualizerPresetBandLevelItemViewModel
                )
                val centerFreq: Int = mEqualizerActivityViewModel.getCenterFrequency(i.toShort())
                textTop.text = FormattersAndParsers.formatBandLevelToString(bandLevel)
                textBottom.text = FormattersAndParsers.formatCenterFreqToString(centerFreq)
                verticalSeekBar.progress = FormattersAndParsers.formatBandToPercent(
                    bandLevel,
                    mEqualizerActivityViewModel.getBandLevelRangeMin(),
                    mEqualizerActivityViewModel.getBandLevelRangeMax()
                )

                //Listen for seekbar events : on press, on release and on progress change
                var havePressed = false
                verticalSeekBar.setOnPressListener {
                    havePressed = true
                }
                verticalSeekBar.setOnReleaseListener {
                    havePressed = false
                }
                verticalSeekBar.setOnProgressChangeListener {
                    if(havePressed){
                        runBlocking {
                            mEqualizerActivityViewModel.setCurrentPreset(null, null)
                            updateUIBandLevel(i.toShort(), it)
                            updateUITextBand(i.toShort(), textTop)
                        }
                    }
                }
                mDataBiding.linearVerticalSeekbars.addView(inflater, mDataBiding.linearVerticalSeekbars.childCount)
            }
        }
    }

    private fun updateUITextBand(bandId: Short, textTop: TextView) {
        lifecycleScope.launch {
            val bandLevel: Short = mEqualizerActivityViewModel.getBandLevel(
                mEqualizerActivityViewModel.currentPreset.value,
                bandId,
                mEqualizerPresetBandLevelItemViewModel
            )
            textTop.text = FormattersAndParsers.formatBandLevelToString(bandLevel)
        }
    }
    private fun updateUIBandLevel(bandId: Short, progressLevel: Int) {
        val level = FormattersAndParsers.formatPercentToBandFreq(
            progressLevel,
            mEqualizerActivityViewModel.getBandLevelRangeMin(),
            mEqualizerActivityViewModel.getBandLevelRangeMax()
        )
        mEqualizerActivityViewModel.setBandLevel(bandId, level)
    }

    private fun initViews() {
        InsetModifiers.updateTopViewInsets(mDataBiding.coordinatorLayout)
        InsetModifiers.updateBottomViewInsets(mDataBiding.constraintContainer)
    }

    private fun registerVolumeEventListener() {
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
    private fun unregisterVolumeEventListener() {
        mSettingsContentObserver?.let { observer ->
            applicationContext.contentResolver.unregisterContentObserver(observer)
        }
        mSettingsContentObserver = null
    }

    override fun onStart() {
        super.onStart()
        registerVolumeEventListener()
    }
    override fun onStop() {
        super.onStop()
        unregisterVolumeEventListener()
        mEqualizerActivityViewModel.stopEqualizer()
    }

    companion object {
        const val TAG = "EqualizerActivity"

        const val INDEX_TEXT_VIEW_TOP_ADD = 11347
        const val INDEX_TEXT_VIEW_BOTTOM_ADD = 42324
        const val INDEX_VERTICAL_SEEKBAR_ADD = 71231
    }

    class SettingsContentObserver(
        private val mContext: Context,
        mHandler: Handler?,
        private val mViewModel: EqualizerActivityViewModel
    ) : ContentObserver(mHandler) {

        private var previousVolume: Int
        var maxVolume: Int

        init {
            val audio = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
            maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            previousVolume = currentVolume
            mViewModel.volume.value = currentVolume
        }

        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            try {
                val audio = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
                val delta = previousVolume - currentVolume
                if (delta > 0) {
                    previousVolume = currentVolume
                    mViewModel.volume.value = currentVolume
                } else if (delta < 0) {
                    previousVolume = currentVolume
                    mViewModel.volume.value = currentVolume
                }
            }catch (error: Throwable){
                error.printStackTrace()
            }
        }
    }
}