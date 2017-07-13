package easy.com.todo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.i("Receive","inside");


final Context handlercontext=context;

        NotificationCompat.Builder builder=new NotificationCompat.Builder(context).setAutoCancel(true).setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("Todo Alert !").setContentText("You have a task to be reviewed !");
Intent result=new Intent(context,MainActivity.class);
       int id= intent.getIntExtra("ID1",-1);
        Log.i("Receive",id+"");
        PendingIntent pendingIntent=PendingIntent.getActivity(context,id,result,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id,builder.build());
        Uri ringtone= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
      final  Ringtone r=RingtoneManager.getRingtone(handlercontext,ringtone);
        r.play();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               r.stop();
            }
        }, 15000);
//        Intent i=new Intent(context,MainActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);


//        Long timeatstop = System.currentTimeMillis()+6000;
//        while(true)
//        {
//            if(System.currentTimeMillis()==timeatstop) {
//                r.stop();
//                break;
//            }
//        }

//        long end=System.currentTimeMillis()+5000;
//        while(System.currentTimeMillis()<end) ;



    }
}
