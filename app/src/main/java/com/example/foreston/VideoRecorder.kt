package com.example.foreston

import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import com.google.ar.sceneform.SceneView
import java.io.File
import java.io.IOException
import android.content.res.Configuration
import android.util.Size
import android.view.Surface


class VideoRecorder {
    // recordingVideoFlag is true when the media recorder is capturing video.
    var isRecording = false

    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var videoSize: Size
    private lateinit var sceneView: SceneView
    private var videoCodec = 0
    private lateinit var videoDirectory: File
    private lateinit var videoBaseName: String
    lateinit var videoPath: File

    private var bitRate = DEFAULT_BITRATE
    private var frameRate = DEFAULT_FRAMERATE
    private var encoderSurface: Surface? = null

    fun setBitRate(bitRate: Int) {
        this.bitRate = bitRate
    }

    fun setFrameRate(frameRate: Int) {
        this.frameRate = frameRate
    }

    fun setSceneView(sceneView: SceneView?) {
        this.sceneView = sceneView!!
    }

    /**
     * Toggles the state of video recording.
     *
     * @return true if recording is now active.
     */
    fun onToggleRecord(): Boolean {
        if (isRecording) {
            stopRecordingVideo()
        } else {
            startRecordingVideo()
        }
        return isRecording
    }

    private fun startRecordingVideo() {
            mediaRecorder = MediaRecorder()
        try {
            buildFilename()
            setUpMediaRecorder()
        } catch (e: IOException) {
            Log.e(TAG, "Exception setting up recorder", e)
            return
        }

        // Set up Surface for the MediaRecorder
        encoderSurface = mediaRecorder!!.surface

        sceneView!!.startMirroringToSurface(
            encoderSurface, 0, 0, videoSize!!.width, videoSize!!.height
        )
        isRecording = true
    }

    private fun buildFilename() {
        videoDirectory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/ForestOn" )
        videoBaseName = "Sample"

        videoPath = File(
            videoDirectory,
            videoBaseName + java.lang.Long.toHexString(System.currentTimeMillis()) + ".mp4"
        )
        val dir = videoPath!!.parentFile
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    private fun stopRecordingVideo() {
        // UI
        isRecording = false
        if (encoderSurface != null) {
            sceneView!!.stopMirroringToSurface(encoderSurface)
            encoderSurface = null
        }
        // Stop recording
        mediaRecorder!!.stop()
        mediaRecorder!!.reset()
    }

    @Throws(IOException::class)
    private fun setUpMediaRecorder() {
        mediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder!!.setOutputFile(videoPath!!.absolutePath)
        mediaRecorder!!.setVideoEncodingBitRate(bitRate)
        mediaRecorder!!.setVideoFrameRate(frameRate)
        mediaRecorder!!.setVideoSize(videoSize!!.width, videoSize!!.height)
        mediaRecorder!!.setVideoEncoder(videoCodec)
        mediaRecorder!!.prepare()

        try {
            mediaRecorder!!.start()
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Exception starting capture: " + e.message, e)
        }
    }

    fun setVideoSize(width: Int, height: Int) {
        videoSize = Size(width, height)
    }

    fun setVideoQuality(quality: Int, orientation: Int) {
        var profile: CamcorderProfile? = null
        if (CamcorderProfile.hasProfile(quality)) {
            profile = CamcorderProfile.get(quality)
        }
        if (profile == null) {
            // Select a quality  that is available on this device.
            for (level in FALLBACK_QUALITY_LEVELS) {
                if (CamcorderProfile.hasProfile(level)) {
                    profile = CamcorderProfile.get(level)
                    break
                }
            }
        }
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setVideoSize(profile!!.videoFrameWidth, profile.videoFrameHeight)
        } else {
            setVideoSize(profile!!.videoFrameHeight, profile.videoFrameWidth)
        }
        setVideoCodec(profile.videoCodec)
        setBitRate(profile.videoBitRate)
        setFrameRate(profile.videoFrameRate)
    }

    fun setVideoCodec(videoCodec: Int) {
        this.videoCodec = videoCodec
    }

    companion object {
        private const val TAG = "VideoRecorder"
        private const val DEFAULT_BITRATE = 10000000
        private const val DEFAULT_FRAMERATE = 30
        private val FALLBACK_QUALITY_LEVELS = intArrayOf(
            CamcorderProfile.QUALITY_HIGH,
            CamcorderProfile.QUALITY_2160P,
            CamcorderProfile.QUALITY_1080P,
            CamcorderProfile.QUALITY_720P,
            CamcorderProfile.QUALITY_480P
        )
    }
}