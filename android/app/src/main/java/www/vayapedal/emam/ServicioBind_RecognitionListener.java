package www.vayapedal.emam;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.widget.Toast;

import org.kaldi.Assets;
import org.kaldi.KaldiRecognizer;
import org.kaldi.Model;
import org.kaldi.RecognitionListener;
import org.kaldi.SpeechService;
import org.kaldi.Vosk;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;


public class ServicioBind_RecognitionListener extends Service implements RecognitionListener {


    private final Funciones funciones = new Funciones(this);

    /******** Vosk-Kaldi **********/
    private SpeechService speechService;
    private KaldiRecognizer kaldiRcgnzr;

    /**** comunicacion SERVICIO ****/
    private final IBinder binder = new LocalBinder();
    private ResultReceiver resultReceiver;


    @Override
    public void onCreate() {
        try {
            super.onCreate();
            funciones.vibrar(this, Constantes.VIRAR_CORTO);
            configurarSpeechService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // String usuarioKey = intent.getExtras().getString(Constantes.USUARIO);
        resultReceiver = intent.getParcelableExtra(Constantes.RECEIVER);
        return binder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        //fixme borrar todos los recursos del servicio
        pararSpeechService();
        funciones.vibrar(this, 2000);
        super.onDestroy();
    }


    /**
     * clase utilizda para devolver  Binder para acceder a las variables y metodos publicos del servicio
     */
    public class LocalBinder extends Binder {
        ServicioBind_RecognitionListener getBindService() {
            return ServicioBind_RecognitionListener.this;
        }
    }

    /**
     * ENVIARDO PAQUETE Y RESULT_DATA_KEY AL MAIN
     */
    private void toReceiver(String p, String key) {
        Bundle bundle = new Bundle();
        bundle.putString(key, p);
        resultReceiver.send(Constantes.SUCCESS_RESULT, bundle);

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
                Model model = new Model(assetDir.toString() + "/model-android");
                activityReference.get().kaldiRcgnzr = new KaldiRecognizer(model, 16000.0f);
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


    @Override
    public void onResult(String hypothesis) {
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
        try {
            String text = funciones.getFraseFromJson(hypothesis);
            if (!text.equals("")) {
                String[] arWord = funciones.decodeKaldiJSon(hypothesis, "words");
                String[] arConf = funciones.decodeKaldiJSon(hypothesis, "conf");
                String[] arText = funciones.decodeKaldiJSon(hypothesis, "text");
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


    /**
     * {
     * "partial" :"esto amigo"
     * }
     */

    @Override
    public void onPartialResult(String hypothesis) {
        try {
            String[] arPartial = funciones.decodeKaldiJSon(hypothesis, "partial");

            for (String s : arPartial) {
                if (!s.equals("")) {
                    procesarPartialSpechToText(getApplicationContext(), s);
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
     * INVOCADO CADA VEZ QUE TENEMOS UN RESULTADO PARCIAL DEL SPEECH CON INFORMACION
     */
    private void procesarPartialSpechToText(Context context, String s) {
        toReceiver(s, Constantes.NOTIFICACION_PARCIAL);
    }

    /**
     * INVOCADO CADA VEZ QUE TENEMOS UN RESULTADO DEL SPEECH CON INFORMACION
     */
    private void procesarResultSpechToText(Context context, String s, int confianza) {
        toReceiver(s, Constantes.NOTIFICACION_PALABRA);
    }


    /**
     * PROCESAR FRASE
     * se llama despues de todas las  llamadas a this.procesarResultadoSpechToText()
     */
    private void procesarTextTextToSpech(String frase) {
        toReceiver(frase, Constantes.NOTIFICACION_FRASE);
        if (frase.equals(Constantes.FRASE_ALERTA)) {
            Toast toast = new Toast(getApplicationContext());
            toast.setText("PArdillo xDD");
            toast.show();
        }
    }


    /**
     * ********** arranca el reconocedor **********>>
     */
    private void iniciarSpeechService() {
        try {
            speechService.startListening();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    /**
     * ********** para el reconocedor **********>>
     */
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

}






