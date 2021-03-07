package www.vayapedal.emam;

import android.Manifest;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.AsyncTask;


import android.app.Service;
import android.os.Build;
import android.os.IBinder;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.content.pm.PackageManager;
import android.provider.Settings;


import org.kaldi.Assets;
import org.kaldi.KaldiRecognizer;
import org.kaldi.Model;
import org.kaldi.RecognitionListener;
import org.kaldi.SpeechService;
import org.kaldi.Vosk;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import www.vayapedal.emam.datos.Alarma;
import www.vayapedal.emam.datos.DB;
import www.vayapedal.emam.datos.Palabra;
import www.vayapedal.emam.datos.Usuario;

import static android.telephony.AvailableNetworkInfo.PRIORITY_LOW;
import static android.view.Display.STATE_OFF;
import static android.view.Display.STATE_ON;


public class Servicio_RecognitionListener extends Service implements RecognitionListener {


    private final Funciones funciones = new Funciones(this);

    /**
     * KALDY
     */
    private SpeechService speechService;
    private KaldiRecognizer kaldiRcgnzr;

    /**
     * TTS
     */
    private TTSpeech tts;

    /**
     * GPS
     */
    private LocationManager locManager;
    private String localizacion;


    /**
     * MAIL
     */
    private String passMail;
    private String mailFrom;
    private String cuerpoMail = "Envio alerta! \nposición:";
    private String asuntoMail = "EMAN Alerta";
    private String texto = "cuerpo del mail";

    /**
     * SMS
     */
    private String cuerpoSms = "Alarma :";

    /**
     * DB
     */
    private Usuario usuario;
    private List<Palabra> listaPalabras = new ArrayList<>();
    private List<Alarma> listaAlarmas = new ArrayList<>();

    /**
     * fuses para los toast
     */
    //todo lanzar los toast con una tabla de la BD
    private boolean initSpeechServiceToast;
    private boolean mostrarToastConfig = false;
    private boolean mostrarToastUsuario = true;


    /******************************************** notificacion para startForeground ******************************************/
    public Notification createNotification() {
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.icon_custom_large)
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


    /*****************************listener para el estado de la pantalla (dispositivo: bloqueado / desbloqueado)***********************/
    private void displayListener() {
        DisplayManager displayManager =
                (DisplayManager) this.getSystemService(Context.DISPLAY_SERVICE);
        displayManager.registerDisplayListener(new DisplayManager.DisplayListener() {
            private boolean first = true;

            @Override
            public void onDisplayAdded(int displayId) {
                Log.i("onDisplayAdded", "displayId: " + displayId);
            }

            @Override
            public void onDisplayRemoved(int displayId) {
                Log.i("onDisplayRemoved", "displayId: " + displayId);
            }

            @Override
            public void onDisplayChanged(int displayId) {
                Log.i("onDisplayChanged", "displayId: " + displayId);
                Display display = displayManager.getDisplay(0);
                int state = display.getState();
                switch (state) {
                    case STATE_OFF:
                        initDB();
                        initTTS();
                        initSpeechService();
                        break;
                    case STATE_ON:
                        stopSpeechService();
                        stopTTS();
                        break;
                    default:
                        break;
                }
            }
        }, new Handler(Looper.myLooper()));
    }


    /*************************************************************  INIT DB *************************************************************/

    private void initUser(String usuario) {
        try {
            DB db = Room.databaseBuilder(this, DB.class, Constantes.DB_NAME).allowMainThreadQueries().build();
            this.usuario = db.Dao().selectUsuario(usuario);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void initDB() {
        DB db = Room.databaseBuilder(this, DB.class, Constantes.DB_NAME).allowMainThreadQueries().build();
        listaPalabras = db.Dao().selectPalabras(usuario.usuario);
        listaAlarmas = db.Dao().selectAlarmasFun(usuario.usuario, Constantes.TRIGER2);
        passMail = usuario.mailPass;
        mailFrom = usuario.mailFrom;
        //todo recorrer la lista de alarmas para lanzar tareas asincronas con el envio de sms
        // las llamadas programarlas para lanzarlas en intervalos de tiempo


    }

    /******************************************************* ciclo vida servize **********************************************************/

    @Override
    public void onCreate() {
        try {
            super.onCreate();
            this.displayListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            String s = intent.getExtras().getString(Constantes.ORIGEN_INTENT);
            /**  VARIABLES CONFIGURACION  */
            String usuarioKey = intent.getExtras().getString(Constantes.USUARIO);
            initUser(usuarioKey);
            initDB();
            switch (s) {
                case Constantes.ON_TOGGLE:
                    this.startForeground(Constantes.ID_SERVICIO, createNotification());
                    funciones.vibrar(this, Constantes.VIRAR_CORTO);
                    if (!checkGPS()) {
                        Toast toast = Toast.makeText(getApplicationContext(), Constantes.MSG_ERROR_GPS, Toast.LENGTH_LONG);
                        toast.show();
                    }
                    break;
                case Constantes.ON_WIDGET: //todo
                    break;
                default:
                    throw new IllegalStateException("onStartCommand - Valor inesperado: " + s);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        //fixme borrar todos los recursos del servicio
        if (speechService != null) {
            this.speechService.cancel();
            this.speechService = null;
            this.kaldiRcgnzr = null;
        }
        super.onDestroy();
    }


    public void initSpeechService() {
        try {
            speechService = null;
            new SetupSpeechTask(this).execute();
            funciones.vibrar(this, Constantes.VIRAR_CORTO);
        } catch (Exception ex) {
            Toast toast = Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

    }


    /*********************************************** TTS ************************************************************/

    private void initTTS() {
    }

    private void stopTTS() {
    }

    /*********************************************** KALDI ************************************************************/

    private static class SetupSpeechTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<Servicio_RecognitionListener> activityReference;

        SetupSpeechTask(Servicio_RecognitionListener activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                Vosk.SetLogLevel(0);
                /**
                 * Vosk-Kaldi
                 */
                Model model = new Model(assetDir.toString() + "/model-android");
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
            if (speechService.startListening()) {  /* ----********** arranca el reconocedor *******---->> */
                /**compronabos gps activo*/
                locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    muestraProviders();
                } else {
                    stopSpeechService();
                }
            } else {
                stopSpeechService();
            }
        } catch (Exception e) {
            stopSpeechService();
        }
    }

    public void stopSpeechService() {
        try {
            if (speechService != null) {
                speechService.stop();
                speechService.cancel();
                speechService.shutdown();
                speechService = null;
                kaldiRcgnzr.delete();
                kaldiRcgnzr = null;
            }
        } catch (Exception e) {
            e.fillInStackTrace();
            throw e;
        }
    }


    /************************************************** eventos kaldi  *************************************************************/
    @Override
    public void onResult(String hypothesis) {
        /*
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
                    procesarResultSpechToText(getApplicationContext(), arWord[i], (int) confianza);//-----> procesar palabra
                    Log.i("Vosk", "palabra oida -> " + arWord[i]);
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
            String[] arPartial = funciones.decodeKaldiJSon(hypothesis, "partial");
            for (String s : arPartial) {
                if (!s.equals("")) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Exception e) {
        this.stopSpeechService();
    }

    @Override
    public void onTimeout() {
    }


    /*************************************************************************************************************************************
     * INVOCADO CADA VEZ QUE TENEMOS UN RESULTADO DEL SPEECH CON INFORMACION
     */
    private void procesarResultSpechToText(Context context, String s, int confianza) {

        /**recorremos las palabras filtro*/
        for (Palabra palabra : listaPalabras) {
            switch (palabra.funcion) {
                case Constantes.TRIGER1:
                    if (palabra.clave.equals(s)) {
                        /**si hay una coincidencia actulaizamos la fecha a todas las palabras*/
                        for (Palabra p : listaPalabras) {
                            p.fecha = new Date();
                        }
                        funciones.vibrar(context, Constantes.VIRAR_CORTO);
                        if (mostrarToastUsuario) {
                            Toast toast = Toast.makeText(context, "Reconocida   \"" +
                                    palabra.clave + "\"  . Tienes " + Constantes.PERIODO_EN_ALERTA +
                                    " segundos para pronunciar la segunda palabra clave ", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                    break;
                case Constantes.TRIGER2:
                    if (palabra.clave.equals(s)) {
                        Date d = new Date();
                        long thisTime = d.getTime();
                        long save = palabra.fecha.getTime();
                        long dif = Math.abs(thisTime - save) / 1000;
                        if (dif < Constantes.PERIODO_EN_ALERTA) {
                            funciones.vibrar(context, Constantes.VIBRAR_LARGO);
                            this.getLocalizacion();
                            // todo -> tasks con todos los numeros de la lista alarma
                            funciones.llamar(listaAlarmas.get(0).numTlfTo, this);
                        } else {
                            funciones.vibrar(context, Constantes.VIRAR_CORTO);
                        }
                    }
                    break;
                case Constantes.TRIGER3:
                    if (palabra.clave.equals(s)) {
                    // todo
                    }
                    break;
            }
        }
    }


    /**
     * PROCESAR FRASE
     * este metodo se llama despues de todas las llamadas a this.procesarResultadoSpechToText()
     */
    private void procesarTextTextToSpech(String frase) {

    }

    /************************************************ GPS  ***********************************************/
    private void muestraProviders() {
        List<String> proveedores = locManager.getAllProviders();
        for (String proveedor : proveedores) {
            String[] A = {"n/d", "preciso", "impreciso"};
            String[] P = {"n/d", "bajo", "medio", "alto"};
            LocationProvider info = locManager.getProvider(proveedor);
            Log.i("locManager", "LocationProvider[ " + "getName=" + info.getName()
                    + ", isProviderEnabled="
                    + locManager.isProviderEnabled(proveedor) + ", getAccuracy="
                    + A[Math.max(0, info.getAccuracy())] + ", getPowerRequirement="
                    + P[Math.max(0, info.getPowerRequirement())]
                    + ", hasMonetaryCost=" + info.hasMonetaryCost()
                    + ", requiresCell=" + info.requiresCell()
                    + ", requiresNetwork=" + info.requiresNetwork()
                    + ", requiresSatellite=" + info.requiresSatellite()
                    + ", supportsAltitude=" + info.supportsAltitude()
                    + ", supportsBearing=" + info.supportsBearing()
                    + ", supportsSpeed=" + info.supportsSpeed() + " ]\n");
        }
    }

    private boolean checkGPS() {
        boolean result = true;
        int permGPS = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permGps = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permGPS == PackageManager.PERMISSION_GRANTED && permGps == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener(Runnable::run, location -> {
                            if (location != null) {
                                //todo que hace ssi el gps esta activado?
                            }
                        });
            } else {
                result = false;
                startActivityTurnOnGps();
            }
        }
        return result;
    }

    public void getLocalizacion() {

        try {
            int permGPS = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int permGps = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permGPS == PackageManager.PERMISSION_GRANTED && permGps == PackageManager.PERMISSION_GRANTED) {
                /**GPS**/
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    for (Alarma alarma : listaAlarmas) {
                        if (alarma.funcion.equals(Constantes.TRIGER2)) {
                            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                                    .addOnSuccessListener(Runnable::run, location -> {
                                        if (location != null) {
                                            this.onResulLocation(alarma.numTlfTo, passMail, mailFrom, alarma.mailTo, cuerpoMail, asuntoMail, texto, location);
                                        }
                                    });
                        }
                    }
                } else {
                    startActivityTurnOnGps();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onResulLocation(String numTlfTo, String passMail, String mailFrom, String mailTo, String cuerpoMail, String asuntoMail, String texto, Location location) {

        double lat = location.getLatitude();
        double lon = location.getLongitude();
        localizacion = lat + "," + lon;
        try {
            //todo comprobar que sea un mail con formato valido, o en el insert a la BD
            new MailBuilder(getApplicationContext(), passMail, mailFrom, mailTo, cuerpoMail, asuntoMail, texto, localizacion).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String text = cuerpoSms + " " + Constantes.W_MAPS + localizacion;
            funciones.enviarSms(numTlfTo, text);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startActivityTurnOnGps() {
        Intent intentGps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intentGps.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentGps);
    }


}






