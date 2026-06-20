package com.suming.reparacion.FunctionalPack

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import com.suming.reparacion.DataPack.AppInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object WallpaperSetor {


    private var wallpaperManager: WallpaperManager? = null
    private var coroutine_wild_setor = CoroutineScope(Dispatchers.IO)
    private var coroutine_running = false

    //仅返回是否接受本次设置命令
    fun applySystemWallpaper(bitmap: Bitmap, context: Context) : Boolean {
        if(coroutine_running){
            _state.value = state_command_running
            return false
        }
        coroutine_running = true
        _state.value = state_command_running

        coroutine_wild_setor.launch {
            wallpaperManager = WallpaperManager.getInstance(context)
            //执行设置(需要收集各系统执行情况,扩展方法自定义)

            wallpaperManager?.setBitmap(
                bitmap, null, false,
                WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
            )
            coroutine_running = false
            _state.value = state_command_success
        }
        return true
    }


    //状态码
    const val state_command_idle = "state_command_idle"
    const val state_command_success = "state_command_success"
    const val state_command_running = "state_command_running"
    //可观察的应用状态
    private val _state = MutableStateFlow(state_command_idle)
    val state: StateFlow<String> = _state.asStateFlow()



}