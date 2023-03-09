package com.orangebox.kit.notification

import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/notification")
class NotificationRestService {

    @Inject
    private lateinit var notificationService: NotificationService

    @GET
    @Path("/listNotifications/{idUser}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    fun listNotifications(@PathParam("idUser") idUser: String): List<Notification?>? {
        return notificationService.listNotifications(idUser)
    }

    @GET
    @Path("/listNewNotifications/{idUser}/{lastNotification}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    fun listNewNotifications(
        @PathParam("idUser") idUser: String,
        @PathParam("lastNotification") lastNotification: String?
    ): List<Notification?>? {
        return notificationService.listNewNotifications(idUser, lastNotification)
    }

    @PUT
    @Path("/checkNotificationsAsReaded/{idUser}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    fun checkNotificationsAsReaded(@PathParam("idUser") idUser: String?) {
        notificationService.checkNotificationsAsReaded(idUser)
    }

    @PUT
    @Path("/checkNotificationAsReaded/{idUser}/{idNotification}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    fun checkNotificationAsReaded(
        @PathParam("idUser") idUser: String?,
        @PathParam("idNotification") idNotification: String?
    ) {
        notificationService.checkNotificationAsReaded(idUser, idNotification)
    }

    @PUT
    @Path("/cleanNotifications/{idUser}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    fun cleanNotifications(@PathParam("idUser") idUser: String?) {
        notificationService.cleanNotifications(idUser)
    }

    @GET
    @Path("/removeNotification/{idTitle}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    fun removeNotification(@PathParam("idTitle") idTitle: String) {
        notificationService.removeNotification(idTitle)
    }

    @GET
    @Path("/countNotificationsUnreaded/{idUser}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    fun countNotificationsUnreaded(@PathParam("idUser") idUser: String?): Notification {
        val notification = Notification()
        notification.count = notificationService.countNotificationsUnreaded(idUser)
        return notification
    }
}