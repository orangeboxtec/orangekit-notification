package com.orangebox.kit.notification

import com.orangebox.kit.core.annotation.OKEntity
import com.orangebox.kit.core.annotation.OKId
import org.bson.codecs.pojo.annotations.BsonProperty

@OKEntity(name = "userNotifications")
class UserNotifications {

	@OKId
    @BsonProperty("_id")
    var idUser: String? = null

    var notifications: ArrayList<Notification>? = null

    constructor()
    constructor(id: String?) {
        idUser = id
    }
}