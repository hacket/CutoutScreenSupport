package me.hacket.cutoutscreen.core

import android.graphics.Rect
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import java.util.*

/**
 * Android P(Api>=28)
 *
 * 有的手机厂商在P上会放弃O适配方案，有的手机厂商自己的O适配方案和Android P方案两套方案都兼容
 */
internal class AndroidPCutoutScreenSupportStrategy : AbsCutoutScreenSupportStrategy() {

    @RequiresApi(api = Build.VERSION_CODES.P)
    override fun isCutoutScreen(window: Window): Boolean {
        val decorView = window.decorView
        val windowInsets = decorView.rootWindowInsets ?: return false
        val dct = windowInsets.displayCutout
        return dct != null && (
            dct.safeInsetTop != 0 ||
                dct.safeInsetBottom != 0 ||
                dct.safeInsetLeft != 0 ||
                dct.safeInsetRight != 0
            )
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    override fun getCutoutRects(window: Window): List<Rect> {
        val result = ArrayList<Rect>()
        val rect: Rect
        if (!isCutoutScreen(window) || isHideCutoutScreen(window.context)) {
            rect = Rect(0, 0, 0, 0)
            result.add(rect)
            return result
        }
        val decorView = window.decorView
        val windowInsets = decorView.rootWindowInsets ?: return result
        val dct = windowInsets.displayCutout
        if (dct != null) {
            result.addAll(dct.boundingRects)
        }
        return result
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    override fun getCutoutRect(window: Window): Rect {
        return getCutoutRects(window)[0]
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    override fun getCutoutHeight(window: Window): Int {
        return getCutoutRect(window).bottom - getCutoutRect(window).top
    }

    override fun setPortraitWindowLayoutCutout(window: Window) {
        super.setPortraitWindowLayoutCutout(window)
        val attributes = window.attributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        window.attributes = attributes
    }

    override fun clearPortraitWindowLayoutCutout(window: Window) {
        super.clearPortraitWindowLayoutCutout(window)
        val attributes = window.attributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        }
        window.attributes = attributes
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
}
