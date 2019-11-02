package me.hacket.cutoutscreen.core

import android.content.Context
import android.graphics.Rect
import android.provider.Settings
import android.view.Window
import me.hacket.cutoutscreen.utils.DevicesUtils
import java.util.*

/**
 * 华为Android O刘海屏适配
 *
 * https://developer.huawei.com/consumer/cn/devservice/doc/50114
 */
internal class HuaweiCutoutScreenSupportStrategy : AbsCutoutScreenSupportStrategy() {

    /**
     * com.huawei.android.util.HwNotchSizeUtil
     *
     * public static boolean hasNotchInScreen()
     *
     * 是否是刘海屏手机：true：是刘海屏；false：非刘海屏。
     *
     */
    override fun isCutoutScreen(window: Window): Boolean {
        return try {
            val get = getHwNotchSizeUtil(window.context)?.getMethod("hasNotchInScreen")
            get?.invoke(null) as Boolean
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            false
        }
    }

    /**
     * 获取默认和隐藏刘海区开关值接口
     *
     * 0表示“默认”，1表示“隐藏显示区域”
     */
    override fun isHideCutoutScreen(ctx: Context): Boolean {
        val isNotchSwitchOpen = Settings.Secure.getInt(ctx.contentResolver, DISPLAY_NOTCH_STATUS, 0)
        return isNotchSwitchOpen == 1
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
        return try {
            val realNotchSize = getRealNotchSize(window)
            rect.left = (DevicesUtils.getScreenWidth(ctx) - realNotchSize[0]) / 2
            rect.top = 0
            rect.right = rect.left + realNotchSize[0]
            rect.bottom = rect.top + realNotchSize[1]
            result.add(rect)
            result
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            result
        }
    }

    override fun getCutoutRect(window: Window): Rect {
        return getCutoutRects(window)[0]
    }

    override fun getCutoutHeight(window: Window): Int {
        return getRealNotchSize(window)[1]
    }

    /**
     *
     * com.huawei.android.util.HwNotchSizeUtil
     *
     * public static int[] getNotchSize()
     *
     * 获取刘海尺寸：width、height
     *
     * int[0]值为刘海宽度 int[1]值为刘海高度。
     */
    private fun getRealNotchSize(window: Window): IntArray {
        val ctx = window.context
        if (!isCutoutScreen(window)) { // 不是刘海
            return intArrayOf(0, 0)
        }
        if (isHideCutoutScreen(ctx)) { // 隐藏了刘海
            return intArrayOf(0, 0)
        }
        val getNotchSizeMethod = getHwNotchSizeUtil(ctx)?.getMethod("getNotchSize")
        return getNotchSizeMethod?.invoke(null) as IntArray
    }

    /**
     *
     * 通过类com.huawei.android.view.LayoutParamsEx
     *
     * public void addHwFlags (int hwFlags)方法
     *
     * 应用通过增加华为自定义的刘海屏flag，请求使用刘海区显示
     */
    override fun setPortraitWindowLayoutCutout(window: Window) {
        super.setPortraitWindowLayoutCutout(window)
        val layoutParams = window.attributes
        try {
            var addHwFlagsMethod =
                getHwLayoutParamsEx()?.getMethod("addHwFlags", Int::class.javaPrimitiveType)
            val con = getHwLayoutParamsEx()?.getConstructor(layoutParams.javaClass)
            val layoutParamsExObj = con?.newInstance(layoutParams)
            addHwFlagsMethod?.invoke(layoutParamsExObj, HW_FLAG_NOTCH_SUPPORT)
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
    }

    /**
     *
     * 通过类com.huawei.android.view.LayoutParamsEx
     *
     * public void clearHwFlags (int hwFlags)方法
     *
     * 清除添加的华为刘海屏Flag，恢复应用不使用刘海区显示。
     */
    override fun clearPortraitWindowLayoutCutout(window: Window) {
        super.clearPortraitWindowLayoutCutout(window)
        val layoutParams = window.attributes
        try {
            var addHwFlagsMethod =
                getHwLayoutParamsEx()?.getMethod("clearHwFlags", Int::class.javaPrimitiveType)
            val con = getHwLayoutParamsEx()?.getConstructor(layoutParams.javaClass)
            val layoutParamsExObj = con?.newInstance(layoutParams)
            addHwFlagsMethod?.invoke(layoutParamsExObj, HW_FLAG_NOTCH_SUPPORT)
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
    }

    // 横屏: 竖屏保持一致
    override fun setLandScapeWindowLayoutCutout(window: Window) {
        super.setLandScapeWindowLayoutCutout(window)
        setPortraitWindowLayoutCutout(window)
    }

    // 横屏: 竖屏保持一致
    override fun clearLandScapeWindowLayoutCutout(window: Window) {
        super.clearLandScapeWindowLayoutCutout(window)
        clearPortraitWindowLayoutCutout(window)
    }

    // 横屏竖屏都一样，设置一个就行
    override fun setWindowLayoutCutout(window: Window) {
        super.setWindowLayoutCutout(window)
        setPortraitWindowLayoutCutout(window)
    }

    // 横屏竖屏都一样，设置一个就行
    override fun clearWindowLayoutCutout(window: Window) {
        super.clearWindowLayoutCutout(window)
        clearPortraitWindowLayoutCutout(window)
    }

    private companion object {
        // 隐藏刘海区开关值接口
        private const val DISPLAY_NOTCH_STATUS = "display_notch_status"

        // 通过添加窗口FLAG的方式设置页面使用刘海区显示
        private const val HW_FLAG_NOTCH_SUPPORT = 0x00010000

        // 类 com.huawei.android.util.HwNotchSizeUtil
        private var hwNotchSizeUtil: Class<*>? = null

        // 类 com.huawei.android.view.LayoutParamsEx
        private var hwLayoutParamsEx: Class<*>? = null

        private fun getHwNotchSizeUtil(ctx: Context): Class<*>? {
            if (hwNotchSizeUtil == null) {
                hwNotchSizeUtil = ctx.classLoader
                    .loadClass("com.huawei.android.util.HwNotchSizeUtil")
            }
            return hwNotchSizeUtil
        }

        private fun getHwLayoutParamsEx(): Class<*>? {
            if (hwLayoutParamsEx != null) {
                return hwLayoutParamsEx
            }
            try {
                hwLayoutParamsEx = Class.forName("com.huawei.android.view.LayoutParamsEx")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return hwLayoutParamsEx
        }
    }
}
