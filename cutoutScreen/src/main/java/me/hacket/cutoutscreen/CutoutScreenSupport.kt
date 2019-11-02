package me.hacket.cutoutscreen

import android.graphics.Rect
import android.os.Build
import android.view.Window
import me.hacket.cutoutscreen.core.AndroidPCutoutScreenSupportStrategy
import me.hacket.cutoutscreen.core.DefaultCutoutScreenSupportStrategy
import me.hacket.cutoutscreen.core.HuaweiCutoutScreenSupportStrategy
import me.hacket.cutoutscreen.core.ICutoutScreenSupportStrategy
import me.hacket.cutoutscreen.core.MiuiCutoutScreenSupportStrategy
import me.hacket.cutoutscreen.core.OppoCutoutScreenSupportStrategy
import me.hacket.cutoutscreen.core.VivoCutoutScreenSupportStrategy
import me.hacket.cutoutscreen.utils.DevicesUtils

/**
 * 刘海屏屏适配工具类
 *
 * 默认Android O以上才有刘海屏,  Android O利用厂商API判断, Android P及以上调用官方API判断.
 */
object CutoutScreenSupport {

    private lateinit var currentICutoutScreenSupportStrategy: ICutoutScreenSupportStrategy

    init {
        initCutoutScreenSupport()
    }

    @JvmStatic
    fun getInstance(): CutoutScreenSupport {
        return CutoutScreenSupport
    }

    /**
     * 判断当前是否是刘海屏手机
     */
    fun isCutoutScreen(window: Window): Boolean {
        return currentICutoutScreenSupportStrategy.isCutoutScreen(window)
    }

    /**
     * 设置横竖屏都使用刘海屏
     */
    fun setDisplayCutoutScreen(window: Window) {
        currentICutoutScreenSupportStrategy.setWindowLayoutCutout(window)
    }

    /**
     * 设置横竖屏都不使用刘海屏
     */
    fun clearDisplayCutoutScreen(window: Window) {
        currentICutoutScreenSupportStrategy.clearWindowLayoutCutout(window)
    }

    /**
     * 获取刘海高度
     *
     * 1. 不是刘海屏，返回0
     * 2. 刘海关闭了，返回0
     */
    fun getCutoutHeight(window: Window): Int {
        return currentICutoutScreenSupportStrategy.getCutoutHeight(window)
    }

    /**
     * 获取刘海区域
     *
     * 1. 不是刘海屏，Rect都是0
     * 2. 刘海关闭了，Rect都是0
     */
    fun getCutoutRect(window: Window): Rect {
        return currentICutoutScreenSupportStrategy.getCutoutRect(window)
    }

    private fun initCutoutScreenSupport() {
        currentICutoutScreenSupportStrategy = when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> DefaultCutoutScreenSupportStrategy()
            // 测试发现，华为在android P依然需要使用华为的api来适配
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> AndroidPCutoutScreenSupportStrategy()
            DevicesUtils.isHuawei() -> HuaweiCutoutScreenSupportStrategy()
            DevicesUtils.isOppoRom() -> OppoCutoutScreenSupportStrategy()
            DevicesUtils.isVivoRom() -> VivoCutoutScreenSupportStrategy()
            DevicesUtils.isMiuiRom() -> MiuiCutoutScreenSupportStrategy()
            else -> DefaultCutoutScreenSupportStrategy()
        }
    }
}
