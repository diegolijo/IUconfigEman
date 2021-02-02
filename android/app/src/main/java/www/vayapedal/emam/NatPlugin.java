package www.vayapedal.emam;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

/************************************ CAPACITOR ********************************/
@NativePlugin()
public class NatPlugin extends Plugin {



    private static final String TAG = "toggleServicio";
    private final Funciones funciones = new Funciones();
    // instancia servicio
    private Servicio_RecognitionListener mainService;

    @PluginMethod()
    public void customCall(PluginCall call) {
        String message = call.getString("message");
        this.toggleServicio();


        call.success();
    }

    @PluginMethod()
    public void customFunction(PluginCall call) {
        call.resolve();
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
                }else{
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






}