package com.suming.reparacion.ActivityComponents.WidgetManager;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.compose.ui.text.font.FontWeight;

import com.suming.reparacion.R;
import com.suming.reparacion.SettingsRequestCenter;

import java.util.Arrays;
import java.util.List;


public class ClockWidgetProvider extends AppWidgetProvider {


    @Override
    public void onEnabled(Context context) {
        consoleLog("ClockWidgetProvider: onEnabled" );
    }
    @Override
    public void onDisabled(Context context) {
        consoleLog("ClockWidgetProvider: onDisabled" );
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions){
        consoleLog("ClockWidgetProvider: onAppWidgetOptionsChanged" );
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        consoleLog("ClockWidgetProvider: onUpdate" );

        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //获取信息
        String action = intent.getAction();
        consoleLog("ClockWidgetProvider: onReceive: action: " + action);

        //响应更新小部件的广播
        if(AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            ComponentName provider = new ComponentName(context, ClockWidgetProvider.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }


        super.onReceive(context, intent);
    }



    //更新小部件(TextClock组件直接调用更新就能自己刷新)
    static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        consoleLog("ClockWidgetProvider: updateWidget: appWidgetId: " + appWidgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_desk_clock);

        //构造空点击事件
        Intent intent_no_action = new Intent(context, ClockWidgetProvider.class);
        intent_no_action.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent_no_action.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent_no_action = PendingIntent.getBroadcast(context, appWidgetId, intent_no_action,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //获取颜色
        int actualColor = Color.WHITE;
        try {
            String colorString = SettingsRequestCenter.get_PREFS_Color_Config_Actual(context);
            consoleLog("ClockWidgetProvider: updateWidget: 获取当前colorString为: " + colorString);
            actualColor = Color.parseColor("#" + colorString);
        } catch (Exception ignored) {
            String colorString = SettingsRequestCenter.get_PREFS_Color_Config_Actual(context);
            consoleLog("ClockWidgetProvider: updateWidget: 获取到actualColor时发生严重错误, 无法转换该颜色");
        }
        //应用颜色到全部
        views.setTextColor(R.id.widget_time, actualColor);
        views.setTextColor(R.id.widget_date, actualColor);
        views.setTextColor(R.id.widget_custom_text, actualColor);

        //应用自定义文本
        String customText = SettingsRequestCenter.get_PREFS_Color_Config_Custom_Text(context);
        views.setTextViewText(R.id.widget_custom_text, customText);


        //应用字体大小
        int fontSize = SettingsRequestCenter.get_PREFS_Widget_General_Text_Size(context);
        int fontSizeSecondary = SettingsRequestCenter.get_PREFS_Widget_General_Text_Size_Secondary(context);
        views.setTextViewTextSize(R.id.widget_time, TypedValue.COMPLEX_UNIT_SP, fontSize);
        views.setTextViewTextSize(R.id.widget_date, TypedValue.COMPLEX_UNIT_SP, fontSizeSecondary);
        views.setTextViewTextSize(R.id.widget_custom_text, TypedValue.COMPLEX_UNIT_SP, fontSizeSecondary);


        //应用空点击事件到全部
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent_no_action);
        views.setOnClickPendingIntent(R.id.widget_time, pendingIntent_no_action);
        views.setOnClickPendingIntent(R.id.widget_date, pendingIntent_no_action);
        views.setOnClickPendingIntent(R.id.widget_custom_text, pendingIntent_no_action);

        //Apply
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }




    //日志
    static final boolean LOG_ENABLE = false;
    static void consoleLog(String msg) {
        if (LOG_ENABLE) {
            Log.d("SuMing", msg);
        }
    }


}
