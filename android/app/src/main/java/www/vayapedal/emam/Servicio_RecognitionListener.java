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
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.core.app.NotificationCompat;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


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

import static android.telephony.AvailableNetworkInfo.PRIORITY_LOW;
import static android.view.Display.STATE_OFF;
import static android.view.Display.STATE_ON;


public class Servicio_RecognitionListener extends Service implements RecognitionListener {


    private final Funciones funciones = new Funciones(this);

    /* fuses para los toast*/
    private boolean mostrarToastConfig = false;
    private boolean mostrarToastUsuario = true;

    /*Vosk-Kaldi*/
    private static Model model;
    private SpeechService speechService;
    private KaldiRecognizer kaldiRcgnzr;

    /*comunicacion SERVICIO*/
    private final IBinder binder = new LocalBinder();
    private ResultReceiver resultReceiver;

    //banderas
    public boolean sevicioIniciciado = false;
    public boolean isActividadEnlazada = false;

    /*GPS*/
    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locManager;
    private String localizacion;

    /*VARIABLES CONFIGURACION*/
    /* MAIL*/
    private String asuntoMail = "EMAN Alerta";
    private String passMail = "Angustia31";
    private String fromMail = "enviosemam@gmail.com";
    private String cuerpoMail = "Envio alerta! \nposición:";
    private String toMail = "diegolijo@gmail.com";
    private String numLlamada = "662023955";

    /*SMS*/
    private String cuerpoSms = "Alarma :";
    private String numSms = "662023955";
    private ArrayList<Palabra> listaPalabras = new ArrayList<>(); // todo palabras contra las que se compara


    private String texto = "cuerpo del mail";

    /* clase utilizda para devolver  Binder para acceder a las variables y metodos publicos del servicio*/
    public class LocalBinder extends Binder {
        Servicio_RecognitionListener getService() {
            return Servicio_RecognitionListener.this;
        }
    }

    /* ENVIARDO PAQUETE Y RESULT_DATA_KEY AL MAIN*/
/*    private void toReceiver(String p, String key) {
        Bundle bundle = new Bundle();
        bundle.putString(key, p);
        resultReceiver.send(Constantes.SUCCESS_RESULT, bundle);
        if (mostrarToastConfig) {
            Toast toast = Toast.makeText(this, " (speechService) ENVIANDO DATOS [" + p + "]", Toast.LENGTH_SHORT);
            toast.show();
        }
        Log.i("ToRECEIVER", "toReceiver: (speechService) ENVIANDO DATOS [" + p + "]");
    }*/


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
                        //              configurarSpeechService();
                        break;
                    case STATE_ON:
                        //     pararSpeechService();
                        break;
                    default:
                        break;
                }
            }
        }, new Handler(Looper.myLooper()));
    }


    /**
     * ----------->
     * creamos listener para el estado de la pantalla bloqueo
     */
    @Override
    public void onCreate() {
        try {
            super.onCreate();
            this.displayListener();
            this.listaPalabras.add(new Palabra("agua", Constantes.TRIGER1, new Date()));
            this.listaPalabras.add(new Palabra("fuego", Constantes.TRIGER2, new Date()));
            funciones.vibrar(this, Constantes.VIRAR_CORTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            String s = intent.getExtras().getString(Constantes.ORIGEN_INTENT);
            switch (s) {
                case Constantes.ON_TOGGLE:
                    this.startForeground(Constantes.ID_SERVICIO, createNotification());
                    configurarSpeechService();
                    break;
                case Constantes.ON_WIDGET:

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + s);
            }
        } catch (
                Exception exception) {
            exception.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
    /*    resultReceiver = intent.getParcelableExtra(Constantes.RECEIVER);
        String s = intent.getExtras().getString(Constantes.ORIGEN_INTENT);
        if (s.equals(Constantes.ON_CONFIG)) {
            toReceiver(Constantes.ON_CONFIG, Constantes.NOTIFICACION_SERVICIO);
        }
        if (s.equals(Constantes.ON_TOGGLE)) {
            toReceiver(Constantes.ON_TOGGLE, Constantes.NOTIFICACION_SERVICIO);
        }*/
        return binder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
/*
        toReceiver(Constantes.OFF_SERVICIO, Constantes.NOTIFICACION_SERVICIO);
        if (mostrarToastConfig) {
            Toast toast = Toast.makeText(getApplicationContext(), "onUnbind", Toast.LENGTH_LONG);
            toast.show();
        }*/
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        //fixme borrar todos los recursos del servicio
        if (speechService != null) {
            speechService.cancel();
            speechService = null;
        }
        if (mostrarToastConfig) {
            Toast toast = Toast.makeText(getApplicationContext(), "EL SERVICIO ESTA DESCONECTADO", Toast.LENGTH_LONG);
            toast.show();
        }
        funciones.vibrar(this, 2000);
        super.onDestroy();
    }


    public void configurarSpeechService() {
        if (speechService == null) {
            funciones.vibrar(this, Constantes.VIRAR_CORTO);
            new SetupSpeechTask(this).execute();
            if (mostrarToastConfig) {
                Toast toast = Toast.makeText(this, R.string.cconfigurando, Toast.LENGTH_LONG);
                toast.show();
            }
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
            if (speechService.startListening()) {  /** *********** arranca el reconocedor **********>>
             */
                Toast toast = Toast.makeText(this, "El reconocimiento de voz esta habilitado", Toast.LENGTH_SHORT);
                toast.show();

                //compronabos gps activo
                locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    muestraProviders();
                } else {
                    //  toReceiver(Constantes.NOTIFICACION_GPS_DISABLE, Constantes.NOTIFICACION_SERVICIO);
                    pararSpeechService();
                }
            } else {
                pararSpeechService();
            }
            //  toReceiver(Constantes.ON_BOTONES, Constantes.NOTIFICACION_SERVICIO);
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
            //  this.stopSelf();
        } catch (Exception e) {
            e.fillInStackTrace();
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
            String text = "";
            text = funciones.getFraseFromJson(hypothesis);
            if (!text.equals("")) {
                String[] arWord = funciones.decodeJSon(hypothesis, "words");
                String[] arConf = funciones.decodeJSon(hypothesis, "conf");
                String[] arText = funciones.decodeJSon(hypothesis, "text");
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
            String[] arPartial = funciones.decodeJSon(hypothesis, "partial");
            for (String s : arPartial) {
                if (!s.equals("")) {
                    //       toReceiver(s, Constantes.NOTIFICACION_PARCIAL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Exception e) {
        Toast toast = Toast.makeText(getApplicationContext(), "SERVICIO  onError", Toast.LENGTH_LONG);
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


        if (isActividadEnlazada) {
            if (mostrarToastConfig) {
                Toast toast = Toast.makeText(getApplicationContext(), "Oida  [" + s + "]", Toast.LENGTH_SHORT);
                toast.show();
            }
            // enviamos la palabra al receiver -> recogemos en main

            // toReceiver(s, Constantes.NOTIFICACION_PALABRA);

        }
        //   recorremos las palabas BD
        for (Palabra palabra : listaPalabras) {
            switch (palabra.rol) {
                case Constantes.TRIGER1:
                    if (palabra.clave.equals(s)) {
                        //actulaizamos la fecha a todas las palabras
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
                            enviarLocalizacion();
                            funciones.llamar(numLlamada, this);
                        } else {
                            funciones.vibrar(context, Constantes.VIRAR_CORTO);
                        }
                    }
                    break;
            }
        }
    }


    /*  PROCESAR FRASE
        este metodo
        se llama
        despues de
        todas las
        llamadas a this.procesarResultadoSpechToText()
        */
    private void procesarTextTextToSpech(String frase) {
        //  toReceiver(frase, Constantes.NOTIFICACION_FRASE);
        if (frase.equals(Constantes.FRASE_ALERTA)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Pardillo XD", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    public void enviarLocalizacion() {
        try {
            int permGPS = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int permGps = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permGPS == PackageManager.PERMISSION_GRANTED && permGps == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(Runnable::run, location -> {
                                if (location != null) {
                                    onResulLocation(location);
                                }
                            });

                    fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)  //getLastLocation()
                            .addOnSuccessListener(Runnable::run, location -> {
                                if (location != null) {
                                    onResulLocation(location);
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
            // funciones.enviarSms(numSms, text);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void enviaMail(String texto) {
        this.texto = texto;
        new MailBuilder(this).execute();

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
                /* NOS REDIRIGE A LA WEB DE GOOGLE para permitir al acceso de aplicaciones poco seguras */
                // todo - debe ser el mail@  con el que está logeado el telefono
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






