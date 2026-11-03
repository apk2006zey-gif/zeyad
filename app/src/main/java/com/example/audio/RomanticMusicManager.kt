package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

data class MusicTrack(
    val id: Int,
    val title: String,
    val subtitle: String,
    val chordNotes: List<DoubleArray>
)

class RomanticMusicManager {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrack = MutableStateFlow(0)
    val currentTrack: StateFlow<Int> = _currentTrack.asStateFlow()

    private var audioTrack: AudioTrack? = null
    private var playerScope: CoroutineScope? = null
    private var playbackJob: Job? = null

    // Predefined soothing romantic piano frequencies (Hz)
    // C4=261.63, D4=293.66, E4=329.63, F4=349.23, G4=392.00, A4=440.00, B4=493.88
    // C5=523.25, D5=587.33, E5=659.25, G5=783.99, A5=880.00
    private val tracks = listOf(
        MusicTrack(
            id = 0,
            title = "لحن العشق الهادئ",
            subtitle = "بيانو رومانسي دافئ",
            chordNotes = listOf(
                doubleArrayOf(261.63, 329.63, 392.00, 523.25), // C maj
                doubleArrayOf(220.00, 261.63, 329.63, 440.00), // A min
                doubleArrayOf(174.61, 220.00, 261.63, 349.23), // F maj
                doubleArrayOf(196.00, 246.94, 293.66, 392.00)  // G maj
            )
        ),
        MusicTrack(
            id = 1,
            title = "همسات تحت ضوء القمر",
            subtitle = "ألحان وترية عذبة",
            chordNotes = listOf(
                doubleArrayOf(329.63, 392.00, 493.88, 659.25), // E min
                doubleArrayOf(261.63, 329.63, 392.00, 523.25), // C maj
                doubleArrayOf(196.00, 246.94, 293.66, 392.00), // G maj
                doubleArrayOf(293.66, 369.99, 440.00, 587.33)  // D maj
            )
        ),
        MusicTrack(
            id = 2,
            title = "نبضات قلبين للأبد",
            subtitle = "سيمفونية الذكريات الجميلة",
            chordNotes = listOf(
                doubleArrayOf(220.00, 277.18, 329.63, 440.00), // A maj
                doubleArrayOf(185.00, 220.00, 277.18, 369.99), // F# min
                doubleArrayOf(246.94, 293.66, 369.99, 493.88), // B min
                doubleArrayOf(164.81, 207.65, 246.94, 329.63)  // E maj
            )
        )
    )

    fun getTrackName(index: Int): String {
        return tracks.getOrNull(index)?.title ?: "لحن رومانسي"
    }

    fun startMusic() {
        if (_isPlaying.value) return
        _isPlaying.value = true

        playerScope?.cancel()
        val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        playerScope = scope

        playbackJob = scope.launch {
            val sampleRate = 22050
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = minBuf.coerceAtLeast( sampleRate * 2 )

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack = track

            try {
                track.play()
                val currentTrackData = tracks[_currentTrack.value]

                while (isActive && _isPlaying.value) {
                    for (chord in currentTrackData.chordNotes) {
                        if (!isActive || !_isPlaying.value) break

                        // Synthesize smooth arpeggio notes for this romantic chord
                        val noteDuration = 0.55 // seconds per arpeggiated note
                        val samplesPerNote = (sampleRate * noteDuration).toInt()
                        val shortBuffer = ShortArray(samplesPerNote)

                        for (f in chord) {
                            if (!isActive || !_isPlaying.value) break

                            for (i in 0 until samplesPerNote) {
                                val t = i.toDouble() / sampleRate
                                // Warm acoustic bell/piano envelope with exponential decay and gentle attack
                                val attack = (t / 0.05).coerceAtMost(1.0)
                                val decay = exp(-2.2 * t)
                                val envelope = attack * decay

                                // Fundamental + gentle harmonics (1st, 2nd, 3rd)
                                val fundamental = sin(2.0 * PI * f * t)
                                val harmonic2 = 0.35 * sin(2.0 * PI * (f * 2.0) * t)
                                val harmonic3 = 0.15 * sin(2.0 * PI * (f * 3.0) * t)
                                val sampleVal = (fundamental + harmonic2 + harmonic3) * envelope * 0.45

                                val sampleShort = (sampleVal * 32767).toInt().coerceIn(-32767, 32767).toShort()
                                shortBuffer[i] = sampleShort
                            }

                            track.write(shortBuffer, 0, samplesPerNote)
                            delay(120) // slight overlap between arpeggio notes
                        }

                        // Gentle pause between chord measures
                        delay(200)
                    }
                }
            } catch (e: Exception) {
                // Handled gracefully
            } finally {
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
        }
    }

    fun pauseMusic() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
        } catch (_: Exception) {}
    }

    fun toggleMusic() {
        if (_isPlaying.value) {
            pauseMusic()
        } else {
            startMusic()
        }
    }

    fun nextTrack() {
        pauseMusic()
        _currentTrack.value = (_currentTrack.value + 1) % tracks.size
        startMusic()
    }

    fun release() {
        pauseMusic()
        playerScope?.cancel()
        playerScope = null
        try {
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }
}
