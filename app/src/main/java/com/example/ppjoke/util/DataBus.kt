package com.example.ppjoke.util

import androidx.lifecycle.*
import java.util.concurrent.ConcurrentHashMap

object DataBus {

    private val eventMap = ConcurrentHashMap<String, StickyLiveData<*>>()

    fun <T> with(eventName: String): StickyLiveData<T> {
        //基于事件名称 订阅、分发消息
        //由于livedata 只能发送一种数据类型
        //所以不同的event事件，需要使用不同的livadata实例去分发
        var liveData = eventMap[eventName]
        if (liveData == null) {
            liveData = StickyLiveData<T>(eventName)
            eventMap[eventName] = liveData
        }
        return liveData as StickyLiveData<T>
    }

    class StickyLiveData<T>(private val eventName: String) : LiveData<T>() {
        var mStickyData: T? = null
        var mVersion = 0

        fun setStickyData(stickyData: T) {
            //只能在主线程发送数据
            mStickyData = stickyData
            setValue(stickyData)
        }

        fun postStickyData(stickyData: T) {
            //既能在主线程也能在子线程发送数据
            mStickyData = stickyData
            postValue(stickyData)
        }

        override fun setValue(value: T) {
            mVersion++
            super.setValue(value)
        }

        override fun postValue(value: T) {
            mVersion++
            super.postValue(value)
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            observerSticky(owner, false, observer)
        }

        fun observerSticky(owner: LifecycleOwner, sticky: Boolean, observer: Observer<in T>) {
            //允许指定注册的观察者是否需要关心粘性事件
            //sticky为true，如果之前存在已经发送的数据，那么这个observer会受到之前的粘性事件消息
            owner.lifecycle.addObserver(LifecycleEventObserver { source, event ->
                //监听宿主发生销毁事件，主动将livedata移除
                if (event == Lifecycle.Event.ON_DESTROY) {
                    eventMap.remove(eventName)
                }
            })
            super.observe(owner, StickyObserver(this, sticky, observer))
        }

        class StickyObserver<T>(
            val stickyLiveData: StickyLiveData<T>,
            val sticky: Boolean,
            val observer: Observer<in T>
        ) : Observer<T> {
            // lastVersion 和 livedata 的 mVersion 对齐的原因是为了控制粘性事件的分发
            private var lastVersion = stickyLiveData.mVersion
            override fun onChanged(t: T) {

                if (lastVersion >= stickyLiveData.mVersion) {
                    //说明 stickyLiveData 没有更新的数据需要发送
                    if (sticky&&stickyLiveData.mStickyData!=null){
                        observer.onChanged(stickyLiveData.mStickyData)
                    }
                    return
                }

//                lastVersion = stickyLiveData.mVersion
                observer.onChanged(t)
            }

        }
    }
}


