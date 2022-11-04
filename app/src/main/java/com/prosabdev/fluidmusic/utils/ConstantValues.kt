package com.prosabdev.fluidmusic.utils

abstract class ConstantValues {

    companion object {
        const val TAG = "FluidMusic"

        const val SHARED_PREFERENCES_BROADCAST = "SHARED_PREFERENCES_BROADCAST"
        const val SHARED_PREFERENCES_REPEAT = "SHARED_PREFERENCES_REPEAT"
        const val SHARED_PREFERENCES_SHUFFLE = "SHARED_PREFERENCES_SHUFFLE"
        const val SHARED_PREFERENCES_QUEUE_LIST = "SHARED_PREFERENCES_QUEUE_LIST"
        const val SHARED_PREFERENCES_CURRENT_SONG = "SHARED_PREFERENCES_CURRENT_SONG"

        //Permissions
        const val CALL_PERMISSION_CODE = 1
        const val LOCATION_PERMISSION_CODE = 2
        const val SMS_PERMISSION_CODE = 3
        const val BLUETOOTH_PERMISSION_CODE = 3
        const val CAMERA_PERMISSION_CODE = 6
        const val AUDIO_RECORD_PERMISSION_CODE = 7
        const val STORAGE_PERMISSION_CODE = 11

        //foreground service
        const val SERVICE_DIED = 10
        const val FADE_UP = 11
        const val FADE_DOWN = 12
        const val FOCUS_CHANGE = 13
        const val GO_TO_NEXT_TRACK = 22

        // Actions for notifications
        const val TOGGLE_PAUSE_ACTION = "TOGGLE_PAUSE_ACTION"
        const val PLAY_ACTION = "ACTION_PLAY"
        const val PAUSE_ACTION = "ACTION_PAUSE"
        const val STOP_ACTION = "ACTION_STOP"
        const val NEXT_ACTION = "ACTION_NEXT"
        const val PREV_ACTION = "ACTION_PREV"
        const val FAVORITE_ACTION = "FAVORITE_ACTION"
        const val REPEAT_ACTION = "REPEAT_ACTION"
        const val CLOSE_ACTION = "CLOSE_ACTION"

        //Notification mode
        const val NOTIFICATION_MODE_NON = 0
        const val NOTIFICATION_MODE_FOREGROUND = 1
        const val NOTIFICATION_MODE_BACKGROUND = 2

        //Repeat modes
        const val REPEAT_NONE = 0
        const val REPEAT_ALL = 1
        const val REPEAT_ONE = 2
        //Shuffle modes
        const val SHUFFLE_NONE = 0
        const val SHUFFLE_DEFAULT = 1


        const val ARGS_EXPLORE_CONTENT = "ARGS_EXPLORE_CONTENT"
        const val EXPLORE_ALL_FOLDERS = "EXPLORE_ALL_FOLDERS"
        const val EXPLORE_ALL_SONGS = "EXPLORE_ALL_SONGS"
        const val EXPLORE_ALBUMS = "EXPLORE_ALBUMS"
        const val EXPLORE_ALL_ARTISTS = "EXPLORE_ALL_ARTISTS"
        const val EXPLORE_ALL_GENRES = "EXPLORE_ALL_GENRES"
    }
}