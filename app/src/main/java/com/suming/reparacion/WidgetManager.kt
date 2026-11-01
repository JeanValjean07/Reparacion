package com.suming.reparacion

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.suming.reparacion.ActivityComponents.WidgetManager.ConfigCenter
import com.suming.reparacion.AddonTools.ToolVibrate
import com.suming.reparacion.AddonTools.showCustomToast
import com.suming.reparacion.UI_SEG_Compose.ComposeElementBundle
import kotlin.math.roundToInt

class WidgetManager : AppCompatActivity() {


    //ctx
    private val context = this@WidgetManager
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



        //ComposableRoot
        setContent {
            ComposeRoot()
        }


    }

    private fun init() {
        //界面配置
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dark_mode)


        //初始化是否为多窗口模式
        isMultiWindowMode = isInMultiWindowMode

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



            //内容层
            ContentRoot(topAll_Height_Dp)

            //最顶层
            ComposeElementBundle.ADVANCED_TOP_BAR_c_v2_dual (
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
    fun ADVANCED_TOP_BAR_Start(title:String = "小组件管理"){
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

        //
        val top = isContentTop.value
        val capsuleProgress by animateFloatAsState(
            targetValue = if (!top) 1f else 0f,
            animationSpec = tween(durationMillis = 300),
            label = "capsuleProgress"
        )

        Box {

            //胶囊标题区
            ComposeElementBundle.CAPSULE_BUTTON_c_v2_avoid_circle (
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
                text_content = { text_content(title) },
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
                Icon (
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
        //问号按钮
        ComposeElementBundle.CIRCLE_BUTTON_c_v1 (
            onClick = {
                vibrate()
                showCustomToast("修改设置后，请点击一次桌面小组件以触发刷新")
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
                Icons.Filled.QuestionMark,
                contentDescription = "说明",
                modifier = Modifier.background(Color.Transparent),
                tint = ComposeElementBundle.ColorPack.secondary
            )
        }
        //刷新按钮
        ComposeElementBundle.CIRCLE_BUTTON_c_v1 (
            onClick = {
                vibrate()

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
                Icons.Filled.Refresh,
                contentDescription = "刷新",
                modifier = Modifier.background(Color.Transparent),
                tint = ComposeElementBundle.ColorPack.secondary
            )
        }

    }
    @Composable
    fun ADVANCED_TOP_BAR_Brush(statusBarHeight_Dp: Dp, content_Height_Dp: Dp){
        ComposeElementBundle.BRUSH_AREA_TOP_c_v2(
            color = if (isDarkMode) Color(0xFF000000) else Color(0xFFFFFFFF),
            statusBarHeight_Dp = if (isMultiWindowMode) 0.dp else statusBarHeight_Dp,
            content_Height_Dp = content_Height_Dp
        )
    }

    @Composable
    fun ContentRoot(topBarHeight: Dp) {
        //贴顶检测
        val scrollState = rememberScrollState()
        val top by remember {
            derivedStateOf { scrollState.value == 0 }
        }
        isContentTop.value = top

        Column (
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = topBarHeight),
        ) {
            //颜色配置卡片
            TextColorConfigCard()
            //文本配置卡片
            TextCustomAreaCard()
            //文字大小,字重自定义
            TextConfigCard()
        }
    }
    @Composable
    fun TextColorConfigCard() {
        //设置项1 - 使用字体颜色配置 < 1 - 仅黑白 / 2 - MD3自动取色 / 3 - 完全自定义 >
        val flag_colorConfig = remember { mutableIntStateOf(0) }
        val state_showColorConfigMenu = remember { mutableStateOf(false) }
        fun updateLocalPrefRemember_flag_colorConfig(){
            flag_colorConfig.intValue = SettingsRequestCenter.get_PREFS_Color_Config(this)
        }
        //颜色配置1-仅黑白 < 1 - 深色 / 2 - 浅色 >
        val flag_colorConFig_Type_1_Mode = remember { mutableIntStateOf(0) }
        val state_showColorConFigType_1_ModeMenu = remember { mutableStateOf(false) }
        val flag_colorConFig_Type_1_Smooth = remember { mutableStateOf(false) }
        fun updateLocalPrefRemember_flag_colorConFig_Type_1_Mode(value: Int = 0){
            if(value != 0){
                flag_colorConFig_Type_1_Mode.intValue = value
            }else{
                flag_colorConFig_Type_1_Mode.intValue = SettingsRequestCenter.get_PREFS_Color_Config_Type_1_Mode(this)
            }
        }
        fun updateLocalPrefRemember_flag_colorConFig_Type_1_smooth(value: Boolean = false){
            flag_colorConFig_Type_1_Smooth.value = value
        }
        //读取设置
        LaunchedEffect(Unit) {
            flag_colorConfig.intValue = SettingsRequestCenter.get_PREFS_Color_Config(this@WidgetManager)
            flag_colorConFig_Type_1_Mode.intValue = SettingsRequestCenter.get_PREFS_Color_Config_Type_1_Mode(this@WidgetManager)
            flag_colorConFig_Type_1_Smooth.value = SettingsRequestCenter.get_PREFS_Color_Config_Type_1_Smooth(this@WidgetManager)
        }



        //菜单内容
        @Composable
        fun card_menu_mode_content(){
            Column {
                val ColorPack = ComposeElementBundle.ColorPack
                DropdownMenuItem (
                    text = { Text(text = "简洁", fontSize = 12.sp, color = ColorPack.primary) },
                    onClick = {
                        vibrate()
                        //
                        SettingsRequestCenter.set_PREFS_Color_Config(this@WidgetManager, 1)
                        updateLocalPrefRemember_flag_colorConfig()
                        //关闭菜单
                        state_showColorConfigMenu.value = false
                    }
                )
                DropdownMenuItem (
                    text = { Text(text = "Material Design 取色", fontSize = 12.sp, color = ColorPack.primary) },
                    onClick = {
                        vibrate()

                        SettingsRequestCenter.set_PREFS_Color_Config(this@WidgetManager, 2)
                        updateLocalPrefRemember_flag_colorConfig()
                        //关闭菜单
                        state_showColorConfigMenu.value = false
                    }
                )
                DropdownMenuItem (
                    text = { Text(text = "自定义颜色", fontSize = 12.sp, color = ColorPack.primary) },
                    onClick = {
                        vibrate()
                        SettingsRequestCenter.set_PREFS_Color_Config(this@WidgetManager, 3)
                        updateLocalPrefRemember_flag_colorConfig()
                        //关闭菜单
                        state_showColorConfigMenu.value = false
                    }
                )
            }

        }
        @Composable
        fun card_menu_dark_or_light_content(){
            Column {
                val ColorPack = ComposeElementBundle.ColorPack
                DropdownMenuItem(
                    text = { Text(text = "深色", fontSize = 12.sp, color = ColorPack.primary) },
                    onClick = {
                        vibrate()

                        ConfigCenter.setBasicConfigColorMode(this@WidgetManager, ConfigCenter.color_mode_dark)
                        flag_colorConFig_Type_1_Mode.intValue = 1
                        //关闭菜单
                        state_showColorConFigType_1_ModeMenu.value = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = "浅色", fontSize = 12.sp, color = ColorPack.primary) },
                    onClick = {
                        vibrate()

                        ConfigCenter.setBasicConfigColorMode(this@WidgetManager, ConfigCenter.color_mode_light)
                        flag_colorConFig_Type_1_Mode.intValue = 2
                        //关闭菜单
                        state_showColorConFigType_1_ModeMenu.value = false
                    }
                )
            }
        }
        @Composable
        fun card_content() {
            Column (
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                //首行 - 字体颜色选择按钮
                Row (
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column (
                        modifier = Modifier.wrapContentHeight().padding(start = 10.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "字体颜色",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorPack.primary,
                            modifier = Modifier.padding(start = 0.dp)
                        )
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(
                            text = "选择字体颜色",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = ColorPack.secondary,
                        )
                    }
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxHeight().wrapContentWidth()
                    ) {
                        Card (
                            modifier = Modifier
                                .wrapContentWidth()
                                .fillMaxHeight()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(5.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(if (isDarkMode) Color(0xFF5C6BC0) else Color(0xFFB39DDB) ),
                            onClick = {
                                vibrate()
                                state_showColorConfigMenu.value = true
                            },
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxHeight().wrapContentWidth()
                            ) {
                                Text (
                                    text = if (flag_colorConfig.intValue == 1) "简洁" else if (flag_colorConfig.intValue == 2) "Material Design 取色" else if (flag_colorConfig.intValue == 3) "自定义颜色" else "Error",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = ColorPack.secondary,
                                    modifier = Modifier.padding(start = 10.dp,end = 4.dp, top = 5.dp, bottom = 5.dp)
                                )
                                Icon (
                                    Icons.Filled.Add,
                                    contentDescription = "选择颜色配置",
                                    modifier = Modifier.height(15.dp).background(Color.Transparent).padding(end = 8.dp),
                                    tint = ColorPack.secondary,
                                )
                                if (state_showColorConfigMenu.value) {
                                    ComposeElementBundle.MENU_Dropdown_c_v1 (
                                        expanded = state_showColorConfigMenu.value,
                                        onDismissRequest = {
                                            state_showColorConfigMenu.value = false
                                        },
                                    ) {
                                        card_menu_mode_content()
                                    }
                                }
                            }
                        }
                    }
                }
                //分隔线
                HorizontalDivider (
                    thickness = 1.dp,
                    color = if (isDarkMode) Color(0x99212121) else Color(0xFFFAFAFA)
                )
                //显示不同面板
                when (flag_colorConfig.intValue) {
                    1 -> {
                        //简洁模式面板
                        Column (
                            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                        ) {
                            //整行 - 选择深浅色
                            Row (
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                //描述
                                Column (
                                    modifier = Modifier.wrapContentHeight().padding(start = 10.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text (
                                        text = "当前字体颜色",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorPack.primary,
                                        modifier = Modifier.padding(start = 0.dp)
                                    )
                                    Spacer(modifier = Modifier.padding(top = 5.dp))
                                    Text (
                                        text = "在简洁模式下，仅有深色和浅色两种可选",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = ColorPack.secondary,
                                    )
                                }
                                //选择深浅色菜单按钮
                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxHeight().wrapContentWidth()
                                ) {
                                    Card (
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .fillMaxHeight()
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        elevation = CardDefaults.cardElevation(5.dp),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.cardColors(if (isDarkMode) Color(0xFF5C6BC0) else Color(0xFFB39DDB) ),
                                        onClick = {
                                            ToolVibrate().vibrate(this@WidgetManager)
                                            state_showColorConFigType_1_ModeMenu.value = true
                                        },
                                    ) {
                                        Row (
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxHeight().wrapContentWidth()
                                        ) {
                                            Text (
                                                text = if (flag_colorConFig_Type_1_Mode.intValue == 1) "深色" else if (flag_colorConFig_Type_1_Mode.intValue == 2) "浅色" else "Error",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = ColorPack.secondary,
                                                modifier = Modifier.padding(start = 10.dp,end = 4.dp, top = 5.dp, bottom = 5.dp)
                                            )
                                            Icon (
                                                Icons.Filled.DarkMode,
                                                contentDescription = "选择颜色",
                                                modifier = Modifier.height(15.dp).background(Color.Transparent).padding(end = 8.dp),
                                                tint = ColorPack.secondary,
                                            )
                                            if (state_showColorConFigType_1_ModeMenu.value) {
                                                ComposeElementBundle.MENU_Dropdown_c_v1 (
                                                    expanded = state_showColorConFigType_1_ModeMenu.value,
                                                    onDismissRequest = {
                                                        state_showColorConFigType_1_ModeMenu.value = false
                                                    },
                                                ) {
                                                    card_menu_dark_or_light_content()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //第二行 - 柔和色模式
                            Row (
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column (
                                    modifier = Modifier.padding(start = 10.dp)
                                ) {
                                    Text (
                                        text = "柔和模式",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorPack.primary,
                                        modifier = Modifier.padding(start = 0.dp)
                                    )
                                    Spacer(modifier = Modifier.padding(top = 2.dp))
                                    Text (
                                        text = "开启后，黑色将被替换为更柔和的灰色",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = ColorPack.secondary,
                                        modifier = Modifier.padding(start = 0.dp)
                                    )
                                }
                                Row (
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Switch (
                                        checked = flag_colorConFig_Type_1_Smooth.value,
                                        onCheckedChange = {
                                            ToolVibrate().vibrate(this@WidgetManager)

                                            if(it){
                                                ConfigCenter.EnableSmoothColor(this@WidgetManager)
                                            }else{
                                                ConfigCenter.DisableSmoothColor(this@WidgetManager)
                                            }
                                            flag_colorConFig_Type_1_Smooth.value = it
                                        },
                                        modifier = Modifier.padding(end = 10.dp),
                                        colors = SwitchDefaults.colors(
                                            checkedTrackColor = if (isDarkMode) Color(0xFF5C6BC0) else Color(0xFFB39DDB) ,
                                        )
                                    )
                                }
                            }
                        }
                    }
                    2 -> {
                        //Material Design 取色面板

                    }
                    3 -> {
                        //自定义颜色面板

                    }
                }
            }
        }

        //Entrance
        ComposeElementBundle.CARD_WITH_CORNER_c_v1(
            onClick = {  },
            content = { card_content() }
        )
    }
    @Composable
    fun TextCustomAreaCard() {
        //焦点管理器
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        val state_textFieldFocused = remember { mutableStateOf(false) }
        //自定义文本
        var customText by remember { mutableStateOf("") }

        //加载配置
        LaunchedEffect(Unit) {
            customText = SettingsRequestCenter.get_PREFS_Color_Config_Custom_Text(this@WidgetManager)
        }


        @Composable
        fun card_content() {
            Column (
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                //标题
                Row (
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column (
                        modifier = Modifier.wrapContentHeight().padding(start = 10.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "自定义文本",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorPack.primary,
                            modifier = Modifier.padding(start = 0.dp)
                        )
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(
                            text = "自定义文本将显示在日期左侧",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = ColorPack.secondary,
                        )
                    }
                }
                //文本框
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.wrapContentHeight().fillMaxWidth()
                ) {
                    TextField (
                        value = customText,
                        onValueChange = { customText = it },
                        colors = TextFieldDefaults.colors(
                            //背景色
                            focusedContainerColor = if (isDarkMode) Color(0x30311B92) else Color(0x30D1C4E9),         //获得焦点时的背景色
                            unfocusedContainerColor = if (isDarkMode) Color(0xFF212121) else Color(0xFFEEEEEE),    //失去焦点时的背景色
                            //文字颜色
                            focusedTextColor = ColorPack.secondary,       //获得焦点时文字颜色
                            unfocusedTextColor = ColorPack.secondary,     //失去焦点时文字颜色
                            //下方横线颜色
                            focusedIndicatorColor = if (isDarkMode) Color(0xFFFAFAFA) else Color(0xFF212121),  //获得焦点时横线颜色
                            unfocusedIndicatorColor = Color.Transparent, //失去焦点时横线颜色
                            //光标
                            cursorColor = if (isDarkMode) Color(0xFFFAFAFA) else Color(0xFF212121),
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp, end = 0.dp, bottom = 5.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                //仅在失去焦点时触发保存
                                if (focusState.isFocused) {
                                    //
                                    state_textFieldFocused.value = true
                                }
                                if (!focusState.isFocused) {
                                    if(state_textFieldFocused.value){
                                        //仅在失去焦点时保存
                                        SettingsRequestCenter.set_PREFS_Color_Config_Custom_Text(this@WidgetManager, customText)
                                        //
                                        showCustomToast("已保存文本")
                                    }
                                }
                            },
                    )
                    Spacer(modifier = Modifier.padding(start = 5.dp))
                    Card (
                        modifier = Modifier.wrapContentWidth().padding(start = 10.dp, end = 10.dp),
                        elevation = CardDefaults.cardElevation(5.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(if (isDarkMode) Color(0xFF5C6BC0) else Color(0xFFB39DDB)),
                        onClick = {
                            ToolVibrate().vibrate(this@WidgetManager)
                            focusManager.clearFocus() },
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxHeight().wrapContentWidth()
                        ) {
                            Text(
                                text = "保存",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = ColorPack.secondary,
                                modifier = Modifier.padding(start = 10.dp,end = 4.dp, top = 5.dp, bottom = 5.dp)
                            )
                            Icon(
                                Icons.Filled.Save,
                                contentDescription = "保存文本",
                                modifier = Modifier.height(15.dp).background(Color.Transparent).padding(end = 8.dp),
                                tint = ColorPack.secondary,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.padding(start = 5.dp))
                    Card (
                        modifier = Modifier.wrapContentWidth().padding(start = 0.dp, end = 5.dp, top = 5.dp, bottom = 5.dp),
                        elevation = CardDefaults.cardElevation(5.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(if (isDarkMode) Color(0xFF5C6BC0) else Color(0xFFB39DDB)),
                        onClick = {
                            vibrate()

                            focusManager.clearFocus()
                        },
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxHeight().wrapContentWidth()
                        ) {
                            Text(
                                text = "取消",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = ColorPack.secondary,
                                modifier = Modifier.padding(start = 10.dp,end = 10.dp, top = 5.dp, bottom = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        //Entrance
        ComposeElementBundle.CARD_WITH_CORNER_c_v1 (
            onClick = {  },
            content = { card_content() }
        )
    }
    @Composable
    fun TextConfigCard() {
        //字体大小
        var textSize by remember { mutableIntStateOf(85) }
        var textSizeSecondary by remember { mutableIntStateOf(15) }
        //字重
        var textFontWeight by remember { mutableStateOf(FontWeight.Normal) }
        val fontWeightOptions = listOf(
            FontWeight.Thin,      // 0
            FontWeight.Light,     // 1
            FontWeight.Normal,    // 2
            FontWeight.Medium,    // 3
            FontWeight.Bold,      // 4
            FontWeight.Black      // 5
        )
        var sliderPosition by rememberSaveable { mutableFloatStateOf(2f) }
        var selectedIndex = sliderPosition.roundToInt()
        val selectedFontWeightText = when(selectedIndex){
            0 -> "FontWeight.Thin"
            1 -> "FontWeight.Light"
            2 -> "FontWeight.Normal"
            3 -> "FontWeight.Medium"
            4 -> "FontWeight.Bold"
            5 -> "FontWeight.Black"
            else -> "错误"
        }

        //读取
        LaunchedEffect(Unit) {
            textSize = SettingsRequestCenter.get_PREFS_Widget_General_Text_Size(this@WidgetManager)
            textSizeSecondary = SettingsRequestCenter.get_PREFS_Widget_General_Text_Size_Secondary(this@WidgetManager)
            selectedIndex = SettingsRequestCenter.get_PREFS_Widget_General_Text_Weight_Index(this@WidgetManager)
            sliderPosition = selectedIndex.toFloat()
        }

        @Composable
        fun card_content() {
            Column (
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                //主要字体大小
                Row(
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column (
                        modifier = Modifier.wrapContentHeight().padding(start = 10.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "主要字体大小",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorPack.primary,
                            modifier = Modifier.padding(start = 0.dp)
                        )
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(
                            text = "调整时钟等主要区域的字体大小",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = ColorPack.secondary,
                        )
                    }
                    Text(
                        text = "$textSize SP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = ColorPack.secondary,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.wrapContentHeight().padding(start = 10.dp, end = 10.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Slider(
                            value = textSize.toFloat(),
                            onValueChange = { textSize = it.toInt() },
                            valueRange = 30f..160f,
                            onValueChangeFinished = {
                                SettingsRequestCenter.set_PREFS_Widget_General_Text_Size(this@WidgetManager, textSize)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                //次级字体大小
                Row(
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column (
                        modifier = Modifier.wrapContentHeight().padding(start = 10.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "次级字体大小",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorPack.primary,
                            modifier = Modifier.padding(start = 0.dp)
                        )
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(
                            text = "调整日期和自定文本等次级区域的字体大小",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = ColorPack.secondary,
                        )
                    }
                    Text(
                        text = "$textSizeSecondary SP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = ColorPack.secondary,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.wrapContentHeight().padding(start = 10.dp, end = 10.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Slider(
                            value = textSizeSecondary.toFloat(),
                            onValueChange = { textSizeSecondary = it.toInt() },
                            valueRange = 10f..50f,
                            onValueChangeFinished = {
                                SettingsRequestCenter.set_PREFS_Widget_General_Text_Size_Secondary(this@WidgetManager, textSizeSecondary)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                //字重
                Row(
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column (
                        modifier = Modifier.wrapContentHeight().padding(start = 10.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "字重",
                            fontSize = 15.sp,
                            fontWeight = textFontWeight,
                            color = ColorPack.primary,
                            modifier = Modifier.padding(start = 0.dp)
                        )
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(
                            text = "调整字体粗细",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = ColorPack.secondary,
                        )
                    }
                    Text(
                        text = selectedFontWeightText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = ColorPack.secondary,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                }

                //字重滑块
                Row(
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.wrapContentHeight().padding(start = 10.dp, end = 10.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Slider(
                            value = sliderPosition,
                            onValueChange = { newValue ->
                                //计算索引
                                selectedIndex = newValue.roundToInt()


                                sliderPosition = newValue
                            },
                            valueRange = 0f..(fontWeightOptions.lastIndex).toFloat(),
                            steps = fontWeightOptions.size - 2,
                            onValueChangeFinished = {
                                SettingsRequestCenter.set_PREFS_Widget_General_Text_Weight_Index(this@WidgetManager, selectedIndex)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

            }
        }


        //Entrance
        ComposeElementBundle.CARD_WITH_CORNER_c_v1(
            onClick = {  },
            content = { card_content() }
        )
    }




}