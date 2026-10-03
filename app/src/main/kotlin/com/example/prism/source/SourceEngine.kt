package com.example.prism.source

import android.content.Context
import org.json.JSONObject
import org.mozilla.javascript.Context as RhinoContext
import org.mozilla.javascript.Function
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * 音源脚本引擎
 * 参考洛雪：脚本里用 globalThis.lx 和应用通信，我们简化成 http.get / http.post
 */
object SourceEngine {

    /**
     * 执行脚本的 search(keyword, page) 方法
     */
    fun executeSearch(scriptPath: String, keyword: String, page: Int = 1): String? {
        return execute(scriptPath) { scope, cx ->
            val fn = scope.get("search", scope) as? Function ?: return@execute null
            fn.call(cx, scope, scope, arrayOf<Any>(keyword, page))?.toString()
        }
    }

    fun executeGetUrl(scriptPath: String, songId: String): String? {
        return execute(scriptPath) { scope, cx ->
            val fn = scope.get("getMusicUrl", scope) as? Function ?: return@execute null
            fn.call(cx, scope, scope, arrayOf<Any>(songId))?.toString()
        }
    }

    fun executeGetLyrics(scriptPath: String, songId: String): String? {
        return execute(scriptPath) { scope, cx ->
            val fn = scope.get("getLyrics", scope) as? Function ?: return@execute null
            fn.call(cx, scope, scope, arrayOf<Any>(songId))?.toString()
        }
    }

    fun executeGetHotList(scriptPath: String): String? {
        return execute(scriptPath) { scope, cx ->
            val fn = scope.get("getHotList", scope) as? Function ?: return@execute null
            fn.call(cx, scope, scope, emptyArray())?.toString()
        }
    }

    /**
     * 读取脚本里定义的 sourceInfo
     */
    fun readMeta(scriptPath: String): SourceMeta? {
        val result = execute(scriptPath) { scope, _ ->
            val info = scope.get("sourceInfo", scope)
            if (info is ScriptableObject) {
                val name = ScriptableObject.getProperty(info, "name")?.toString() ?: ""
                val version = ScriptableObject.getProperty(info, "version")?.toString() ?: ""
                val author = ScriptableObject.getProperty(info, "author")?.toString() ?: ""
                val desc = ScriptableObject.getProperty(info, "description")?.toString() ?: ""
                return@execute "$name|$version|$author|$desc"
            }
            null
        }
        if (result == null) return null
        val parts = result.split("|")
        if (parts.size < 4) return null
        return SourceMeta(parts[0], parts[1], parts[2], parts[3])
    }

    private fun execute(
        scriptPath: String,
        block: (scope: Scriptable, cx: RhinoContext) -> String?
    ): String? {
        return try {
            val file = File(scriptPath)
            if (!file.exists()) return null
            val script = file.readText()

            val cx = RhinoContext.enter()
            try {
                cx.optimizationLevel = -1
                val scope = cx.initStandardObjects()

                // 注入 http / console 工具
                val httpObj = buildHttpObject(cx, scope)
                ScriptableObject.putProperty(scope, "http", httpObj)

                val consoleObj = buildConsoleObject(cx, scope)
                ScriptableObject.putProperty(scope, "console", consoleObj)

                // 注入 base64 / md5（占位，简单场景不需要）
                cx.evaluateString(scope, script, "source", 1, null)

                block(scope, cx)
            } finally {
                RhinoContext.exit()
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun buildHttpObject(cx: RhinoContext, scope: Scriptable): ScriptableObject {
        val obj = cx.newObject(scope) as ScriptableObject

        // http.get(url)
        val getFn: Function = object : org.mozilla.javascript.BaseFunction() {
            override fun call(cx2: RhinoContext, s: Scriptable, thisObj: Scriptable, args: Array<Any>): Any {
                val url = args.getOrNull(0)?.toString() ?: return ""
                return httpGet(url)
            }
        }
        ScriptableObject.putProperty(obj, "get", getFn)

        // http.post(url, body)
        val postFn: Function = object : org.mozilla.javascript.BaseFunction() {
            override fun call(cx2: RhinoContext, s: Scriptable, thisObj: Scriptable, args: Array<Any>): Any {
                val url = args.getOrNull(0)?.toString() ?: return ""
                val body = args.getOrNull(1)?.toString() ?: ""
                return httpPost(url, body)
            }
        }
        ScriptableObject.putProperty(obj, "post", postFn)

        return obj
    }

    private fun buildConsoleObject(cx: RhinoContext, scope: Scriptable): ScriptableObject {
        val obj = cx.newObject(scope) as ScriptableObject
        val logFn: Function = object : org.mozilla.javascript.BaseFunction() {
            override fun call(cx2: RhinoContext, s: Scriptable, thisObj: Scriptable, args: Array<Any>): Any {
                val msg = args.joinToString(" ") { it?.toString() ?: "" }
                android.util.Log.d("PrismSource", msg)
                return ""
            }
        }
        ScriptableObject.putProperty(obj, "log", logFn)
        return obj
    }

    private fun httpGet(url: String): String {
        return try {
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.connectTimeout = 15000
            conn.readTimeout = 15000
            conn.requestMethod = "GET"
            conn.setRequestProperty("User-Agent", "Prism/1.0")
            val stream = if (conn.responseCode in 200..299) conn.inputStream else conn.errorStream
            val text = stream.bufferedReader().use { it.readText() }
            conn.disconnect()
            text
        } catch (_: Exception) { "" }
    }

    private fun httpPost(url: String, body: String): String {
        return try {
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.connectTimeout = 15000
            conn.readTimeout = 15000
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.outputStream.use { it.write(body.toByteArray()) }
            val stream = if (conn.responseCode in 200..299) conn.inputStream else conn.errorStream
            val text = stream.bufferedReader().use { it.readText() }
            conn.disconnect()
            text
        } catch (_: Exception) { "" }
    }
}

data class SourceMeta(
    val name: String,
    val version: String,
    val author: String,
    val description: String
)