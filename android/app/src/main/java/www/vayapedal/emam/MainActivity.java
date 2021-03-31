package www.vayapedal.emam;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getcapacitor.BridgeActivity;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginHandle;

import java.util.ArrayList;


public class MainActivity extends BridgeActivity {


    // Requesting permission
    private final String[] audioPermissions = {Manifest.permission.RECORD_AUDIO};
    private final String[] callPermissions = {Manifest.permission.CALL_PHONE};
    private final String[] gpsPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION};
    private final String[] locationPermissions = {Manifest.permission.ACCESS_FINE_LOCATION};
    private final String[] smsPermissions = {Manifest.permission.SEND_SMS};
    private final String[] contactsPermissions = {Manifest.permission.READ_CONTACTS};


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /************** CAPACITOR ***************** Initializes the Bridge ************/
        this.init(savedInstanceState, new ArrayList<Class<? extends Plugin>>() {{
            add(NatPlugin.class);
        }});


        this.checkPermisos();


 /*       PluginHandle appHandle = this.bridge.getPlugin("FirebaseDynamicLink");
        NatPlugin NatPlugin = (NatPlugin) appHandle.getInstance();*/

    }

    /**
     * lanzamos el siguiente  requestPermissions cuando se completa el anterior
     */
    private void checkPermisos() {
        int permGPS = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permGps = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int permSms = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int permLlamada = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int permContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (permGPS != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, gpsPermissions, Constantes.PERMISSIONS_REQUEST_GPS);
        } else if (permGps != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, locationPermissions, Constantes.PERMISSIONS_REQUEST_LOCATION);
        } else if (permLlamada != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, callPermissions, Constantes.PERMISSIONS_REQUEST_CALL_PHONE);
        } else if (permSms != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, smsPermissions, Constantes.PERMISSIONS_REQUEST_SMS);
        } else if (permMic != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, audioPermissions, Constantes.PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permContacts != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, contactsPermissions, Constantes.PERMISSIONS_REQUEST_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            if (requestCode == Constantes.PERMISSIONS_REQUEST_RECORD_AUDIO) {
                checkPermisos();
            }
            if (requestCode == Constantes.PERMISSIONS_REQUEST_CALL_PHONE) {
                checkPermisos();
            }
            if (requestCode == Constantes.PERMISSIONS_REQUEST_GPS) {
                checkPermisos();
            }
            if (requestCode == Constantes.PERMISSIONS_REQUEST_LOCATION) {
                checkPermisos();
            }
            if (requestCode == Constantes.PERMISSIONS_REQUEST_SMS) {
                checkPermisos();
            }
            if (requestCode == Constantes.PERMISSIONS_REQUEST_CONTACTS) {
                checkPermisos();
            }
        }
    }


}
