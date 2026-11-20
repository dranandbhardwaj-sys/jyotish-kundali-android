package com.example.domain

import com.example.model.BirthPlace

object CitiesRepository {
    val cities: List<BirthPlace> = listOf(
        BirthPlace("varanasi", "वाराणसी", "Varanasi", "उत्तर प्रदेश", "Uttar Pradesh", 25.3176, 82.9739, 5.5),
        BirthPlace("delhi", "नई दिल्ली", "New Delhi", "दिल्ली", "Delhi", 28.6139, 77.2090, 5.5),
        BirthPlace("mumbai", "मुंबई", "Mumbai", "महाराष्ट्र", "Maharashtra", 19.0760, 72.8777, 5.5),
        BirthPlace("jaipur", "जयपुर", "Jaipur", "राजस्थान", "Rajasthan", 26.9124, 75.7873, 5.5),
        BirthPlace("prayagraj", "प्रयागराज", "Prayagraj", "उत्तर प्रदेश", "Uttar Pradesh", 25.4358, 81.8463, 5.5),
        BirthPlace("ujjain", "उज्जैन", "Ujjain", "मध्य प्रदेश", "Madhya Pradesh", 23.1765, 75.7885, 5.5),
        BirthPlace("ayodhya", "अयोध्या", "Ayodhya", "उत्तर प्रदेश", "Uttar Pradesh", 26.7922, 82.1998, 5.5),
        BirthPlace("haridwar", "हरिद्वार", "Haridwar", "उत्तराखंड", "Uttarakhand", 29.9457, 78.1642, 5.5),
        BirthPlace("bengaluru", "बेंगलुरु", "Bengaluru", "कर्नाटक", "Karnataka", 12.9716, 77.5946, 5.5),
        BirthPlace("hyderabad", "हैदराबाद", "Hyderabad", "तेलंगाना", "Telangana", 17.3850, 78.4867, 5.5),
        BirthPlace("chennai", "चेन्नई", "Chennai", "तमिलनाडु", "Tamil Nadu", 13.0827, 80.2707, 5.5),
        BirthPlace("kolkata", "कोलकाता", "Kolkata", "पश्चिम बंगाल", "West Bengal", 22.5726, 88.3639, 5.5),
        BirthPlace("pune", "पुणे", "Pune", "महाराष्ट्र", "Maharashtra", 18.5204, 73.8567, 5.5),
        BirthPlace("ahmedabad", "अहमदाबाद", "Ahmedabad", "गुजरात", "Gujarat", 23.0225, 72.5714, 5.5),
        BirthPlace("lucknow", "लखनऊ", "Lucknow", "उत्तर प्रदेश", "Uttar Pradesh", 26.8467, 80.9462, 5.5),
        BirthPlace("patna", "पटना", "Patna", "बिहार", "Bihar", 25.5941, 85.1376, 5.5),
        BirthPlace("bhopal", "भोपाल", "Bhopal", "मध्य प्रदेश", "Madhya Pradesh", 23.2599, 77.4126, 5.5),
        BirthPlace("indore", "इंदौर", "Indore", "मध्य प्रदेश", "Madhya Pradesh", 22.7196, 75.8577, 5.5),
        BirthPlace("chandigarh", "चंडीगढ़", "Chandigarh", "पंजाब/हरियाणा", "Punjab", 30.7333, 76.7794, 5.5),
        BirthPlace("amritsar", "अमृतसर", "Amritsar", "पंजाब", "Punjab", 31.6340, 74.8723, 5.5),
        BirthPlace("rishikesh", "ऋषिकेश", "Rishikesh", "उत्तराखंड", "Uttarakhand", 30.0869, 78.2676, 5.5),
        BirthPlace("mathura", "मथुरा", "Mathura", "उत्तर प्रदेश", "Uttar Pradesh", 27.4924, 77.6737, 5.5),
        BirthPlace("vrindavan", "वृंदावन", "Vrindavan", "उत्तर प्रदेश", "Uttar Pradesh", 27.5806, 77.7006, 5.5),
        BirthPlace("surat", "सूरत", "Surat", "गुजरात", "Gujarat", 21.1702, 72.8311, 5.5),
        BirthPlace("vadodara", "वडोदरा", "Vadodara", "गुजरात", "Gujarat", 22.3072, 73.1812, 5.5),
        BirthPlace("rajkot", "राजकोट", "Rajkot", "गुजरात", "Gujarat", 22.3039, 70.8022, 5.5),
        BirthPlace("nagpur", "नागपुर", "Nagpur", "महाराष्ट्र", "Maharashtra", 21.1458, 79.0882, 5.5),
        BirthPlace("nashik", "नासिक", "Nashik", "महाराष्ट्र", "Maharashtra", 19.9975, 73.7898, 5.5),
        BirthPlace("guwahati", "गुवाहाटी", "Guwahati", "असम", "Assam", 26.1445, 91.7362, 5.5),
        BirthPlace("bhubaneswar", "भुवनेश्वर", "Bhubaneswar", "ओडिशा", "Odisha", 20.2961, 85.8245, 5.5),
        BirthPlace("puri", "पुरी", "Puri", "ओडिशा", "Odisha", 19.8135, 85.8312, 5.5),
        BirthPlace("ranchi", "रांची", "Ranchi", "झारखंड", "Jharkhand", 23.3441, 85.3096, 5.5),
        BirthPlace("raipur", "रायपुर", "Raipur", "छत्तीसगढ़", "Chhattisgarh", 21.2514, 81.6296, 5.5),
        BirthPlace("dehradun", "देहरादून", "Dehradun", "उत्तराखंड", "Uttarakhand", 30.3165, 78.0322, 5.5),
        BirthPlace("jammu", "जम्मू", "Jammu", "जम्मू और कश्मीर", "Jammu & Kashmir", 32.7266, 74.8570, 5.5),
        BirthPlace("srinagar", "श्रीनगर", "Srinagar", "जम्मू और कश्मीर", "Jammu & Kashmir", 34.0837, 74.7973, 5.5),
        BirthPlace("shimla", "शिमला", "Shimla", "हिमाचल प्रदेश", "Himachal Pradesh", 31.1048, 77.1734, 5.5),
        BirthPlace("kochi", "कोच्चि", "Kochi", "केरल", "Kerala", 9.9312, 76.2673, 5.5),
        BirthPlace("trivandrum", "तिरुवनंतपुरम", "Thiruvananthapuram", "केरल", "Kerala", 8.5241, 76.9366, 5.5),
        BirthPlace("madurai", "मदुरै", "Madurai", "तमिलनाडु", "Tamil Nadu", 9.9252, 78.1198, 5.5),
        BirthPlace("visakhapatnam", "विशाखापत्तनम", "Visakhapatnam", "आंध्र प्रदेश", "Andhra Pradesh", 17.6868, 83.2185, 5.5),
        BirthPlace("tirupati", "तिरुपति", "Tirupati", "आंध्र प्रदेश", "Andhra Pradesh", 13.6288, 79.4192, 5.5),
        BirthPlace("mysuru", "मैसूर", "Mysuru", "कर्नाटक", "Karnataka", 12.2958, 76.6394, 5.5),
        BirthPlace("udaipur", "उदयपुर", "Udaipur", "राजस्थान", "Rajasthan", 24.5854, 73.7125, 5.5),
        BirthPlace("jodhpur", "जोधपुर", "Jodhpur", "राजस्थान", "Rajasthan", 26.2389, 73.0243, 5.5),
        BirthPlace("kota", "कोटा", "Kota", "राजस्थान", "Rajasthan", 25.2138, 75.8648, 5.5),
        BirthPlace("agra", "आगरा", "Agra", "उत्तर प्रदेश", "Uttar Pradesh", 27.1767, 78.0081, 5.5),
        BirthPlace("kanpur", "कानपुर", "Kanpur", "उत्तर प्रदेश", "Uttar Pradesh", 26.4499, 80.3319, 5.5),
        BirthPlace("gorakhpur", "गोरखपुर", "Gorakhpur", "उत्तर प्रदेश", "Uttar Pradesh", 26.7606, 83.3732, 5.5),
        BirthPlace("gwalior", "ग्वालियर", "Gwalior", "मध्य प्रदेश", "Madhya Pradesh", 26.2183, 78.1828, 5.5),
        BirthPlace("jabalpur", "जबलपुर", "Jabalpur", "मध्य प्रदेश", "Madhya Pradesh", 23.1815, 79.9864, 5.5),
        BirthPlace("ludhiana", "लुधियाना", "Ludhiana", "पंजाब", "Punjab", 30.9010, 75.8573, 5.5),
        BirthPlace("kathmandu", "काठमांडू", "Kathmandu", "नेपाल", "Nepal", 27.7172, 85.3240, 5.75),
        BirthPlace("dubai", "दुबई", "Dubai", "यूएई", "UAE", 25.2048, 55.2708, 4.0),
        BirthPlace("london", "लंदन", "London", "यूके", "UK", 51.5072, -0.1276, 0.0),
        BirthPlace("newyork", "न्यूयॉर्क", "New York", "अमेरिका", "USA", 40.7128, -74.0060, -5.0),
        BirthPlace("sanfrancisco", "सैन फ्रांसिस्को", "San Francisco", "अमेरिका", "USA", 37.7749, -122.4194, -8.0),
        BirthPlace("singapore", "सिंगापुर", "Singapore", "सिंगापुर", "Singapore", 1.3521, 103.8198, 8.0),
        BirthPlace("toronto", "टोरंटो", "Toronto", "कनाडा", "Canada", 43.6532, -79.3832, -5.0),
        BirthPlace("tokyo", "टोक्यो", "Tokyo", "जापान", "Japan", 35.6762, 139.6503, 9.0),
        BirthPlace("sydney", "सिडनी", "Sydney", "ऑस्ट्रेलिया", "Australia", -33.8688, 151.2093, 10.0)
    )

    val defaultCity: BirthPlace = cities.first() // Varanasi
    val delhi: BirthPlace = cities[1]

    fun searchOffline(query: String): List<BirthPlace> {
        val q = query.trim()
        if (q.isBlank()) return cities
        return cities.filter {
            it.nameHi.contains(q, ignoreCase = true) ||
                it.nameEn.contains(q, ignoreCase = true) ||
                it.stateHi.contains(q, ignoreCase = true) ||
                it.stateEn.contains(q, ignoreCase = true)
        }
    }
}
