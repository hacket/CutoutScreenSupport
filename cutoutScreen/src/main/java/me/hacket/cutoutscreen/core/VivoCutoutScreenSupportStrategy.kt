package me.hacket.cutoutscreen.core

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import me.hacket.cutoutscreen.utils.DevicesUtils
import java.util.*

/**
 * Vivo Android O刘海屏适配
 *
 * Vivo手机没有适配规则，在FullScreen Flag下永远都是在刘海下面
 *
 * https://dev.vivo.com.cn/documentCenter/doc/103
 *
 */
internal class VivoCutoutScreenSupportStrategy : AbsCutoutScreenSupportStrategy() {

    /**
     * 屏幕是否有凹槽
     */
    override fun isCutoutScreen(window: Window): Boolean {
        return try {
            val isFeatureSupportMethod = getVivoFtFeature(window.context)?.getMethod(
                "isFeatureSupport",
                Int::class.javaPrimitiveType
            )
            isFeatureSupportMethod?.invoke(vivoFtFeature, VIVO_HAS_NOTCH_DISPLAY) as Boolean
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            false
        }
    }

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

    override fun getCutoutRect(window: Window): Rect {
        return getCutoutRects(window)[0]
    }

    override fun getCutoutHeight(window: Window): Int {
        return getRealNotchHeight(window)
    }

    /**
     * 获取刘海高度
     *
     * 刘海固定高度27dp
     */
    private fun getRealNotchHeight(window: Window): Int {
        val ctx = window.context
        var result = 0
        if (!isCutoutScreen(window)) { // 不是刘海屏
            return result
        }
        if (isHideCutoutScreen(ctx)) { // 隐藏了刘海
            return result
        }
        val displayMetrics = window.context.resources.displayMetrics
        val notchHeight =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27f, displayMetrics).toInt()
        result = Math.min(notchHeight, DevicesUtils.getStatusBarHeight(ctx)) //  取状态栏高度和80最小值
        return result
    }

    /**
     * 获取刘海宽度
     *
     * 刘海固定宽度100dp
     */
    private fun getRealNotchWidth(window: Window): Int {
        val ctx = window.context
        var result = 0
        if (!isCutoutScreen(window)) { // 不是刘海屏
            return result
        }
        if (isHideCutoutScreen(ctx)) { // 隐藏了刘海
            return result
        }
        val displayMetrics = window.context.resources.displayMetrics
        val notchWidth =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, displayMetrics).toInt()
        result = Math.min(notchWidth, DevicesUtils.getScreenWidth(ctx)) //  取屏幕宽度和324最小值
        return result
    }

    override fun setPortraitWindowLayoutCutout(window: Window) {
        super.setPortraitWindowLayoutCutout(window)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.decorView.systemUiVisibility = systemUiVisibility
    }

    override fun clearPortraitWindowLayoutCutout(window: Window) {
        super.clearPortraitWindowLayoutCutout(window)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility = systemUiVisibility and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN.inv()
        systemUiVisibility = systemUiVisibility and View.SYSTEM_UI_FLAG_LAYOUT_STABLE.inv()
        window.decorView.systemUiVisibility = systemUiVisibility
    }

    override fun setLandScapeWindowLayoutCutout(window: Window) {
        super.setLandScapeWindowLayoutCutout(window)
        setPortraitWindowLayoutCutout(window)
    }

    override fun clearLandScapeWindowLayoutCutout(window: Window) {
        super.clearLandScapeWindowLayoutCutout(window)
        clearPortraitWindowLayoutCutout(window)
    }

    override fun setWindowLayoutCutout(window: Window) {
        super.setWindowLayoutCutout(window)
        setPortraitWindowLayoutCutout(window)
    }

    override fun clearWindowLayoutCutout(window: Window) {
        super.clearWindowLayoutCutout(window)
        clearPortraitWindowLayoutCutout(window)
    }

    private companion object {
        private var vivoFtFeature: Class<*>? = null

        // 表示是否有凹槽
        private const val VIVO_HAS_NOTCH_DISPLAY = 0x00000020

        @SuppressLint("PrivateApi")
        private fun getVivoFtFeature(ctx: Context): Class<*>? {
            if (vivoFtFeature == null) {
                vivoFtFeature = ctx.classLoader
                    .loadClass("android.util.FtFeature")
            }
            return vivoFtFeature
        }
    }
}
