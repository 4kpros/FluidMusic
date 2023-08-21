package com.prosabdev.common.media

class PlayerSettings {
    //Audio notification settings
    private var isForegroundService = true
    private var forceKeepNotification = true
    private var alwaysKeepNotificationUI = false
    private var removeNotificationOnDisconnection = true
    private var colorizeNotifications = true
    private var showSkipPrevAction = true
    private var showSkipNextAction = true

    //Audio equ settings
    private var equEnable = false
    private var toneEnable = false
    private var audioEffectsEnable = false
    //Audio focus settings
    private var pauseOnNotificationReceived = false
    private var playPauseOnMinVolumeChange = false
    private var pauseOnAudioFocusChange = true
    private var skipCrossFadeEnable = true
    private var skipCrossFadeLength = 1000 //In milliseconds
    private var fadePlayPause = true
    private var playPauseCrossFadeLength = 1000 //In milliseconds
    private var fadeOnSeek = true
    private var seekCrossFadeLength = 500 //In milliseconds
    //Audio focus settings
    private var resumeAfterCall = true
    private var resumeOnStart = false
    private var resumeOnReOpen = false
    //Audio headset settings
    private var pauseOnHeadsetDisconnect = true
    private var pauseOnBluetoothDisconnect = true
    private var resumeOnWiredHeadset = true
    private var resumeOnBluetooth = true
    private var enableHeadsetControls = true
    private var headsetControlsType = 1
    private var enableBluetoothControls = true
    private var bluetoothControlsType = 1
    private var enableBeep = false
    private var enableVibration = false
    //Audio lockscreen settings
    private var showCoverArt = true
    private var blurCoverArt = true
    private var showDefaultImage = false
}