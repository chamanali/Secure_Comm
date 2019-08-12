package com.example.secure_comm;

/**
 * Created by hp on 3/6/2017.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.Random;

public class Incoming_sms extends BroadcastReceiver {

    String source="";
    @Override
    public void onReceive(Context context, Intent intent) {

        try {

            if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
            {
                Bundle bundle = intent.getExtras();
                SmsMessage[] msgs = null;
                String msg_from;
                if (bundle != null)
                {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++)
                    {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        source = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();

                        if(msgBody.contains("ABESS-"))
                        {
                            DB db=new DB(context);
                            db.open();
                            String ans=db.getcont_Name_fornotification(source);
                            db.close();

                            if(ans.compareTo("no")!=0)
                            {
                                source=ans;
                            }

                            int randomid=random();

                            Intent notificationIntent = new Intent(context, Login.class);
                            notificationIntent.putExtra("nid",randomid);
                            PendingIntent contentIntent = PendingIntent.getActivity(context,randomid, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            Notification.Builder builder = new Notification.Builder(context);

                            builder.setContentIntent(contentIntent)
                                    .setSmallIcon(R.drawable.abess_icon)
                                    .setTicker("SMS Encryption System")
                                    .setWhen(System.currentTimeMillis())
                                    .setContentTitle("SMS Encryption System")
                                    .setContentText("A Message from - "+source);
                            Notification n = builder.getNotification();
                            nm.notify("abess",randomid, n);
                        }
                    }
                }
            }

        } catch (Exception e)
        {
            Toast.makeText(context, "exp-"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public int random()
    {
        String ans="";
        int nos[]=new int[]{0,1,2,3,4,5,6,7,8,9};
        Random rand=new Random();
        ans+=nos[rand.nextInt(nos.length)];
        ans+=nos[rand.nextInt(nos.length)];
        ans+=nos[rand.nextInt(nos.length)];
        return Integer.parseInt(ans);
    }

}

