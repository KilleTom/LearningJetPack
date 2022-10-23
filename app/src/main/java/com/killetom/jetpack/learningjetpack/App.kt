package com.killetom.jetpack.learningjetpack

import android.app.Application
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

/**
 * create by 易庞宙(KilleTom) on 2022/10/20 15:34
 * email : 1986545332@qq.com
 * description：
 **/
class App :Application() {

    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(object :LifecycleEventObserver{

            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                Log.i("ypzApplication",event.name)
            }
        })
    }

}