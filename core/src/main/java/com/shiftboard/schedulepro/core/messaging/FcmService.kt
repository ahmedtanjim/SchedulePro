package com.shiftboard.schedulepro.core.messaging

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.shiftboard.schedulepro.core.R
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.notification.ActionNotification
import com.shiftboard.schedulepro.core.network.model.notification.BulkMessaging
import com.shiftboard.schedulepro.core.persistence.Converters
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.get
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.util.*
import kotlin.coroutines.CoroutineContext


class FcmService : FirebaseMessagingService(), CoroutineScope {

    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    private val fcmInterface by inject<FcmTokenInterface>()
    private val fcmUserValidator by inject<FcmUserValidator>()
    private val messageRepo by inject<MessagingRepo>()
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Timber.d("::: PUSH RECEIVE :::")
        Timber.d("::: ${p0.messageType}")
        Timber.d("::: ${p0.data}")

        if (p0.data.containsKey("notification_id")) {
            launch {
                try {
                    val message = MessageContent.parseData(p0.data) ?: return@launch
                    if (!fcmUserValidator.isUserValid(message)) return@launch

                    when (val result =
                        messageRepo.fetchNotificationDetails(message.notification_id)) {
                        is Maybe.Success -> {
                            val content = result.data

                            // notificationId is a unique int for each notification that you must define
                            val lastId = MessagingPrefs.lastNotificationId
                            // If we have 10000 active notifications on this users device we have more issues than reusing the same id
                            val newId = if (lastId > 10000) 0 else lastId + 1

                            MessagingPrefs.lastNotificationId = newId
                            val pendingIntent = Intent(this@FcmService,
                                Class.forName("com.shiftboard.schedulepro.activities.router.RouterActivity")).apply {
                                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

                                putExtra(NotificationConstants.EXTRA_ID, content.id)

                                if (content.type == NotificationConstants.BULK_MESSAGE) {
                                    val contentString =
                                        Converters().messageContentAdapter.toJson(content.content as BulkMessaging)
                                    putExtra(NotificationConstants.EXTRA_BULK_MESSAGE,
                                        contentString)
                                }

                                content.focusDate()?.let {
                                    putExtra(NotificationConstants.EXTRA_START_DATE,
                                        it.format(DateTimeFormatter.ISO_DATE))
                                }
                            }

                            val contentText = MessagingTemplates.parseMessage(this@FcmService,
                                content.type,
                                content.content)

                            val builder = NotificationCompat.Builder(this@FcmService,
                                NotificationConstants.mapTypeToChannel(content.type))
                                .setSmallIcon(R.drawable.ic_logo)
                                .setContentTitle(MessagingTemplates.notificationTitle(this@FcmService,
                                    content.type, content.content))
                                .setContentText(contentText)
                                .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(PendingIntent.getActivity(this@FcmService,
                                    newId,
                                    pendingIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT))
                                .setAutoCancel(true)

                            with(NotificationManagerCompat.from(this@FcmService)) {
                                notify(newId, builder.build())
                            }
                            builder.build()

                            messageRepo.cacheNotificationData(content)

                            if (content.type != NotificationConstants.BULK_MESSAGE) {
                                val start = content.focusDate() ?: return@launch
                                val end = content.focusEndDate() ?: return@launch

                                messageRepo.cacheNewScheduleData(start, end)
                            }

                        }
                        is Maybe.Failure -> {

                        }
                    }
                } catch (e: Exception) {

                }
            }
        }

        else {
            val action = p0.data["action"]
            action?.let { actionValue ->
                when (actionValue.toInt()) {
                    ActionNotification.REFRESH_SCHEDULE.value -> {
                        try {
                            val startDate = LocalDate.parse(p0.data["start_date"])
                            val endDate = LocalDate.parse(p0.data["end_date"])
                            launch {
                                if (!messageRepo.didRefreshSchedule(startDate, endDate)) {
                                    val pendingIntent = Intent(this@FcmService,
                                        Class.forName("com.shiftboard.schedulepro.activities.router.RouterActivity")).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        val date = LocalDate.now();
                                        putExtra(NotificationConstants.EXTRA_START_DATE, date.format(DateTimeFormatter.ISO_DATE))
                                    }
                                    startActivity(pendingIntent)
                                }
                            }
                        } catch (e: Exception) {
                            Timber.d("::: INVALID DATE :::")
                        }
                    }
                    ActionNotification.NO_ACTION.value -> {
                        Timber.d("::: NO ACTION :::")
                    }
                    else -> Timber.d("::: INVALID ACTION :::")
                }
            }
        }
    }

    override fun onDestroy() {
        coroutineJob.cancel()
        super.onDestroy()
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        fcmInterface.fcmToken = p0
        fcmInterface.tokenSynced = 0L
    }
}

interface FcmUserValidator {
    suspend fun isUserValid(message: MessageContent): Boolean
}