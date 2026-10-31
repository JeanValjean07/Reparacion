package com.suming.reparacion

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import com.suming.reparacion.ActivityComponents.LocalAppManager.LocalAppFragment
import com.suming.reparacion.ActivityComponents.LocalAppManager.LocalAppViewModel
import com.suming.reparacion.ActivityComponents.NotificationManager.NotificationManagerRepo
import com.suming.reparacion.AddonTools.ToolVibrate
import com.suming.reparacion.DataPack.AppInfo
import com.suming.reparacion.UI_SEG_Compose.ComposeElementBundle

class LocalAppManager: AppCompatActivity() {

    //连接到LocalAppViewModel
    private val localAppViewModel: LocalAppViewModel by viewModels()
    //ctx
    private val context = this@LocalAppManager
    //ComposeElementBundle
    private lateinit var ComposeElementBundle: ComposeElementBundle
    //深色模式标记
    private var isDarkMode: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //界面配置
        enableEdgeToEdge()
        @Suppress("DEPRECATION")
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        //手动设置状态栏字体颜色
        setStatusBarFontColor()

        //初始化颜色模式
        isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES



        //启动读取
        if (savedInstanceState == null){
            localAppViewModel.loadLocalAppList(this)
        }

        //托管给ComposableRoot
        setContent {
            ComposeRoot()
        }

    }

    override fun onResume() {
        super.onResume()





    }




    @Composable
    fun ComposeRoot() {
        //初始化组件仓库
        ComposeElementBundle = ComposeElementBundle(isDarkMode)


        //使用Box作为根布局
        Box(modifier = Modifier.fillMaxSize()) {

            //获取状态栏高度
            val statusBarHeight_Px = WindowInsets.statusBars.getTop(LocalDensity.current)
            val statusBarHeight_Dp = with(LocalDensity.current) { statusBarHeight_Px.toDp() }

            //缓存总高度值
            var topAll_Height_Px by remember { mutableIntStateOf(300) }
            val topAll_Height_Dp = with(LocalDensity.current) { topAll_Height_Px.toDp() }



            //自动感知应用列表变化
            val applicationList by localAppViewModel.list.collectAsState()
            //应用列表
            AppListColumn(applicationList, topAll_Height_Dp)


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
    @SuppressLint("UnrememberedMutableState")
    @Composable
    fun AppListColumn(applicationList: List<AppInfo>, animatedTopPadding: Dp) {
        //指定边距
        val contentPadding by derivedStateOf {
            PaddingValues(
                top = animatedTopPadding,
                bottom = 200.dp,
                start = 0.dp,
                end = 0.dp
            )
        }
        //点击菜单
        var showMenu by remember { mutableStateOf(false) }

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
            items(applicationList) { application ->
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppCard(
                        packageName = application.appPackageName,
                        appName = application.appName,
                        onClick = {
                            ToolVibrate().vibrate(this@LocalAppManager)
                            selectedUUID = application.uniqueID; showMenu = true }
                    )
                    //显示选项菜单
                    if (selectedUUID == application.uniqueID) {
                        Menu (
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            content = {
                                Column {
                                    Text(
                                        text = "删除",
                                        fontSize = 10.sp,
                                        color = ComposeElementBundle.ColorPack.secondary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                            },
                            application = application
                        )
                    }
                }
            }
        }
    }
    var selectedUUID by mutableStateOf<String?>(null)
    @Composable
    fun Menu(expanded:Boolean,onDismissRequest:() -> Unit,content:@Composable () -> Unit,application:AppInfo) {
        if (expanded) {
            DropdownMenu(
                expanded = true,
                offset = DpOffset(
                    x = 100.dp,
                    y = 0.dp
                ),
                onDismissRequest = onDismissRequest,
                shape = RoundedCornerShape(5.dp),
                containerColor = Color.Transparent,
                modifier = Modifier.background(ComposeElementBundle.ColorPack.onBackground)
            ) {
                Column {
                    val ColorPack = ComposeElementBundle.ColorPack
                    Text(
                        text = "ID $selectedUUID",
                        fontSize = 10.sp,
                        color = ColorPack.secondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                    Text(
                        text = "包名 ${application.appPackageName}",
                        fontSize = 10.sp,
                        color = ColorPack.secondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                    DropdownMenuItem(
                        text = { Text(text = "查看详情", fontSize = 12.sp, color = ColorPack.primary) },
                        onClick = {
                            ToolVibrate().vibrate(this@LocalAppManager)

                            selectedUUID = null
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = "进入系统通知设置", fontSize = 12.sp, color = ColorPack.primary) },
                        onClick = {
                            ToolVibrate().vibrate(this@LocalAppManager)
                            //打开系统通知设置页面
                            openNotificationSetting(application.appPackageName)
                            //关闭菜单
                            selectedUUID = null
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = "进入应用管理页面", fontSize = 12.sp, color = ColorPack.primary) },
                        onClick = {
                            ToolVibrate().vibrate(this@LocalAppManager)
                            //打开应用管理页面
                            openAppSettingPage(application.appPackageName)
                            //关闭菜单
                            selectedUUID = null
                        }
                    )
                }
            }
        }
    }
    @Composable
    fun AppCard(packageName: String, appName: String, onClick: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 3.dp)
                .uniformShadow()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(15.dp),
            border = BorderStroke(
                width = 0.5.dp,
                color = Color.Gray.copy(alpha = 0.1f)
            ),
            colors = CardDefaults.cardColors(containerColor = ComposeElementBundle.ColorPack.background,),
            onClick = onClick
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(13.dp)
            ) {
                val ColorPack = ComposeElementBundle.ColorPack
                //App名称
                Text(
                    text = appName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorPack.primary
                )
                //大小标题间距
                Spacer(modifier = Modifier.height(4.dp))
                //包名
                Text(
                    text = packageName,
                    fontSize = 9.sp,
                    color = ColorPack.secondary
                )
            }
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
                text = "应用管理",
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
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "退出",
                    modifier = Modifier.background(Color.Transparent),
                    tint = ComposeElementBundle.ColorPack.secondary
                )
            }


        }



    }
    @Composable
    fun ADVANCED_TOP_BAR_End(){
        //设置按钮
        ComposeElementBundle.CIRCLE_BUTTON_c_v1 (
            onClick = {
                ToolVibrate().vibrate(context)
                startSettingFragment()
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
    //自定义阴影
    @Suppress("DEPRECATION")
    fun Modifier.uniformShadow(
        blurRadius: Float = 15f,
        shadowColor: Color = Color.Black.copy(alpha = 0.1f)
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



    //打开系统通知设置页面
    private fun openNotificationSetting(packageName: String){
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        startActivity(intent)
    }
    //打开应用管理页面
    private fun openAppSettingPage(packageName: String){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
    //打开设置面板
    private fun startSettingFragment(){
        val fragment = LocalAppFragment.newInstance()
        fragment.show(supportFragmentManager, "LocalAppFragment")
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
    private fun consoleLog(msg: String, mark: Boolean = true) {
        if (mark) {
            Log.d("SuMing", msg)
        }
    }


}