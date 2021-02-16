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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.net.Uri;

import www.vayapedal.emam.datos.DB;
import www.vayapedal.emam.datos.Palabra;

import static android.telephony.AvailableNetworkInfo.PRIORITY_LOW;
import static android.view.Display.STATE_OFF;
import static android.view.Display.STATE_ON;


public class Servicio_RecognitionListener extends Service implements RecognitionListener {


    private final Funciones funciones = new Funciones(this);


    /**
     * Vosk-Kaldi
     */
    private static Model model;
    private SpeechService speechService;
    private KaldiRecognizer kaldiRcgnzr;

    /**
     * GPS
     */
    private LocationManager locManager;
    private String localizacion;

    /**
     * VARIABLES CONFIGURACION
     */
    private String usuario;

    /**
     * MAIL
     */
    private String asuntoMail = "EMAN Alerta";
    private String passMail = "Angustia31";
    private String fromMail = "enviosemam@gmail.com";
    private String cuerpoMail = "Envio alerta! \nposición:";
    private String toMail = "diegolijo@gmail.com";
    private String numLlamada = "662023955";
    private String texto = "cuerpo del mail";

    /**
     * SMS
     */
    private String cuerpoSms = "Alarma :";
    private String numSms = "662023955";
    private List<Palabra> listaPalabras = new ArrayList<>();


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
                .setSmallIcon(R.drawable.icon_custom_large) //todo icono para la notificación
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
                        initSpeechService();
                        break;
                    case STATE_ON:
                        pararSpeechService();
                        break;
                    default:
                        break;
                }
            }
        }, new Handler(Looper.myLooper()));
    }


    /********************************************************  INIT DB ************************************************************* */
    private void initDB() {
        DB db = Room.databaseBuilder(this, DB.class, Constantes.DB_NAME).allowMainThreadQueries().build();
        listaPalabras = db.Dao().selectPalabras(usuario);
    }


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
            usuario = intent.getExtras().getString(Constantes.USUARIO);
            switch (s) {
                case Constantes.ON_TOGGLE:
                    this.startForeground(Constantes.ID_SERVICIO, createNotification());
                    funciones.vibrar(this, Constantes.VIRAR_CORTO);
                    break;
                case Constantes.ON_WIDGET: //todo
                    break;
                default:
                    throw new IllegalStateException("onStartCommand - Valor inesperado: " + s);
            }
        } catch (
                Exception exception) {
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
            this.initDB();
            funciones.vibrar(this, Constantes.VIRAR_CORTO);
        } catch (Exception ex) {
            Toast toast = Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

    }


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
            if (speechService.startListening()) {  /**----********** arranca el reconocedor *******---->> */
                //compronabos gps activo
                locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    muestraProviders();
                } else {
                    pararSpeechService();
                }
            } else {
                pararSpeechService();
            }
        } catch (Exception e) {
            pararSpeechService();
        }
    }

    public void pararSpeechService() {
        try {
            if (speechService != null) {
                speechService.stop();
                speechService.cancel();
                speechService.shutdown();
                speechService = null;
                kaldiRcgnzr.delete();
            }
        } catch (Exception e) {
            e.fillInStackTrace();
            throw e;
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
                String[] arWord = funciones.decodeKaldiJSon(hypothesis, "words");
                String[] arConf = funciones.decodeKaldiJSon(hypothesis, "conf");
                String[] arText = funciones.decodeKaldiJSon(hypothesis, "text");
                for (int i = 0; i < arWord.length; i++) {
                    float confianza = Float.parseFloat(arConf[i]) * 100;
                    procesarResultSpechToText(getApplicationContext(), arWord[i], (int) confianza);        //-----> procesar palabra
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
                    //    toReceiver(s, Constantes.NOTIFICACION_PARCIAL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Exception e) {
               this.pararSpeechService();
    }

    @Override
    public void onTimeout() {
    }


    /**
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
                            Toast toast = Toast.makeText(context, "Reconocida   \"" + palabra.clave + "\"  . Tienes " + Constantes.PERIODO_EN_ALERTA + " segundos para pronunciar la segunda palabra clave ", Toast.LENGTH_LONG);
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
                            funciones.llamar(numLlamada, this);
                        } else {
                            funciones.vibrar(context, Constantes.VIRAR_CORTO);
                        }
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


    public void getLocalizacion() {
        try {
            int permGPS = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int permGps = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permGPS == PackageManager.PERMISSION_GRANTED && permGps == PackageManager.PERMISSION_GRANTED) {
                /*GPS*/
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(Runnable::run, location -> {
                                if (location != null) {
                                    this.onResulLocation(location);
                                }
                            });

                    fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)  //getLastLocation()
                            .addOnSuccessListener(Runnable::run, location -> {
                                if (location != null) {
                                    this.onResulLocation(location);
                                }
                            });

                } else {
                    Intent intentGps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intentGps.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentGps);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onResulLocation(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        localizacion = lat + "," + lon;
        new MailBuilder(this).execute();
        try {
            String text = cuerpoSms + " " + Constantes.W_MAPS + localizacion;
            funciones.enviarSms(numSms, text);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private class MailBuilder extends AsyncTask<Void, Void, Void> {
        WeakReference<Servicio_RecognitionListener> activityReference;

        MailBuilder(Servicio_RecognitionListener activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Properties propiedades = new Properties();
                propiedades.put("mail.smtp.auth", "true");
                propiedades.put("mail.smtp.starttls.enable", "true");
                propiedades.put("mail.smtp.host", "smtp.gmail.com");
                propiedades.put("mail.smtp.port", "587");
                propiedades.put("mail.smtp.socketFactory.port", 587);
                Session session = Session.getInstance(propiedades, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(activityReference.get().fromMail,
                                activityReference.get().passMail);
                    }
                });
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(activityReference.get().fromMail));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(activityReference.get().toMail));
                message.setSubject(activityReference.get().asuntoMail);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy 'T'HH:mm:ss'Z'", new Locale("ES"));
                Date date = new Date();
                String dateTime = dateFormat.format(date);
                String text = activityReference.get().cuerpoMail + " " + Constantes.W_MAPS + activityReference.get().localizacion + "\n" + "\n" + dateTime + "\n--" + texto;
                message.setText(text);
                Transport.send(message);


            } catch (MessagingException e) {
                /** NOS REDIRIGE A LA WEB DE GOOGLE para permitir al acceso de aplicaciones poco seguras */
                // fixme- debe ser el gmail@  con el que está logeado el telefono
                String url;
                url = Constantes.PERMITIR_APPS_POCO_SEGURAS;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Log.e("MailBuilder", Objects.requireNonNull(e.getMessage()));
            }
            return null;
        }

        //todo  FUNCIONES NUEVAS:
        // ---  tarea programada que chequee la actividad del usuario con el dispositivo para disparar la alarma

    }
}






