// RCTAlphaVideoModule.java
package com.alphavideo

import android.net.http.HttpResponseCache
import com.facebook.react.bridge.*
import com.alphavideo.util.CacheUtil
import java.io.File

class RCTAlphaVideoModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    override fun getName(): String {
        return "RCTAlphaVideoModule"
    }

    /**
     * 预下载
     */
    @ReactMethod
    fun advanceDownload(urls: ReadableArray?) {
        if (urls != null && urls.size() > 0) {
            if (!CacheUtil.isDirExists()) {
                CacheUtil.init(reactContext)
            }
            urls?.toArrayList()?.forEach { it ->
//                println("预缓存alpha video url : $it")
                if (it.toString().startsWith("http")) {
                    AlphaVideoParser.playVideoFromUrl(it.toString(), false) {
                        println("$it  缓存成功")
                    }
                }
            }
        }
    }

    @ReactMethod
    fun pause() {
        RCTAlphaVideoManager.videoView.getMxVideoView().pause()
    }

    @ReactMethod
    fun play() {
        RCTAlphaVideoManager.videoView.getMxVideoView().start()
    }

    @ReactMethod
    fun stop() {
        RCTAlphaVideoManager.videoView.getMxVideoView().stop()
    }

    @ReactMethod
    fun clear() {
        RCTAlphaVideoManager.videoView.getMxVideoView().release()
//        RCTAlphaVideoManager.videoView.closeView()
    }

    init {
        val cacheDir = File(reactContext.cacheDir, "http")
        HttpResponseCache.install(cacheDir, 1024 * 1024 * 128)
        CacheUtil.init(reactContext)
    }
}
