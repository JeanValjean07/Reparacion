package com.suming.reparacion

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BlurMaskFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.Space
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.suming.reparacion.AddonTools.ToolVibrate
import com.suming.reparacion.AddonTools.showCustomToast
import com.suming.reparacion.OSS_LICENSE.OssLicensePage
import com.suming.reparacion.UI_SEG_Compose.ComposeElementBundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import kotlin.time.Duration.Companion.milliseconds

@Suppress("LocalVariableName","/unused","NewApi")
class SettingsActivity : AppCompatActivity() {

    //ctx
    private val context = this@SettingsActivity
    //ComposeElementBundle
    private lateinit var ComposeElementBundle: ComposeElementBundle
    //ComposeColorPack
    private lateinit var ColorPack: ColorScheme
    //深色模式标记
    private var isDarkMode: Boolean = false
    //振动
    private fun vibrate() {
        ToolVibrate().vibrate(context)
    }
    //统一日志控制
    private fun consoleLog(msg: String, mark: Boolean = true) {
        if (mark) {
            Log.d("SuMing", msg)
        }
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //界面配置
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        //初始化
        init()



        //TopBarCompose
        val TopBarCompose = findViewById<ComposeView>(R.id.TopBarCompose)
        TopBarCompose.setContent {
            ComposeRoot()
        }

        //注册
        register()


    }

    override fun onResume() {
        super.onResume()

    }




    //内容区域view
    private lateinit var ContentRoot: NestedScrollView
    //内容区域是否在顶部标记
    private val isContentTop = mutableStateOf(true)
    @Composable
    fun ComposeRoot() {
        //初始化颜色模式
        isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        //初始化组件仓库
        ComposeElementBundle = ComposeElementBundle(isDarkMode)
        //初始化颜色模式
        ColorPack = ComposeElementBundle.ColorPack



        //获取状态栏高度
        val statusBarHeight_Px = WindowInsets.statusBars.getTop(LocalDensity.current)
        val statusBarHeight_Dp = with(LocalDensity.current) { statusBarHeight_Px.toDp() }

        //缓存总高度值
        var topAll_Height_Px by remember { mutableIntStateOf(300) }
        val topAll_Height_Dp = with(LocalDensity.current) { topAll_Height_Px.toDp() }


        Box(modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(Color.Transparent)) {


            //最顶层
            ComposeElementBundle.ADVANCED_TOP_BAR_c_v2_dual(
                onHeightMeasured = { height ->
                    topAll_Height_Px = height
                    //更新内边距
                    if (height > 0){
                        updateNestTopPadding_Advanced(topAll_Height_Px)
                    }
                },
                modifier = Modifier,
                statusBarHeight_Dp = statusBarHeight_Dp,
                startContent = { ADVANCED_TOP_BAR_Start() },
                endContent = {  },
                brushContent = { ADVANCED_TOP_BAR_Brush(statusBarHeight_Dp, ComposeElementBundle.ADVANCED_TOP_BAR_content_height_Dp) }
            )

        }
    }
    @Composable
    fun ADVANCED_TOP_BAR_Start(){
        //标题文本
        @Composable
        fun text_content(text:String){

            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color(0xFFFFFFFF) else Color(0xFF212121),
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        //LazyColumn贴顶检测
        val top = isContentTop.value
        val capsuleProgress by animateFloatAsState(
            targetValue = if (!top) 1f else 0f,
            animationSpec = tween(durationMillis = 300),
            label = "capsuleProgress"
        )

        Box {

            //胶囊标题区
            ComposeElementBundle.CAPSULE_BUTTON_c_v2_avoid_circle(
                text_onClick = { },
                modifier = Modifier.padding(start = 15.dp),
                elevation = if (!top) 10.dp else 0.dp,
                backgroundColor = if (isDarkMode) {
                    Color(0xFA181818).copy(alpha = if (!top) 1f else 0f)
                } else {
                    Color(0xFFFFFFFF).copy(alpha = if (!top) 1f else 0f)
                },
                border = BorderStroke(
                    width = 0.3.dp * capsuleProgress,
                    color = Color.Gray.copy(alpha = 0.3f * capsuleProgress)
                ),
                padding_start = 0.dp,
                padding_end = 15.dp,
                padding_vertical = 0.dp,
                text_content = { text_content("设置") },
            )

            //返回按钮
            ComposeElementBundle.CIRCLE_BUTTON_c_v1 (
                onClick = {
                    vibrate()
                    finish()
                },
                backgroundColor = if (isDarkMode){
                    Color(0xFA181818)
                }else{
                    Color(0xFFFFFFFF)
                },
                border = BorderStroke(width = 0.3.dp, color = Color.Gray.copy(alpha = 0.3f)),
                modifier = Modifier.padding(start = 15.dp),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "退出",
                    modifier = Modifier.background(Color.Transparent),
                    tint = ComposeElementBundle.ColorPack.secondary
                )
            }


        }

    }
    @Composable
    fun ADVANCED_TOP_BAR_Brush(statusBarHeight_Dp: Dp, content_Height_Dp: Dp){
        ComposeElementBundle.BRUSH_AREA_TOP_c_v2(
            color = if (isDarkMode) Color(0xFF000000) else Color(0xFFFFFFFF),
            statusBarHeight_Dp = statusBarHeight_Dp,
            content_Height_Dp = content_Height_Dp
        )
    }
    //自定义阴影
    @Suppress("DEPRECATION")
    fun Modifier.uniformShadow(
        blurRadius: Float = 15f,
        shadowColor: Color = Color.Black.copy(alpha = 0.1f)
    ) = this.drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                color = shadowColor
                asFrameworkPaint().maskFilter = BlurMaskFilter(
                    blurRadius,
                    BlurMaskFilter.Blur.NORMAL
                )
            }

            canvas.drawRoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                radiusX = 12.dp.toPx(),
                radiusY = 12.dp.toPx(),
                paint = paint
            )
        }
    }




    @SuppressLint("SetTextI18n")
    private fun register() {
        lifecycleScope.launch(Dispatchers.IO) {
            delay(100.milliseconds)
            //读取版本
            val version = packageManager.getPackageInfo(packageName, 0).versionName
            //获取是不是debug版
            val isDebug = isDebugVersion()


            //更新UI
            withContext(Dispatchers.Main) {
                //修改版本号显示
                val versionText = findViewById<TextView>(R.id.version)
                versionText.text = "版本: $version" + if (isDebug) " (Debug)" else ""
                //SvgRepo
                val buttonGoSvgRepo = findViewById<FrameLayout>(R.id.buttonGoSvgRepo)
                buttonGoSvgRepo.setOnClickListener {
                    vibrate()
                    AlertDialog.Builder(context)
                        .setTitle("确定跳转吗?")
                        .setMessage("将唤醒浏览器并打开svgrepo.com")
                        .setPositiveButton("确认") { dialog, _ ->
                            vibrate()

                            val url = "https://www.svgrepo.com/"
                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                            startActivity(intent)

                            dialog.dismiss()
                        }
                        .setNegativeButton("取消") { dialog, _ ->
                            vibrate()

                            dialog.dismiss()
                        }
                        .setCancelable(true)
                        .show()
                }
                //按钮：查看Github仓库
                val ButtonGoGithub = findViewById<TextView>(R.id.buttonGoGithubRelease)
                ButtonGoGithub.setOnClickListener {
                    vibrate()

                    AlertDialog.Builder(context)
                        .setTitle("将跳转至浏览器")
                        .setMessage("是否继续?")
                        .setPositiveButton("确认") { dialog, _ ->
                            vibrate()

                            val url = "https://github.com/JeanValjean07/HarmonyOS4_Reparacion/releases"
                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                            startActivity(intent)

                            dialog.dismiss()
                        }
                        .setNegativeButton("取消") { dialog, _ ->
                            vibrate()

                            dialog.dismiss()
                        }
                        .setCancelable(true)
                        .show()


                }
                //超链接：开放源代码许可
                val ButtonOpenSourceLicense = findViewById<TextView>(R.id.openSourceLicense)
                ButtonOpenSourceLicense.paint.isUnderlineText = true
                ButtonOpenSourceLicense.invalidate()
                ButtonOpenSourceLicense.setOnClickListener {
                    vibrate()
                    //启动OSS页面
                    startActivity(Intent(context, OssLicensePage::class.java))
                }
                //振动模式
                val ButtonCardVibrateMode = findViewById<CardView>(R.id.ButtonCardVibrateMode)
                updateVibrateModeText()
                ButtonCardVibrateMode.setOnClickListener {
                    vibrate()
                    //使用弹出菜单选择
                    val popup = PopupMenu(context, ButtonCardVibrateMode)
                    popup.menuInflater.inflate(R.menu.activity_settings_popup_vibrate_mode, popup.menu)
                    popup.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.item_NoVibrate -> {
                                chooseVibrateMode(0); true
                            }

                            R.id.item_EFFECT_CLICK -> {
                                chooseVibrateMode(1); true
                            }

                            R.id.item_EFFECT_TICK -> {
                                chooseVibrateMode(2); true
                            }

                            R.id.item_EFFECT_DOUBLE_CLICK -> {
                                chooseVibrateMode(3); true
                            }

                            R.id.item_EFFECT_HEAVY_CLICK -> {
                                chooseVibrateMode(4); true
                            }

                            else -> true
                        }
                    }
                    popup.show()
                }

            }

        }
    }




    //振动模式
    private fun chooseVibrateMode(mode: Int) {
        //振动模式表
        // 0 = No Vibrate
        // 1 = VibrationEffect.EFFECT_CLICK
        // 2 = VibrationEffect.EFFECT_TICK
        // 3 = VibrationEffect.EFFECT_DOUBLE_CLICK
        // 4 = VibrationEffect.EFFECT_HEAVY_CLICK

        //执行设置
        ToolVibrate().setVibrateMode(context, mode)
        //测试振动
        vibrate()
        //更新振动模式文本
        updateVibrateModeText()

    }
    @SuppressLint("SetTextI18n")
    private fun updateVibrateModeText() {
        val ButtonTextVibrateMode = findViewById<TextView>(R.id.ButtonTextVibrateMode)
        val vibrateMode = ToolVibrate().getVibrateMode(context)
        when(vibrateMode){
            0 -> ButtonTextVibrateMode.text = "无振动"
            1 -> ButtonTextVibrateMode.text = "EFFECT_CLICK"
            2 -> ButtonTextVibrateMode.text = "EFFECT_TICK"
            3 -> ButtonTextVibrateMode.text = "EFFECT_DOUBLE_CLICK"
            4 -> ButtonTextVibrateMode.text = "EFFECT_HEAVY_CLICK"
        }

    }

    //修改滚动区域顶部内边距
    private fun updateNestTopPadding(topPadding: Int){
        ContentRoot.setPadding(0, topPadding, 0, 0)
        //强制刷新视图

        Handler(Looper.getMainLooper()).postDelayed({
            ContentRoot.setPadding(0, topPadding, 0, 0)
        }, 1000)

    }
    //修改滚动区域顶部内边距 Advanced
    private var paddingAnimator: ValueAnimator? = null
    private fun updateNestTopPadding_Advanced(topPadding: Int) {
        paddingAnimator?.cancel()

        val currentPadding = ContentRoot.paddingTop
        if (currentPadding == topPadding) return

        paddingAnimator = ValueAnimator.ofInt(currentPadding, topPadding).apply {
            duration = 500L
            interpolator = DecelerateInterpolator(2f)
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                ContentRoot.setPadding(0, value, 0, 0)
            }
            start()
        }
    }
    //监听内容区域nestedScrollView是否在顶部
    private fun setupScrollContentListener(){
        ContentRoot.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            //未在顶部时隐藏顶部栏文字区
            if (scrollY == 0){
                isContentTop.value = true
            }else{
                isContentTop.value = false
            }
        }
    }

    //基本初始化
    private fun init() {
        //共享视图初始化
        ContentRoot = findViewById(R.id.ScrollContentRoot)
        //初始化滚动监听
        setupScrollContentListener()


    }


    //检测当前是release版还是debug版
    @SuppressLint("ObsoleteSdkInt")
    @RequiresApi(Build.VERSION_CODES.P)
    private fun isDebugVersion(): Boolean {
        //仅支持安卓9及以上版本
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
            return false
        }


        //获取签名信息
        val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
        val signingInfo = packageInfo.signingInfo ?: return false
        //
        if (signingInfo.hasMultipleSigners()) {
            return false
        }

        val signatures = signingInfo.signingCertificateHistory
        for (sig in signatures) {
            val cert = sig.toByteArray()
            val input = ByteArrayInputStream(cert)
            val cf = CertificateFactory.getInstance("X509")
            val c = cf.generateCertificate(input) as X509Certificate
            val name = c.subjectDN.name
            if (name.contains("Android Debug")) {
                return true
            }else{
                return false
            }
        }
        return true
    }



}
