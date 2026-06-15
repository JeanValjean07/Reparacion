package com.suming.reparacion.ActivityComponents.WidgetManager

import android.content.Context
import com.suming.reparacion.SettingsRequestCenter

object ConfigCenter {
    //基础颜色
    private val basic_color_dark = "FF000000"
    private val basic_color_dark_smooth = "FF313131"
    private val basic_color_light = "FFFFFFFF"
    private val basic_color_light_smooth = "FFFAFAFA"
    //模式代号
    val color_mode_dark = "dark"
    val color_mode_light = "light"

    //检查是否开启柔和色
    fun isSmoothColorEnabled(context: Context): Boolean {
        return SettingsRequestCenter.get_PREFS_Color_Config_Type_1_Smooth(context)
    }
    //检查当前颜色模式
    fun getCurrentColorMode(context: Context): String {
        val currentColorMode = SettingsRequestCenter.get_PREFS_Color_Config_Type_1_Mode(context)

        return if(currentColorMode == 1){
            color_mode_dark
        }else if(currentColorMode == 2){
            color_mode_light
        } else {
            color_mode_light
        }
    }

    fun EnableSmoothColor(context: Context){
        //检查当前颜色模式
        val currentColorMode = getCurrentColorMode(context)
        //根据模式写入颜色
        when (currentColorMode) {
            color_mode_dark -> {
                SettingsRequestCenter.set_PREFS_Color_Config_Actual(context, basic_color_dark_smooth)
            }
            color_mode_light -> {
                SettingsRequestCenter.set_PREFS_Color_Config_Actual(context, basic_color_light_smooth)
            }
        }
        //修改设置
        SettingsRequestCenter.set_PREFS_Color_Config_Type_1_Smooth(context, true)
    }

    fun DisableSmoothColor(context: Context){
        //检查当前颜色模式
        val currentColorMode = getCurrentColorMode(context)
        //根据模式写入颜色
        when (currentColorMode) {
            color_mode_dark -> {
                SettingsRequestCenter.set_PREFS_Color_Config_Actual(context, basic_color_dark)
            }
            color_mode_light -> {
                SettingsRequestCenter.set_PREFS_Color_Config_Actual(context, basic_color_light)
            }
        }
        //修改设置
        SettingsRequestCenter.set_PREFS_Color_Config_Type_1_Smooth(context, false)
    }
    //接收colorMode: "dark" or "light"
    fun setBasicConfigColorMode(context: Context, colorMode: String){
        //检查是否开启柔和色
        val useSmooth = isSmoothColorEnabled(context)
        //根据模式写入颜色和配置
        when (colorMode) {
            color_mode_dark -> {
                if(useSmooth){
                    SettingsRequestCenter.set_PREFS_Color_Config_Actual(context, basic_color_dark_smooth)
                    SettingsRequestCenter.set_PREFS_Color_Config_Type_1_Mode(context, 1)
                }else{
                    SettingsRequestCenter.set_PREFS_Color_Config_Actual(context, basic_color_dark)
                    SettingsRequestCenter.set_PREFS_Color_Config_Type_1_Mode(context, 1)
                }
            }
            color_mode_light -> {
                if(useSmooth){
                    SettingsRequestCenter.set_PREFS_Color_Config_Actual(context, basic_color_light_smooth)
                    SettingsRequestCenter.set_PREFS_Color_Config_Type_1_Mode(context, 2)
                }else{
                    SettingsRequestCenter.set_PREFS_Color_Config_Actual(context, basic_color_light)
                    SettingsRequestCenter.set_PREFS_Color_Config_Type_1_Mode(context, 2)
                }
            }
        }
    }
}