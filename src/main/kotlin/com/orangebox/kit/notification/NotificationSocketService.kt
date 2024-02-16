package com.orangebox.kit.notification

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.websocket.*
import jakarta.websocket.server.PathParam
import jakarta.websocket.server.ServerEndpoint
import java.util.function.Consumer


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