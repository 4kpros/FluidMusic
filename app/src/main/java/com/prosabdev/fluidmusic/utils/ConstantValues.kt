package com.prosabdev.fluidmusic.utils

abstract class ConstantValues {

    companion object {
        const val BUNDLE_CURRENT_SONG_META_DATA = "BUNDLE_CURRENT_SONG_META_DATA"
        const val BUNDLE_QUEUE_LIST = "BUNDLE_QUEUE_LIST"
        const val BUNDLE_SOURCE_FROM = "BUNDLE_SOURCE_FROM"
        const val BUNDLE_SOURCE_FROM_VALUE = "BUNDLE_SOURCE_FROM_VALUE"
        const val BUNDLE_SHUFFLE_VALUE = "BUNDLE_SHUFFLE_VALUE"
        const val BUNDLE_REPEAT_VALUE = "BUNDLE_REPEAT_VALUE"
        const val BUNDLE_CURRENT_SONG_ID = "BUNDLE_CURRENT_SONG_ID"

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
        const val TOGGLE_PLAY_PAUSE_ACTION = "TOGGLE_PLAY_PAUSE_ACTION"
        const val PLAY_ACTION = "ACTION_PLAY"
        const val PAUSE_ACTION = "ACTION_PAUSE"
        const val STOP_ACTION = "ACTION_STOP"
        const val NEXT_ACTION = "ACTION_NEXT"
        const val PREV_ACTION = "ACTION_PREV"
        const val FAVORITE_ACTION = "FAVORITE_ACTION"
        const val REPEAT_ACTION = "REPEAT_ACTION"
        const val CLOSE_ACTION = "CLOSE_ACTION"
        const val SEEK_TO_ACTION = "SEEK_TO_ACTION"

        const val MEDIA_SESSION_TAG = "MEDIA_SESSION_TAG"

        //Notification mode
        const val HANDLER_THREAD = "HANDLER_THREAD"
        const val CHANNEL_ID = "CHANNEL_ID"
        const val NOTIFICATION_ID = "21323123"
        const val NOTIFICATION_REQUEST_CODE : Int = 21301
        const val NOTIFICATION_MODE_NON = 0
        const val NOTIFICATION_MODE_FOREGROUND = 1
        const val NOTIFICATION_MODE_BACKGROUND = 2

        //Music library flags
        const val ARGS_EXPLORE_MUSIC_LIBRARY = "ARGS_EXPLORE_MUSIC_LIBRARY"
        const val EXPLORE_ALL_SONGS = "EXPLORE_ALL_SONGS"
        const val EXPLORE_ALBUMS = "EXPLORE_ALBUMS"
        const val EXPLORE_ALBUM_ARTISTS = "EXPLORE_ALBUM_ARTISTS"
        const val EXPLORE_ARTISTS = "EXPLORE_ARTISTS"
        const val EXPLORE_COMPOSERS = "EXPLORE_COMPOSERS"
        const val EXPLORE_FOLDERS = "EXPLORE_FOLDERS"
        const val EXPLORE_GENRES = "EXPLORE_GENRES"
        const val EXPLORE_YEARS = "EXPLORE_YEARS"

        //Media scanner flags
        const val MEDIA_SCANNER_WORKER_METHOD = "MEDIA_SCANNER_WORKER_METHOD"
        const val MEDIA_SCANNER_WORKER_METHOD_REMOVE_FOLDER_URI_TREE = "MEDIA_SCANNER_WORKER_METHOD_REMOVE_FOLDER_URI_TREE"
        const val MEDIA_SCANNER_WORKER_METHOD_REMOVE_FOLDER_URI_TREE_POSITION = "MEDIA_SCANNER_WORKER_METHOD_REMOVE_FOLDER_URI_TREE_POSITION"
        const val MEDIA_SCANNER_WORKER_METHOD_CLEAR_ALL = "MEDIA_SCANNER_WORKER_METHOD_CLEAR_ALL"
        const val MEDIA_SCANNER_WORKER_METHOD_ADD_NEW = "MEDIA_SCANNER_WORKER_METHOD_ADD_NEW"

        //Database strings
        const val FLUID_MUSIC_DATABASE_NAME = "fluid_music_database_name"
    }
}