# PrecisionTimer for Android

A lightweight, precise stopwatch component for Android, written in Kotlin.

基于协程 + StateFlow 的高精度安卓秒表组件：单调时钟计时、约 60fps 的 UI 刷新、开始/暂停/重置开箱即用。

## Features

- **Monotonic clock** — 计时基于 `SystemClock.elapsedRealtime()`，不受系统时间被修改的影响
- **StateFlow-driven UI** — 对外只暴露 `StateFlow`，UI 层 `collect` 即可自动刷新，天然契合 MVVM / Jetpack Compose
- **~60fps refresh** — 每 16ms 更新一次显示，流畅且开销低
- **Pause & resume** — 暂停时累计已用时间，继续计时无缝衔接
- **Centisecond precision** — 显示格式 `MM:SS.cc`（分:秒.百分之一秒）

## Usage

```kotlin
// 1. Create it with a coroutine scope (e.g. viewModelScope or lifecycleScope)
val timer = PrecisionTimer(viewModelScope)

// 2. Observe in the UI layer
lifecycleScope.launch {
    timer.formattedTime.collect { text ->
        binding.timerText.text = text
    }
}

// 3. Control it
timer.start()  // start or resume
timer.pause()  // pause
timer.reset()  // back to 00:00.00
```

## Requirements

- Android (uses `android.os.SystemClock`)
- Kotlin + `kotlinx.coroutines`

## Files

- `PrecisionTimer.kt` — the whole component, a single self-contained class
