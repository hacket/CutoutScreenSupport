package me.hacket.cutoutscreen.core

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.provider.Settings
import android.view.Window
import androidx.annotation.RequiresApi
import me.hacket.cutoutscreen.utils.DevicesUtils
import me.hacket.cutoutscreen.utils.SystemProperties
import java.util.*

/**
 * 小米Android O刘海屏适配
 *
 * https://dev.mi.com/console/doc/detail?pId=1293
 */
internal class MiuiCutoutScreenSupportStrategy : AbsCutoutScreenSupportStrategy() {

    override fun isCutoutScreen(window: Window): Boolean {
        return try {
            "1" == SystemProperties["ro.miui.notch"]
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            false
        }
    }

    /**
     * MIUI 针对 Notch 设备，有一个“隐藏屏幕刘海”的设置项（设置-全面屏-隐藏屏幕刘海
     * 具体表现是：系统会强制盖黑状态栏（无视应用的Notch使用声明）
     * 视觉上达到隐藏刘海的效果。但会给某些应用带来适配问题（控件/内容遮挡或过于靠边等）。
     * 因此开发者在适配时，还需要检查开启“隐藏屏幕刘海”后，应用的页面是否显示正常。
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun isHideCutoutScreen(ctx: Context): Boolean {
        return Settings.Global.getInt(ctx.contentResolver, "force_black", 0) == 1
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getCutoutRects(window: Window): List<Rect> {
        val result = ArrayList<Rect>()
        val rect: Rect
        if (!isCutoutScreen(window) || isHideCutoutScreen(window.context)) {
            rect = Rect(0, 0, 0, 0)
            result.add(rect)
            return result
        }
        val ctx = window.context
        rect = Rect()
        rect.top = 0
        rect.bottom = getRealNotchHeight(window)
        rect.left = DevicesUtils.getScreenWidth(ctx) / 2 - getRealNotchWidth(window) / 2
        rect.right = rect.left + getRealNotchWidth(window)
        result.add(rect)
        return result
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getCutoutRect(window: Window): Rect {
        return getCutoutRects(window)[0]
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getCutoutHeight(window: Window): Int {
        return getRealNotchHeight(window)
    }

    override fun setPortraitWindowLayoutCutout(window: Window) {
        super.setPortraitWindowLayoutCutout(window)
        addExtraFlags(window, FLAG_CUTOUT_PORTRAINT)
    }

    override fun clearPortraitWindowLayoutCutout(window: Window) {
        super.clearPortraitWindowLayoutCutout(window)
        clearExtraFlags(window, FLAG_CUTOUT_PORTRAINT)
    }

    override fun setLandScapeWindowLayoutCutout(window: Window) {
        super.setLandScapeWindowLayoutCutout(window)
        addExtraFlags(window, FLAG_CUTOUT_LANDSCAPE)
    }

    override fun clearLandScapeWindowLayoutCutout(window: Window) {
        super.clearLandScapeWindowLayoutCutout(window)
        clearExtraFlags(window, FLAG_CUTOUT_LANDSCAPE)
    }

    override fun setWindowLayoutCutout(window: Window) {
        addExtraFlags(window, FLAG_CUTOUT_ALL)
    }

    override fun clearWindowLayoutCutout(window: Window) {
        clearExtraFlags(window, FLAG_CUTOUT_ALL)
    }

    private fun addExtraFlags(window: Window, flag: Int) {
        try {
            val method = Window::class.java.getMethod("addExtraFlags", Int::class.javaPrimitiveType)
            method.invoke(window, flag)
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
    }

    private fun clearExtraFlags(window: Window, flag: Int) {
        try {
            val method =
                Window::class.java.getMethod("clearExtraFlags", Int::class.javaPrimitiveType)
            method.invoke(window, flag)
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
    }

    /**
     * 获取刘海高度
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun getRealNotchHeight(window: Window): Int {
        val ctx = window.context
        var result = 0
        if (!isCutoutScreen(window)) { // 不是刘海屏
            return result
        }
        if (isHideCutoutScreen(ctx)) { // 隐藏了刘海
            return result
        }
        val resourceId = ctx.resources.getIdentifier("notch_height", "dimen", "android")
        if (resourceId > 0) {
            result = ctx.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     * 获取刘海宽度
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun getRealNotchWidth(window: Window): Int {
        val ctx = window.context
        var result = 0
        if (!isCutoutScreen(window)) { // 不是刘海屏
            return result
        }
        if (isHideCutoutScreen(ctx)) { // 隐藏了刘海
            return result
        }
        val resourceId = ctx.resources.getIdentifier("notch_width", "dimen", "android")
        if (resourceId > 0) {
            result = ctx.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     * 0x00000100 | 0x00000200 竖屏绘制到耳朵区
     * 0x00000100 | 0x00000400 横屏绘制到耳朵区
     * 0x00000100 | 0x00000200 | 0x00000400 横竖屏都绘制到耳朵区
     */
    private companion object {
        // 开启绘制到刘海区域
        private const val FLAG_CUTOUT_OPEN = 0x00000100

        // 竖屏绘制到刘海区域
        private const val FLAG_CUTOUT_PORTRAINT = FLAG_CUTOUT_OPEN or 0x00000200

        // 横屏绘制刘海区域
        private const val FLAG_CUTOUT_LANDSCAPE = FLAG_CUTOUT_OPEN or 0x00000400

        // 横屏竖屏都绘制刘海区域
        private const val FLAG_CUTOUT_ALL = 0x00000100 or 0x00000200 or 0x00000400
    }
}
