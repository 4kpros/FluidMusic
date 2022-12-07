package com.prosabdev.fluidmusic.utils

abstract class ConstantValues {

    companion object {

        const val TAG = "Fluid Music"
        const val PACKAGE_NAME = "com.prosabdev.fluidmusic"

        //List
        const val ORGANIZE_LIST_SMALL_NO_IMAGE : Int = 101
        const val ORGANIZE_LIST_MEDIUM_NO_IMAGE : Int = 102
        const val ORGANIZE_LIST_LARGE_NO_IMAGE : Int = 103
        const val ORGANIZE_LIST_EXTRA_SMALL : Int = 201
        const val ORGANIZE_LIST_SMALL : Int = 202
        const val ORGANIZE_LIST_MEDIUM : Int = 203
        const val ORGANIZE_LIST_LARGE : Int = 204
        //Grid
        const val ORGANIZE_GRID_SMALL_NO_IMAGE : Int = 1001
        const val ORGANIZE_GRID_MEDIUM_NO_IMAGE : Int = 1002
        const val ORGANIZE_GRID_EXTRA_SMALL : Int = 2001
        const val ORGANIZE_GRID_SMALL : Int = 2002
        const val ORGANIZE_GRID_MEDIUM : Int = 2003
        const val ORGANIZE_GRID_LARGE : Int = 2004
        const val ORGANIZE_GRID_EXTRA_LARGE : Int = 2005


        const val BUNDLE_CURRENT_SONG_META_DATA = "${PACKAGE_NAME}.BUNDLE_CURRENT_SONG_META_DATA"
        const val BUNDLE_QUEUE_LIST = "${PACKAGE_NAME}.BUNDLE_QUEUE_LIST"
        const val BUNDLE_SOURCE_FROM = "${PACKAGE_NAME}.BUNDLE_SOURCE_FROM"
        const val BUNDLE_SOURCE_FROM_VALUE = "${PACKAGE_NAME}.BUNDLE_SOURCE_FROM_VALUE"
        const val BUNDLE_SHUFFLE_VALUE = "${PACKAGE_NAME}.BUNDLE_SHUFFLE_VALUE"
        const val BUNDLE_REPEAT_VALUE = "${PACKAGE_NAME}.BUNDLE_REPEAT_VALUE"
        const val BUNDLE_CURRENT_SONG_ID = "${PACKAGE_NAME}.BUNDLE_CURRENT_SONG_ID"

        //Permissions
        const val BLUETOOTH_PERMISSION_CODE = 3
        const val AUDIO_RECORD_PERMISSION_CODE = 7
        const val STORAGE_PERMISSION_CODE = 11

        //foreground service
        const val SERVICE_DIED = 10
        const val FADE_UP = 11
        const val FADE_DOWN = 12
        const val FOCUS_CHANGE = 13
        const val GO_TO_NEXT_TRACK = 22

        // Actions for notifications
        const val TOGGLE_PLAY_PAUSE_ACTION = "${PACKAGE_NAME}.TOGGLE_PLAY_PAUSE_ACTION"
        const val PLAY_ACTION = "${PACKAGE_NAME}.ACTION_PLAY"
        const val PAUSE_ACTION = "${PACKAGE_NAME}.ACTION_PAUSE"
        const val STOP_ACTION = "${PACKAGE_NAME}.ACTION_STOP"
        const val NEXT_ACTION = "${PACKAGE_NAME}.ACTION_NEXT"
        const val PREV_ACTION = "${PACKAGE_NAME}.ACTION_PREV"
        const val FAVORITE_ACTION = "${PACKAGE_NAME}.FAVORITE_ACTION"
        const val REPEAT_ACTION = "${PACKAGE_NAME}.REPEAT_ACTION"
        const val CLOSE_ACTION = "${PACKAGE_NAME}.CLOSE_ACTION"
        const val SEEK_TO_ACTION = "${PACKAGE_NAME}.SEEK_TO_ACTION"

        const val MEDIA_SESSION_TAG = "${PACKAGE_NAME}.MEDIA_SESSION_TAG"

        //Notification mode
        const val HANDLER_THREAD = "${PACKAGE_NAME}.HANDLER_THREAD"
        const val CHANNEL_ID = "${PACKAGE_NAME}.CHANNEL_ID"
        const val NOTIFICATION_ID = "${PACKAGE_NAME}.21323123"
        const val NOTIFICATION_REQUEST_CODE : Int = 21301
        const val NOTIFICATION_MODE_NON = 0
        const val NOTIFICATION_MODE_FOREGROUND = 1
        const val NOTIFICATION_MODE_BACKGROUND = 2
    }
}