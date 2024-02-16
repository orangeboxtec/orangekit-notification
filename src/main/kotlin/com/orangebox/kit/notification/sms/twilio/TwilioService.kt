package com.orangebox.kit.notification.sms.twilio

import com.orangebox.kit.core.configuration.ConfigurationService
import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message
import com.twilio.type.PhoneNumber
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class TwilioService {

    @Inject
    private lateinit var configurationService: ConfigurationService


    fun sendMessage(phone: String?, messageSMS: String?) {
        Twilio.init(
            configurationService.loadByCode("TWILIO_SID")?.value,
            configurationService.loadByCode("TWILIO_TOKEN")?.value
        )
        val message: Message = Message
            .creator(
                PhoneNumber("+$phone"),  // to
                PhoneNumber(configurationService.loadByCode("TWILIO_NUMBER")?.value),  // from
                messageSMS
            )
            .create()
        System.out.println(message.getSid())
    }
}