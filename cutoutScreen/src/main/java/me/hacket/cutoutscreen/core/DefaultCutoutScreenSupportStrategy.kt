package me.hacket.cutoutscreen.core

import android.graphics.Rect
import android.view.Window
import java.util.*

/**
 * 默认处理策略
 */
internal class DefaultCutoutScreenSupportStrategy : AbsCutoutScreenSupportStrategy() {

    override fun isCutoutScreen(window: Window): Boolean {
        return false
    }

    override fun getCutoutRects(window: Window): List<Rect> {
        val result = ArrayList<Rect>()
        val rect = Rect(0, 0, 0, 0)
        result.add(rect)
        return result
    }

    override fun getCutoutRect(window: Window): Rect {
        return getCutoutRects(window)[0]
    }

    override fun getCutoutHeight(window: Window): Int {
        return 0
    }
}
