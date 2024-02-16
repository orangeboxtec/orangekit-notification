package com.orangebox.kit.notification

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.orangebox.kit.core.dao.AbstractDAO
import jakarta.enterprise.context.ApplicationScoped
import org.bson.Document
import java.util.*

@ApplicationScoped
class UserNotificationsDAO : AbstractDAO<UserNotifications>(UserNotifications::class.java) {
    override fun getId(bean: UserNotifications): Any? {
        return bean.idUser
    }

    fun listNotifications(idUser: String?): List<Notification> {
        val list: MutableList<Notification> = ArrayList()
        val db: MongoDatabase = getDb()
        val myColl: MongoCollection<Document> = db.getCollection("userNotifications")
        val match = Document("\$match", Document("idUser", idUser))
        val unwind = Document("\$unwind", "\$notifications")
        val sort = Document("\$sort", Document("notifications.creationDate", -1))
        val listResults = ArrayList<Document>()
        myColl.aggregate(Arrays.asList(match, unwind, sort)).into(listResults)
        for (obj in listResults) {
            val notifications: Document = obj.get("notifications") as Document
            val notification = convertNotification(notifications)
            list.add(notification)
        }
        return list
    }

    fun listNewNotifications(idUser: String?, lastNotification: String?): List<Notification> {
        val list: MutableList<Notification> = ArrayList()
        val db: MongoDatabase = getDb()
        val myColl: MongoCollection<Document> = db.getCollection("userNotifications")
        val matchData = Document("idUser", idUser)
        val match = Document("\$match", matchData)
        val unwind = Document("\$unwind", "\$notifications")
        val sort = Document("\$sort", Document("notifications.creationDate", -1))
        val listResults = ArrayList<Document>()
        myColl.aggregate(Arrays.asList(match, unwind, sort)).into(listResults)
        for (obj in listResults) {
            val notifications: Document = obj.get("notifications") as Document
            val notification = convertNotification(notifications)
            if (notifications["id"]?.equals(lastNotification) == true) {
                break
            }
            list.add(notification)
        }
        return list
    }

    fun convertNotification(posts: Document): Notification {
        val notification = Notification()
        notification.creationDate = posts["creationDate"] as Date?
        notification.id = posts["_id"].toString()
        notification.idLink = posts["idLink"].toString()
        notification.idFrom = posts["idFrom"].toString()
        notification.message = posts["message"].toString()
        notification.nameFrom = posts["nameFrom"].toString()
        notification.fgReaded = posts["fgReaded"] as Boolean?
        notification.typeFrom = posts["typeFrom"].toString()
        notification.title = posts["title"].toString()
        return notification
    }
}