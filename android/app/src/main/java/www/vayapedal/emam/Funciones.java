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
    public final int EXTERNAL_REQUEST = 50;
    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private final String fileTxt = Constantes.FILE_TXT;
    private final String filePath = Constantes.PATH;
    private Context context;


    public Funciones(Context context) {
        this.context = context;
    }

    public Funciones() {

    }

    /**
     * devuelve un array de string con el valor de las clave("conf" "start" "end" "text" "partial")
     */
    public String[] decodeJSon(String json, String key) {
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
        String[] strArray = new String[20];
        String item;
        try {
            JSONObject jObject = new JSONObject(json);
            item = jObject.getString("text");
            strArray[0] = item;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return strArray[0];
    }

    //File
    public void txtSave(String texto) {
        if (true) {
            try {
                File carpeta = new File(Environment.getExternalStoragePublicDirectory(filePath).getAbsolutePath());
                if (!carpeta.exists()) {
                    if (carpeta.mkdir())
                        Toast.makeText(this.context.getApplicationContext(), "Carpeta creada: " + carpeta.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                }
                File f = new File(carpeta, fileTxt);
                FileWriter flwriter = new FileWriter(f, true);
                BufferedWriter bfwriter = new BufferedWriter(flwriter);
                bfwriter.write(texto + " ");
                bfwriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void vibrar(Context context, long ms) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
//            long[] pattern = {1500, 800, 800, 800};
//            v.vibrate(VibrationEffect.createWaveform(pattern, 0));
        } else {
            //deprecated in API 26
            v.vibrate(ms);
        }
    }

    //todo
    public String formatoTexto(String s, String tag) {

        switch (tag) {
            case Constantes.NOTIFICACION_PARCIAL:
                return "\t -" + s + "-";
            case Constantes.NOTIFICACION_PALABRA:
                return "\t <" + s + ">\n";
            case Constantes.NOTIFICACION_FRASE:
                return "\t [" + s + "]\n";
            default:
                throw new IllegalStateException("Unexpected value: " + tag);
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
        try { //fixme funciona, pero vale pasta ->    sms.sendTextMessage(numTlf, null, texto, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String construirSms() {
        String texto = "Sms de prueba";
        return texto;
    }


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


}
