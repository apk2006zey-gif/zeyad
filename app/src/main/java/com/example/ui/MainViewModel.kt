package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.audio.RomanticMusicManager
import com.example.data.local.AppDatabase
import com.example.data.local.PreferencesManager
import com.example.data.model.LoveLetter
import com.example.data.model.MemoryItem
import com.example.data.model.StoryMilestone
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppScreen {
    LOCK,
    SETUP,
    HOME,
    GALLERY,
    TIMELINE,
    LETTERS,
    SETTINGS,
    ADMIN
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val prefs = PreferencesManager(application)
    val musicManager = RomanticMusicManager()

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "couple_love_sanctuary.db"
    ).fallbackToDestructiveMigration().build()

    private val memoryDao = db.memoryDao()
    private val milestoneDao = db.milestoneDao()
    private val loveLetterDao = db.loveLetterDao()

    val memories: StateFlow<List<MemoryItem>> = memoryDao.getAllMemories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val milestones: StateFlow<List<StoryMilestone>> = milestoneDao.getAllMilestones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val letters: StateFlow<List<LoveLetter>> = loveLetterDao.getAllLetters()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScreen = MutableStateFlow(AppScreen.LOCK)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    val isPlayingMusic: StateFlow<Boolean> = musicManager.isPlaying
    val currentTrackIndex: StateFlow<Int> = musicManager.currentTrack

    init {
        seedInitialRomanticDataIfNeeded()
    }

    fun seedInitialRomanticDataIfNeeded(force: Boolean = false) {
        viewModelScope.launch {
            if (force || milestoneDao.getCount() == 0) {
                if (force) milestoneDao.clearAllMilestones()
                milestoneDao.insertMilestone(
                    StoryMilestone(
                        title = "أول لقاء بين زياد ومريومه 💖",
                        dateString = "بداية أجمل حكاية",
                        storyText = "التقينا صدفة ولم تكن صدفة عادية، بل كانت بداية لأعظم قصة حب رتبها القدر لقلبينا.",
                        photoUri = null
                    )
                )
                milestoneDao.insertMilestone(
                    StoryMilestone(
                        title = "أول اعتراف بالحب 🌹",
                        dateString = "نبضة لا تُنسى",
                        storyText = "حين باحت العيون بما يخفيه القلب، وقيلت كلمة 'بحبك' لأول مرة فامتلأت الدنيا نوراً وفرحاً.",
                        photoUri = null
                    )
                )
                milestoneDao.insertMilestone(
                    StoryMilestone(
                        title = "وعد البقاء معاً للأبد 💍",
                        dateString = "عهد الوفاء (18/12)",
                        storyText = "وعدتكِ يا مريومه أن أكون لكِ السند والملاذ والأمان، وأن تبقي أنتِ أميرة قلبي دائماً.",
                        photoUri = null
                    )
                )
            }

            if (force || loveLetterDao.getCount() == 0) {
                if (force) loveLetterDao.clearAllLetters()
                loveLetterDao.insertLetter(
                    LoveLetter(
                        senderName = "زياد",
                        recipientName = "مريومه",
                        title = "إلى أميرة قلبي مريومه 💌",
                        letterBody = "حبيبتي مريومه الغالية، صنعت هذا المكان ليكون سراً جميلاً يجمعنا وحدنا، نحفظ فيه كل ضحكة، وكل صورة، وكل كلمة حب بيننا. أنتِ أعظم نعم حياتي.",
                        dateFormatted = "18/12"
                    )
                )
            }
        }
    }

    fun unlockApp() {
        _isUnlocked.value = true
        _currentScreen.value = AppScreen.HOME
        if (prefs.autoPlayMusic) {
            musicManager.startMusic()
        }
    }

    fun lockApp() {
        _isUnlocked.value = false
        _currentScreen.value = AppScreen.LOCK
        musicManager.pauseMusic()
    }

    fun navigateTo(screen: AppScreen) {
        if (!_isUnlocked.value && screen != AppScreen.SETUP && screen != AppScreen.LOCK && screen != AppScreen.ADMIN) {
            _currentScreen.value = AppScreen.LOCK
        } else {
            _currentScreen.value = screen
        }
    }

    fun addMemory(uri: String, type: String, title: String, description: String, date: String, mood: String) {
        viewModelScope.launch {
            memoryDao.insertMemory(
                MemoryItem(
                    uri = uri,
                    mediaType = type,
                    title = title,
                    description = description,
                    dateString = date,
                    moodTag = mood
                )
            )
        }
    }

    fun toggleMemoryFavorite(memory: MemoryItem) {
        viewModelScope.launch {
            memoryDao.updateMemory(memory.copy(isFavorite = !memory.isFavorite))
        }
    }

    fun deleteMemory(memory: MemoryItem) {
        viewModelScope.launch {
            memoryDao.deleteMemory(memory)
        }
    }

    fun clearAllMemories() {
        viewModelScope.launch {
            memoryDao.clearAllMemories()
        }
    }

    fun clearAllMilestones() {
        viewModelScope.launch {
            milestoneDao.clearAllMilestones()
        }
    }

    fun clearAllLetters() {
        viewModelScope.launch {
            loveLetterDao.clearAllLetters()
        }
    }

    fun addMilestone(title: String, date: String, story: String, photo: String?) {
        viewModelScope.launch {
            milestoneDao.insertMilestone(
                StoryMilestone(
                    title = title,
                    dateString = date,
                    storyText = story,
                    photoUri = photo
                )
            )
        }
    }

    fun deleteMilestone(milestone: StoryMilestone) {
        viewModelScope.launch {
            milestoneDao.deleteMilestone(milestone)
        }
    }

    fun addLetter(sender: String, recipient: String, title: String, body: String, date: String) {
        viewModelScope.launch {
            loveLetterDao.insertLetter(
                LoveLetter(
                    senderName = sender,
                    recipientName = recipient,
                    title = title,
                    letterBody = body,
                    dateFormatted = date
                )
            )
        }
    }

    fun deleteLetter(letter: LoveLetter) {
        viewModelScope.launch {
            loveLetterDao.deleteLetter(letter)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicManager.release()
    }
}
