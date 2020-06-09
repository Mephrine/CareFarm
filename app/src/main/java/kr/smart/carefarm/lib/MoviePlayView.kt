package kr.smart.carefarm.lib

import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaCodec
import android.media.MediaFormat
import android.os.Build
import android.util.Log
import android.view.SurfaceHolder
import com.loopj.android.http.AsyncHttpClient
import kr.smart.carefarm.config.C
import java.io.IOException
import java.nio.ByteBuffer

class MoviePlayView internal constructor() : Thread() {
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mStatus = 0
    private var mIP: String
    private var mChannel: Int
    private val mNdasId: String
    private val mNdasKey: String
    private val mNdasDiskNo: Int
    private val mNdasMember: String
    private var mStop: Int
    private var mDecoder: MediaCodec? = null
    var mCamHandle: Long = 0
    private var mWidth: Int
    private var mHeight: Int
    private val mMode: Int
    private val mForceSoftwareCodec: Int
    private var flag_get_data = false
    var protocol_stun_enable = 0
    private val mEnableHTTPMessage = 0
    private var mURL: String? = null
    var video_status = 0
    private var temp_mWidth = 0
    private var temp_mHeight = 0

    companion object {
        const val H264_MIME_TYPE = "video/avc"
        const val MPEG4_MIME_TYPE = "video/mp4v-es"
        const val LIVE_MODE = 1
        const val PLAYBACK_MODE = 2
        const val SLEEP_TIME = 100 // 100 ms
        const val RECONNECT_TIME_COUNT = 30
        const val DESTROY_WAIT_TIME_COUNT = 30
        const val WAIT_I_FRAME_TIME_COUNT = 30
        const val WAIT_RECORD_FORCE_TIME_COUNT = 10
        external fun initBasicPlayer(): Int
        external fun openMovie(Ip: String?, _channel: Int, NdasId: String?, NdasKey: String?, _NdasDiskNo: Int, NdasMember: String?, mode: Int, webPort: String?, videoPort: String?, id: String?, pw: String?): Int
        external fun setDisplay(handle: Long): Int
        external fun setDisplay2(handle: Long, _width: Int, _height: Int): Int
        external fun renderFrame(handle: Long, bitmap: Bitmap?, _width: Int, _height: Int): Int
        external fun getFrame(handle: Long, frame: ByteArray?): Int
        external fun LXXSendCommandStun(user_number: String?, userID: String?, userPW: String?, cmd: String?): String?
        external fun LXXSendCommandStun2(server_ip: String?, server_port: String?, user_number: String?): String?
        external fun getMovieWidth(handle: Long): Int
        external fun getMovieHeight(handle: Long): Int
        external fun closeMovie(handle: Long)

        init {
            System.loadLibrary("basicplayer")
        }
    }


    fun setDisplay(sHolder: SurfaceHolder?) {
        mSurfaceHolder = sHolder
        if (initBasicPlayer() < 0) {
        }
    }

    fun setIP(sIp: String) {
        mIP = sIp
    }

    fun setChannel(c: Int) {
        mChannel = c
    }

    override fun run() {
        if (mIP.contains("stun://")) {
            protocol_stun_enable = 1
        }
        var result: Int
        var count = 0
        val tmp_buffer = ByteBuffer.allocate(1920 * 1080 * 3)
        var sample_size: Int
        var inIndex = -1
        var first_loop_flag = true
        var url: String
        val client = AsyncHttpClient()
        client.setTimeout(2000)
        do {
            if (!first_loop_flag) {
                count = 0
                while (count < RECONNECT_TIME_COUNT) {
                    if (mStop == 1) {
                        break
                    }
                    try {
                        println("Time out1")
                        sleep(SLEEP_TIME.toLong())
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                        break
                    }
                    count++
                }
            } else {
                first_loop_flag = false
            }
            if (mStop == 1) {
                break
            }
            println("영상재생시작")
            mStatus = 0
            try {
                sleep(SLEEP_TIME * WAIT_RECORD_FORCE_TIME_COUNT.toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            flag_get_data = true
            var mCanvas: Canvas? = null
            var mBitMap: Bitmap? = null
            println("함수 시작11")
            if (C.iframe_flag == 1 && mMode == LIVE_MODE) {
                if (protocol_stun_enable == 1) {
                    mURL = C.ip + ":9001" + "/channel" + C.channel + "?tcp&ifrmonly"
                } else {
                    url = "ndsp://" + C.ip + ":" + C.videoport + "/channel" + C.channel + "?tcp&ifrmonly"
                }
            } else {
                mURL = C.ip
            }
            println("Test 1")
            println("TTTTTTTTTTTTT == " + mURL + " " + C.channel + " " + C.disk_id + " " + C.disk_key + " " + C.disk_num
                    + " " + C.member + " " + mMode)
            println("Test 2")
            if (mMode == LIVE_MODE) {
                if (C.lowplay_flag == 0) {
                    mCamHandle = openMovie(mURL, C.channel!!.toInt(), mNdasId, mNdasKey, mNdasDiskNo, mNdasMember,
                        mMode, Integer.toString(mMode, C.webport!!.toInt()), C.videoport, C.id, C.pw).toLong()
                    C.disk_id = mNdasId
                    C.disk_key = mNdasKey
                    C.disk_num = mNdasDiskNo
                    C.member = mNdasMember
                }
                mCamHandle = openMovie(mURL, C.channel!!.toInt(), C.disk_id, C.disk_key, C.disk_num,
                    C.member, mMode, Integer.toString(mMode, C.webport!!.toInt()), C.videoport,
                    C.id, C.pw).toLong()
            } else {
                println("mCam.disk_key == " + C.disk_key)
                println("mCam.disk_num == " + C.disk_num)
                println("mCam.member == " + C.member)
                mCamHandle = openMovie(mURL, C.channel!!.toInt(), C.disk_id, C.disk_key, C.disk_num, C.member, mMode,
                    Integer.toString(mMode, C.webport!!.toInt()), C.videoport, C.id, C.pw).toLong()
            }
            println("mCamHandle == $mCamHandle")
            if (mCamHandle != -1L && mCamHandle != 0L) {
                Log.v("BackThread", "android.os.Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT)
                if (mForceSoftwareCodec == 1
                    || Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                } else {
                    println("setdisplay")
                    result = setDisplay(mCamHandle)
                }
            }
            if (mForceSoftwareCodec == 1
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                println("Software Codec")
                var getwidth = 1
                var getHeight = 1
                while (mStop == 0) {
                    if (temp_mWidth != getwidth) {
                        video_status = 1
                        temp_mWidth = getwidth
                        temp_mHeight = getHeight
                        mCanvas = mSurfaceHolder!!.lockCanvas()
                        if (mBitMap != null) {
                            mBitMap.recycle()
                            mBitMap = null
                            System.gc()
                        }
                        mBitMap = Bitmap.createBitmap(mCanvas.width, mCanvas.height, Bitmap.Config.RGB_565)
                        result = setDisplay2(mCamHandle, mCanvas.width, mCanvas.height)
                        getwidth = mCanvas.width
                        getHeight = mCanvas.height
                        mSurfaceHolder!!.unlockCanvasAndPost(mCanvas)
                    }
                    result = renderFrame(mCamHandle, mBitMap, 0, 0)
                    if (result < 0) {
                        Log.v("BackThread", "result = $result")
                        break
                    } else {
                        if (true) {
                            mCanvas = mSurfaceHolder!!.lockCanvas()
                            if (mCanvas != null) {
                                mCanvas.drawBitmap(mBitMap, 0f, 0f, null)
                                getwidth = mCanvas.width
                                getHeight = mCanvas.height
                                mSurfaceHolder!!.unlockCanvasAndPost(mCanvas)
                            }
                        }
                    }
                }
            } else {
                println("Hardware Codec")
                mStatus = 0
                count = 0
                while (true) {
                    tmp_buffer.clear()
                    sample_size = getFrame(mCamHandle, tmp_buffer.array())
                    Log.v("BackThread", "sampleSize = $sample_size")
                    if (sample_size >= 0) {
                        break
                    }
                    try {
                        sleep(SLEEP_TIME.toLong())
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    count++
                    if (count > WAIT_I_FRAME_TIME_COUNT) {
                        println("Can't get I-frame !!!! ")
                        break
                    }
                }
                if (count > WAIT_I_FRAME_TIME_COUNT) {
                    println("WAIT_I_FRAME_TIME_COUNT")
                    mStatus = -2
                }
                mWidth = getMovieWidth(mCamHandle)
                mHeight = getMovieHeight(mCamHandle)
                println("mWidth = $mWidth")
                println("mHeight = $mHeight")
                if (mWidth == 0 || mHeight == 0) // fail
                {
                    println("mWidth == 0")
                    mStatus = -2
                }
                if (mStatus < 0) {
                    if (mCamHandle != 0L) {
                        closeMovie(mCamHandle)
                    }
                    mCamHandle = 0
                    continue
                }
                var format: MediaFormat?
                C.codec = "H264"
                println("mCam.codec ==> " + C.codec.toString())
                println("mCam.member ==> " + C.member.toString())
                if (C.codec!!.compareTo("H264") == 0) {
                    try {
                        mDecoder = MediaCodec.createDecoderByType(H264_MIME_TYPE)
                    } catch (e: IOException) { // TODO Auto-generated catch block
                        e.printStackTrace()
                    }
                    format = MediaFormat.createVideoFormat(H264_MIME_TYPE, mWidth, mHeight)
                    println("createVideoFormat : mWidth = $mWidth mWidth : $mHeight")
                } else if (C.codec!!.compareTo("MPG4") == 0) {
                    try {
                        mDecoder = MediaCodec.createDecoderByType(MPEG4_MIME_TYPE)
                    } catch (e: IOException) { // TODO Auto-generated catch block
                        e.printStackTrace()
                    }
                    format = MediaFormat.createVideoFormat(MPEG4_MIME_TYPE, mWidth, mHeight)
                } else if (C.codec.toString().compareTo("null") == 0) {
                    C.codec = "H264"
                    try {
                        mDecoder = MediaCodec.createDecoderByType(H264_MIME_TYPE)
                    } catch (e: IOException) { // TODO Auto-generated catch block
                        e.printStackTrace()
                    }
                    format = MediaFormat.createVideoFormat(H264_MIME_TYPE, mWidth, mHeight)
                    println("createVideoFormat : mWidth = $mWidth mWidth : $mHeight")
                } else {
                    println("Unsupported Codec Type = " + C.codec + " !!!! ")
                    mStatus = -3
                    continue
                }
                val info = MediaCodec.BufferInfo()
                mDecoder!!.configure(format, mSurfaceHolder!!.surface, null, 0)
                val outputFormat = mDecoder!!.outputFormat
                mDecoder!!.start()
                video_status = 1
                while (mStop == 0) {
                    inIndex = -1
                    inIndex = try {
                        mDecoder!!.dequeueInputBuffer(-1)
                    } catch (e: Exception) {
                        break
                    }
                    if (inIndex >= 0) {
                        var buffer: ByteBuffer
                        buffer = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            mDecoder!!.inputBuffers[inIndex]
                        } else {
                            mDecoder!!.getInputBuffer(inIndex)
                        }
                        buffer.clear()
                        buffer.put(tmp_buffer.array(), 0, sample_size)
                        if (sample_size < 0) {
                            Log.d("DecodeActivity", "InputBuffer BUFFER_FLAG_END_OF_STREAM")
                            mDecoder!!.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        } else {
                            mDecoder!!.queueInputBuffer(inIndex, 0, sample_size, 0, 0)
                        }
                    }
                    var outIndex = -1
                    try {
                        outIndex = mDecoder!!.dequeueOutputBuffer(info, 100)
                    } catch (err: Exception) {
                    }
                    if (outIndex >= 0) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            mDecoder!!.outputBuffers
                        } else {
                            mDecoder!!.getOutputBuffer(outIndex)
                        }
                    }
                    when (outIndex) {
                        MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                            Log.v("DecodeActivity", "INFO_OUTPUT_BUFFERS_CHANGED")
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                mDecoder!!.outputBuffers
                            }
                        }
                        MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                        }
                        MediaCodec.INFO_TRY_AGAIN_LATER -> Log.v("DecodeActivity", "dequeueOutputBuffer timed out!")
                        else -> {
                        }
                    }
                    if (outIndex >= 0) {
                        mDecoder!!.releaseOutputBuffer(outIndex, true)
                    }
                    if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        Log.v("DecodeActivity", "OutputBuffer BUFFER_FLAG_END_OF_STREAM")
                        break
                    }
                    tmp_buffer.clear()
                    sample_size = getFrame(mCamHandle, tmp_buffer.array())
                    Log.v("BackThread", "sampleSize = $sample_size")
                    if (sample_size < 0) {
                        Log.v("BackThread", "sampleSize = $sample_size")
                        break
                    }
                }
                try {
                    mDecoder!!.stop()
                } catch (err: Exception) {
                }
                try {
                    mDecoder!!.release()
                } catch (err: Exception) {
                }
                mDecoder = null
            }
            video_status = 0
            closeMovie(mCamHandle)
            println("Thread end " + C.channel)
            mCamHandle = 0
            if (mStop == 1) {
                break
            }
        } while (mMode == LIVE_MODE)
        mStop = 2
        System.gc()
    }

    fun cancel(bFlag: Boolean) { // TODO Auto-generated method stub
        println("Thread canceled")
        var count: Int
        if (mStop == 2) return
        count = 0
        mStop = 1
        while (mStop == 1) {
            try {
                sleep(SLEEP_TIME.toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
                break
            }
            count++
            if (count > DESTROY_WAIT_TIME_COUNT) break
        }
    }

    init {
        mIP = ""
        mChannel = 1
        mNdasId = ""
        mNdasKey = ""
        mNdasDiskNo = 0
        mNdasMember = ""
        mStop = 0
        mWidth = 0
        mHeight = 0
        mMode = LIVE_MODE
        mForceSoftwareCodec = 0
    }
}