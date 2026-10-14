package com.example.data.model

data class NepseMarket(
    val index: String = "२,५९१.८०",
    val change: String = "+४.५५",
    val percentChange: String = "+०.१८%",
    val isPositive: Boolean = true,
    val turnover: String = "रु. ४.८३ अर्ब",
    val totalTransactions: String = "६१,२४०",
    val status: String = "नेप्से परिसूचक",
    val isMarketOpen: Boolean = false,
    val date: String = "नेपाल स्टक एक्सचेन्ज"
)

data class GoldMarket(
    val fineGoldPerTola: String = "२,९२,२००",
    val tejabiGoldPerTola: String = "२,९१,५००",
    val silverPerTola: String = "४,३५५",
    val unit: String = "प्रति तोला (रु)",
    val changeText: String = "-२,२०० (नेपाल सुनचाँदी व्यवसायी महासंघ)",
    val isIncreased: Boolean = false,
    val date: String = "आजको बजार दर"
)

data class ForexItem(
    val currencyCode: String,
    val currencyNameNepali: String,
    val unit: Int,
    val buyRate: String,
    val sellRate: String,
    val flag: String
)

data class FinancialMarketsState(
    val nepse: NepseMarket = NepseMarket(),
    val gold: GoldMarket = GoldMarket(),
    val forexList: List<ForexItem> = defaultForexRates(),
    val lastUpdated: String = "नेपाल राष्ट्र बैंक तथा नेप्से"
)

fun defaultForexRates(): List<ForexItem> {
    return listOf(
        ForexItem(
            currencyCode = "USD",
            currencyNameNepali = "अमेरिकी डलर",
            unit = 1,
            buyRate = "१५४.४८",
            sellRate = "१५५.०८",
            flag = "🇺🇸"
        ),
        ForexItem(
            currencyCode = "EUR",
            currencyNameNepali = "युरोपेली युरो",
            unit = 1,
            buyRate = "१७३.३२",
            sellRate = "१७३.९९",
            flag = "🇪🇺"
        ),
        ForexItem(
            currencyCode = "GBP",
            currencyNameNepali = "युके पाउन्ड स्टर्लिङ",
            unit = 1,
            buyRate = "२०४.५५",
            sellRate = "२०५.३५",
            flag = "🇬🇧"
        ),
        ForexItem(
            currencyCode = "AUD",
            currencyNameNepali = "अस्ट्रेलियन डलर",
            unit = 1,
            buyRate = "१०७.८३",
            sellRate = "१०८.२५",
            flag = "🇦🇺"
        ),
        ForexItem(
            currencyCode = "QAR",
            currencyNameNepali = "कतारी रियाल",
            unit = 1,
            buyRate = "४२.३८",
            sellRate = "४२.५४",
            flag = "🇶🇦"
        ),
        ForexItem(
            currencyCode = "SAR",
            currencyNameNepali = "साउदी रियाल",
            unit = 1,
            buyRate = "४१.१४",
            sellRate = "४१.३०",
            flag = "🇸🇦"
        ),
        ForexItem(
            currencyCode = "AED",
            currencyNameNepali = "यूएई दिर्हाम",
            unit = 1,
            buyRate = "४२.०६",
            sellRate = "४२.२२",
            flag = "🇦🇪"
        ),
        ForexItem(
            currencyCode = "INR",
            currencyNameNepali = "भारतीय रुपैयाँ (सय)",
            unit = 100,
            buyRate = "१६०.००",
            sellRate = "१६०.१५",
            flag = "🇮🇳"
        ),
        ForexItem(
            currencyCode = "JPY",
            currencyNameNepali = "जापानी येन (१०)",
            unit = 10,
            buyRate = "९.७६",
            sellRate = "९.८०",
            flag = "🇯🇵"
        )
    )
}
