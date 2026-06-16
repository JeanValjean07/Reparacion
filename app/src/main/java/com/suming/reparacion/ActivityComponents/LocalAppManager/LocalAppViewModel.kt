package com.suming.reparacion.ActivityComponents.LocalAppManager

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.suming.reparacion.DataPack.AppInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.Collator
import java.util.Locale
import java.util.UUID

class LocalAppViewModel : ViewModel() {

    //应用列表
    private val _list = MutableStateFlow(listOf<AppInfo>())
    val list: StateFlow<List<AppInfo>> = _list.asStateFlow()
    //添加应用
    fun add(appInfo: AppInfo) {
        val currentList = _list.value.toMutableList()
        //只判断包名,标题,内容是否相同
        val existingIndex = currentList.indexOfFirst {
            it.appPackageName == appInfo.appPackageName

        }

        if (existingIndex >= 0) {
            //已存在时：覆盖成最新
            currentList[existingIndex] = appInfo
            //或把旧的移到顶部,丢掉新的
            //val notification = currentList.removeAt(existingIndex)
            //currentList.add(0, notification)
        } else {
            //不存在时：直接添加到列表顶部
            currentList.add(0, appInfo)
        }

        _list.value = currentList
    }


    //读取应用列表
    private var coroutine_read_application: CoroutineScope = CoroutineScope(Dispatchers.IO)
    fun loadLocalAppList(context: Context){
        coroutine_read_application.launch {
            //读取应用列表
            getApplicationList(context)
        }
    }

    fun getApplicationList(context: Context) {
        val packageManager = context.packageManager
        val packageInfo = packageManager.getInstalledPackages(0)
        val appList = mutableListOf<AppInfo>()

        for (item in packageInfo) {
            val appInfo = AppInfo(
                appName = item.applicationInfo?.loadLabel(packageManager).toString(),
                appPackageName = item.packageName,
                appNameChar = item.applicationInfo?.loadLabel(packageManager).toString().first().toString(),
                uniqueID = UUID.randomUUID().toString(),
            )
            appList.add(appInfo)
        }
        // 排序
        val collator = Collator.getInstance(Locale.CHINA)

        //根据应用名称排序
        appList.sortWith(Comparator { app1, app2 ->
            collator.compare(app1.appName, app2.appName)
        })
        _list.value = appList
    }




    //统一日志控制
    private fun consoleLog(msg: String, mark: Boolean = true) {
        if (mark) {
            Log.d("SuMing", msg)
        }
    }

}