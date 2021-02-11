package www.vayapedal.emam;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import org.kaldi.Assets;
import org.kaldi.KaldiRecognizer;
import org.kaldi.Model;
import org.kaldi.RecognitionListener;
import org.kaldi.SpeechService;
import org.kaldi.Vosk;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import static android.telephony.AvailableNetworkInfo.PRIORITY_LOW;


public class ServicioBind_RecognitionListener extends Service implements RecognitionListener {


    private final Funciones funciones = new Funciones(this);


    /*Vosk-Kaldi*/
    private static Model model;
    private SpeechService speechService;
    private KaldiRecognizer kaldiRcgnzr;
    /*comunicacion SERVICIO*/
    private final IBinder binder = new LocalBinder();
    private ResultReceiver resultReceiver;


    public void pararServicio(){
        this.stopSelf();
    }



    @Override
    public void onCreate() {
        try {
            super.onCreate();
            funciones.vibrar(this, Constantes.VIRAR_CORTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
      //  this.startForeground(Constantes.ID_SERVICIO_BIND, createNotification());
            String s = intent.getExtras().getString(Constantes.ORIGEN_INTENT);
            configurarSpeechService();

        } catch (
                Exception exception) {
            exception.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        resultReceiver = intent.getParcelableExtra(Constantes.RECEIVER);
        String s = intent.getExtras().getString(Constantes.ORIGEN_INTENT);
        if (s.equals(Constantes.ON_CONFIG)) {
            toReceiver(Constantes.ON_CONFIG, Constantes.NOTIFICACION_SERVICIO);
        }
        if (s.equals(Constantes.ON_TOGGLE)) {
            toReceiver(Constantes.ON_TOGGLE, Constantes.NOTIFICACION_SERVICIO);
        }
        return binder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        toReceiver(Constantes.OFF_SERVICIO, Constantes.NOTIFICACION_SERVICIO);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        //fixme borrar todos los recursos del servicio
        pararSpeechService();

        funciones.vibrar(this, 2000);
        super.onDestroy();
    }



    /* clase utilizda para devolver  Binder para acceder a las variables y metodos publicos del servicio*/
    public class LocalBinder extends Binder {
        ServicioBind_RecognitionListener getBindService() {
            return ServicioBind_RecognitionListener.this;
        }
    }

    /* ENVIARDO PAQUETE Y RESULT_DATA_KEY AL MAIN*/
    private void toReceiver(String p, String key) {
        Bundle bundle = new Bundle();
        bundle.putString(key, p);
        resultReceiver.send(Constantes.SUCCESS_RESULT, bundle);

    }




    /*********************************************notificacion para startForeground**************************************/
    public Notification createNotification() {
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark) //todo icono para la notificación
                .setContentTitle("EMAN");
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
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        String name = "Ao coidado de EMAN";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel("EMAN channel", name, importance);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }
        return "EMAN channel";
    }


    public void configurarSpeechService() {
        if (speechService == null) {
            funciones.vibrar(this, Constantes.VIRAR_CORTO);
            new SetupSpeechTask(this).execute();

        }
    }


    private static class SetupSpeechTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<ServicioBind_RecognitionListener> activityReference;

        SetupSpeechTask(ServicioBind_RecognitionListener activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                Vosk.SetLogLevel(0);
                model = new Model(assetDir.toString() + "/model-android");
                activityReference.get().kaldiRcgnzr = new KaldiRecognizer(model, 16000.0f); // rec = new KaldiRecognizer(model, 16000.0f, grammar);
                activityReference.get().speechService = new SpeechService(activityReference.get().kaldiRcgnzr, 16000.0f);
                activityReference.get().speechService.addListener(activityReference.get());
                Log.d("Vosk", "Sync files in the folder " + assetDir.toString());

            } catch (IOException e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception result) {
            activityReference.get().iniciarSpeechService();
        }
    }


    private void iniciarSpeechService() {
        try {
            if (speechService.startListening()) {  /** *********** arranca el reconocedor **********>>*/
                Toast toast = Toast.makeText(this, "El reconocimiento de voz esta habilitado", Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (Exception e) {

            e.fillInStackTrace();
        }
    }

    public void pararSpeechService() {
        try {
            if (speechService != null) {
                speechService.cancel();
                speechService = null;
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }


    /**
     * @param hypothesis json con los resultados
     *                   "result" : [{
     *                   "conf" : 1.000000,
     *                   "end" : 1.170000,
     *                   "start" : 0.780000,
     *                   "word" : "hola"
     *                   }, {
     *                   "conf" : 0.568311,
     *                   "end" : 1.590000,
     *                   "start" : 1.230000,
     *                   "word" : "si"
     *                   }],
     *                   "text" : "hola si"
     *                   }
     */

    @Override
    public void onResult(String hypothesis) {
        try {
            String text = funciones.getFraseFromJson(hypothesis);
            if (!text.equals("")) {
                String[] arWord = funciones.decodeJSon(hypothesis, "words");
                String[] arConf = funciones.decodeJSon(hypothesis, "conf");
                String[] arText = funciones.decodeJSon(hypothesis, "text");
                for (int i = 0; i < arWord.length; i++) {
                    float confianza = Float.parseFloat(arConf[i]) * 100;
                    procesarResultSpechToText(getApplicationContext(), arWord[i], (int) confianza);        //-----> procesar palabra
                }
                procesarTextTextToSpech(arText[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onPartialResult(String hypothesis) {
        try {
            String[] arPartial = funciones.decodeJSon(hypothesis, "partial");
            for (String s : arPartial) {
                if (!s.equals("")) {
                    toReceiver(s, Constantes.NOTIFICACION_PARCIAL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Exception e) {
        Toast toast = Toast.makeText(getApplicationContext(), "kaldi Error", Toast.LENGTH_LONG);
        toast.show();
        speechService.cancel();
        speechService = null;
        this.stopSelf();
    }

    @Override
    public void onTimeout() {
        Toast toast = Toast.makeText(getApplicationContext(), "SERVICIO  onTimeout", Toast.LENGTH_LONG);
        toast.show();
        speechService.cancel();
        speechService = null;
        this.stopSelf();
    }


    /**
     * INVOCADO CADA VEZ QUE TENEMOS UN RESULTADO DEL SPEECH CON INFORMACION
     */
    private void procesarResultSpechToText(Context context, String s, int confianza) {
        toReceiver(s, Constantes.NOTIFICACION_PALABRA);
    }


    /**
     * PROCESAR FRASE
     * este metodo se llama despues de todas las  llamadas a this.procesarResultadoSpechToText()
     */
    private void procesarTextTextToSpech(String frase) {


    }


}






