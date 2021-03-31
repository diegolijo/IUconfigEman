package www.vayapedal.emam;

import android.annotation.TargetApi;

import androidx.annotation.NonNull;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.telephony.AvailableNetworkInfo.PRIORITY_LOW;


public class Funciones {

    private static final String TAG = "Error";
    private Context context;


    public Funciones(Context context) {
        this.context = context;
    }

    public Funciones() {
    }


    public void vibrar(Context context, long ms) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(ms);
        }
    }

    /** long[] pattern = {1500, 800, 800, 800};*/
    /**
     * v.vibrate(VibrationEffect.createWaveform(pattern, 0));
     */
    public void vibrarEffect(Context context, long ms) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(ms);
        }
    }


    public void llamar(String numTlf, Context context) {
        Uri call = Uri.parse("tel:" + numTlf);
        Intent intentCall = new Intent(Intent.ACTION_CALL, call);
        intentCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentCall);
    }


    public void enviarSms(String numTlf, String texto) {
        SmsManager sms = SmsManager.getDefault();
        try {
            if (!numTlf.equals("")) {
                sms.sendTextMessage(numTlf, null, texto, null, null);
               // vibrar(context, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // todo
    public String construirSms() {
        String texto = "Sms de prueba";
        return texto;
    }

    /******************************************* SERVICE *********************************************/
    public boolean isServiceRunning(Context context) {
        boolean isRuning = false;
        try {
            Class<?> serviceClass = Servicio_RecognitionListener.class;
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    isRuning = true;
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "isServiceRunning: ", ex);
        }
        return isRuning;
    }

    public boolean isServiceBindRunning(Context context) {
        boolean isRuning = false;
        try {
            Class<?> serviceClass = ServicioBind_RecognitionListener.class;
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    isRuning = true;
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "isServiceRunning: ", ex);
        }
        return isRuning;
    }


    /**
     * devuelve un array de string con el valor de las clave("conf" "start" "end" "text" "partial")
     */
    public String[] decodeKaldiJSon(String json, String key) {
        try {
            String item;
            JSONObject jObject = new JSONObject(json);
            String[] strArray = new String[1];
            JSONArray arResult = null;
            if (!key.equals("partial")) {
                arResult = jObject.getJSONArray("result");
            }
            switch (key) {
                case "partial":
                    item = jObject.getString(key);
                    strArray[0] = item;
                    return strArray;
                case "words":

                    String[] wordsArray = new String[arResult.length()];
                    for (int i = 0; i < arResult.length(); i++) {
                        JSONObject obj = arResult.getJSONObject(i);
                        item = obj.getString("word");
                        wordsArray[i] = item;
                    }
                    return wordsArray;
                case "conf":

                    String[] confArray = new String[arResult.length()];
                    for (int i = 0; i < arResult.length(); i++) {
                        JSONObject obj = arResult.getJSONObject(i);
                        item = obj.getString("conf");
                        confArray[i] = item;
                    }
                    return confArray;
                case "text":
                    item = jObject.getString(key);
                    strArray[0] = item;
                    return strArray;
                default:
                    throw new IllegalStateException("Unexpected value: " + key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFraseFromJson(String json) {

        String[] strArray = new String[1];
        String item;
        try {
            JSONObject jObject = new JSONObject(json);
            item = jObject.getString("text");
            strArray[0] = item;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return strArray[0]; //  return   this.decodeKaldiJSon(  json, "text")[0];

    }

    public void findInGoogle(String busqueda) {
        String url = Constantes.GOOGLE + busqueda;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(intent.FLAG_RECEIVER_FOREGROUND);
        context.startActivity(intent);
    }

    /******************************************** notificacion para startForeground ******************************************/
    public Notification createNotification() {

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);


        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channel)
                .setSmallIcon(R.drawable.icon_custom_large)
                .setContentIntent(pendingIntent)
                .setContentTitle(Constantes.NOTIFICATION_TITLE)
                .setContentText(Constantes.NOTIFICATION_BODY);

        Notification notification;
        notification = mBuilder
                .setPriority(PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        return notification;
    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String name = "Ao coidado de EMAN";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel("EMAN channel", name, importance);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            // context.stopSelf();
        }
        return "EMAN channel";
    }


}
