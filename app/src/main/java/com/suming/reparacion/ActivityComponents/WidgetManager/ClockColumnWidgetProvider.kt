package com.suming.reparacion.ActivityComponents.WidgetManager

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.core.graphics.toColorInt
import com.suming.reparacion.R
import com.suming.reparacion.SettingsRequestCenter


class ClockColumnWidgetProvider : AppWidgetProvider() {


    override fun onEnabled(context: Context?) {
        super.onEnabled(context)


    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)


    }


    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        consoleLog("ClockColumnWidgetProvider: onAppWidgetOptionsChanged")
    }


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        consoleLog("ClockColumnWidgetProvider: onUpdate")

        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }


    override fun onReceive(context: Context, intent: Intent) {
        //获取信息
        val action = intent.action
        consoleLog("ClockColumnWidgetProvider: onReceive: action: $action")

        //响应更新小部件的广播
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE == action) {
            val provider = ComponentName(context, ClockColumnWidgetProvider::class.java)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(provider)
            onUpdate(context, appWidgetManager, appWidgetIds)
        }


        super.onReceive(context, intent)
    }


    //更新小部件(TextClock组件直接调用更新就能自己刷新)
    fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        consoleLog("ClockColumnWidgetProvider: updateWidget: appWidgetId: $appWidgetId")
        val views = RemoteViews(context.packageName, R.layout.widget_desk_clock_column)

        //构造空点击事件
        val intent_no_action = Intent(context, ClockColumnWidgetProvider::class.java)
        intent_no_action.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        intent_no_action.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        val pendingIntent_no_action = PendingIntent.getBroadcast(
            context, appWidgetId, intent_no_action,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        //获取颜色
        var actualColor = Color.WHITE
        try {
            val colorString = SettingsRequestCenter.get_PREFS_Color_Config_Actual(context)
            consoleLog("ClockColumnWidgetProvider: updateWidget: 获取当前colorString为: $colorString")
            actualColor = ("#$colorString").toColorInt()
        } catch (_: Exception) {
            val colorString = SettingsRequestCenter.get_PREFS_Color_Config_Actual(context)
            consoleLog("ClockColumnWidgetProvider: updateWidget: 获取到actualColor时发生严重错误, 无法转换该颜色")
        }
        //应用颜色到全部
        views.setTextColor(R.id.widget_time_hour, actualColor)
        views.setTextColor(R.id.widget_time_minute, actualColor)
        views.setTextColor(R.id.widget_date, actualColor)
        views.setTextColor(R.id.widget_custom_text, actualColor)


        //应用字体大小
        val fontSize = SettingsRequestCenter.get_PREFS_Widget_General_Text_Size(context)
        val fontSizeSecondary = SettingsRequestCenter.get_PREFS_Widget_General_Text_Size_Secondary(context)
        views.setTextViewTextSize(R.id.widget_time_hour, TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
        views.setTextViewTextSize(R.id.widget_time_minute, TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
        views.setTextViewTextSize(R.id.widget_date, TypedValue.COMPLEX_UNIT_SP, fontSizeSecondary.toFloat())
        views.setTextViewTextSize(R.id.widget_custom_text, TypedValue.COMPLEX_UNIT_SP, fontSizeSecondary.toFloat())


        //应用自定义文本
        val customText = SettingsRequestCenter.get_PREFS_Color_Config_Custom_Text(context)
        views.setTextViewText(R.id.widget_custom_text, customText)

        //应用空点击事件到全部
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent_no_action)
        views.setOnClickPendingIntent(R.id.widget_time_hour, pendingIntent_no_action)
        views.setOnClickPendingIntent(R.id.widget_time_minute, pendingIntent_no_action)
        views.setOnClickPendingIntent(R.id.widget_date, pendingIntent_no_action)
        views.setOnClickPendingIntent(R.id.widget_custom_text, pendingIntent_no_action)

        //Apply
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    //统一日志控制
    private fun consoleLog(msg: String, mark: Boolean = true) {
        if (mark) {
            Log.d("SuMing", msg)
        }
    }


}