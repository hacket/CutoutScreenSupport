package me.hacket.cutoutscreen.core

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.Window
import androidx.annotation.RequiresApi

internal interface ICutoutScreenSupportStrategy {

    /**
     * 判断当前是否是刘海屏手机
     */
    fun isCutoutScreen(window: Window): Boolean

    /**
     * 是否设置隐藏了刘海屏，默认不隐藏
     *
     * 有的手机可以隐藏刘海：华为，小米
     * 有的手机不能隐藏刘海：Oppo
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun isHideCutoutScreen(ctx: Context): Boolean

    /**
     * 获取刘海屏区域，可能有多个
     *
     * 1. 不是刘海屏手机，返回Rect都为0
     * 2. 隐藏了刘海，返回Rect都为0
     *
     */
    fun getCutoutRects(window: Window): List<Rect>

    /**
     * 获取第一个刘海屏区域
     * 1. 不是刘海屏手机，返回Rect都为0
     * 2. 隐藏了刘海，返回Rect都为0
     */
    fun getCutoutRect(window: Window): Rect

    /**
     * 获取刘海的高度
     * 1. 不是刘海屏手机，返回0
     * 2. 隐藏了刘海，返回0
     */
    fun getCutoutHeight(window: Window): Int

    /**
     * 纵向，占用刘海屏区域
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun setPortraitWindowLayoutCutout(window: Window)

    /**
     * 纵向，不占用刘海屏区域
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun clearPortraitWindowLayoutCutout(window: Window)

    /**
     * 横向，占用刘海屏区域
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun setLandScapeWindowLayoutCutout(window: Window)

    /**
     * 横向，不占用刘海屏区域
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun clearLandScapeWindowLayoutCutout(window: Window)

    /**
     * 横向，纵向都占用刘海屏区域
     */
    fun setWindowLayoutCutout(window: Window)

    /**
     * 横向，纵向不占用刘海屏区域
     */
    fun clearWindowLayoutCutout(window: Window)
}
