package www.vayapedal.emam;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


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
        try { //fixme funciona, pero vale pasta ->          sms.sendTextMessage(numTlf, null, texto, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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

        //hypothesis{
        //  "result" : [{
        //      "conf" : 1.000000,
        //      "end" : 1.170000,
        //      "start" : 0.780000,
        //      "word" : "hola"
        //    }, {
        //      "conf" : 0.568311,
        //      "end" : 1.590000,
        //      "start" : 1.230000,
        //      "word" : "si"
        //    }],
        //  "text" : "hola si"
        //}

        //{//  "partial" : ""
        //}
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


}
