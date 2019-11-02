package me.hacket.cutoutscreen.utils

import androidx.annotation.Nullable
import java.lang.reflect.Method

internal object SystemProperties {

    private val getStringProperty = getMethod(getClass("android.os.SystemProperties"))

    @Nullable
    private fun getClass(name: String): Class<*>? {
        return try {
            Class.forName(name) ?: throw ClassNotFoundException()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            try {
                ClassLoader.getSystemClassLoader().loadClass(name)
            } catch (e1: ClassNotFoundException) {
                e1.printStackTrace()
                null
            }
        }
    }

    private fun getMethod(clz: Class<*>?): Method? {
        return try {
            clz?.getMethod("get", String::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @Nullable
    @JvmStatic
    operator fun get(key: String): String? {
        return try {
            val value = getStringProperty?.invoke(null, key) as String?
            value?.trim()
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            null
        }
    }

    @Nullable
    @JvmStatic
    operator fun get(key: String, defaultValue: String?): String? {
        return try {
            val value = getStringProperty?.invoke(null, key) as String?
            if (value?.trim().isNullOrEmpty()) {
                defaultValue
            } else {
                value?.trim()
            }
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            defaultValue
        }
    }
}
