package www.vayapedal.emam;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import androidx.room.Room;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.List;

import www.vayapedal.emam.datos.Alarma;
import www.vayapedal.emam.datos.DB;
import www.vayapedal.emam.datos.Usuario;
import www.vayapedal.emam.datos.Palabra;

/*************************************** CAPACITOR ******************************************/
@NativePlugin()
public class NatPlugin extends Plugin {


    private final Funciones funciones = new Funciones();
    // instancia servicio
    private Servicio_RecognitionListener mainService;



/*
         @PluginMethod()
        public void customMetod(PluginCall call) {
        bridge = getBridge();
        String message = call.getString("message");
        this.toggleServicio();
        JSObject ret = new JSObject();
        ret.put("conectado...", message);
        //    call.resolve(ret);
        notifyListeners("myPluginEvent", ret);
        }
*/


    @PluginMethod()
    public void servizeOperations(PluginCall call) {
        String accion = call.getString(Constantes.ACCION);
        this.toggleServicio();
        JSObject ret = new JSObject();
        ret.put(Constantes.ACCION, "RESPUESTA");
        call.resolve(ret);
    }

    /**
     * Inserta resgistros en la base de datos pasando por parametro un Json {"tabla":"USUARIOS","registro":{"usuario":"jit","loginPass":"","mailFrom":""}} */
    @PluginMethod()
    public void insertDB(PluginCall call) {
        JSObject resJson = new JSObject();
        try {
            String tabla = call.getString(Constantes.TABLA);
            JSObject registro = call.getObject(Constantes.REGISTRO);
            Context context = getContext();
            DB db = Room.databaseBuilder(context, DB.class, Constantes.DB_NAME).allowMainThreadQueries().build();
            switch (tabla) {
                case Constantes.USUARIOS:
                    Usuario user = new Usuario(
                            registro.getString("usuario"),
                            registro.getString("loginPass"),
                            registro.getString("mailFrom"),
                            registro.getString("mailPass"));
                    db.Dao().insertUsuario(user);
                    resJson.put(Constantes.RESULT, true);
                    break;
                case Constantes.PALABRAS:
                    Palabra palabra = new Palabra(
                            registro.getString("clave"),
                            registro.getString("rol"));
                    db.Dao().insertPalabra(palabra);
                    resJson.put(Constantes.RESULT, true);
                    break;
                case Constantes.ALARMAS:
                    Alarma alarma = new Alarma(
                            registro.getString("usuario"),
                            registro.getString("clave"),
                            registro.getString("numTlfTo"),
                            registro.getString("mailTo"));
                    db.Dao().insertAlarma(alarma);
                    resJson.put(Constantes.RESULT, true);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + tabla);
            }
            call.resolve(resJson);
        } catch (Exception ex) {
            ex.printStackTrace();
            resJson.put(Constantes.RESULT, false);
            call.resolve(resJson);
        }


    }


    @PluginMethod()
    public void selectDB(@NotNull PluginCall call) {
        String tabla = call.getString(Constantes.TABLA);
        String clave = call.getString(Constantes.CLAVE);
        JSObject respuesta = new JSObject();
        respuesta.put(Constantes.TABLA, tabla);
        if (!clave.equals("")) {
            /**consulta a la BD*/
            Context context = getContext();
            DB db = Room.databaseBuilder(context, DB.class, Constantes.DB_NAME).allowMainThreadQueries().build();
            switch (tabla) {
                case Constantes.USUARIOS:
                    Usuario usuario = db.Dao().selectUsuario(clave);
                    respuesta.put(Constantes.CLAVE, tabla);
                    break;
                case Constantes.PALABRAS:
                    //         listaPalabras = db.Dao().selectPalabras();

                    break;
                case Constantes.ALARMAS:
                    //        listaPalabras = db.Dao().selectPalabras();

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + tabla);
            }


            db.close();
            call.resolve(respuesta);
        }
        call.resolve(null);
    }


    /********************************************************configuracion**************************************************/
    public void toggleServicio() {
        Context context = getContext();
        try {
            if (!funciones.isServiceRunning(context)) {
                Toast toast = Toast.makeText(context, "Inentamos arrancar el servicio", Toast.LENGTH_SHORT);
                toast.show();
                Intent i = new Intent(context, Servicio_RecognitionListener.class);
                i.putExtra(Constantes.ORIGEN_INTENT, Constantes.ON_TOGGLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(i);
                    //    this.bindServicio("");
                } else {
                    context.startService(i);
                }
            } else {
                //unBindServicio();
                context.stopService(new Intent(context, Servicio_RecognitionListener.class));
                Toast toast = Toast.makeText(context, "Inentamos detener el servicio", Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Interefaz utilizada para enlazarse al servicio iniciado = this.mainService
     */
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Servicio_RecognitionListener.LocalBinder binder = (Servicio_RecognitionListener.LocalBinder) service;
            mainService = binder.getService();
            Toast toast = Toast.makeText(getContext(), "onServiceConnected [ " + className + " ]", Toast.LENGTH_LONG);
            toast.show();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Toast toast = Toast.makeText(getContext(), "onServiceDisconnected [" + arg0 + "]", Toast.LENGTH_LONG);
            toast.show();
        }
    };


    public void bindServicio(String mensaje) {
        try {
            /* ****************************  receiver   SERVICIO   *****************************/
            Receiver resultReceiver = new Receiver(new Handler());
            Intent i = new Intent(getContext(), Servicio_RecognitionListener.class);
            i.putExtra(Constantes.ORIGEN_INTENT, mensaje);
            i.putExtra(Constantes.RECEIVER, resultReceiver);
            if (getContext().bindService(i, connection, Context.BIND_AUTO_CREATE)) {  /* main enlazado al servicio servicio */
                if (mainService != null) {
                    mainService.configurarSpeechService();
                }
                Log.i("bindeService", "intentamos enlazarnos al servicio/iniciarlo");

            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    private void unBindServicio() {
        //desdenlazamos la actividad del servicio
        if (mainService != null) {
            getContext().unbindService(connection);
        }
    }


    /**
     * Receiver de los datos enviados por el servicio.
     */
    private class Receiver extends ResultReceiver {
        Receiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String receiverPatial = resultData.getString(Constantes.NOTIFICACION_PARCIAL);
            String receiverPalabra = resultData.getString(Constantes.NOTIFICACION_PALABRA);
            String receiverTexto = resultData.getString(Constantes.NOTIFICACION_FRASE);
            if (receiverPalabra != null && !receiverPalabra.equals("")) {
                sendResult(receiverPalabra);
            }
            //  MENSAJES DEL COMPORTAMIENTO DEL SERVICIO
   /*         String receiverServicio = resultData.getString(Constantes.NOTIFICACION_SERVICIO);
            if (receiverServicio != null) {
                switch (receiverServicio) {
                    case Constantes.ON_CONFIG:

                        break;
                    case Constantes.ON_TOGGLE:

                        break;
                    case Constantes.ON_BOTONES:


                        break;
                    case Constantes.OFF_SERVICIO:

                        break;
                    case Constantes.NOTIFICACION_GPS_DISABLE:

                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + receiverServicio);
                }
            }


            if (receiverPatial != null) {
                receiverPatial = funciones.formatoTexto(receiverPatial, Constantes.NOTIFICACION_PARCIAL);

            }


            if (receiverTexto != null) {
                receiverTexto = funciones.formatoTexto(receiverTexto, Constantes.NOTIFICACION_FRASE);

            }
            */
        }

    }

    private void sendResult(String receiverPalabra) {
        JSObject ret = new JSObject();
        ret.put("conectado...", receiverPalabra);//todo preparar la respuesta para el webView
        notifyListeners(Constantes.PLUGIN_EVENT, ret);
    }


}