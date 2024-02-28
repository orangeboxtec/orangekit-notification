package com.orangebox.kit.notification

import com.orangebox.kit.core.exception.BusinessException
import com.orangebox.kit.core.user.GeneralUser
import com.orangebox.kit.notification.email.data.EmailData
import com.orangebox.kit.notification.email.data.EmailDataTemplate
import java.util.*

class NotificationBuilder {
    private val notification: Notification

    init {
        notification = Notification()
        notification.fgReaded = false
        notification.id = UUID.randomUUID().toString()
        notification.creationDate = Date()
    }

    @Throws(Exception::class)
    fun build(): Notification {
        if (notification.to == null) {
            throw BusinessException("To needed")
        }
        return notification
    }

    fun setIdFrom(idFrom: String?): NotificationBuilder {
        notification.idFrom = idFrom
        return this
    }

    fun setInfo(info: Map<String, Any>?): NotificationBuilder {
        notification.info = info
        return this
    }

    fun setNameFrom(nameFrom: String?): NotificationBuilder {
        notification.nameFrom = nameFrom
        return this
    }

    fun setTo(user: GeneralUser?): NotificationBuilder {
        notification.to = user
        return this
    }

    fun setFrom(sender: Sender): NotificationBuilder {
        notification.from = sender
        return this
    }

    fun setCreationDate(creationDate: Date?): NotificationBuilder {
        notification.creationDate = creationDate
        return this
    }

    fun setMessage(message: String?): NotificationBuilder {
        notification.message = message
        return this
    }

    fun setIdLink(idLink: String?): NotificationBuilder {
        notification.idLink = idLink
        return this
    }

    fun setTitle(title: String?): NotificationBuilder {
        notification.title = title
        return this
    }

    fun setTypeSending(typeSending: TypeSendingNotificationEnum?): NotificationBuilder {
        notification.typeSending = typeSending
        return this
    }

    fun setTypeFrom(typeFrom: String?): NotificationBuilder {
        notification.typeFrom = typeFrom
        return this
    }

    fun setFgAlertOnly(fgAlertOnly: Boolean?): NotificationBuilder {
        notification.fgAlertOnly = fgAlertOnly
        return this
    }

    fun setEmailData(emailData: EmailData?): NotificationBuilder {
        notification.emailData = emailData
        return this
    }

    fun setEmailDataTemplate(emailDataTemplate: EmailDataTemplate?): NotificationBuilder {
        notification.emailDataTemplate = emailDataTemplate
        return this
    }

    fun setAttachment(attachment: ByteArray?): NotificationBuilder {
        notification.attachment = attachment!!
        return this
    }

    fun setAttachmentName(attachmentName: String?): NotificationBuilder {
        notification.attachmentName = attachmentName
        return this
    }

    fun setAttachmentFileMimeType(attachmentFileMimeType: String?): NotificationBuilder {
        notification.attachmentFileMimeType = attachmentFileMimeType
        return this
    }

    fun setActionn(action: String?): NotificationBuilder {
        notification.action = action
        return this
    }
}