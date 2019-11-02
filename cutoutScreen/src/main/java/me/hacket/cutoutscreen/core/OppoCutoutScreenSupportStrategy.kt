package me.hacket.cutoutscreen.core

import android.graphics.Rect
import android.view.View
import android.view.Window
import android.view.WindowManager
import me.hacket.cutoutscreen.utils.DevicesUtils
import java.util.*

/**
 * Oppo Android O刘海屏适配
 *
 * 目前Oppo刘海屏机型尺寸规格都是统一的,显示屏宽度为1080px，高度为2280px,刘海区域宽度为324px, 高度为80px，截止2018年12月09日
 *
 * https://open.oppomobile.com/wiki/doc#id=10159
 *
 */
internal class OppoCutoutScreenSupportStrategy : AbsCutoutScreenSupportStrategy() {

    override fun isCutoutScreen(window: Window): Boolean {
        return try {
            window.context.packageManager
                .hasSystemFeature("com.oppo.feature.screen.heteromorphism")
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            false
        }
    }

    // 目前Oppo刘海屏机型尺寸规格都是统一的,显示屏宽度为1080px，高度为2280px,刘海区域宽度为324px, 高度为80px
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

    /**
     * 获取刘海高度
     *
     * 目前Oppo刘海屏机型尺寸规格都是统一的,显示屏宽度为1080px，高度为2280px,刘海区域宽度为324px, 高度为80px
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
        result = Math.min(80, DevicesUtils.getStatusBarHeight(ctx)) //  取状态栏高度和80最小值
        return result
    }

    /**
     * 获取刘海宽度
     *
     * 目前Oppo刘海屏机型尺寸规格都是统一的,显示屏宽度为1080px，高度为2280px,刘海区域宽度为324px, 高度为80px
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
        result = Math.min(324, DevicesUtils.getScreenWidth(ctx)) //  取屏幕宽度和324最小值
        return result
    }
}
