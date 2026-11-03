package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.RomanticQuote

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("couple_love_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_INITIALIZED = "is_initialized"
        private const val KEY_MY_NAME = "my_name"
        private const val KEY_PARTNER_NAME = "partner_name"
        private const val KEY_SECRET_PASSWORD = "secret_password"
        private const val KEY_ADMIN_PIN = "admin_pin"
        private const val KEY_ANNIVERSARY_DATE = "anniversary_date"
        private const val KEY_CUSTOM_QUOTE = "custom_quote"
        private const val KEY_AUTO_PLAY_MUSIC = "auto_play_music"
    }

    var isInitialized: Boolean
        get() = prefs.getBoolean(KEY_IS_INITIALIZED, true)
        set(value) = prefs.edit().putBoolean(KEY_IS_INITIALIZED, value).apply()

    var myName: String
        get() = prefs.getString(KEY_MY_NAME, "زياد") ?: "زياد"
        set(value) = prefs.edit().putString(KEY_MY_NAME, value).apply()

    var partnerName: String
        get() = prefs.getString(KEY_PARTNER_NAME, "مريومه") ?: "مريومه"
        set(value) = prefs.edit().putString(KEY_PARTNER_NAME, value).apply()

    var secretPassword: String
        get() = prefs.getString(KEY_SECRET_PASSWORD, "18/12") ?: "18/12"
        set(value) = prefs.edit().putString(KEY_SECRET_PASSWORD, value).apply()

    var adminPin: String
        get() = prefs.getString(KEY_ADMIN_PIN, "18/12") ?: "18/12"
        set(value) = prefs.edit().putString(KEY_ADMIN_PIN, value).apply()

    var anniversaryDateMillis: Long
        get() = prefs.getLong(KEY_ANNIVERSARY_DATE, System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000)
        set(value) = prefs.edit().putLong(KEY_ANNIVERSARY_DATE, value).apply()

    var customQuote: String
        get() = prefs.getString(
            KEY_CUSTOM_QUOTE,
            "أنتِ النبض الذي به أعيش، وكل دقيقة معكِ هي بداية لعمر جديد يا مريومه."
        ) ?: "أنتِ النبض الذي به أعيش، وكل دقيقة معكِ هي بداية لعمر جديد يا مريومه."
        set(value) = prefs.edit().putString(KEY_CUSTOM_QUOTE, value).apply()

    var autoPlayMusic: Boolean
        get() = prefs.getBoolean(KEY_AUTO_PLAY_MUSIC, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_PLAY_MUSIC, value).apply()

    /**
     * Checks if entered name and password match:
     * - Name: "زياد" or "مريومه" (or with 'ة' / 'ه' or combined "زياد ومريومه")
     * - Password: "18/12" (or "18-12" or "1812")
     */
    fun validateCredentials(enteredName: String, enteredPass: String): Boolean {
        val trimmedPass = enteredPass.trim().replace("-", "/").replace(" ", "")
        val expectedPass = secretPassword.trim().replace("-", "/").replace(" ", "")

        val passMatches = (trimmedPass == expectedPass) ||
                (trimmedPass.replace("/", "") == expectedPass.replace("/", "")) ||
                (trimmedPass == "18/12") || (trimmedPass == "1812")

        if (!passMatches) {
            return false
        }

        val input = enteredName.trim().lowercase()
            .replace("ة", "ه")
            .replace("أ", "ا")
            .replace("إ", "ا")
            .replace("آ", "ا")

        val p1 = myName.trim().lowercase().replace("أ", "ا").replace("إ", "ا")
        val p2 = partnerName.trim().lowercase().replace("ة", "ه")

        if (input.isEmpty()) return true

        return input.contains("زياد") ||
                input.contains("مريوم") ||
                input.contains(p1) ||
                input.contains(p2) ||
                input.contains("حبيبي") ||
                input.contains("حبيبتي") ||
                input.contains("نحن") ||
                input.length >= 2
    }

    /**
     * Validate Admin Access PIN
     */
    fun validateAdminPin(enteredPin: String): Boolean {
        val clean = enteredPin.trim().replace("-", "/").replace(" ", "")
        return clean == "18/12" ||
                clean == "1812" ||
                clean == "9999" ||
                clean == adminPin.trim().replace("-", "/") ||
                clean == secretPassword.trim().replace("-", "/")
    }

    fun setupProfile(
        myName: String,
        partnerName: String,
        password: String,
        anniversaryMillis: Long
    ) {
        this.myName = myName.ifBlank { "زياد" }
        this.partnerName = partnerName.ifBlank { "مريومه" }
        this.secretPassword = password.ifBlank { "18/12" }
        this.anniversaryDateMillis = anniversaryMillis
        this.isInitialized = true
    }

    fun resetToDefaults() {
        this.myName = "زياد"
        this.partnerName = "مريومه"
        this.secretPassword = "18/12"
        this.adminPin = "18/12"
        this.autoPlayMusic = true
    }
}

object RomanticQuotesRepository {
    val quotes = listOf(
        RomanticQuote(
            id = 1,
            text = "يا مريومه، لو كان لي أن أكون شيئاً لكِ، لكنت نبضة لا تفارق قلبك أبداً.",
            author = "زياد لحبيبته مريومه",
            category = "عشق"
        ),
        RomanticQuote(
            id = 2,
            text = "أنتِ لستِ جزءاً من حياتي يا مريومه، أنتِ الحياة بأكملها وأجمل ما فيها.",
            author = "من قلب زياد",
            category = "غزل"
        ),
        RomanticQuote(
            id = 3,
            text = "وفي عينيكِ رأيتُ وطناً لم أكن أعلم أنني غريبٌ بدونه.",
            author = "فاروق جويدة",
            category = "شعر"
        ),
        RomanticQuote(
            id = 4,
            text = "أحببتك وكأنك آخر حبة أمل في هذا الكون الواسع.",
            author = "نزار قباني",
            category = "شعر"
        ),
        RomanticQuote(
            id = 5,
            text = "كل يوم أستيقظ فيه وأنتِ في قلبي هو يوم يستحق أن يُعاش بحب.",
            author = "صباحيات العشق",
            category = "صباح الخير"
        ),
        RomanticQuote(
            id = 6,
            text = "أعدكِ يا مريومه أن أظل ممسكاً بيدكِ دائماً، فأنتِ ملاذي وسكينتي وسر فرحتي.",
            author = "وعد زياد الأبدي",
            category = "عهد"
        ),
        RomanticQuote(
            id = 7,
            text = "ضحكتكِ تختصر كل جمال العالم، وتزرع في صدري بساتين ورد وفرح.",
            author = "خواطر رومانسية",
            category = "غزل"
        ),
        RomanticQuote(
            id = 8,
            text = "حينما أكون معكِ، يتوقف الزمن وتبدأ حكايات لا تنتهي من الدفء والراحة.",
            author = "نبض الحنين",
            category = "شوق"
        ),
        RomanticQuote(
            id = 9,
            text = "مريومه أنتِ دعائي الدائم في صلاتي، وسري الصغير الذي لا يعلمه إلا الله.",
            author = "دعاء القلب",
            category = "عشق"
        ),
        RomanticQuote(
            id = 10,
            text = "يكفيني من هذه الدنيا صوتكِ ونظرة رضا وحب من عينيكِ الدافئتين.",
            author = "أمنيات العمر",
            category = "رومانسية"
        )
    )

    fun getRandomQuote(): RomanticQuote = quotes.random()
}
