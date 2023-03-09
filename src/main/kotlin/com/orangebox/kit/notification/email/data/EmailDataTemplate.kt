package com.orangebox.kit.notification.email.data

interface EmailDataTemplate {
    val data: Map<String?, Any?>?
    val templateId: Int?
}