package me.hacket.cutoutscreen.utils

import android.content.Context
import android.os.Build
import android.text.TextUtils
import java.util.*

object DevicesUtils {

    private var statusBarHeight = -1
    private var widthPixels = -1
    private var heightPixels = -1
    private var hashAndroidId = ""

    private fun getSystemProperty(propName: String): String? {
        return SystemProperties[propName, null]
    }

    @JvmStatic
    fun isHuawei(): Boolean {
        val manufacturer = Build.MANUFACTURER
        return !manufacturer.isNullOrBlank() &&
            manufacturer.contains("HUAWEI", true)
    }

    @JvmStatic
    fun isMiuiRom(): Boolean {
        return !getSystemProperty("ro.miui.ui.version.name").isNullOrBlank()
    }

    @JvmStatic
    fun isOppoRom(): Boolean {
        val a = getSystemProperty("ro.product.brand")
        return !a.isNullOrBlank() &&
            a.contains("oppo", true)
    }

    @JvmStatic
    fun isVivoRom(): Boolean {
        val a = getSystemProperty("ro.vivo.os.name")
        return !a.isNullOrBlank() &&
            a.contains("funtouch", true)
    }

    @JvmStatic
    fun getScreenWidth(context: Context): Int {
        if (widthPixels != -1) {
            return widthPixels
        }
        widthPixels = context.resources.displayMetrics.widthPixels
        return widthPixels
    }

    @JvmStatic
    fun getScreenHeight(context: Context): Int {
        if (heightPixels != -1) {
            return heightPixels
        }
        heightPixels = context.resources.displayMetrics.heightPixels
        return heightPixels
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return Int
     */
    @JvmStatic
    fun getStatusBarHeight(context: Context): Int {
        if (statusBarHeight != -1) {
            return statusBarHeight
        }
        if (statusBarHeight <= 0) {
            val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resId > 0) {
                statusBarHeight = context.resources.getDimensionPixelSize(resId)
            }
        }
        return statusBarHeight
    }

    /**
     * MD5(androidId)
     */
    @JvmStatic
    fun getUUID(): String {
        return hashAndroidId
    }

    @JvmStatic
    fun getDeviceInfo(): String {
        return try {
            val model = Build.MODEL.trim { it <= ' ' }
            var device = deviceName(Build.MANUFACTURER.trim { it <= ' ' }, model)
            if (TextUtils.isEmpty(device)) {
                device = deviceName(Build.BRAND.trim { it <= ' ' }, model)
            }

            ((device ?: "") + "/" + model + "|" + Build.VERSION.RELEASE).toUpperCase()
        } catch (t: Throwable) {
            "unknown|unknown"
        }
    }

    @JvmStatic
    private fun deviceName(manufacturer: String, model: String): String? {
        val str = manufacturer.toLowerCase(Locale.getDefault())
        return if (str.startsWith("unknown") || str.startsWith("alps") ||
            str.startsWith("android") || str.startsWith("sprd") ||
            str.startsWith("spreadtrum") || str.startsWith("rockchip") ||
            str.startsWith("wondermedia") || str.startsWith("mtk") ||
            str.startsWith("mt65") || str.startsWith("nvidia") ||
            str.startsWith("brcm") || str.startsWith("marvell") ||
            model.toLowerCase(Locale.getDefault()).contains(str)
        ) {
            null
        } else str
    }

    @JvmStatic
    fun strip(s: String): String {
        val b = StringBuilder()
        var i = 0
        val length = s.length
        while (i < length) {
            val c = s[i]
            if (c in '\u001f'..'\u007f') {
                b.append(c)
            }
            i++
        }
        return b.toString()
    }
}
