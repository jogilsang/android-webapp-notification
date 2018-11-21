package app.com.web.mywebapp;

/**
 * Created by user on 11/20/2018.
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.renderscript.RenderScript;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


//title, contents, imgurl 데이터를 가지는 json 푸시 데이터가 오면 안드로이드에서 사용가능한 데이터로 추출하여 푸시 알림을 설정하는 내용 입니다.
//출처: http://playgroundblog.tistory.com/332
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FirebaseMsgService";
    private static final int PRIORITY_MAX = 2;
    public String title, contents, imgurl;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    // [START receive_message]
    @Override

    public void onMessageReceived(RemoteMessage remoteMessage) {


        sendPushNotification(remoteMessage.getData().get("message"));

    }


//    {
//        "data" :{
//        "message" :{
//            "title" : "안녕하세요",
//                    "contents" : "블로그에 오신 여러분을 환영합니다.",
//                    "imgurl" : "https://blogpfthumb-phinf.pstatic.net/MjAxNzEyMDhfMTMy/MDAxNTEyNzA3MTc0ODA3.rbnlwP_k8OCxoan813kT-Q0DVAxw8Vgb0co6Ivpcg1Yg.QdHiNjmkf1OVkzCy8ZIyyy1cjDe2947sLVuj9vynVpQg.PNG.jogilsang/JS.png?type=w161",
//                    "link" : ""
//        }
//    },
//        "to":"/topics/notice"
//
//    }


    private void sendPushNotification(String message) {


        System.out.println("received message : " + message);


        try {

            JSONObject jsonRootObject = new JSONObject(message);
            title = jsonRootObject.getString("title");
            contents = jsonRootObject.getString("contents");
            imgurl = jsonRootObject.getString("imgurl");


        } catch (JSONException e) {

            e.printStackTrace();

        }


        Bitmap bitmap = getBitmapFromURL(imgurl);

        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,

                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // 안드로이드 공식 문서(확장 등 관련) : https://developer.android.com/training/notify-user/expanded#java
        // 안드로이드 공식 문서(구현): https://developer.android.com/guide/topics/ui/notifiers/notifications?hl=ko
        // setPriority : -2~2 , 높으면 헤드업알립
        // setVisibility : 잠금화면알림
        // 알림 예시 설명 굿 : http://itmir.tistory.com/457
        // addAction : 읽음,닫음 같은 버튼생김 ( 아이콘, 텍스트 , 인탠트)

        // addAction에 쓸 action 예시
        //        Notification.Action action = new Notification.Action.Builder(
        //                Icon.createWithResource(this, R.drawable.ic_prev),
        //                "읽음",
        //                pendingIntent).build();


        // 그림 날릴때 쓰는 BigPictureStyle
        //        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this , channelId)
        //                .setSmallIcon(R.drawable.bar_icon)
        //                .setLargeIcon(bitmap)
        //                .setContentTitle(title)
        //                .setContentText(contents)
        //                .setAutoCancel(true)
        //                .setSound(defaultSoundUri).setLights(000000255, 500, 2000)
        //                .setPriority(PRIORITY_MAX)
        //                .setContentIntent(pendingIntent)
        //                .setStyle(new NotificationCompat.BigPictureStyle()
        //                    .bigPicture(bitmap)
        //                    .bigLargeIcon(null));

        //   .setVisibility(Notification.VISIBILITY_PUBLIC)
        //        VISIBILITY_PUBLIC은 알림의 전체 콘텐츠를 표시합니다.
        //        VISIBILITY_SECRET은 이 알림의 어떤 부분도 화면에 표시하지 않습니다.
        //        VISIBILITY_PRIVATE은 알림 아이콘과 콘텐츠 제목 등의 기본 정보는 표시하지만 알림의 전체 콘텐츠는 숨깁니다.

        // 일반적인 알림
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this , channelId)
                .setSmallIcon(R.drawable.bar_icon)
                .setLargeIcon(bitmap)
                .setContentTitle(title)
                .setContentText(contents)
                .setAutoCancel(true)
                .setSound(defaultSoundUri).setLights(000000255, 500, 2000)
                .setPriority(PRIORITY_MAX)
                        .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contents)
                                .setBigContentTitle(title)
                                .setSummaryText("요약")
                        )
                .setContentIntent(pendingIntent);

        // 빅텍스트 스타일
        //        Notification.BigTextStyle style = new Notification.BigTextStyle();
        //        style.setSummaryText("and More +");
        //        style.setBigContentTitle("BigText Expanded Title");
        //        style.bigText(contents);
        //
        //        notificationBuilder.setStyle(style);
        // 빅텍스트 스타일

         //확장알림 (Expand indicator)

        // 인박스 스타일 start

//        NotificationCompat.InboxStyle inBoxStyle =
//                new NotificationCompat.InboxStyle();
//
//        // Sets a title for the Inbox in expanded layout
//        inBoxStyle.setBigContentTitle(title);
//        inBoxStyle.setSummaryText("(광고)");
//
//        // \n에 따라 나눈다
//        String[] events = contents.split(";;");
//
//        // Moves events into the expanded layout
//        for (int i=0; i < events.length; i++) {
//
//            inBoxStyle.addLine(events[i].trim());
//        }
//        // Moves the expanded layout object into the notification object.
//        notificationBuilder.setStyle(inBoxStyle);
//        // Issue the notification here.
//        // 인박스 style finish


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakelock.acquire(5000);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("description");
            notificationManager.createNotificationChannel(channel);
        }


        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }


    public Bitmap getBitmapFromURL(String strURL) {

        try {

            URL url = new URL(strURL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);

            connection.connect();

            InputStream input = connection.getInputStream();

            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            return myBitmap;

        } catch (IOException e) {

            e.printStackTrace();

            return null;

        }

    }


}
