package com.orangebox.kit.notification

import com.orangebox.kit.core.configuration.ConfigurationService
import com.orangebox.kit.core.dao.OperationEnum
import com.orangebox.kit.notification.email.EmailService
import com.orangebox.kit.notification.sms.twilio.TwilioService
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.stream.Collectors
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class NotificationService {

    @Inject
    private lateinit var configurationService: ConfigurationService

    @Inject
    private lateinit var twilioService: TwilioService

    @Inject
    private lateinit var emailService: EmailService

    @Inject
    private lateinit var userNotificationsDAO: UserNotificationsDAO


    fun sendNotification(notification: Notification) {
        var usuNot: UserNotifications? = userNotificationsDAO.retrieve(UserNotifications(notification.to?.id))
        if (notification.fgAlertOnly == null || !notification.fgAlertOnly!!) {
            var inserir = false
            if (usuNot == null) {
                inserir = true
                usuNot = UserNotifications()
                usuNot.idUser = notification.to?.id
                usuNot.notifications = ArrayList()
            }
            usuNot.notifications?.add(notification)
            if (inserir) {
                userNotificationsDAO.insert(usuNot)
            } else {
                userNotificationsDAO.update(usuNot)
            }
        }
        sendMessage(notification)
    }

    fun countNotificationsUnreaded(idUser: String?): Int? {
        var count = 0
        val userNotifications: UserNotifications? = userNotificationsDAO.retrieve(UserNotifications(idUser))
        if (userNotifications != null && userNotifications.notifications != null) {
            count = userNotifications.notifications!!.stream()
                .filter { p: Notification -> p.fgReaded == null || !p.fgReaded!! }
                .collect(Collectors.toList())
                .size
        }
        return count
    }

    @Throws(Exception::class)
    private fun sendMessage(notification: Notification) {
        when (notification.typeSending) {
            TypeSendingNotificationEnum.APP -> sendMessageApp(notification)
            TypeSendingNotificationEnum.SMS -> sendMessageSMS(notification)
            TypeSendingNotificationEnum.EMAIL -> sendMessageEmail(notification)
            TypeSendingNotificationEnum.APP_EMAIL -> {
                sendMessageApp(notification)
                sendMessageEmail(notification)
            }

            else -> {}
        }
    }

    private fun sendMessageApp(notification: Notification) {
        if (notification.to?.tokenFirebase != null) {
            sendMessageFirebase(notification)
        }
    }


    @Deprecated("use sendMessageFirebase")
    private fun sendMessageAndroid(notification: Notification) {
        sendMessageFirebase(notification)
    }


    private fun sendMessageFirebase(notification: Notification) {
        val url = URL("https://fcm.googleapis.com/fcm/send")
        val conn = url.openConnection() as HttpURLConnection
        conn.useCaches = false
        conn.doInput = true
        conn.doOutput = true
        conn.requestMethod = "POST"
        conn.setRequestProperty("Authorization", "key=" + configurationService.loadByCode("GCM_KEY")?.value)
        conn.setRequestProperty("Content-Type", "application/json")
        val json = JSONObject()
        json.put("to", notification.to?.tokenFirebase)
        val info = JSONObject()
        info.put("body", notification.message)
        info.put("title", notification.title)
        json.put("notification", info)
        val wr = OutputStreamWriter(conn.outputStream)
        wr.write(json.toString())
        wr.flush()
        conn.inputStream
        wr.close()
        val br = BufferedReader(InputStreamReader(conn.inputStream))
        val message: String = IOUtils.toString(br)
        println(message)
    }


    private fun sendMessageSMS(notification: Notification?) {
        twilioService.sendMessage(
            notification!!.to!!.phoneCountryCode.toString() + notification.to!!.phoneNumber.toString(),
            notification.message
        )
    }

    private fun sendMessageEmail(notification: Notification) {
        emailService.sendEmailNotificationWithTemplate(notification)
    }

    fun listNotifications(idUser: String): List<Notification?>? {
        return userNotificationsDAO.listNotifications(idUser)
    }

    fun listNewNotifications(idUser: String, lastNotification: String?): List<Notification?>? {
        return userNotificationsDAO.listNewNotifications(idUser, lastNotification)
    }

    fun checkNotificationsAsReaded(idUser: String?) {
        val nots: UserNotifications? = userNotificationsDAO.retrieve(UserNotifications(idUser))
        if (nots != null && nots.notifications != null) {
            for (notification in nots.notifications!!) {
                notification.fgReaded = true
            }
            userNotificationsDAO.update(nots)
        }
    }

    fun checkNotificationAsReaded(idUser: String?, idNotification: String?) {
        val nots: UserNotifications? = userNotificationsDAO.retrieve(UserNotifications(idUser))
        if (nots != null && nots.notifications != null) {
            val notification = nots.notifications!!.stream()
                .filter { p: Notification -> p.id == idNotification }
                .findAny()
                .orElse(null)
            if (notification != null) {
                notification.fgReaded = true
                userNotificationsDAO.update(nots)
            }
        }
    }
    fun cleanNotifications(idUser: String?) {
        val usuNot: UserNotifications? = userNotificationsDAO.retrieve(UserNotifications(idUser))
        if (usuNot != null) {
            usuNot.notifications = null
            userNotificationsDAO.update(usuNot)
        }
    }

    fun removeNotification(idTitle: String) {
        val list: List<UserNotifications>? = userNotificationsDAO.search(userNotificationsDAO.createBuilder()
            .appendParamQuery("notifications.id|notifications.message", idTitle, OperationEnum.OR_FIELDS)
            .build())
        if (list != null) {
            for (un in list) {
                val notification = un.notifications?.stream()
                    ?.filter { p: Notification -> p.id == idTitle || p.message != null && p.message == idTitle }
                    ?.findFirst()
                    ?.orElse(null)
                if (notification != null) {
                    un.notifications?.remove(notification)
                    userNotificationsDAO.update(un)
                }
            }
        }
    }
}