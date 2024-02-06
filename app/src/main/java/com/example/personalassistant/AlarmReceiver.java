package com.example.personalassistant;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("alarmreceiver", "onReceive: 接收到广播");
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case TodoActivity.ACTION_SET_REMINDER:
                    handleSetReminder(context, intent);
                    break;
                case TodoActivity.ACTION_CANCEL_REMINDER:
                    handleCancelReminder(context, intent);
                    break;
            }
        }
    }

    private void handleSetReminder(Context context, Intent intent) {

        long todoId = intent.getLongExtra("TODO_ID", -1);
        Log.d("AlarmReceiver", "Handling set reminder for Todo ID: " + todoId);
        Log.d("AlarmReceiver", "handleSetReminder: Todo ID - " + todoId);
        if (todoId != -1) {
            // 处理设置提醒的逻辑，例如发送通知

            sendNotification(context, "提醒标题", "提醒内容",todoId);
        }
    }

    private void handleCancelReminder(Context context, Intent intent) {
        long todoId = intent.getLongExtra("TODO_ID", -1);
        if (todoId != -1) {
            // 处理取消提醒的逻辑，例如取消通知
            cancelNotification(context, todoId);
        }
    }

    private void sendNotification(Context context, String title, String content, long todoId) {
        Log.d("AlarmReceiver", "Preparing to send notification for Todo ID: " + todoId);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        String toastMessage;
        toastMessage = "Stand Up Alarm On!";
        Toast.makeText(context, toastMessage,Toast.LENGTH_SHORT).show();
        // 创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "ReminderChannel_" + todoId;
            CharSequence channelName = "Reminder Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        // 在使用 notify 方法之前检查权限
        if (context.checkSelfPermission(android.Manifest.permission.USE_FULL_SCREEN_INTENT) == PackageManager.PERMISSION_GRANTED) {
            // 创建通知
            Intent contentIntent = new Intent(context, TodoActivity.class);
            PendingIntent contentPendingIntent = PendingIntent.getActivity(context, (int) todoId, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ReminderChannel_" + todoId)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(contentPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND)  // 使用 DEFAULT_SOUND 代替 DEFAULT_ALL
                    .setAutoCancel(true);

            // 发送通知
            Log.d("AlarmReceiver", "Sending notification for Todo ID: " + todoId);
            notificationManager.notify((int) todoId, builder.build());
            Log.d("AlarmReceiver", "Notification sent for Todo ID: " + todoId);
        } else {
            // 如果没有权限，可以在这里请求权限
            Log.e("AlarmReceiver", "Permission denied. Requesting permission...");
        }
    }


    private void cancelNotification(Context context, long todoId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            Log.e("AlarmReceiver", "NotificationManager is null");
            return;
        }

        notificationManager.cancel((int) todoId);
    }
}
