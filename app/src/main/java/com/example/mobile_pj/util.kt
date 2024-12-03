package com.example.mobile_pj

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun String.stringToUnixTimestamp(): Long {
    val localDate = LocalDate.parse(this, DateTimeFormatter.ISO_DATE)
    return localDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
}
