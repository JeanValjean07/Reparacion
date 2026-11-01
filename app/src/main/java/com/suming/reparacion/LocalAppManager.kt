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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import com.suming.reparacion.ActivityComponents.LocalAppManager.LocalAppFragment
import com.suming.reparacion.ActivityComponents.LocalAppManager.LocalAppViewModel
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
    //ComposeColorPack
    private lateinit var ColorPack: ColorScheme
    //深色模式标记
    private var isDarkMode: Boolean = false
    //是否为多窗口模式
    private var isMultiWindowMode: Boolean = isInMultiWindowMode
    //振动
    private fun vibrate(){
        ToolVibrate().vibrate(context)
    }
    //日志
    private fun consoleLog(msg: String, mark: Boolean = true) {
        if (mark) {
            Log.d("SuMing", msg)
        }
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()



        //启动读取
        if (savedInstanceState == null){
            localAppViewModel.loadLocalAppList(this)
        }

        //托管给ComposableRoot
        setContent {
            ComposeRoot()
        }

    }

    private fun init() {
        //界面配置
        enableEdgeToEdge()
        @Suppress("DEPRECATION")
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        //手动设置状态栏字体颜色
        setStatusBarFontColor()

        //初始化是否为多窗口模式
        isMultiWindowMode = isInMultiWindowMode

    }

    override fun onResume() {
        super.onResume()





    }




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
                brushContent = { ADVANCED_TOP_BAR_Brush(statusBarHeight_Dp, ComposeElementBundle.ADVANCED_TOP_BAR_content_height_Dp) }
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

        //应用列表状态监听
        val listState = rememberLazyListState()
        val top by remember {
            derivedStateOf { listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0 }
        }
        isContentTop.value = top



        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().background(Color.Transparent),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(applicationList) { application ->
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    //显示卡片
                    ComposeElementBundle.CARD_WITH_CORNER_c_v1(
                        onClick = {
                            vibrate()
                            selectedUUID = application.uniqueID; showMenu = true
                        },
                        content = {
                            AppCard_Content(application.appPackageName, application.appName)
                        }
                    )
                    //显示选项菜单
                    if (selectedUUID == application.uniqueID) {
                        ComposeElementBundle.MENU_Dropdown_c_v1(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            content = { AppCard_Menu_Content(application) },
                        )
                    }
                }
            }
        }

    }
    private var selectedUUID by mutableStateOf<String?>(null)
    @Composable
    fun AppCard_Menu_Content(application:AppInfo) {
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
                            vibrate()

                            selectedUUID = null
                        }
                    )
            DropdownMenuItem(
                        text = { Text(text = "进入系统通知设置", fontSize = 12.sp, color = ColorPack.primary) },
                        onClick = {
                            vibrate()
                            //打开系统通知设置页面
                            openNotificationSetting(application.appPackageName)
                            //关闭菜单
                            selectedUUID = null
                        }
                    )
            DropdownMenuItem(
                        text = { Text(text = "进入应用管理页面", fontSize = 12.sp, color = ColorPack.primary) },
                        onClick = {
                            vibrate()
                            //打开应用管理页面
                            openAppSettingPage(application.appPackageName)
                            //关闭菜单
                            selectedUUID = null
                        }
                    )
        }
    }
    @Composable
    fun AppCard_Content(packageName: String, appName: String) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(13.dp)
        ){
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
                text_content = { text_content("应用管理") },
            )

            //返回按钮
            ComposeElementBundle.CIRCLE_BUTTON_c_v1 (
                onClick = {
                    vibrate()
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
                vibrate()
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
        ComposeElementBundle.BRUSH_AREA_TOP_c_v2 (
            color = if (isDarkMode) Color(0xFF000000) else Color(0xFFFFFFFF),
            statusBarHeight_Dp = if (isMultiWindowMode) 0.dp else statusBarHeight_Dp,
            content_Height_Dp = content_Height_Dp
        )
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


}