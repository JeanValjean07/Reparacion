package com.suming.reparacion

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BurstMode
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SnippetFolder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.suming.reparacion.ActivityComponents.MainViewModel
import com.suming.reparacion.ActivityComponents.WidgetManager.ConfigCenter
import com.suming.reparacion.AddonTools.showCustomToast
import com.suming.reparacion.DataPack.ToolPackage

class WidgetManager : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //界面配置
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dark_mode)
        //准备工作
        init()


        //ComposableRoot
        setContent {
            ComposeRoot()
        }


    }

    private fun init() {

    }





    @Composable
    fun ComposeRoot() {
        //在root中取颜色模式
        isDarkMode = isSystemInDarkTheme()
        ColorPack = if (isDarkMode) DarkColorScheme else LightColorScheme
        //使用Box作为根布局
        Box(modifier = Modifier.fillMaxSize()) {

            //顶部栏高度值
            val statusBarHeight = WindowInsets.statusBars.getTop(LocalDensity.current)
            var topBarHeight by remember { mutableIntStateOf(300) }
            val topPaddingDp = with(LocalDensity.current) {
                (statusBarHeight + topBarHeight).toDp()
            }

            //顶部栏高度值动画 也可不使用动画单纯传值
            //曲线可选 CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
            val animatedTopPadding by animateDpAsState(
                targetValue = topPaddingDp,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            )

            //最底层
            GlobalBackPic()

            //内容层
            ContentRoot(topPaddingDp)

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
                            text = "小组件管理",
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
                        CircleButton(
                            onClick = {
                                showCustomToast("修改设置后，请点击一次桌面小组件以触发刷新")
                            },
                            backgroundColor = ColorPack.background.copy(alpha = 0.99f),
                            size = 40.dp,
                            border = BorderStroke(
                                width = 0.5.dp,
                                color = Color.Gray.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.padding(end = 15.dp)
                        ) {
                            Icon(
                                Icons.Filled.QuestionMark,
                                contentDescription = "刷新",
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(ColorPack.surface)
        )


    }
    @Composable
    fun ContentRoot(topBarHeight: Dp) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = topBarHeight),
        ) {
            //颜色配置卡片
            TextColorConfigCard()
            //文本配置卡片
            TextCustomAreaCard()
        }
    }
    @Composable
    fun TextColorConfigCard(){
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

        //
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 10.dp, vertical = 3.dp)
                .uniformShadow()
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                .background(Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(15.dp),
            border = BorderStroke(
                width = 0.5.dp,
                color = Color.Gray.copy(alpha = 0.1f)
            ),
            colors = CardDefaults.cardColors(containerColor = ColorPack.background)
        ){
            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                //首行 - 字体颜色选择按钮
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxHeight().wrapContentWidth()
                    ) {
                        Card(
                            modifier = Modifier
                                .wrapContentWidth()
                                .fillMaxHeight()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(5.dp),
                            shape = RoundedCornerShape(20.dp),
                            //border = BorderStroke(width = 0.5.dp, color = Color.Gray.copy(alpha = 0.1f)),
                            colors = CardDefaults.cardColors(ColorPack.tertiary),
                            onClick = {
                                state_showColorConfigMenu.value = true
                            },
                        ){
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxHeight().wrapContentWidth()
                            ) {
                                Text(
                                    text = if (flag_colorConfig.intValue == 1) "简洁" else if (flag_colorConfig.intValue == 2) "Material Design 取色" else if (flag_colorConfig.intValue == 3) "自定义颜色" else "Error",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = ColorPack.secondary,
                                    modifier = Modifier.padding(start = 10.dp,end = 4.dp, top = 5.dp, bottom = 5.dp)
                                )
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "选择颜色配置",
                                    modifier = Modifier.height(15.dp).background(Color.Transparent).padding(end = 8.dp),
                                    tint = ColorPack.secondary,
                                )
                                if (state_showColorConfigMenu.value) {
                                    DropdownMenu(
                                        expanded = state_showColorConfigMenu.value,
                                        onDismissRequest = {
                                            state_showColorConfigMenu.value = false
                                        },
                                        modifier = Modifier.background(ColorPack.onBackground)
                                    ) {
                                        DropdownMenuItem(
                                            onClick = {
                                                SettingsRequestCenter.set_PREFS_Color_Config(this@WidgetManager, 1)
                                                updateLocalPrefRemember_flag_colorConfig()
                                                //关闭菜单
                                                state_showColorConfigMenu.value = false
                                            }
                                        ) {
                                            Text(
                                                text = "简洁",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = ColorPack.secondary,
                                                modifier = Modifier.padding(start = 10.dp,end = 4.dp, top = 5.dp, bottom = 5.dp)
                                            )
                                        }
                                        DropdownMenuItem(
                                            onClick = {
                                                SettingsRequestCenter.set_PREFS_Color_Config(this@WidgetManager, 2)
                                                updateLocalPrefRemember_flag_colorConfig()
                                                //关闭菜单
                                                state_showColorConfigMenu.value = false
                                            }
                                        ) {
                                            Text(
                                                text = "Material Design 取色",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = ColorPack.secondary,
                                                modifier = Modifier.padding(start = 10.dp,end = 4.dp, top = 5.dp, bottom = 5.dp)
                                            )
                                        }
                                        DropdownMenuItem(
                                            onClick = {
                                                SettingsRequestCenter.set_PREFS_Color_Config(this@WidgetManager, 3)
                                                updateLocalPrefRemember_flag_colorConfig()
                                                //关闭菜单
                                                state_showColorConfigMenu.value = false
                                            }
                                        ) {
                                            Text(
                                                text = "自定义颜色",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = ColorPack.secondary,
                                                modifier = Modifier.padding(start = 10.dp,end = 4.dp, top = 5.dp, bottom = 5.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //分隔线
                Divider(modifier = Modifier.height(0.5.dp).background(ColorPack.onBackground))
                //显示不同面板
                when (flag_colorConfig.intValue) {
                    1 -> {
                        //简洁模式面板
                        Column(
                            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                        ) {
                            //整行 - 选择深浅色
                            Row(
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                //描述
                                Column (
                                    modifier = Modifier.wrapContentHeight().padding(start = 10.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "当前字体颜色",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorPack.primary,
                                        modifier = Modifier.padding(start = 0.dp)
                                    )
                                    Spacer(modifier = Modifier.padding(top = 5.dp))
                                    Text(
                                        text = "在简洁模式下，仅有深色和浅色两种可选",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = ColorPack.secondary,
                                    )
                                }
                                //选择深浅色菜单按钮
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxHeight().wrapContentWidth()
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .fillMaxHeight()
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        elevation = CardDefaults.cardElevation(5.dp),
                                        shape = RoundedCornerShape(20.dp),
                                        //border = BorderStroke(width = 0.5.dp, color = Color.Gray.copy(alpha = 0.1f)),
                                        colors = CardDefaults.cardColors(ColorPack.tertiary),
                                        onClick = {
                                            state_showColorConFigType_1_ModeMenu.value = true
                                        },
                                    ){
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxHeight().wrapContentWidth()
                                        ) {
                                            Text(
                                                text = if (flag_colorConFig_Type_1_Mode.intValue == 1) "深色" else if (flag_colorConFig_Type_1_Mode.intValue == 2) "浅色" else "Error",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = ColorPack.secondary,
                                                modifier = Modifier.padding(start = 10.dp,end = 4.dp, top = 5.dp, bottom = 5.dp)
                                            )
                                            Icon(
                                                Icons.Filled.DarkMode,
                                                contentDescription = "选择颜色",
                                                modifier = Modifier.height(15.dp).background(Color.Transparent).padding(end = 8.dp),
                                                tint = ColorPack.secondary,
                                            )
                                            if (state_showColorConFigType_1_ModeMenu.value) {
                                                DropdownMenu(
                                                    expanded = state_showColorConFigType_1_ModeMenu.value,
                                                    onDismissRequest = {
                                                        state_showColorConFigType_1_ModeMenu.value = false
                                                    },
                                                    modifier = Modifier.background(ColorPack.onBackground)
                                                ) {
                                                    DropdownMenuItem(
                                                        onClick = {
                                                            ConfigCenter.setBasicConfigColorMode(this@WidgetManager, ConfigCenter.color_mode_dark)
                                                            flag_colorConFig_Type_1_Mode.intValue = 1
                                                            //关闭菜单
                                                            state_showColorConFigType_1_ModeMenu.value = false
                                                        }
                                                    ) {
                                                        Text(
                                                            text = "深色",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Normal,
                                                            color = ColorPack.secondary,
                                                            modifier = Modifier.padding(start = 10.dp,end = 4.dp, top = 5.dp, bottom = 5.dp)
                                                        )
                                                    }
                                                    DropdownMenuItem(
                                                        onClick = {
                                                            ConfigCenter.setBasicConfigColorMode(this@WidgetManager, ConfigCenter.color_mode_light)
                                                            flag_colorConFig_Type_1_Mode.intValue = 2
                                                            //关闭菜单
                                                            state_showColorConFigType_1_ModeMenu.value = false
                                                        }
                                                    ) {
                                                        Text(
                                                            text = "浅色",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Normal,
                                                            color = ColorPack.secondary,
                                                            modifier = Modifier.padding(start = 10.dp,end = 4.dp, top = 5.dp, bottom = 5.dp)
                                                        )
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //第二行 - 柔和色模式
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.padding(start = 10.dp)
                                ) {
                                    Text(
                                        text = "柔和模式",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorPack.primary,
                                        modifier = Modifier.padding(start = 0.dp)
                                    )
                                    Spacer(modifier = Modifier.padding(top = 2.dp))
                                    Text(
                                        text = "开启后，黑色将被替换为更柔和的灰色",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = ColorPack.secondary,
                                        modifier = Modifier.padding(start = 0.dp)
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Switch(
                                        checked = flag_colorConFig_Type_1_Smooth.value,
                                        onCheckedChange = {
                                            if(it){
                                                ConfigCenter.EnableSmoothColor(this@WidgetManager)
                                            }else{
                                                ConfigCenter.DisableSmoothColor(this@WidgetManager)
                                            }
                                            flag_colorConFig_Type_1_Smooth.value = it
                                        },
                                        modifier = Modifier.padding(end = 10.dp),
                                        colors = SwitchDefaults.colors(
                                            //checkedThumbColor = ColorPack.secondary,
                                            checkedTrackColor = ColorPack.tertiary,
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
    }
    @Composable
    fun TextCustomAreaCard(){
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

        //
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 10.dp, vertical = 3.dp)
                .uniformShadow()
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                .background(Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(15.dp),
            border = BorderStroke(
                width = 0.5.dp,
                color = Color.Gray.copy(alpha = 0.1f)
            ),
            colors = CardDefaults.cardColors(containerColor = ColorPack.background)
        ){
            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ){
                //标题
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.wrapContentHeight().fillMaxWidth()
                ) {
                    TextField(
                        value = customText,
                        onValueChange = { customText = it },
                        colors = TextFieldDefaults.colors(
                            //背景色
                            focusedContainerColor = ColorPack.onBackground,         //获得焦点时的背景色
                            unfocusedContainerColor = ColorPack.onBackground,    //失去焦点时的背景色
                            //文字颜色
                            focusedTextColor = ColorPack.secondary,       //获得焦点时文字颜色
                            unfocusedTextColor = ColorPack.secondary,     //失去焦点时文字颜色
                            //下方横线
                            focusedIndicatorColor = ColorPack.tertiary,  //获得焦点时横线颜色
                            unfocusedIndicatorColor = ColorPack.secondary, //失去焦点时横线颜色
                            //光标
                            cursorColor = ColorPack.tertiary,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp, end = 0.dp, bottom = 5.dp)
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                //仅在失去焦点时触发保存
                                if (focusState.isFocused) {
                                    consoleLog("TextField：焦点变更：focusState.isFocused")
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
                    Spacer(modifier = Modifier.padding(start = 10.dp))
                    Card(
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(50.dp)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(5.dp),
                        shape = RoundedCornerShape(20.dp),
                        //border = BorderStroke(width = 0.5.dp, color = Color.Gray.copy(alpha = 0.1f)),
                        colors = CardDefaults.cardColors(ColorPack.tertiary),
                        onClick = { focusManager.clearFocus() },
                    ){
                        Row(
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
                }
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
        onBackground = Color(0xFFF6F6F6),
        //主题色
        tertiary = Color(0xFFD1C4E9),

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
        //主题色
        tertiary = Color(0xFF6048A0),


        )




    //统一日志控制
    private fun consoleLog(msg: String, mark: Boolean = true) {
        if (mark) {
            Log.d("SuMing", msg)
        }
    }








}