package com.orangebox.kit.notification.email.data

import com.orangebox.kit.notification.Notification

interface EmailData {
    @Throws(Exception::class)
    fun loadData(email: String, notification: Notification): String
}