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

    /**
     * instancia servicio
     */
    private Servicio_RecognitionListener mainService;
    private ServicioBind_RecognitionListener bindServize;

    /**
     * @PluginMethod() public void customMetod(PluginCall call) {
     * bridge = getBridge();
     * String message = call.getString("message");
     * this.toggleServicio();
     * JSObject ret = new JSObject();
     * ret.put("conectado...", message);
     * //    call.resolve(ret);
     * notifyListeners("myPluginEvent", ret);
     * }
     */


    @PluginMethod()
    public void servizeOperations(PluginCall call) {
        String accion = call.getString(Constantes.ACTION);
        switch (accion) {
            case Constantes.BIND:
                bindServicio(Constantes.ON_TOGGLE);
                break;
            case Constantes.UNBIND:
                unBindServicio();
                break;
            case Constantes.ON:
                this.toggleServicio();
                JSObject ret = new JSObject();
                ret.put(Constantes.ACTION, "RESPUESTA");
                call.resolve(ret);
                break;
            case Constantes.OFF:

                break;
        }
    }

    /** ********************************************** BD *****************************************************/
    /**
     * Inserta resgistros en la base de datos pasando por parametro un Json
     * {"tabla":"USUARIOS","registro":{"usuario":"jit","loginPass":"","mailFrom":""}...}
     */
    @PluginMethod()
    public void insertDB(PluginCall call) {
        JSObject resultJson = new JSObject();
        try {
            String tabla = call.getString(Constantes.TABLA);
            JSObject registro = call.getObject(Constantes.REGISTRO);
            JSObject row = registro.getJSObject("row");
            Context context = getContext();
            DB db = Room.databaseBuilder(context, DB.class, Constantes.DB_NAME).allowMainThreadQueries().build();
            switch (tabla) {
                case Constantes.USUARIOS:
                    Usuario user = new Usuario(
                            row.getString("usuario"),
                            row.getString("loginPass"),
                            row.getString("mailFrom"),
                            row.getString("mailPass"));
                    db.Dao().insertUsuario(user);
                    resultJson.put(Constantes.RESULT, true);
                    break;
                case Constantes.PALABRAS:
                    Palabra palabra = new Palabra(
                            row.getString("clave"),
                            row.getString("funcion"));
                    db.Dao().insertPalabra(palabra);
                    resultJson.put(Constantes.RESULT, true);
                    break;
                case Constantes.ALARMAS:
                    Alarma alarma = new Alarma(
                            row.getString("usuario"),
                            row.getString("clave"),
                            row.getString("numTlfTo"),
                            row.getString("mailTo"),
                            row.getBoolean("enable", false));
                    db.Dao().insertAlarma(alarma);
                    resultJson.put(Constantes.RESULT, true);
                    break;
            }
            db.close();
            call.resolve(resultJson);
        } catch (Exception ex) {
            ex.printStackTrace();
            resultJson = new JSObject();
            resultJson.put(Constantes.RESULT, false);
            call.resolve(resultJson);
        }


    }


    @PluginMethod()
    public void selectDB(@NotNull PluginCall call) {
        JSObject resultJson = new JSObject();
        try {
            String tabla = call.getString(Constantes.TABLA);
            String clave = call.getString(Constantes.CLAVE);
            Context context = getContext();
            DB db = Room.databaseBuilder(context, DB.class, Constantes.DB_NAME).allowMainThreadQueries().build();
            if (!clave.equals("")) {
                /**consulta a la BD*/
                switch (tabla) {
                    case Constantes.USUARIOS:
                        Usuario usuario = db.Dao().selectUsuario(clave);
                        resultJson.put(Constantes.RESULT, true);
                        JSObject user = new JSObject();
                        user.put("usuario", usuario.usuario);
                        user.put("loginPass", usuario.loginPass);
                        user.put("mailFrom", usuario.mailFrom);
                        user.put("mailPass", usuario.mailPass);
                        resultJson.put(Constantes.REGISTRO, user);
                        break;
                    case Constantes.PALABRAS:
                        // listaPalabras = db.Dao().selectPalabras();

                        break;
                    case Constantes.ALARMAS:
                        // listaPalabras = db.Dao().selectPalabras();

                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + tabla);
                }
            }
            db.close();
            call.resolve(resultJson);
        } catch (Exception ex) {
            ex.printStackTrace();
            resultJson = new JSObject();
            resultJson.put(Constantes.RESULT, false);
            call.resolve(resultJson);
        }
    }


    @PluginMethod()
    public void selectFuncion(@NotNull PluginCall call) {
        JSObject resultJson = new JSObject();
        try {
            String funcion = call.getString(Constantes.FUNCION);
            Context context = getContext();
            DB db = Room.databaseBuilder(context, DB.class, Constantes.DB_NAME).allowMainThreadQueries().build();
            if (!funcion.equals("")) {
                /**consulta a la BD*/


                List<Palabra> palabras = db.Dao().selectFuncion(funcion);
                resultJson.put(Constantes.RESULT, true);
                JSObject rows = new JSObject();
                JSObject p = new JSObject();
                int n = 0;
                for (Palabra palabra : palabras) {
                    p.put("clave", palabra.clave);
                    p.put("funcion", palabra.funcion);
                    p.put("fecha", palabra.fecha);
                    rows.put(n + "", p);
                    n += 1;
                }
                resultJson.put(Constantes.ROWS, rows);
            }
            db.close();
            call.resolve(resultJson);
        } catch (Exception ex) {
            ex.printStackTrace();
            resultJson = new JSObject();
            resultJson.put(Constantes.RESULT, false);
            call.resolve(resultJson);
        }
    }


    /************************************************* SERVIZE **************************************************/
    public void toggleServicio() {
        Context context = getContext();
        try {
            if (!funciones.isServiceRunning(context)) {
                Toast toast = Toast.makeText(context, "Inentamos arrancar el servicio", Toast.LENGTH_SHORT);
                toast.show();
                Intent i = new Intent(context, Servicio_RecognitionListener.class);
                //todo enviar el usuario para
                i.putExtra(Constantes.ORIGEN_INTENT, Constantes.ON_TOGGLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(i);
                } else {
                    context.startService(i);
                }
            } else {
                context.stopService(new Intent(context, Servicio_RecognitionListener.class));
                Toast toast = Toast.makeText(context, "Inentamos detener el servicio", Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Interefaz utilizada para enlazarse al servicio iniciado => this.bindServize
     */
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            ServicioBind_RecognitionListener.LocalBinder binder = (ServicioBind_RecognitionListener.LocalBinder) service;
            bindServize = binder.getBindService();
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
            Context context = getContext();
            if (!funciones.isServiceBindRunning(context)) {
                Intent in = new Intent(context, ServicioBind_RecognitionListener.class);
                in.putExtra(Constantes.ORIGEN_INTENT, Constantes.ON_TOGGLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startService(in);
                } else {
                    context.startService(in);
                }


                /************************************    receiver   SERVICIO   ************************************/
                Receiver resultReceiver = new Receiver(new Handler());
                Intent i = new Intent(getContext(), ServicioBind_RecognitionListener.class);
                i.putExtra(Constantes.ORIGEN_INTENT, mensaje);
                i.putExtra(Constantes.RECEIVER, resultReceiver);
                if (getContext().bindService(i, connection, Context.BIND_AUTO_CREATE)) {  /** main enlazado al servicio servicio */
                    if (bindServize != null) {
                        bindServize.configurarSpeechService();
                    }
                    Log.i("bindeService", "intentamos enlazarnos al servicio/iniciarlo");
                }
            } else {
                unBindServicio();
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    private void unBindServicio() {
        if (bindServize != null) {
            bindServize.pararServicio();
            Context context = bindServize.getApplicationContext();
            context.stopService(new Intent(context, ServicioBind_RecognitionListener.class));
            bindServize = null;
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

            if (receiverPatial != null && !receiverPatial.equals("")) {
                sendPartial(receiverPatial);
            }

            if (receiverPalabra != null && !receiverPalabra.equals("")) {
                sendResult(receiverPalabra);
            }

        }
    }

    /**************************************************  ToView ***************************************************/
    private void sendResult(String receiverPalabra) {
        JSObject ret = new JSObject();
        ret.put(Constantes.RESULT, receiverPalabra);//todo preparar la respuesta para el webView
        notifyListeners(Constantes.HOME_EVENT, ret);
    }

    private void sendPartial(String receiverPatial) {
        JSObject ret = new JSObject();
        ret.put(Constantes.RESULT, receiverPatial);//todo preparar la respuesta para el webView
        notifyListeners(Constantes.PALABRA_EVENT, ret);
    }

}