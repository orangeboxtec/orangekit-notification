package com.orangebox.kit.notification.email

import com.mailjet.client.ClientOptions
import com.mailjet.client.MailjetClient
import com.mailjet.client.MailjetRequest
import com.mailjet.client.MailjetResponse
import com.mailjet.client.resource.Contact
import com.mailjet.client.resource.Email
import com.mailjet.client.resource.Emailv31
import com.orangebox.kit.core.configuration.Configuration
import com.orangebox.kit.core.configuration.ConfigurationService
import com.orangebox.kit.notification.Notification
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import kotlin.reflect.jvm.internal.impl.utils.ExceptionUtilsKt

@ApplicationScoped
class EmailService {

    @Inject
    private lateinit var configurationService: ConfigurationService


    fun sendEmailNotificationWithTemplate(notification: Notification) {
        println(
            "########################## VAI MANDAR UM EMAIL para " + notification.to?.email +
                    "COM TEMPLATE " + notification.emailDataTemplate?.templateId
        )

        val configuration: Configuration? = configurationService.loadByCode("MAIL_DATA")

        if (configuration == null) {
            println("MAIL_DATA configuration is required to send email")
        } else if (notification.emailDataTemplate == null) {
            println("EmailDataTemplate is required to send email")
        } else {
            val requestVars = JSONObject()
            for (key in notification.emailDataTemplate?.data?.keys!!) {
                requestVars.put(key, notification.emailDataTemplate?.data?.get(key))
            }
            val mailData: HashMap<String, String>? = configurationService.loadByCode("MAIL_DATA")?.valueData
            MailjetClient(mailData?.get("user"), mailData?.get("password"), ClientOptions("v3.1"))
            val request: MailjetRequest = MailjetRequest(Emailv31.resource)
                .property(
                    Emailv31.MESSAGES, JSONArray()
                        .put(
                            JSONObject()
                                .put(
                                    Emailv31.Message.FROM, JSONObject()
                                        .put("Email", mailData?.get("from"))
                                        .put("Name", mailData?.get("fromName"))
                                )
                                .put(
                                    Emailv31.Message.TO, JSONArray()
                                        .put(
                                            JSONObject()
                                                .put("Email", notification.to?.email)
                                        )
                                )
                                .put(Emailv31.Message.TEMPLATEID, notification.emailDataTemplate?.templateId)
                                .put(Emailv31.Message.TEMPLATELANGUAGE, true)
                                .put(Emailv31.Message.SUBJECT, notification.title)
                                .put(Emailv31.Message.VARIABLES, requestVars)
                        )
                )
            if (notification.attachment != null) {
                val fileData: String = com.mailjet.client.Base64.encode(notification.attachment)
                request.property(
                    Email.ATTACHMENTS, JSONArray()
                        .put(
                            JSONObject()
                                .put("Content-type", notification.attachmentFileMimeType)
                                .put("Filename", notification.attachmentName)
                                .put("content", fileData)
                        )
                )
            }

            //Envio utilizando a lib do mailjet nao esta funcionando, esta dando erro 400
            //por esse motivo estamos enviado o json gerado pela lib do mailjet via http client
            try {
                val url = URL("https://api.mailjet.com/v3.1/send")
                val con: HttpURLConnection = url.openConnection() as HttpURLConnection
                con.requestMethod = "POST"
                con.setRequestProperty("Accept", "application/json")
                con.doOutput = true

                val body: String = request.body
                val wr = OutputStreamWriter(con.outputStream)
                wr.write(body)
                wr.flush()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendEmailError(e: Exception?) {
        val mailDataConfe: Configuration? = configurationService.loadByCode("MAIL_DATA")
        val emailTemplateConfe: Configuration? = configurationService.loadByCode("MSG_EMAIL_PT")
        if (configurationService.loadByCode("PRODUCTION")?.value.toBoolean() && mailDataConfe != null && emailTemplateConfe != null
        ) {
            val mailData: HashMap<String, String>? = configurationService.loadByCode("MAIL_DATA")?.valueData
            val client: MailjetClient
            val request: MailjetRequest
            client = MailjetClient(mailData?.get("user"), mailData?.get("password"))
            val requestVars = JSONObject()
            requestVars.put("msg", e?.stackTraceToString())
            requestVars.put("user_name", "Suporte")
            request = MailjetRequest(Email.resource)
                .property(Email.FROMEMAIL, mailData?.get("from"))
                .property(Email.FROMNAME, mailData?.get("fromName"))
                .property(Email.MJTEMPLATEID, emailTemplateConfe.value?.toInt())
                .property(Email.MJTEMPLATELANGUAGE, true)
                .property(Email.SUBJECT, "Erro no Servidor")
                .property(Email.RECIPIENTS, JSONArray().put(JSONObject().put(Contact.EMAIL, "dev@orangebox.technology")))
                .property(Email.VARS, requestVars)
            client.post(request)
        }
    }

    fun sendEmailMessage(email: String?, name: String?, msg: String?) {
        val mailDataConfe: Configuration? = configurationService.loadByCode("MAIL_DATA")
        val emailTemplateConfe: Configuration? = configurationService.loadByCode("MSG_EMAIL_PT")
        if (mailDataConfe != null && emailTemplateConfe != null) {
            val mailData: HashMap<String, String>? = configurationService.loadByCode("MAIL_DATA")?.valueData
            val client: MailjetClient
            val request: MailjetRequest
            client = MailjetClient(mailData?.get("user"), mailData?.get("password"))
            val requestVars = JSONObject()
            requestVars.put("msg", msg)
            requestVars.put("user_name", name)
            request = MailjetRequest(Email.resource)
                .property(Email.FROMEMAIL, mailData?.get("from"))
                .property(Email.FROMNAME, mailData?.get("fromName"))
                .property(Email.MJTEMPLATEID, emailTemplateConfe.value?.toInt())
                .property(Email.MJTEMPLATELANGUAGE, true)
                .property(Email.SUBJECT, "Erro no Servidor")
                .property(Email.RECIPIENTS, JSONArray().put(JSONObject().put(Contact.EMAIL, email)))
                .property(Email.VARS, requestVars)
            client.post(request)
        }
    }
}