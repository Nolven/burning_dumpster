package carcassonne.se.carcassonnecustomclone.zoom

import android.support.annotation.IntDef
import android.util.Log

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Utility class that can log traces and info.
 */
class ZoomLogger private constructor(private val mTag: String) {

    @IntDef(LEVEL_VERBOSE, LEVEL_WARNING, LEVEL_ERROR)
    @Retention(RetentionPolicy.SOURCE)
    internal annotation class LogLevel

    internal fun v(message: String) {
        if (should(LEVEL_VERBOSE)) {
            Log.v(mTag, message)
            lastMessage = message
            lastTag = mTag
        }
    }

    internal fun i(message: String) {
        if (should(LEVEL_INFO)) {
            Log.i(mTag, message)
            lastMessage = message
            lastTag = mTag
        }
    }

    internal fun w(message: String) {
        if (should(LEVEL_WARNING)) {
            Log.w(mTag, message)
            lastMessage = message
            lastTag = mTag
        }
    }

    internal fun e(message: String) {
        if (should(LEVEL_ERROR)) {
            Log.e(mTag, message)
            lastMessage = message
            lastTag = mTag
        }
    }

    private fun should(messageLevel: Int): Boolean {
        return level <= messageLevel
    }

    private fun string(messageLevel: Int, vararg ofData: Any): String {
        var message = ""
        if (should(messageLevel)) {
            for (o in ofData) {
                message += o.toString()
                message += " "
            }
        }
        return message.trim { it <= ' ' }
    }

    internal fun v(vararg data: Any) {
        i(string(LEVEL_VERBOSE, *data))
    }

    internal fun i(vararg data: Any) {
        i(string(LEVEL_INFO, *data))
    }

    internal fun w(vararg data: Any) {
        w(string(LEVEL_WARNING, *data))
    }

    internal fun e(vararg data: Any) {
        e(string(LEVEL_ERROR, *data))
    }

    companion object {

        const val LEVEL_VERBOSE = 0
        val LEVEL_INFO = 1
        const val LEVEL_WARNING = 2
        const val LEVEL_ERROR = 3

        private var level = LEVEL_ERROR

        fun setLogLevel(logLevel: Int) {
            level = logLevel
        }

        internal lateinit var lastMessage: String
        internal lateinit var lastTag: String

        internal fun create(tag: String): ZoomLogger {
            return ZoomLogger(tag)
        }
    }
}

