package com.orangebox.kit.notification

import com.orangebox.kit.core.user.GeneralUser
import com.orangebox.kit.notification.email.data.EmailData
import com.orangebox.kit.notification.email.data.EmailDataTemplate
import java.util.*

class Notification {

    var id: String? = null

    var idFrom: String? = null

    var nameFrom: String? = null

    var idLink: String? = null

    var action: String? = null

    var creationDate: Date? = null

    var message: String? = null

    var title: String? = null

    var attachment: ByteArray? = null

    var attachmentName: String? = null

    var attachmentFileMimeType: String? = null

    var fgReaded: Boolean? = null

    var count: Int? = null

    var typeSending: TypeSendingNotificationEnum? = null

    var typeFrom: String? = null

    var to: GeneralUser? = null

    var fgAlertOnly: Boolean? = null

    var emailDataTemplate: EmailDataTemplate? = null

    var emailData: EmailData? = null

    var info: Map<String, Any>? = null
}