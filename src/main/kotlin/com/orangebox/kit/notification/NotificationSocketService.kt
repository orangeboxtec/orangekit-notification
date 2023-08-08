package com.orangebox.kit.notification

import java.util.function.Consumer
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint


@ServerEndpoint("/notification/{userId}")
@ApplicationScoped
class NotificationSocketService {

    @Inject
    private lateinit var notificationService: NotificationService

    @OnOpen
    fun onOpen(session: Session, @PathParam("username") username: String) {
        broadcast("User $username joined")
        notificationService.sessions[username] = session
    }

    @OnClose
    fun onClose(session: Session?, @PathParam("username") username: String) {
        notificationService.sessions.remove(username)
        broadcast("User $username left")
    }

    @OnError
    fun onError(session: Session?, @PathParam("username") username: String, throwable: Throwable) {
        notificationService.sessions.remove(username)
        broadcast("User $username left on error: $throwable")
    }

    @OnMessage
    fun onMessage(message: String, @PathParam("username") username: String) {
        broadcast(">> $username: $message")
    }

    private fun broadcast(message: String) {
        notificationService.sessions.values.forEach(Consumer { s: Session ->
            s.asyncRemote.sendObject(message) { result ->
                if (result.exception != null) {
                    println("Unable to send message: " + result.exception)
                }
            }
        })
    }
}