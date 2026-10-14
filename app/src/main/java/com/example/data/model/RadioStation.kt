package com.example.data.model

data class RadioStation(
    val id: String,
    val nameNepali: String,
    val nameEnglish: String,
    val frequency: String,
    val streamUrl: String,
    val location: String,
    val tagline: String,
    val brandColorHex: Long = 0xFFC8102EL
)

fun getDefaultRadioStations(): List<RadioStation> {
    return listOf(
        RadioStation(
            id = "radio_nepal",
            nameNepali = "रेडियो नेपाल",
            nameEnglish = "Radio Nepal",
            frequency = "१०० MHz",
            streamUrl = "https://streaming.softnep.net:10982/stream",
            location = "सिंहदरबार, काठमाडौं (Singhadurbar)",
            tagline = "नेपालको पहिलो र राष्ट्रिय प्रसारण",
            brandColorHex = 0xFFC8102EL
        ),
        RadioStation(
            id = "kantipur_fm",
            nameNepali = "रेडियो कान्तिपुर",
            nameEnglish = "Radio Kantipur",
            frequency = "९६.१ MHz",
            streamUrl = "https://radio-broadcast.ekantipur.com/stream",
            location = "काठमाडौं (Kathmandu)",
            tagline = "राष्ट्रको रेडियो • The Station of the Nation",
            brandColorHex = 0xFF005696L
        ),
        RadioStation(
            id = "ujyaalo_90",
            nameNepali = "उज्यालो ९० नेटवर्क",
            nameEnglish = "Ujyaalo 90 Network",
            frequency = "९०.० MHz",
            streamUrl = "https://streaming.softnep.net:10850/stream",
            location = "काठमाडौं (Kathmandu)",
            tagline = "समाचार, विचार र विश्लेषण",
            brandColorHex = 0xFFD32F2FL
        ),
        RadioStation(
            id = "sagarmatha_fm",
            nameNepali = "रेडियो सगरमाथा",
            nameEnglish = "Radio Sagarmatha",
            frequency = "१०२.४ MHz",
            streamUrl = "https://streaming.softnep.net:10865/stream",
            location = "ललितपुर (Lalitpur)",
            tagline = "दक्षिण एसियाकै पहिलो सामुदायिक रेडियो",
            brandColorHex = 0xFF1976D2L
        ),
        RadioStation(
            id = "bbc_nepali",
            nameNepali = "बीबीसी नेपाली सेवा",
            nameEnglish = "BBC Nepali Service",
            frequency = "१०३.० MHz",
            streamUrl = "https://stream.live.vc.bbcmedia.co.uk/bbc_nepali_radio",
            location = "लन्डन / काठमाडौं",
            tagline = "विश्वसनीय र निष्पक्ष अन्तर्राष्ट्रिय समाचार",
            brandColorHex = 0xFFB71C1CL
        ),
        RadioStation(
            id = "hits_fm",
            nameNepali = "हिट्स एफएम",
            nameEnglish = "Hits FM",
            frequency = "९१.२ MHz",
            streamUrl = "https://usa15.fastcast4u.com/proxy/hitsfm912?mp=/1",
            location = "काठमाडौं (Kathmandu)",
            tagline = "संगीत र युवा पुस्ताको ढुकढुकी",
            brandColorHex = 0xFFE65100L
        ),
        RadioStation(
            id = "kalika_fm",
            nameNepali = "कालिका एफएम",
            nameEnglish = "Kalika FM",
            frequency = "९५.२ MHz",
            streamUrl = "https://streaming.softnep.net:10828/stream",
            location = "चितवन (Chitwan)",
            tagline = "मध्य नेपालको सर्वाधिक लोकप्रिय प्रसारण",
            brandColorHex = 0xFF00796BL
        )
    )
}
