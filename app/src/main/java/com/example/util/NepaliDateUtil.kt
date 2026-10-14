package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object NepaliDateUtil {

    private val nepaliDays = arrayOf(
        "आइतबार", "सोमबार", "मङ्गलबार", "बुधबार", "बिहीबार", "शुक्रबार", "शनिबार"
    )

    private val nepaliMonthsBs = arrayOf(
        "वैशाख", "जेठ", "असार", "साउन", "भदौ", "असोज",
        "कात्तिक", "मंसिर", "पुस", "माघ", "फागुन", "चैत"
    )

    fun toDevanagariNumerals(number: Int): String {
        val devanagariDigits = charArrayOf('०', '१', '२', '३', '४', '५', '६', '७', '८', '९')
        return number.toString().map { ch ->
            if (ch in '0'..'9') devanagariDigits[ch - '0'] else ch
        }.joinToString("")
    }

    fun toDevanagariNumerals(text: String): String {
        val devanagariDigits = charArrayOf('०', '१', '२', '३', '४', '५', '६', '७', '८', '९')
        return text.map { ch ->
            if (ch in '0'..'9') devanagariDigits[ch - '0'] else ch
        }.joinToString("")
    }

    /**
     * Approximates Bikram Sambat date from Gregorian calendar.
     * Gregorian Year + 56 / 57 years depending on month.
     */
    fun getCurrentNepaliDateBs(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday
        val dayName = if (dayOfWeekIndex in nepaliDays.indices) nepaliDays[dayOfWeekIndex] else "आज"

        val gYear = calendar.get(Calendar.YEAR)
        val gMonth = calendar.get(Calendar.MONTH) // 0-based
        val gDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Standard BS conversion heuristic:
        // Mid-April starts Baishakh (BS year = AD year + 57)
        val isPostChaitra = (gMonth > 3) || (gMonth == 3 && gDay >= 14)
        val bsYear = if (isPostChaitra) gYear + 57 else gYear + 56

        // BS Month index approximation:
        // Jan: Poush/Magh, Apr: Chaitra/Baishakh, Oct: Ashwin/Kartik
        val bsMonthIndex = when (gMonth) {
            0 -> if (gDay < 15) 8 else 9   // Jan -> Poush / Magh
            1 -> if (gDay < 13) 9 else 10  // Feb -> Magh / Falgun
            2 -> if (gDay < 15) 10 else 11 // Mar -> Falgun / Chaitra
            3 -> if (gDay < 14) 11 else 0  // Apr -> Chaitra / Baishakh
            4 -> if (gDay < 15) 0 else 1   // May -> Baishakh / Jestha
            5 -> if (gDay < 15) 1 else 2   // Jun -> Jestha / Ashadh
            6 -> if (gDay < 16) 2 else 3   // Jul -> Ashadh / Shrawan
            7 -> if (gDay < 17) 3 else 4   // Aug -> Shrawan / Bhadra
            8 -> if (gDay < 17) 4 else 5   // Sep -> Bhadra / Ashwin
            9 -> if (gDay < 17) 5 else 6   // Oct -> Ashwin / Kartik
            10 -> if (gDay < 16) 6 else 7  // Nov -> Kartik / Mangsir
            11 -> if (gDay < 16) 7 else 8  // Dec -> Mangsir / Poush
            else -> 0
        }

        // Approximate day in BS month
        val bsDay = when {
            gDay in 1..16 -> gDay + 14
            else -> gDay - 16
        }.coerceIn(1, 32)

        val bsMonthName = nepaliMonthsBs[bsMonthIndex]
        val bsDayDevanagari = toDevanagariNumerals(bsDay)
        val bsYearDevanagari = toDevanagariNumerals(bsYear)

        return "$dayName, $bsDayDevanagari $bsMonthName $bsYearDevanagari"
    }

    fun getCurrentTimeFormatted(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val formattedTime = sdf.format(Date())
        return toDevanagariNumerals(formattedTime)
    }

    fun getCurrentGregorianDateFormatted(): String {
        val sdf = SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH)
        return sdf.format(Date())
    }
}
