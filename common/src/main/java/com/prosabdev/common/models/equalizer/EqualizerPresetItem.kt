package com.prosabdev.common.models.equalizer

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["presetName"], unique = true)]
)
data class EqualizerPresetItem (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var presetName: String? = ""
)
