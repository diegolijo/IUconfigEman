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

import androidx.room.Room;

import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.util.ArrayList;
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
                    if (!user.usuario.equals("")) {
                        db.Dao().insertUsuario(user);
                        resultJson.put(Constantes.RESULT, true);
                    }
                    break;
                case Constantes.PALABRAS:
                    Palabra palabra = new Palabra(
                            row.getString("clave"),
                            row.getString("funcion"),
                            row.getString("descripcion"),
                            row.getString("usuario"));
                    if (!palabra.clave.equals("")) {
                        db.Dao().insertPalabra(palabra);
                        resultJson.put(Constantes.RESULT, true);
                    }
                    break;
                case Constantes.ALARMAS:
                    Alarma alarma = new Alarma(
                            row.getString("funcion"),
                            row.getString("usuario"),
                            row.getString("numTlfTo"),
                            row.getString("mailTo"),
                            row.getBoolean("enable", false));
                    if (!alarma.funcion.equals("")) {
                        db.Dao().insertAlarma(alarma);
                        resultJson.put(Constantes.RESULT, true);
                    }
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
            String usuario = call.getString(Constantes.USER);
            Context context = getContext();
            DB db = Room.databaseBuilder(context, DB.class, Constantes.DB_NAME).allowMainThreadQueries().build();

            /**consulta a la BD*/
            switch (tabla) {
                case Constantes.USUARIOS:
                    Usuario dbUsuario = db.Dao().selectUsuario(clave);
                    resultJson.put(Constantes.RESULT, true);
                    JSObject user = new JSObject();
                    user.put("usuario", dbUsuario.usuario);
                    user.put("loginPass", dbUsuario.loginPass);
                    user.put("mailFrom", dbUsuario.mailFrom);
                    user.put("mailPass", dbUsuario.mailPass);
                    resultJson.put(Constantes.REGISTRO, user);
                    break;
                case Constantes.PALABRAS:
                    List<Palabra> listaPalabras = db.Dao().selectPalabras(usuario);
                    if(!clave.equals("")){
                        listaPalabras = db.Dao().selectPalabrasFuncion(usuario,clave);
                    }
                    resultJson.put(Constantes.RESULT, true);
                    List<JSObject> listPalabras = new ArrayList<>();
                    for (Palabra palabra : listaPalabras) {
                        JSObject jSpalabra = new JSObject();
                        jSpalabra.put("clave", palabra.clave);
                        jSpalabra.put("usuario", palabra.usuario);
                        jSpalabra.put("funcion", palabra.funcion);
                        jSpalabra.put("descripcion", palabra.descripcion);
                        listPalabras.add(jSpalabra);
                    }
                    JSONArray jsonArrayPal = new JSONArray(listPalabras);
                    resultJson.put(Constantes.ROWS, jsonArrayPal);
                    break;
                case Constantes.ALARMAS:
                    // listaPalabras = db.Dao().selectPalabras();
                    List<Alarma> listaAlarmas = db.Dao().selectAlarmas(usuario);
                    if (!clave.equals("")) {
                        Alarma alarma = db.Dao().selectAlarmasFun(usuario, clave);
                        listaAlarmas.clear();
                        listaAlarmas.set(0, alarma);
                    }
                    resultJson.put(Constantes.RESULT, true);
                    List<JSObject> listAlarmas = new ArrayList<>();
                    for (Alarma alarma : listaAlarmas) {
                        JSObject jSAlarma = new JSObject();
                        jSAlarma.put("funcion", alarma.funcion);
                        jSAlarma.put("usuario", alarma.usuario);
                        jSAlarma.put("numTlfTo", alarma.numTlfTo);
                        jSAlarma.put("mailTo", alarma.mailTo);
                        jSAlarma.put("enable", alarma.enable);
                        listAlarmas.add(jSAlarma);
                    }
                    JSONArray jsonArrayAlm = new JSONArray(listAlarmas);
                    resultJson.put(Constantes.ROWS, jsonArrayAlm);
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
    public void deleteDB(@NotNull PluginCall call) {
        JSObject resultJson = new JSObject();
        try {
            String tabla = call.getString(Constantes.TABLA);
            String clave = call.getString(Constantes.CLAVE);
            String usu = call.getString(Constantes.USER);
            Context context = getContext();
            DB db = Room.databaseBuilder(context, DB.class, Constantes.DB_NAME).allowMainThreadQueries().build();
            if (!clave.equals("")) {
                switch (tabla) {
                    case Constantes.USUARIOS:
                        db.Dao().selectUsuario(clave);
                        resultJson.put(Constantes.RESULT, true);
                        break;
                    case Constantes.PALABRAS:
                        Palabra palabra = new Palabra(clave, "", "", usu);
                        db.Dao().deletePalabras(palabra);
                        resultJson.put(Constantes.RESULT, true);
                        break;
                    case Constantes.ALARMAS:
                        Alarma alarma = new Alarma(clave, usu, "", "", false);
                        db.Dao().deleteAlarmas(alarma);
                        resultJson.put(Constantes.RESULT, true);
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


    /************************************************* SERVIZE **************************************************/

    @PluginMethod()
    public void servizeOperations(PluginCall call) {
        String accion = call.getString(Constantes.ACTION);
        String usuario = call.getString(Constantes.USUARIO);
        JSObject resultJson = new JSObject();
        switch (accion) {
            case Constantes.BIND:
                bindServicio(usuario);
                break;
            case Constantes.UNBIND:
                unBindServicio();
                break;
            case Constantes.ON:
                this.onServicio(usuario);
                break;
            case Constantes.OFF:
                this.offServicio();
                break;
            case Constantes.IS_RUNNING:
                boolean b = funciones.isServiceRunning(getContext());
                resultJson.put(Constantes.RESULT, b);
                call.resolve(resultJson);
                break;

        }
    }

    public void onServicio(String usuario) {
        Context context = getContext();
        try {
            if (!funciones.isServiceRunning(context)) {
                Intent i = new Intent(context, Servicio_RecognitionListener.class);
                i.putExtra(Constantes.USUARIO, usuario);
                //todo enviar usuario para cargar sus palabras y alarmas en el servicio para
                i.putExtra(Constantes.ORIGEN_INTENT, Constantes.ON_TOGGLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(i);
                } else {
                    context.startService(i);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void offServicio() {
        Context context = getContext();
        try {
            if (funciones.isServiceRunning(context)) {
                getContext().stopService(new Intent(context, Servicio_RecognitionListener.class));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void bindServicio(String usuario) {
        try {
            Context context = getContext();
            if (!funciones.isServiceBindRunning(context)) {
                /************************************    receiver   SERVICIO   ************************************/
                Receiver resultReceiver = new Receiver(new Handler());
                Intent i = new Intent(getContext(), ServicioBind_RecognitionListener.class);
                i.putExtra(Constantes.RECEIVER, resultReceiver);
                i.putExtra(Constantes.USUARIO, usuario);
                if (getContext().bindService(i, connection, Context.BIND_AUTO_CREATE)) {  /** main enlazado al servicio servicio */
                    if (bindServize != null) {
                        bindServize.configurarSpeechService();
                    }
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    private void unBindServicio() {
        if (bindServize != null) {
            getContext().unbindService(connection);
            bindServize = null;
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
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };


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
        JSObject result = new JSObject();
        result.put(Constantes.RESULT, receiverPalabra);//todo preparar la respuesta para el webView
        notifyListeners(Constantes.HOME_EVENT, result);
    }

    private void sendPartial(String receiverPatial) {
        Bridge b = getBridge();
        JSObject result = new JSObject();
        result.put(Constantes.RESULT, receiverPatial);//todo preparar la respuesta para el webView
        notifyListeners(Constantes.PARTIAL_EVENT, result);
    }

    private void sendFrase(String receiverPalabra) {
        JSObject result = new JSObject();
        result.put(Constantes.RESULT, receiverPalabra);//todo preparar la respuesta para el webView
        notifyListeners(Constantes.PALABRA_EVENT, result);
    }

}