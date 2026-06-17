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
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.core.view.WindowInsetsControllerCompat
import com.suming.reparacion.ActivityComponents.LocalAppManager.LocalAppFragment
import com.suming.reparacion.ActivityComponents.LocalAppManager.LocalAppViewModel
import com.suming.reparacion.ActivityComponents.NotificationManager.NotificationManagerRepo
import com.suming.reparacion.DataPack.AppInfo

class LocalAppManager: AppCompatActivity() {

    //连接到LocalAppViewModel
    private val localAppViewModel = LocalAppViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //界面配置
        enableEdgeToEdge()
        @Suppress("DEPRECATION")
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        //手动设置状态栏字体颜色
        setStatusBarFontColor()

        //启动读取
        localAppViewModel.loadLocalAppList(this)

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
        //在root中取颜色模式
        isDarkMode = isSystemInDarkTheme()
        ColorPack = if (isDarkMode) DarkColorScheme else LightColorScheme
        //自动感知通知列表变化
        val noticeList by NotificationManagerRepo.list.collectAsState()
        //使用Box作为根布局
        Box(modifier = Modifier.fillMaxSize()) {
            //顶部栏高度值
            val statusBarHeight = WindowInsets.statusBars.getTop(LocalDensity.current)
            var topBarHeight by remember { mutableIntStateOf(300) }
            val topPaddingDp = with(LocalDensity.current) {
                (statusBarHeight + topBarHeight).toDp()
            }
            //自动感知应用列表变化
            val applicationList by localAppViewModel.list.collectAsState()


            //最底层
            GlobalBackPic()

            //应用列表
            AppListColumn(applicationList, topPaddingDp )

            //最顶层
            BrushArea()
            AdvancedTopBar(onHeightMeasured = { height ->
                topBarHeight = height
            })
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun AdvancedTopBar(onHeightMeasured: (height: Int) -> Unit) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(60.dp)
                .onGloballyPositioned { coordinates ->
                    onHeightMeasured(coordinates.size.height)
                },
            color = Color.Transparent,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(59.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircleButton(
                            onClick = { finish() },
                            backgroundColor = ColorPack.background.copy(alpha = 0.99f),
                            size = 40.dp,
                            border = BorderStroke(
                                width = 0.5.dp,
                                color = Color.Gray.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.padding(start = 15.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "退出",
                                modifier = Modifier.background(Color.Transparent),
                                tint = ColorPack.secondary
                            )
                        }
                        Text(
                            text = "应用管理",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorPack.primary,
                            modifier = Modifier.padding(start = 0.dp)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //设置按钮
                        CircleButton(
                            onClick = { startSettingFragment() },
                            backgroundColor = ColorPack.background.copy(alpha = 0.99f),
                            size = 40.dp,
                            border = BorderStroke(
                                width = 0.5.dp,
                                color = Color.Gray.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.padding(end = 15.dp)
                        ) {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = "设置",
                                modifier = Modifier.background(Color.Transparent),
                                tint = ColorPack.secondary
                            )
                        }
                    }
                }
            }
        }
    }
    @Composable
    fun CircleButton( onClick: () -> Unit,
                      modifier: Modifier = Modifier,
                      size: Dp = 30.dp,
                      backgroundColor: Color = MaterialTheme.colorScheme.primary,
                      gradient: Brush? = null,
                      border: BorderStroke? = null,
                      elevation: Dp = 3.dp,
                      enabled: Boolean = true,
                      content: @Composable () -> Unit ) {
        val backgroundModifier = when {
            gradient != null -> Modifier.background(gradient)
            else -> Modifier.background(backgroundColor)
        }
        Box(
            modifier = modifier
                .size(size)
                .shadow(
                    elevation = elevation,
                    shape = CircleShape,
                    clip = false,
                    spotColor = Color.Black.copy(alpha = 0.4f),  // 控制阴影颜色
                    ambientColor = Color.Black.copy(alpha = 0.4f)
                )
                .then(if (border != null) Modifier.border(border, CircleShape) else Modifier)
                .clip(CircleShape)
                .then(backgroundModifier)
                .clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(
                        bounded = true,
                        color = Color.Gray
                    )
                ) { onClick() },
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
    @Composable
    fun BrushArea(modifier: Modifier = Modifier, height: Dp = 90.dp) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ColorPack.surface.copy(alpha = 0.90f),
                            ColorPack.surface.copy(alpha = 0.0f)
                        ),
                    )
                )
        )
    }
    @Composable
    fun GlobalBackPic(){
        //放置一个全屏底部区域，未来支持设置自定义壁纸

        val darkTheme: Boolean = isSystemInDarkTheme()
        val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colorScheme.surface)
        )


    }
    @OptIn(ExperimentalMaterial3Api::class)
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
        //
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
                        onClick = { selectedUUID = application.uniqueID; showMenu = true }
                    )
                    //显示选项菜单
                    if (selectedUUID == application.uniqueID) {
                        Menu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.wrapContentSize().background(Color.Transparent),
                            content = {
                                Column() {
                                    Text(
                                        text = "删除",
                                        fontSize = 10.sp,
                                        color = ColorPack.secondary,
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
    fun Menu(
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit,
        application: AppInfo
    ) {
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
                modifier = Modifier.background(ColorPack.onBackground)
            ) {

                    Column {
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
                        Text(
                            text = "内部字符 ${application.appNameChar}",
                            fontSize = 10.sp,
                            color = ColorPack.secondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                        DropdownMenuItem(
                            text = { Text(text = "查看详情", fontSize = 12.sp, color = ColorPack.primary) },
                            onClick = {

                                selectedUUID = null
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "进入系统通知设置", fontSize = 12.sp, color = ColorPack.primary) },
                            onClick = {
                                //打开系统通知设置页面
                                openNotificationSetting(application.appPackageName)
                                //关闭菜单
                                selectedUUID = null
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "进入应用管理页面", fontSize = 12.sp, color = ColorPack.primary) },
                            onClick = {
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
            colors = CardDefaults.cardColors(containerColor = ColorPack.background,),
            onClick = onClick
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(13.dp)
            ) {
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
    //composable颜色配置
    private var isDarkMode: Boolean = false
    private lateinit var ColorPack: ColorScheme
    private val LightColorScheme = lightColorScheme(
        //全局底色
        surface = Color(0xFFFFFFFF),
        //一级和二级文字
        primary = Color(0xFF000000),
        secondary = Color(0xFF313131),
        //卡片底色
        background = Color(0xFFFFFFFF),
        onBackground = Color(0xFFFFFFFF),
        )
    private val DarkColorScheme = darkColorScheme(
        //全局底色
        surface = Color(0xFF000000),
        //一级和二级文字
        primary = Color(0xFFFFFFFF),
        secondary = Color(0xFFF6F6F6),
        //卡片底色
        background = Color(0xFF121212),
        onBackground = Color(0xFF212121),
    )


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