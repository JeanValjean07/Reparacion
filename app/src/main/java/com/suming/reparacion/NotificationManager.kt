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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.suming.reparacion.ActivityComponents.NotificationManager.NotificationManagerDelay
import com.suming.reparacion.ActivityComponents.NotificationManager.NotificationManagerFragment
import com.suming.reparacion.ActivityComponents.NotificationManager.NotificationManagerRepo
import com.suming.reparacion.AddonTools.ToolVibrate
import com.suming.reparacion.AddonTools.showCustomToast
import com.suming.reparacion.DataPack.NotificationPack
import com.suming.reparacion.UI_SEG_Compose.ComposeElementBundle

class NotificationManager: AppCompatActivity() {


    //ctx
    private val context = this@NotificationManager
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
    //日志
    private fun consoleLog(msg: String, mark: Boolean = true) {
        if (mark) {
            Log.d("SuMing", msg)
        }
    }






    @SuppressLint("UnsafeIntentLaunch")
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //界面配置
        enableEdgeToEdge()
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        //手动设置状态栏字体颜色
        setStatusBarFontColor()

        //检查权限
        checkPermission()


        val noticeCount = NotificationManagerRepo.getCount()
        NotificationManagerRepo.setNotificationCount(noticeCount)

        //托管给ComposableRoot
        setContent {
            ComposeRoot()
        }

    }

    override fun onResume() {
        super.onResume()
        //刷新权限
        checkPermission()
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




        Box(modifier = Modifier.fillMaxSize()) {
            //获取状态栏高度
            val statusBarHeight_Px = WindowInsets.statusBars.getTop(LocalDensity.current)
            val statusBarHeight_Dp = with(LocalDensity.current) { statusBarHeight_Px.toDp() }

            //缓存总高度值
            var topAll_Height_Px by remember { mutableIntStateOf(300) }
            val topAll_Height_Dp = with(LocalDensity.current) { topAll_Height_Px.toDp() }


            //自动感知通知列表变化
            val noticeList by NotificationManagerRepo.list.collectAsState()
            //通知列表
            NoticeListColumn(noticeList, topAll_Height_Dp)
            //错误提示区
            ErrorCover()


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
    @Composable
    fun ADVANCED_TOP_BAR_Start() {
        //标题文本
        @Composable
        fun text_content(text:String) {

            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color(0xFFFFFFFF) else Color(0xFF212121),
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        val top = isContentTop.value
        val capsuleProgress by animateFloatAsState (
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
                text_content = { text_content("通知管理") },
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
    fun ADVANCED_TOP_BAR_End() {
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
    fun ADVANCED_TOP_BAR_Brush(statusBarHeight_Dp: Dp, content_Height_Dp: Dp) {
        ComposeElementBundle.BRUSH_AREA_TOP_c_v2 (
            color = if (isDarkMode) Color(0xFF000000) else Color(0xFFFFFFFF),
            statusBarHeight_Dp = statusBarHeight_Dp,
            content_Height_Dp = content_Height_Dp
        )
    }
    @SuppressLint("UnrememberedMutableState")
    @Composable
    fun NoticeListColumn(noticeList: List<NotificationPack>, animatedTopPadding: Dp) {
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



        LazyColumn (
            state = listState,
            modifier = Modifier.fillMaxSize().background(Color.Transparent),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(noticeList) { notice ->
                Box (
                    modifier = Modifier.fillMaxWidth()
                ) {
                    //显示卡片
                    ComposeElementBundle.CARD_WITH_CORNER_c_v1(
                        onClick = {
                            vibrate()
                            selectedUUID = notice.uniqueID; showMenu = true
                        },
                        content = {
                            Notice_Card_Content(notice = notice)
                        }
                    )
                    //显示选项菜单
                    if (selectedUUID == notice.uniqueID) {
                        ComposeElementBundle.MENU_Dropdown_c_v1(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            content = { Notice_Card_Menu_Content(notice) },
                        )
                    }
                }
            }
        }
    }
    private var selectedUUID by mutableStateOf<String?>(null)
    @Composable
    fun Notice_Card_Content(notice:NotificationPack) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(13.dp)
        ) {
            //App名称
            Text(
                text = notice.packageName,
                fontSize = 9.sp,
                color = ColorPack.secondary
            )
            //大小标题间距
            Spacer(modifier = Modifier.height(4.dp))
            //大标题
            Text(
                text = notice.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = ColorPack.primary
            )
            //大小标题间距
            Spacer(modifier = Modifier.height(4.dp))
            //小标题或描述
            Text(
                text = notice.text,
                fontSize = 12.sp,
                color = ColorPack.secondary
            )
        }
    }
    @Composable
    fun Notice_Card_Menu_Content(notice:NotificationPack) {
        Text(
            text = "ID $selectedUUID",
            fontSize = 10.sp,
            color = ColorPack.secondary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
        Text(
            text = "包名 ${notice.packageName}",
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
                openNotificationSetting(notice.packageName)
                //关闭菜单
                selectedUUID = null
            }
        )
        DropdownMenuItem(
            text = { Text(text = "进入应用管理页面", fontSize = 12.sp, color = ColorPack.primary) },
            onClick = {
                vibrate()
                //打开应用管理页面
                openAppSettingPage(notice.packageName)
                //关闭菜单
                selectedUUID = null
            }
        )
        DropdownMenuItem(
            text = { Text(text = "隐藏", fontSize = 12.sp, color = ColorPack.primary) },
            onClick = {
                vibrate()
                //隐藏通知
                snoozeNotificationByKey(notice.key, notice.isOngoing)
                //关闭菜单
                selectedUUID = null
            }
        )
        DropdownMenuItem(
            text = { Text(text = "延后", fontSize = 12.sp, color = ColorPack.primary) },
            onClick = {
                vibrate()
                //延后通知
                startDelayFragment(notice.key,notice.packageName)
                //关闭菜单
                selectedUUID = null
            }
        )
        DropdownMenuItem(
            text = { Text(text = "不再收集该通知", fontSize = 12.sp, color = ColorPack.primary) },
            onClick = {
                vibrate()

                //关闭菜单
                selectedUUID = null
            }
        )
    }
    @Composable
    fun ErrorCover() {
        //全屏信息提示区，在权限未开启或发生错误时使用
        //先读取错误提示状态
        val showErrorCoverMark by NotificationManagerRepo.showErrorCoverMark.collectAsState()
        Box(
            modifier = Modifier
                .then(
                    if (showErrorCoverMark == "NOTIFICATION_ALL_RIGHT") {
                        Modifier.size(0.dp)
                    } else {
                        Modifier.fillMaxWidth().fillMaxHeight()
                    }
                )
                .background(ColorPack.surface)
        ) {
            when(showErrorCoverMark){
                //权限未开启
                "NOTIFICATION_ERROR_PERMISSION" -> {
                    ErrorCoverPermission()
                }
                //服务离线
                "NOTIFICATION_ERROR_SERVICE_OFFLINE" -> {
                    ErrorCoverServiceOffline()
                }
                //没有通知
                "NOTIFICATION_ERROR_ZERO_NOTICE_HERE" -> {
                    ErrorCoverZeroNotice()
                }
            }
        }
    }
    @Composable
    fun ErrorCoverPermission() {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //错误提示
            Text (
                text = "请开启通知访问权限",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = ColorPack.secondary
            )
            //按钮间距
            Spacer(modifier = Modifier.height(5.dp))
            //开启权限按钮
            ComposeElementBundle.CAPSULE_BUTTON_c_v3_just_text(
                text = "去开启权限",
                text_size = 12f,
                onClick = {
                    vibrate()
                    goEnablePermission()
                }
            )
            //按钮间距
            Spacer(modifier = Modifier.height(5.dp))
            //刷新权限状态按钮
            ComposeElementBundle.CAPSULE_BUTTON_c_v3_just_text(
                text = "刷新权限状态",
                text_size = 12f,
                onClick = {
                    vibrate()
                    checkPermission()
                }
            )
        }
    }
    @Composable
    fun ErrorCoverZeroNotice() {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //错误提示
            Text (
                text = "目前没有任何通知",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = ColorPack.secondary
            )
            //按钮间距
            Spacer(modifier = Modifier.height(5.dp))
            //待会儿再来按钮(退出当前页面)
            ComposeElementBundle.CAPSULE_BUTTON_c_v3_just_text (
                text = "待会儿再来看看",
                text_size = 12f,
                onClick = {
                    vibrate()
                    finish()
                }
            )
            //按钮间距
            Spacer(modifier = Modifier.height(5.dp))
            //再收集一次
            ComposeElementBundle.CAPSULE_BUTTON_c_v3_just_text (
                text = "再收集一次",
                text_size = 12f,
                onClick = {
                    vibrate()
                    getCurrentNotifications()
                }
            )
            //按钮间距
            Spacer(modifier = Modifier.height(5.dp))
            //发一条试试按钮
            ComposeElementBundle.CAPSULE_BUTTON_c_v3_just_text (
                text = "发一条试试",
                text_size = 12f,
                onClick =  {
                    vibrate()
                    sendTestNotification()
                }
            )
        }
    }
    @Composable
    fun ErrorCoverServiceOffline() {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //错误提示
            Text (
                text = "服务离线，请稍等",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = ColorPack.secondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text (
                text = "服务通常会在App启动后10秒内由系统启动。若超时，您可能需要重启App",
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = ColorPack.secondary
            )
            //按钮间距
            Spacer(modifier = Modifier.height(5.dp))
            //再等等按钮
            ComposeElementBundle.CAPSULE_BUTTON_c_v3_just_text (
                text = "再等等",
                text_size = 12f,
                onClick = {
                    vibrate()
                }
            )
        }
    }



    //划掉通知
    private fun cancelNotification(key: String) {
        NotificationManagerRepo.setNeedCancelKey(key)
    }
    //隐藏通知(极端延后)
    private fun snoozeNotificationByKey(key: String, isOngoing: Boolean) {
        //先检查:非常驻通知不允许隐藏,直接划掉,除非设置特殊允许了
        if(SettingsRequestCenter.get_PREFS_Notification_Hide_Normal(this)){
            //隐藏通知
            NotificationManagerRepo.setNeedSnoozeKey(key)
        }else{
            if(isOngoing){
                NotificationManagerRepo.setNeedSnoozeKey(key)
            }else{
                showCustomToast("该通知不是固定通知,无需隐藏,划掉即可,已帮您划掉")
                cancelNotification(key)
            }
        }
    }
    //延后通知
    private fun startDelayFragment(targetNotificationKey: String,packageName: String) {
        val fragment = NotificationManagerDelay.newInstance(targetNotificationKey,packageName)
        fragment.show(supportFragmentManager, "NotificationManagerDelay")
    }
    private fun delayNotificationByKey(key: String,seconds: Int) {
        NotificationManagerRepo.setNeedDelayKey(key,seconds)
    } //注意传入秒即可,服务自带转毫秒


    //检查权限
    private fun checkPermission() {
        val enabledListenerPackages = NotificationManagerCompat.getEnabledListenerPackages(this)
        val isNotificationListenerEnabled = enabledListenerPackages.contains(packageName)
        if(isNotificationListenerEnabled){
            NotificationManagerRepo.setIsPermissionGranted(true)
        }else{
            NotificationManagerRepo.setIsPermissionGranted(false)
        }
    }
    //去开启权限
    private fun goEnablePermission() {
        val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    //发一条测试通知
    @SuppressLint("MissingPermission")
    private fun sendTestNotification() {
        val notificationManager = NotificationManagerCompat.from(this)
        val notification = NotificationCompat.Builder(this, "test_channel")
            .setContentTitle("测试通知")
            .setContentText("这是一条测试通知")
            .setSmallIcon(R.drawable.ic_notification)
            .build()
        notificationManager.notify(0, notification)
    }
    //打开设置面板
    private fun startSettingFragment() {
        val fragment = NotificationManagerFragment.newInstance()
        fragment.show(supportFragmentManager, "NotificationManagerFragment")
    }
    //获取当前通知
    private fun getCurrentNotifications() {
        NotificationManagerRepo.setServiceConnect("SERVICE_INTENT_FETCH_ALL")
    }
    //打开系统通知设置页面
    private fun openNotificationSetting(packageName: String) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        startActivity(intent)
    }
    //打开应用管理页面
    private fun openAppSettingPage(packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
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