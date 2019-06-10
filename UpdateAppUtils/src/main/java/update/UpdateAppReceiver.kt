package update

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import teprinciple.updateapputils.R
import util.Utils


/**
 * desc: UpdateAppReceiver
 * author: teprinciple on 2019/06/3.
 */
internal class UpdateAppReceiver : BroadcastReceiver() {

    private val notificationChannel = "1001"

    override fun onReceive(context: Context, intent: Intent) {

        val progress = intent.getIntExtra("progress", 0)
        val title = intent.getStringExtra("title")


        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 显示通知栏  TODO
        val notifyId = 1
//        if (UpdateAppUtils.showNotification) {
//            showNotification(context, notifyId, progress, title, notificationChannel, nm)
//        }


        showNotification(context, notifyId, progress, title, notificationChannel, nm)

        // 下载完成
        if (progress == 100) {
            handleDownloadComplete(context, notifyId, nm)
        }
    }


    /**
     * 下载完成后的逻辑
     */
    private fun handleDownloadComplete(context: Context, notifyId: Int, nm: NotificationManager?) {
        // 关闭通知栏
        if (nm != null) {
            nm.cancel(notifyId)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                nm.deleteNotificationChannel(notificationChannel)
            }
        }

        // 安装apk
        if (DownloadAppUtils.downloadUpdateApkFilePath != null) {
            Utils.installApk(context, DownloadAppUtils.downloadUpdateApkFilePath)
        }
    }


    /**
     * 通知栏显示
     */
    private fun showNotification(context: Context, notifyId: Int, progress: Int, title: String, notificationChannel: String, nm: NotificationManager) {

        val notificationName = "notification"

        // 适配8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 通知渠道
            val channel = NotificationChannel(notificationChannel, notificationName, NotificationManager.IMPORTANCE_HIGH)

            channel.enableLights(false) // 是否在桌面icon右上角展示小红点
            channel.setShowBadge(false) // 是否在久按桌面图标时显示此渠道的通知
            channel.enableVibration(false)

            // 最后在notificationmanager中创建该通知渠道
            nm.createNotificationChannel(channel)
        }

        val builder = Notification.Builder(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(notificationChannel)
        }

        builder.setContentTitle("正在下载 $title")

        // 设置通知图标
        builder.setSmallIcon(R.drawable.ic_logo)
        builder.setLargeIcon(BitmapFactory.decodeResource(context.resources,R.drawable.ic_logo))

        builder.setProgress(100, progress, false)

        // 设置只想一次
        builder.setOnlyAlertOnce(true)

        var notification: Notification? = null
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build()
        } else {
            notification = builder.notification
        }

        nm.notify(notifyId, notification)
    }
}