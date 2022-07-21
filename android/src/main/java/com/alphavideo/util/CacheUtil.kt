package com.alphavideo.util

import android.content.Context
import com.facebook.react.modules.network.OkHttpClientProvider
import okhttp3.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.*
import java.util.zip.Inflater

object CacheUtil {
    lateinit var cacheDir: String

    class FileDownloader {
        // 下载
        fun download(
            cacheKey: String,
            url: String,
            complete: (file: File) -> Unit,
            failure: (e: Exception) -> Unit
        ) {
            val okHttpClient: OkHttpClient = OkHttpClientProvider.getOkHttpClient()
            //创建Request对象
            val request: Request = Request.Builder().url(url).build()
            //把Request对象封装成call对象
            val call: Call = okHttpClient.newCall(request)
            //发起异步请求
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    failure(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val cacheFile: File = findFileByCacheKey(cacheKey)
                    try {
                        cacheFile.takeIf { !it.exists() }?.createNewFile()
                        val inputStream: InputStream? = response.body?.byteStream()
                        inputStream?.let { inputStream ->
                            val outputStream = FileOutputStream(cacheFile)
                            var len: Int
                            val bytes = ByteArray(1024 * 10)
                            while (inputStream.read(bytes).also { len = it } != -1) {
                                outputStream.write(bytes, 0, len)
                            }
                            inputStream.close()
                            outputStream.close()
                            complete(cacheFile)
                        }
                    } catch (e: IOException) {
                        cacheFile.delete()
                        failure(e)
                    }
                }
            })
        }
    }

    var fileDownloader = FileDownloader()

    fun init(context: Context) {
        cacheDir = "${context.externalCacheDir!!.absolutePath}/alpha_video/"
        File(cacheDir).takeIf { !it.exists() }?.mkdirs()
    }

    fun isDirExists(): Boolean {
        return File(cacheDir).exists()
    }

    private fun removeParam(url: String, vararg name: String): String {
        var url = url
        for (s in name) {
            var reg: String = "&?$s=[^&]*"
            // 使用replaceAll正则替换,replace不支持正则
            url = url.replace(reg.toRegex(), "")
        }
        return url
    }


    private fun removeParams(str: String): String {
        var key: String = str
        if (str.contains("Expires") || str.contains("OSSAccessKeyId") || str.contains("Signature") || str.contains("security\\-token")) {
            key = removeParam(str, "Expires", "OSSAccessKeyId", "Signature", "security\\-token")
            println("key : $key")
        }
        return key
    }


    fun getCacheKey(string: String): String {
        val key = removeParams(string)
        val messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.update(key.toByteArray(charset("UTF-8")))
        val digest = messageDigest.digest()
        var str = ""
        for (b in digest) {
            str += String.format("%02x", b)
        }
        return str
    }

    fun isCached(cacheKey: String): Boolean {
        return findFileByCacheKey(cacheKey).exists()
    }

    fun findFileByCacheKey(cacheKey: String): File {
        return File("$cacheDir$cacheKey.mp4")
    }

    // 下载mp4到cache
    fun downloadVideoFromUrl(cacheKey: String, url: String, callback: (path: String) -> Unit) {
        // 解析地址
        fileDownloader.download(cacheKey, url, { file ->
            callback(file.absolutePath)
        }, {
            println("缓存失败 = $it")
        })
    }

}
