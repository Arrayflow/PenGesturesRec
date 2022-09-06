package com.example.pengesturetest

import android.content.Context
import android.view.MotionEvent
import android.view.View

interface ListenTouchEvent {
    fun onTouchView(motionEvent: MotionEvent) {

    }
}