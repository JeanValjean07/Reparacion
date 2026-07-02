package com.suming.reparacion

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit

@Suppress("unused")
object SettingsRequestCenter {

    //统一日志控制
    private fun consoleLog(msg: String, mark: Boolean = true) {
        if (mark) {
            Log.d("SuMing", msg)
        }
    }

    //设置清单-深色模式壁纸---------------------------------------------------
    private lateinit var PREFS_DarkMode: SharedPreferences
    private var state_PREFS_DarkMode_initialized = false

    //设置项：微动数值
    private var PREFS_SlightMove_value = -1
    fun set_PREFS_SlightMove_value(context: Context, value:Int){
        if (!state_PREFS_DarkMode_initialized) {
            PREFS_DarkMode = context.getSharedPreferences("PREFS_DarkMode", 0)
        }
        PREFS_SlightMove_value = value
        PREFS_DarkMode.edit { putInt("PREFS_SlightMove_value", value) }
    }
    fun get_PREFS_SlightMove_value(context: Context): Int {
        //确保配置清单已初始化
        if (!state_PREFS_DarkMode_initialized) {
            PREFS_DarkMode = context.getSharedPreferences("PREFS_DarkMode", 0)
            state_PREFS_DarkMode_initialized = true
        }
        //确保配置项已被读取过
        if (PREFS_SlightMove_value == -1) {
            PREFS_SlightMove_value = PREFS_DarkMode.getInt("PREFS_SlightMove_value", -1)
            if (PREFS_SlightMove_value == -1) {
                PREFS_DarkMode.edit { putInt("PREFS_SlightMove_value", 0) }
            }
        }
        return PREFS_SlightMove_value
    }
    //设置项：启用微动效果裁剪
    private var PREFS_SlightMove_Clip = -1
    fun set_PREFS_SlightMove_Clip(EnableSlightMove: Boolean){
        PREFS_SlightMove_Clip = if (EnableSlightMove) 1 else 0
        PREFS_DarkMode.edit { putInt("PREFS_SlightMove_Clip", if (EnableSlightMove) 1 else 0) }
    }
    fun get_PREFS_SlightMove_Clip(context: Context): Boolean{
        //确保配置清单已初始化
        if (!state_PREFS_DarkMode_initialized){
            PREFS_DarkMode = context.getSharedPreferences("PREFS_DarkMode", 0)
            state_PREFS_DarkMode_initialized = true
        }
        //确保配置项已被读取过
        if (PREFS_SlightMove_Clip == -1){
            PREFS_SlightMove_Clip = PREFS_DarkMode.getInt("PREFS_SlightMove_Clip", -1)
            if (PREFS_SlightMove_Clip == -1){
                PREFS_DarkMode.edit { putInt("PREFS_SlightMove_Clip", 0) }
            }
        }

        return PREFS_SlightMove_Clip == 1
    }
    //数值：微动量数值
    private var VALUE_SlightMove = 0
    fun set_VALUE_SlightMove(value: Int){
        VALUE_SlightMove = value
        PREFS_DarkMode.edit { putInt("VALUE_SlightMove", value) }
    }
    fun get_VALUE_SlightMove(context: Context): Int{
        //确保配置清单已初始化
        if (!state_PREFS_DarkMode_initialized){
            PREFS_DarkMode = context.getSharedPreferences("PREFS_DarkMode", 0)
            state_PREFS_DarkMode_initialized = true
        }

        VALUE_SlightMove = PREFS_DarkMode.getInt("VALUE_SlightMove", 0)


        return VALUE_SlightMove
    }
    //设置项：将裁剪后的图片保存到外部相册
    private var PREFS_Save_Clip_Out = -1
    fun set_PREFS_Save_Clip_Out(EnableSaveClipOut: Boolean){
        PREFS_Save_Clip_Out = if (EnableSaveClipOut) 1 else 0
        PREFS_DarkMode.edit { putInt("PREFS_Save_Clip_Out", if (EnableSaveClipOut) 1 else 0) }
    }
    fun get_PREFS_Save_Clip_Out(context: Context): Boolean{
        //确保配置清单已初始化
        if (!state_PREFS_DarkMode_initialized){
            PREFS_DarkMode = context.getSharedPreferences("PREFS_DarkMode", 0)
            state_PREFS_DarkMode_initialized = true
        }
        //确保配置项已被读取过
        if (PREFS_Save_Clip_Out == -1){
            PREFS_Save_Clip_Out = PREFS_DarkMode.getInt("PREFS_Save_Clip_Out", -1)
            if (PREFS_Save_Clip_Out == -1){
                PREFS_DarkMode.edit { putInt("PREFS_Save_Clip_Out", 0) }
            }
        }

        return PREFS_Save_Clip_Out == 1
    }
    //设置项：返回桌面后结束进程
    private var PREFS_End_Process_After = -1
    fun set_PREFS_End_Process_After(EnableEndProcessAfter: Boolean){
        PREFS_End_Process_After = if (EnableEndProcessAfter) 1 else 0
        PREFS_DarkMode.edit { putInt("PREFS_End_Process_After", if (EnableEndProcessAfter) 1 else 0) }
    }
    fun get_PREFS_End_Process_After(context: Context): Boolean{
        //确保配置清单已初始化
        if (!state_PREFS_DarkMode_initialized){
            PREFS_DarkMode = context.getSharedPreferences("PREFS_DarkMode", 0)
            state_PREFS_DarkMode_initialized = true
        }
        //确保配置项已被读取过
        if (PREFS_End_Process_After == -1){
            PREFS_End_Process_After = PREFS_DarkMode.getInt("PREFS_End_Process_After", -1)
            if (PREFS_End_Process_After == -1){
                PREFS_DarkMode.edit { putInt("PREFS_End_Process_After", 0) }
            }
        }

        return PREFS_End_Process_After == 1
    }
    //状态值：是否已设置浅色壁纸
    private var STATE_dark_paper_set = -1
    fun set_State_dark_paper_set(context: Context,isSet: Boolean){
        //确保已初始化表
        if(!state_PREFS_DarkMode_initialized){
            PREFS_DarkMode = context.getSharedPreferences("PREFS_DarkMode", 0)
            state_PREFS_DarkMode_initialized = true
        }

        STATE_dark_paper_set = if (isSet) 1 else 0
        PREFS_DarkMode.edit { putInt("STATE_dark_paper_set", STATE_dark_paper_set) }
    }
    fun get_State_dark_paper_set(context: Context): Boolean {
        //确保已初始化表
        if(!state_PREFS_DarkMode_initialized){
            PREFS_DarkMode = context.getSharedPreferences("PREFS_DarkMode", 0)
            state_PREFS_DarkMode_initialized = true
        }
        //确保配置项已被读取过
        if (STATE_dark_paper_set == -1){
            STATE_dark_paper_set = PREFS_DarkMode.getInt("STATE_dark_paper_set", -1)
            if (STATE_dark_paper_set == -1){
                PREFS_DarkMode.edit { putInt("STATE_dark_paper_set", 0) }
            }
        }

        return STATE_dark_paper_set == 1
    }
    //状态值：是否已设置深色壁纸
    private var STATE_light_paper_set = -1
    fun set_State_light_paper_set(context: Context,isSet: Boolean){
        //确保已初始化表
        if(!state_PREFS_DarkMode_initialized){
            PREFS_DarkMode = context.getSharedPreferences("PREFS_DarkMode", 0)
            state_PREFS_DarkMode_initialized = true
        }

        STATE_light_paper_set = if (isSet) 1 else 0
        PREFS_DarkMode.edit { putInt("STATE_light_paper_set", STATE_light_paper_set) }
    }
    fun get_State_light_paper_set(context: Context): Boolean {
        //确保已初始化表
        if(!state_PREFS_DarkMode_initialized){
            PREFS_DarkMode = context.getSharedPreferences("PREFS_DarkMode", 0)
            state_PREFS_DarkMode_initialized = true
        }
        //确保配置项已被读取过
        if (STATE_light_paper_set == -1){
            STATE_light_paper_set = PREFS_DarkMode.getInt("STATE_light_paper_set", -1)
            if (STATE_light_paper_set == -1){
                PREFS_DarkMode.edit { putInt("STATE_light_paper_set", 0) }
            }
        }

        return STATE_light_paper_set == 1
    }
    //状态值：磁贴当前状态
    private var STATE_tile_status_on_dark = -1
    fun set_State_tile_status_on_dark(context: Context,isDark: Boolean) {
        //确保已初始化表
        if (!state_PREFS_DarkMode_initialized) {
            PREFS_DarkMode = context.getSharedPreferences("PREFS_DarkMode", 0)
            state_PREFS_DarkMode_initialized = true
        }

        STATE_tile_status_on_dark = if (isDark) 1 else 0

        PREFS_DarkMode.edit { putInt("STATE_tile_status_on_dark", STATE_tile_status_on_dark) }
    }
    fun get_State_tile_status_on_dark(context: Context): Boolean {
        //确保已初始化表
        if (!state_PREFS_DarkMode_initialized) {
            PREFS_DarkMode = context.getSharedPreferences("PREFS_DarkMode", 0)
            state_PREFS_DarkMode_initialized = true
        }
        //确保配置项已被读取过
        if (STATE_tile_status_on_dark == -1){
            STATE_tile_status_on_dark = PREFS_DarkMode.getInt("STATE_tile_status_on_dark", -1)
            if (STATE_tile_status_on_dark == -1){
                PREFS_DarkMode.edit { putInt("STATE_tile_status_on_dark", 0) }
            }
        }
        return STATE_tile_status_on_dark == 1
    }



    //设置清单-通知管理器---------------------------------------------------
    private var PREFS_Notification: SharedPreferences? = null

    //设置项：保留内容为空的通知
    private var PREFS_Notification_Keep_Empty = -1
    fun set_PREFS_Notification_Keep_Empty(context: Context,isKeep: Boolean){
        if(PREFS_Notification == null){
            PREFS_Notification = context.getSharedPreferences("PREFS_Notification", 0)
        }

        PREFS_Notification_Keep_Empty = if (isKeep) 1 else 0
        PREFS_Notification?.edit { putInt("PREFS_Notification_Keep_Empty", PREFS_Notification_Keep_Empty) }
    }
    fun get_PREFS_Notification_Keep_Empty(context: Context): Boolean {
        if(PREFS_Notification == null){
            PREFS_Notification = context.getSharedPreferences("PREFS_Notification", 0)
        }
        PREFS_Notification_Keep_Empty = PREFS_Notification?.getInt("PREFS_Notification_Keep_Empty", -1) ?: -1
        if (PREFS_Notification_Keep_Empty == -1){
            PREFS_Notification?.edit { putInt("PREFS_Notification_Keep_Empty", 0) }
        }

        return PREFS_Notification_Keep_Empty == 1
    }
    //设置项：允许清除通知被隐藏
    private var PREFS_Notification_Hide_Normal = -1
    fun set_PREFS_Notification_Hide_Normal(context: Context,isHide: Boolean){
        if(PREFS_Notification == null){
            PREFS_Notification = context.getSharedPreferences("PREFS_Notification", 0)
        }
        PREFS_Notification_Hide_Normal = if (isHide) 1 else 0
        PREFS_Notification?.edit { putInt("PREFS_Notification_Hide_Normal", PREFS_Notification_Hide_Normal) }
    }
    fun get_PREFS_Notification_Hide_Normal(context: Context): Boolean {
        if (PREFS_Notification == null) {
            PREFS_Notification = context.getSharedPreferences("PREFS_Notification", 0)
        }
        PREFS_Notification_Hide_Normal = PREFS_Notification?.getInt("PREFS_Notification_Hide_Normal", -1) ?: -1
        if (PREFS_Notification_Hide_Normal == -1) {
            PREFS_Notification?.edit { putInt("PREFS_Notification_Hide_Normal", 0) }
        }
        return PREFS_Notification_Hide_Normal == 1
    }


    //设置清单-小组件---------------------------------------------------
    private var PREFS_Widget: SharedPreferences? = null
    const val PREFS_Widget_Name = "PREFS_Widget_Name"
    private fun initPREFS_Widget(context: Context){
        if(PREFS_Widget == null){
            PREFS_Widget = context.getSharedPreferences(PREFS_Widget_Name, 0)
        }
    }
    //设置项：字体颜色配置
    private var PREFS_Color_Config = 0
    fun set_PREFS_Color_Config(context: Context,config: Int){
        if(PREFS_Widget == null){
            PREFS_Widget = context.getSharedPreferences("PREFS_Widget", 0)
        }

        PREFS_Color_Config = config
        PREFS_Widget?.edit { putInt("PREFS_Color_Config", PREFS_Color_Config) }
    }
    fun get_PREFS_Color_Config(context: Context): Int {
        if(PREFS_Widget == null){
            PREFS_Widget = context.getSharedPreferences("PREFS_Widget", 0)
        }

        PREFS_Color_Config = PREFS_Widget?.getInt("PREFS_Color_Config", 1) ?: 1
        if (PREFS_Color_Config == 0){
            PREFS_Widget?.edit { putInt("PREFS_Color_Config", 1) }
        }
        return PREFS_Color_Config
    }
    //简洁模式下的深色/浅色
    private var PREFS_Color_Config_Type_1_Mode = 1
    fun set_PREFS_Color_Config_Type_1_Mode(context: Context,mode: Int){
        if(PREFS_Widget == null){
            PREFS_Widget = context.getSharedPreferences("PREFS_Widget", 0)
        }

        PREFS_Color_Config_Type_1_Mode = mode
        PREFS_Widget?.edit { putInt("PREFS_Color_Config_Type_1_Mode", PREFS_Color_Config_Type_1_Mode) }

    }
    fun get_PREFS_Color_Config_Type_1_Mode(context: Context): Int {
        if (PREFS_Widget == null) {
            PREFS_Widget = context.getSharedPreferences("PREFS_Widget", 0)
        }

        PREFS_Color_Config_Type_1_Mode = PREFS_Widget?.getInt("PREFS_Color_Config_Type_1_Mode", 1) ?: 1

        return PREFS_Color_Config_Type_1_Mode
    }
    //柔和色
    private var PREFS_Color_Config_Type_1_Smooth = -1
    fun set_PREFS_Color_Config_Type_1_Smooth(context: Context,useSmooth: Boolean){
        if(PREFS_Widget == null){
            PREFS_Widget = context.getSharedPreferences("PREFS_Widget", 0)
        }

        PREFS_Color_Config_Type_1_Smooth = if (useSmooth) 1 else 0
        PREFS_Widget?.edit { putInt("PREFS_Color_Config_Type_1_Smooth", PREFS_Color_Config_Type_1_Smooth) }

    }
    fun get_PREFS_Color_Config_Type_1_Smooth(context: Context): Boolean {
        if (PREFS_Widget == null) {
            PREFS_Widget = context.getSharedPreferences("PREFS_Widget", 0)
        }

        PREFS_Color_Config_Type_1_Smooth = PREFS_Widget?.getInt("PREFS_Color_Config_Type_1_Smooth", -1) ?: -1

        if (PREFS_Color_Config_Type_1_Smooth == -1) {
            set_PREFS_Color_Config_Type_1_Smooth(context, true)
        }

        return PREFS_Color_Config_Type_1_Smooth == 1
    }

    //实际颜色
    private var PREFS_Color_Config_Actual = ""
    fun set_PREFS_Color_Config_Actual(context: Context,color: String){
        if(PREFS_Widget == null){
            PREFS_Widget = context.getSharedPreferences("PREFS_Widget", 0)
        }

        PREFS_Color_Config_Actual = color
        PREFS_Widget?.edit { putString("PREFS_Color_Config_Actual", PREFS_Color_Config_Actual) }
    }
    @JvmStatic
    fun get_PREFS_Color_Config_Actual(context: Context): String {
        if (PREFS_Widget == null) {
            PREFS_Widget = context.getSharedPreferences("PREFS_Widget", 0)
        }
        PREFS_Color_Config_Actual = PREFS_Widget?.getString("PREFS_Color_Config_Actual", "") ?: ""
        if (PREFS_Color_Config_Actual == "") {
            PREFS_Color_Config_Actual = "FF000000"
            set_PREFS_Color_Config_Actual(context, PREFS_Color_Config_Actual)
        }
        return PREFS_Color_Config_Actual
    }
    //自定义文本
    private var PREFS_Color_Config_Custom_Text = ""
    fun set_PREFS_Color_Config_Custom_Text(context: Context,text: String){
        if(PREFS_Widget == null){
            PREFS_Widget = context.getSharedPreferences("PREFS_Widget", 0)
        }

        PREFS_Color_Config_Custom_Text = text
        PREFS_Widget?.edit { putString("PREFS_Color_Config_Custom_Text", PREFS_Color_Config_Custom_Text) }
    }
    @JvmStatic
    fun get_PREFS_Color_Config_Custom_Text(context: Context): String {
        if (PREFS_Widget == null) {
            PREFS_Widget = context.getSharedPreferences("PREFS_Widget", 0)
        }

        PREFS_Color_Config_Custom_Text =
            PREFS_Widget?.getString("PREFS_Color_Config_Custom_Text", "") ?: ""

        return PREFS_Color_Config_Custom_Text
    }

    //字体大小(sp直接单位数值)
    private var PREFS_Widget_General_Text_Size = -1
    const val PREFS_Widget_General_Text_Size_Name = "PREFS_Widget_General_Text_Size_Name"
    fun set_PREFS_Widget_General_Text_Size(context: Context,size: Int){
        initPREFS_Widget(context)

        PREFS_Widget_General_Text_Size = size
        PREFS_Widget?.edit { putInt(PREFS_Widget_General_Text_Size_Name, size) }
    }
    @JvmStatic
    fun get_PREFS_Widget_General_Text_Size(context: Context): Int {
        initPREFS_Widget(context)


        if (PREFS_Widget_General_Text_Size == -1) {
            PREFS_Widget_General_Text_Size =
                PREFS_Widget?.getInt(PREFS_Widget_General_Text_Size_Name, -1) ?: -1

            if (PREFS_Widget_General_Text_Size == -1) {
                set_PREFS_Widget_General_Text_Size(context, 85)
            }


        }


        return PREFS_Widget_General_Text_Size
    }
    //字体大小二级(sp直接单位数值)
    private var PREFS_Widget_General_Text_Size_Secondary = -1
    const val PREFS_Widget_General_Text_Size_Secondary_Name = "PREFS_Widget_General_Text_Size_Secondary_Name"
    fun set_PREFS_Widget_General_Text_Size_Secondary(context: Context,size: Int){
        initPREFS_Widget(context)

        PREFS_Widget_General_Text_Size_Secondary = size
        PREFS_Widget?.edit { putInt(PREFS_Widget_General_Text_Size_Secondary_Name, size) }
    }
    @JvmStatic
    fun get_PREFS_Widget_General_Text_Size_Secondary(context: Context): Int {
        initPREFS_Widget(context)



        if (PREFS_Widget_General_Text_Size_Secondary == -1) {
            PREFS_Widget_General_Text_Size_Secondary =
                PREFS_Widget?.getInt(PREFS_Widget_General_Text_Size_Secondary_Name, -1) ?: -1

            if (PREFS_Widget_General_Text_Size_Secondary == -1) {
                set_PREFS_Widget_General_Text_Size_Secondary(context, 15)
            }


        }


        return PREFS_Widget_General_Text_Size_Secondary
    }


    //字体字重(索引)
    private var PREFS_Widget_General_Text_Weight_Index = -1
    const val PREFS_Widget_General_Text_Weight_Index_Name = "PREFS_Widget_General_Text_Weight_Index_Name"
    fun set_PREFS_Widget_General_Text_Weight_Index(context: Context,index: Int){
        initPREFS_Widget(context)

        PREFS_Widget_General_Text_Weight_Index = index
        PREFS_Widget?.edit { putInt(PREFS_Widget_General_Text_Weight_Index_Name, index) }
    }
    @JvmStatic
    fun get_PREFS_Widget_General_Text_Weight_Index(context: Context): Int {
        initPREFS_Widget(context)

        if (PREFS_Widget_General_Text_Weight_Index == -1) {
            PREFS_Widget_General_Text_Weight_Index =
                PREFS_Widget?.getInt(PREFS_Widget_General_Text_Weight_Index_Name, -1) ?: -1

            if (PREFS_Widget_General_Text_Weight_Index == -1) {
                set_PREFS_Widget_General_Text_Weight_Index(context, 4)
            }
        }

        return PREFS_Widget_General_Text_Weight_Index
    }

}
