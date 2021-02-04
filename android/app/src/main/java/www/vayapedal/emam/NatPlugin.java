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
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

/*************************************** CAPACITOR ******************************************/
@NativePlugin()
public class NatPlugin extends Plugin {


    private static final String TAG = "toggleServicio";
    private final Funciones funciones = new Funciones();
    // instancia servicio
    private Servicio_RecognitionListener mainService;
    private String resultToView;

    @PluginMethod()
    public void customCall(PluginCall call) {
        String message = call.getString("message");
        this.toggleServicio();
        JSObject ret = new JSObject();
        ret.put("conectado...", message);
        call.resolve(ret);
    }

    @PluginMethod()
    public void getPalabra(PluginCall call) {
        String message = call.getString("message");
        JSObject ret = new JSObject();
        ret.put(resultToView, message);
        call.resolve(ret);
    }


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
            Log.e(TAG, "toggleServicio: ", ex);
        }
    }


    /********************************************************configuracion**************************************************/
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


    private void sendResult(String receiverPalabra) {
        resultToView = receiverPalabra;
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




}