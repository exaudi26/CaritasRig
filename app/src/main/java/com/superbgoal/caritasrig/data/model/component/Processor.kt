package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Processor(
    val name: String = "",
    val price: Double = 0.0,
    val core_count: Int = 0,
    val core_clock: Double = 0.0,
    val boost_clock: Double = 0.0,
    val tdp: Int = 0,
    val graphics: String = "",
    val smt: Boolean = false,
) :Parcelable
