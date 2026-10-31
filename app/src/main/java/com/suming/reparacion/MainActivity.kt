package com.suming.reparacion

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SnippetFolder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.suming.reparacion.ActivityComponents.MainViewModel
import com.suming.reparacion.AddonTools.ToolVibrate
import com.suming.reparacion.DataPack.ToolList
import com.suming.reparacion.DataPack.ToolPackage
import com.suming.reparacion.AddonTools.showCustomToast
import com.suming.reparacion.UI_SEG_Compose.ComposeElementBundle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("LocalVariableName")
class MainActivity : AppCompatActivity() {

    //连接到ViewModel
    private val mainViewModel: MainViewModel by viewModels()
    //ctx
    private val context = this@MainActivity
    //ComposeElementBundle
    private lateinit var ComposeElementBundle: ComposeElementBundle
    //深色模式标记
    private var isDarkMode: Boolean = false


    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //界面配置
        enableEdgeToEdge()
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        //手动设置状态栏字体颜色
        setStatusBarFontColor()

        //初始化颜色模式
        isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES


        //工具列表
        val toolsList = ToolList.toolsList

        //ComposableRoot
        setContent {
            ComposeRoot(toolsList, mainViewModel)
        }

    }



    @Composable
    fun ComposeRoot(toolsList: List<ToolPackage>, mainViewModel: MainViewModel) {
        //初始化组件仓库
        ComposeElementBundle = ComposeElementBundle(isDarkMode)



        Box(modifier = Modifier.fillMaxSize()) {

            //获取状态栏高度
            val statusBarHeight_Px = WindowInsets.statusBars.getTop(LocalDensity.current)
            val statusBarHeight_Dp = with(LocalDensity.current) { statusBarHeight_Px.toDp() }

            //缓存总高度值
            var topAll_Height_Px by remember { mutableIntStateOf(300) }
            val topAll_Height_Dp = with(LocalDensity.current) { topAll_Height_Px.toDp() }


            //
            ToolsListColumn(toolsList, topAll_Height_Dp)


            //最顶层
            ComposeElementBundle.ADVANCED_TOP_BAR_c_v2_dual(
                onHeightMeasured = { height ->
                    topAll_Height_Px = height
                },
                modifier = Modifier,
                statusBarHeight_Dp = statusBarHeight_Dp,
                startContent = { ADVANCED_TOP_BAR_Start() },
                endContent = { ADVANCED_TOP_BAR_End() },
                brushContent = { ADVANCED_TOP_BAR_Brush(statusBarHeight_Dp, topAll_Height_Dp - statusBarHeight_Dp) }
            )
        }
    }
    //内容区域是否在顶部标记
    private val isContentTop = mutableStateOf(true)
    @Composable
    fun ADVANCED_TOP_BAR_Start(){
        @Composable
        fun content(){
            //标题文本
            Text(
                text = "补全计划",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ComposeElementBundle.ColorPack.primary,
                modifier = Modifier.padding(start = 10.dp)
            )
        }

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
                text_content = { content() },
            )

            //返回按钮
            ComposeElementBundle.CIRCLE_BUTTON_c_v1 (
                onClick = {
                    ToolVibrate().vibrate(context)
                    finish() },
                backgroundColor = if (isDarkMode){
                    Color(0xFA181818)
                }else{
                    Color(0xFFFFFFFF)
                },
                border = BorderStroke(width = 0.3.dp, color = Color.Gray.copy(alpha = 0.3f)),
                modifier = Modifier.padding(start = 15.dp),
            ) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = "退出",
                    modifier = Modifier.background(Color.Transparent),
                    tint = ComposeElementBundle.ColorPack.secondary
                )
            }


        }



    }
    @Composable
    fun ADVANCED_TOP_BAR_End(){
        //指南按钮
        ComposeElementBundle.CIRCLE_BUTTON_c_v1 (
            onClick = {
                ToolVibrate().vibrate(context)
                startGuide()
            },
            backgroundColor = if (isDarkMode){
                Color(0xFA181818)
            }else{
                Color(0xFFFFFFFF)
            },
            border = BorderStroke(width = 0.3.dp, color = Color.Gray.copy(alpha = 0.3f)),
            modifier = Modifier.padding(end = 0.dp)
        ) {
            Icon(
                Icons.Filled.SnippetFolder,
                contentDescription = "指南",
                modifier = Modifier.background(Color.Transparent),
                tint = ComposeElementBundle.ColorPack.secondary
            )
        }
        //设置按钮
        ComposeElementBundle.CIRCLE_BUTTON_c_v1 (
            onClick = {
                ToolVibrate().vibrate(context)
                startSetting()
            },
            backgroundColor = if (isDarkMode){
                Color(0xFA181818)
            }else{
                Color(0xFFFFFFFF)
            },
            border = BorderStroke(width = 0.3.dp, color = Color.Gray.copy(alpha = 0.3f)),
            modifier = Modifier.padding(end = 15.dp)
        ) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "设置",
                modifier = Modifier.background(Color.Transparent),
                tint = ComposeElementBundle.ColorPack.secondary
            )
        }

    }
    @Composable
    fun ADVANCED_TOP_BAR_Brush(statusBarHeight_Dp: Dp, content_Height_Dp: Dp){
        ComposeElementBundle.BRUSH_AREA_TOP_c_v2(
            color = ComposeElementBundle.ColorPack.surface,
            statusBarHeight_Dp = statusBarHeight_Dp,
            content_Height_Dp = content_Height_Dp
        )
    }
    @SuppressLint("UnrememberedMutableState")
    @Composable
    fun ToolsListColumn(toolsList: List<ToolPackage>, animatedTopPadding: Dp) {
        //指定边距
        val contentPadding by derivedStateOf {
            PaddingValues(
                top = animatedTopPadding,
                bottom = 400.dp,
                start = 0.dp,
                end = 0.dp
            )
        }

        val listState = rememberLazyListState()
        val top by remember {
            derivedStateOf { listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0 }
        }
        isContentTop.value = top
        //
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().background(if (isDarkMode) Color(0xFF151515) else Color(0xFFFFFFFF)),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            //item { Spacer(modifier = Modifier.height(100.dp)) }
            items(toolsList) { tool ->
                ToolCard(
                    name = tool.name,
                    description = tool.description,
                    onClick = {
                        ToolVibrate().vibrate(this@MainActivity)
                        onClickListItem(tool.intent)
                    }
                )
            }
        }
    }
    @Composable
    fun ToolCard(name: String, description: String, onClick: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 3.dp)
                //.shadow(elevation = 5.dp, shape = RoundedCornerShape(12.dp), clip = false, spotColor = Color.Black.copy(alpha = 0.2f), ambientColor = Color.Black.copy(alpha = 1f))
                .uniformShadow()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(15.dp),
            border = BorderStroke(
                width = 0.5.dp,
                color = Color.Gray.copy(alpha = 0.1f)
            ),
            colors = CardDefaults.cardColors(
                containerColor = ComposeElementBundle.ColorPack.background,
            ),
            onClick = onClick
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(13.dp)
            ) {
                val ColorPack = ComposeElementBundle.ColorPack
                //大标题
                Text(
                    text = name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorPack.primary
                )
                //大小标题间距
                Spacer(modifier = Modifier.height(4.dp))
                //小标题或描述
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = ColorPack.secondary
                )
            }
        }
    }
    //自定义阴影
    @Suppress("DEPRECATION")
    fun Modifier.uniformShadow(
        blurRadius: Float = 20f,
        shadowColor: Color = ComposeElementBundle.ColorPack.secondary.copy(alpha = 0.05f)
    ) = this.drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                color = shadowColor
                asFrameworkPaint().maskFilter = android.graphics.BlurMaskFilter(
                    blurRadius,
                    android.graphics.BlurMaskFilter.Blur.NORMAL
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



    //处理卡片点击事件
    private fun onClickListItem(toolIntent: String) {
        consoleLog("接收到卡片点击事件丨事件意图字段：$toolIntent")

        //执行
        when(toolIntent){
            "MANAGER_INTENT_DARK_MODE_WALLPAPER_SWITCH" -> {
                startActivity(Intent(this, DarkModeActivity::class.java))
            }
            "MANAGER_INTENT_NOTIFICATION_MANAGER" -> {
                startActivity(Intent(this, NotificationManager::class.java))
            }
            "MANAGER_INTENT_VOLUME_CONTROL" -> {
                startActivity(Intent(this, VolumeControl::class.java))
            }
            "MANAGER_INTENT_WIDGET_MANAGER_CENTER" -> {
                startActivity(Intent(this, WidgetManager::class.java))
            }
            "MANAGER_INTENT_LOCAL_APP_MANAGER" -> {
                startActivity(Intent(this, LocalAppManager::class.java))
            }
            "MANAGER_INTENT_NONE" -> {
                showCustomToast("功能开发中")
            }

        }

    }
    //启动设置页面
    private fun startSetting() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }
    //启动指南
    private fun startGuide() {
        startActivity(Intent(this, GuidanceActivity::class.java))
    }
    //退出应用
    private fun exitApp() {
        consoleLog("主动退出应用")
        //先回桌面
        /*
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)

         */
        moveTaskToBack(true)
        //结束进程
        lifecycleScope.launch {
            delay(500)
            val pid = android.os.Process.myPid()
            android.os.Process.killProcess(pid)
        }
    }



    //深浅色模式检测+设置状态栏字体颜色
    fun colorModeDetector(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> true
            Configuration.UI_MODE_NIGHT_YES -> false
            else -> true
        }
    }
    fun setStatusBarFontColor() {
        val insetsController = WindowInsetsControllerCompat(
            window, window.decorView
        )
        insetsController.isAppearanceLightStatusBars = colorModeDetector(this)
    }
    //统一日志控制
    private fun consoleLog(msg: String, mark: Boolean = false) {
        if (mark) {
            Log.d("SuMing", msg)
        }
    }

}