/*
 * Copyright 2023 Stream.IO, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.webrtc.sample.compose.webrtc.audio

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager

internal class AudioFocusRequestWrapper {

//@SuppressLint 是 Android 开发中常用的一个注解（Annotation），它用于告诉 Lint 工具忽略某些特定的代码警告。
//Lint 是 Android Studio 提供的一个静态代码分析工具，用于在编译时检测代码中可能存在的潜在问题，如性能问题、可用性问题、版本兼容性问题等。

//有时候，开发者可能会故意写出一些 Lint 工具会发出警告的代码，因为他们确信这段代码在特定情况下是安全的或必要的。
//在这种情况下，使用 @SuppressLint 注解可以避免 Lint 工具发出不必要的警告。
  @SuppressLint("NewApi")
  fun buildRequest(audioFocusChangeListener: AudioManager.OnAudioFocusChangeListener): AudioFocusRequest {
    val playbackAttributes = AudioAttributes.Builder()
      .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
      .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
      .build()
    return AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
      .setAudioAttributes(playbackAttributes)
      .setAcceptsDelayedFocusGain(true)
      .setOnAudioFocusChangeListener(audioFocusChangeListener)
      .build()
  }
}
