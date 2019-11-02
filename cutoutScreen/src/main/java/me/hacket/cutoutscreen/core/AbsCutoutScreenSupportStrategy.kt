package me.hacket.cutoutscreen.core

import android.content.Context
import android.view.Window

internal abstract class AbsCutoutScreenSupportStrategy : ICutoutScreenSupportStrategy {

    override fun isHideCutoutScreen(ctx: Context): Boolean {
        return false
    }

    override fun setPortraitWindowLayoutCutout(window: Window) {
    }

    override fun clearPortraitWindowLayoutCutout(window: Window) {
    }

    override fun setLandScapeWindowLayoutCutout(window: Window) {
    }

    override fun clearLandScapeWindowLayoutCutout(window: Window) {
    }

    override fun setWindowLayoutCutout(window: Window) {
    }

    override fun clearWindowLayoutCutout(window: Window) {
    }
}
