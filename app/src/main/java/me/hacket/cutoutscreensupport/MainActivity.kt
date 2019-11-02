package me.hacket.cutoutscreensupport

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import me.hacket.cutoutscreen.CutoutScreenSupport

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "hacket"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        translateStatusBarAndNavigationBar()

        val tvInfo = findViewById<TextView>(R.id.tv_info)

        findViewById<View>(R.id.btn_get_info).setOnClickListener {
            val isCutoutScreen = CutoutScreenSupport.isCutoutScreen(window)
            val cutoutRect = CutoutScreenSupport.getCutoutRect(window)
            var cutoutHeight = CutoutScreenSupport.getCutoutHeight(window)
            val info =
                "isCutoutScreen=$isCutoutScreen, cutoutHeight=$cutoutHeight, cutoutRect=$cutoutRect"
            Log.d(TAG, info)

            tvInfo.text = info
        }
        findViewById<View>(R.id.btn_occupy).setOnClickListener {
            CutoutScreenSupport.setDisplayCutoutScreen(window)
        }
        findViewById<View>(R.id.btn_clear_occupy).setOnClickListener {
            CutoutScreenSupport.clearDisplayCutoutScreen(window)
        }
    }

    private fun translateStatusBarAndNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 注意要清除 FLAG_TRANSLUCENT_STATUS flag
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
            val tag =
                (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or tag
        } else {
        }
    }
}
