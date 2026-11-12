import android.os.SystemClock
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class PrecisionTimer(
    private val scope: CoroutineScope // 在 Android 中通常传入 lifecycleScope 或 viewModelScope
) {
    private var timerJob: Job? = null
    private var startTimeMillis: Long = 0L
    private var accumulatedMillis: Long = 0L

    // 对外暴露格式化后的时间状态流（UI 层直接 collect 即可刷新界面）
    private val _formattedTime = MutableStateFlow("00:00.00")
    val formattedTime: StateFlow<String> = _formattedTime.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    /**
     * 开始或继续计时
     */
    fun start() {
        if (_isRunning.value) return
        _isRunning.value = true
        startTimeMillis = SystemClock.elapsedRealtime()

        timerJob = scope.launch(Dispatchers.Default) {
            while (isActive) {
                val currentElapsed = accumulatedMillis + (SystemClock.elapsedRealtime() - startTimeMillis)
                _formattedTime.value = formatMillis(currentElapsed)
                delay(16L) // 约 60fps 刷新频率，丝滑且省电
            }
        }
    }

    /**
     * 暂停计时
     */
    fun pause() {
        if (!_isRunning.value) return
        _isRunning.value = false
        timerJob?.cancel()
        accumulatedMillis += SystemClock.elapsedRealtime() - startTimeMillis
    }

    /**
     * 重置计时器
     */
    fun reset() {
        _isRunning.value = false
        timerJob?.cancel()
        accumulatedMillis = 0L
        startTimeMillis = 0L
        _formattedTime.value = "00:00.00"
    }

    /**
     * 将毫秒格式化为 MM:SS.ms (分:秒.百分之一秒)
     */
    private fun formatMillis(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val centiseconds = (millis % 1000) / 10
        return String.format(Locale.US, "%02d:%02d.%02d", minutes, seconds, centiseconds)
    }
}
