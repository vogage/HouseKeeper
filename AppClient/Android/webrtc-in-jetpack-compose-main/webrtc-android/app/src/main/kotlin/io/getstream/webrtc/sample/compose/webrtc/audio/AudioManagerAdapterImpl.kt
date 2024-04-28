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
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import io.getstream.log.taggedLogger

internal class AudioManagerAdapterImpl(
  private val context: Context,
  private val audioManager: AudioManager,
  private val audioFocusRequest: AudioFocusRequestWrapper = AudioFocusRequestWrapper(),
  private val audioFocusChangeListener: AudioManager.OnAudioFocusChangeListener
) : AudioManagerAdapter {

  private val logger by taggedLogger("Call:AudioManager")

  private var savedAudioMode = 0
  private var savedIsMicrophoneMuted = false
  private var savedSpeakerphoneEnabled = false
  private var audioRequest: AudioFocusRequest? = null

  init {
    logger.i { "<init> audioFocusChangeListener: $audioFocusChangeListener" }
  }

  override fun hasEarpiece(): Boolean {
    return context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
  }

  // 具体来说，PackageManager.FEATURE_AUDIO_OUTPUT 是一个系统功能的常量，它用于检查设备是否具备音频输出能力。通常，这意味着设备是否具备扬声器或耳机等音频输出设备。

  // context: 是一个 Context 对象，它通常代表了当前应用的环境信息，如资源、类加载器等。
  // packageManager: 是从 context 中获取的 PackageManager 实例，它提供了关于设备上安装的应用包（即应用）的信息。
  // hasSystemFeature(String name): 是 PackageManager 类的一个方法，它用于检查设备是否具备指定的系统功能。
  // 这段代码的执行结果是一个布尔值（true 或 false），表示设备是否支持音频输出。如果设备支持音频输出，则该方法返回 true；否则返回 false。

  // 在编写需要音频输出的应用时，这样的检查是有用的，因为它可以帮助开发者在运行时确定设备是否满足应用的基本需求，从而避免在不支持音频输出的设备上运行应用时出现问题。


  @SuppressLint("NewApi")
  override fun hasSpeakerphone(): Boolean {
    return if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)) {
      val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
      for (device in devices) {
        if (device.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
          return true
        }
      }
      false
    } else {
      true
    }
  }

  @SuppressLint("NewApi")
  override fun setAudioFocus() {
    // Request audio focus before making any device switch.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      audioRequest = audioFocusRequest.buildRequest(audioFocusChangeListener)
      audioRequest?.let {
        val result = audioManager.requestAudioFocus(it)
        logger.i { "[setAudioFocus] #new; completed: ${result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED}" }
      }
    } else {
      val result = audioManager.requestAudioFocus(
        audioFocusChangeListener,
        AudioManager.STREAM_VOICE_CALL,
        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
      )
      logger.i { "[setAudioFocus] #old; completed: ${result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED}" }
    }
    /*
     * Start by setting MODE_IN_COMMUNICATION as default audio mode. It is
     * required to be in this mode when playout and/or recording starts for
     * best possible VoIP performance. Some devices have difficulties with speaker mode
     * if this is not set.
     */
    audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
  }

  override fun enableBluetoothSco(enable: Boolean) {
    logger.i { "[enableBluetoothSco] enable: $enable" }
    audioManager.run { if (enable) startBluetoothSco() else stopBluetoothSco() }
  }

  override fun enableSpeakerphone(enable: Boolean) {
    logger.i { "[enableSpeakerphone] enable: $enable" }
    audioManager.isSpeakerphoneOn = enable
  }

  override fun mute(mute: Boolean) {
    logger.i { "[mute] mute: $mute" }
    audioManager.isMicrophoneMute = mute
  }

  // TODO Consider persisting audio state in the event of process death
  override fun cacheAudioState() {
    logger.i { "[cacheAudioState] no args" }
    savedAudioMode = audioManager.mode
    savedIsMicrophoneMuted = audioManager.isMicrophoneMute
    savedSpeakerphoneEnabled = audioManager.isSpeakerphoneOn
  }

  @SuppressLint("NewApi")
  override fun restoreAudioState() {
    logger.i { "[cacheAudioState] no args" }
    audioManager.mode = savedAudioMode
    mute(savedIsMicrophoneMuted)
    enableSpeakerphone(savedSpeakerphoneEnabled)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      audioRequest?.let {
        logger.d { "[cacheAudioState] abandonAudioFocusRequest: $it" }
        audioManager.abandonAudioFocusRequest(it)
      }
    } else {
      logger.d { "[cacheAudioState] audioFocusChangeListener: $audioFocusChangeListener" }
      audioManager.abandonAudioFocus(audioFocusChangeListener)
    }
  }
}
