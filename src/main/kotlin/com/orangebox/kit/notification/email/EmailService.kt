package com.orangebox.kit.notification.email

import com.mailjet.client.MailjetClient
import com.mailjet.client.MailjetRequest
import com.mailjet.client.resource.Contact
import com.mailjet.client.resource.Email
import com.mailjet.client.resource.Emailv31
import com.orangebox.kit.core.configuration.Configuration
import com.orangebox.kit.core.configuration.ConfigurationService
import com.orangebox.kit.notification.Notification
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.apache.commons.codec.binary.Base64
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

@ApplicationScoped
class EmailService {

    @Inject
    private lateinit var configurationService: ConfigurationService

    @ConfigProperty(name = "orangekit.notification.mailjet.key")
    private lateinit var mailjetKey: String

    @ConfigProperty(name = "orangekit.notification.mailjet.secret")
    private lateinit var mailjetSecret: String


    fun sendEmailNotificationWithTemplate(notification: Notification): Int? {

        println(
            "########################## VAI MANDAR UM EMAIL para " + notification.to?.email +
                    " COM TEMPLATE " + notification.emailDataTemplate?.templateId
        )

        val requestVars = JSONObject()
        for (key in notification.emailDataTemplate?.data?.keys!!) {
            requestVars.put(key, notification.emailDataTemplate?.data?.get(key))
        }

        val request: MailjetRequest = MailjetRequest(Emailv31.resource)
            .property(
                Emailv31.MESSAGES, JSONArray()
                    .put(
                        JSONObject()
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

            val encodedAuth = Base64.encodeBase64("$mailjetKey:$mailjetSecret".toByteArray(StandardCharsets.UTF_8))
            con.setRequestProperty("Authorization", "Basic " + String(encodedAuth))

            val body: String = request.body
            val wr = OutputStreamWriter(con.outputStream)
            wr.write(body)
            wr.flush()

            return con.responseCode
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
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