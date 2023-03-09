package com.orangebox.kit.notification.email.data

import com.orangebox.kit.notification.Notification

class BasicEmailData : EmailData {
    override fun loadData(email: String, notification: Notification): String {
        var email = email
        return email.replace("\${message desc}", notification.message!!).also { email = it }
    }
}