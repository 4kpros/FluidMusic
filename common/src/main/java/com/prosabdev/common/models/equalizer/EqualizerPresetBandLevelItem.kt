package com.prosabdev.common.models.equalizer

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(entity = EqualizerPresetItem::class, parentColumns = ["presetName"], childColumns = ["presetName"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["presetName"], unique = true)]
)
data class EqualizerPresetBandLevelItem (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var presetName: String? = "",
    var bandId: Short = -1,
    var bandCenterFreq: Int = -1,
    var bandLevel: Short = 0
)
