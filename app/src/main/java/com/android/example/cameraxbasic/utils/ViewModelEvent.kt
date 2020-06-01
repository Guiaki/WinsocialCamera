package com.android.example.cameraxbasic.utils

import com.android.example.cameraxbasic.ui.fragments.GalleryFragment

class ViewModelEvent {
    var eventName: String = ""
    var eventValue: String = ""
    var handled: Boolean = false
    private set

    constructor(eventName: String, eventValue: String, handled: Boolean) {
        this.eventName = eventName
        this.eventValue = eventValue
        this.handled = handled
    }

    open fun handle(activity: GalleryFragment) {
        handled = true
    }
}