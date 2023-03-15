package com.orangebox.kit.notification

import com.orangebox.kit.core.user.GeneralUser
import com.orangebox.kit.notification.email.data.EmailDataTemplate
import io.quarkus.test.junit.QuarkusTest
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
class NotificationTest {

    @ConfigProperty(name = "test.notification.email.templateid")
    lateinit var emailTemplateId: String


    @Inject
    lateinit var notificationService: NotificationService



    fun getUser(): GeneralUser{
        return object : GeneralUser {
            override val email: String
                get() = "dev@orangebox.technology"
            override val id: String
                get() = "123"
            override val phoneCountryCode: Int
                get() = 55
            override val phoneNumber: Long
                get() = 11971796566
            override val tokenFirebase: String
                get() = "123"

        }
    }

    @Test
    fun testSendNotificationEmail() {

        val user = getUser()

        Assertions.assertDoesNotThrow { notificationService.sendNotification(NotificationBuilder()
            .setTo(user)
            .setTypeSending(TypeSendingNotificationEnum.EMAIL)
            .setEmailDataTemplate(object : EmailDataTemplate {
                override val data: Map<String?, Any?>
                    get() {
                        val params: MutableMap<String?, Any?> = HashMap()
                        params["user_name"] = "John"
                        params["message"] = "Hello Message"
                        return params
                    }
                override val templateId: Int
                    get() = emailTemplateId.toInt()
            })
            .build()) }
    }
}