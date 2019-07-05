package com.allure.redpackageassistant;

import android.app.Notification;
import android.app.PendingIntent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * 9.0 系统通知监听
 */
public class MyNotificationListenerService extends NotificationListenerService {


    /**
     * 连接成功的时候调用改方法
     */
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Notification notification=sbn.getNotification();
        if (notification==null){
            return;
        }
        //判断如果是微信通知
        if (sbn.getPackageName().equals("com.tencent.mm")){
            //打开对应聊天页面
            PendingIntent pendingIntent=notification.contentIntent;
            try {
                pendingIntent.send();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
