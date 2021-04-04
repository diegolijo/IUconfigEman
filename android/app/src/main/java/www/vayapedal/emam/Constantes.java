package www.vayapedal.emam;

import android.content.Intent;

public class Constantes {

    public static final String PACKAGE_NAME = "com.vayapedal.EMAN";

    /**
     * permisos
     */
    public static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    public static final int PERMISSIONS_REQUEST_CALL_PHONE = 2;
    public static final int PERMISSIONS_REQUEST_GPS = 3;
    public static final int PERMISSIONS_REQUEST_LOCATION = 4;
    public static final int PERMISSIONS_REQUEST_SMS = 5;
    public static final int PERMISSIONS_REQUEST_CONTACTS = 6;

    public static final int VIRAR_CORTO = 100;
    public static final int VIBRAR_LARGO = 1000;

    /**
     * periodo en segundos que triger2 esta escuchando
     */
    public static final long PERIODO_EN_ALERTA = 30 * 1000;


    public static final String W_MAPS = "https://www.google.es/maps/search/";
    public static final String PERMITIR_APPS_POCO_SEGURAS = "https://myaccount.google.com/lesssecureapps";
    public static final String FRASE_ALERTA = "ok google";
    public static final String GOOGLE = "https://www.google.com/search?q=";

    public static final String MSG_GPA_DISABLED = "El GPS esta desactivado, Es necesario para el funcionamiento";

    public static final String NOTIFICACION_PARCIAL = "PARCIAL";
    public static final String NOTIFICACION_PALABRA = "PALABRA";
    public static final String NOTIFICACION_FRASE = "FRASE";
    public static final String NOTIFICACION_GPS_DISABLE = "GPS";

    public static final String ON_TOGGLE = "ON_TOGGLE";
    public static final String ON_WIDGET = "ON_WIDGET";
    public static final int ID_SERVICIO = 10000;

    public static final String ORIGEN_INTENT = "ORIGEN";
    public static final String RECEIVER = "RECEIVER";

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    /**
     * ******************************** Notificación ******************************
     **/
    public static final CharSequence NOTIFICATION_TITLE = "EMAM";
    public static final CharSequence NOTIFICATION_BODY = "Activo";


    /**
     * ******************************** Native  Plugin ******************************
     **/
    public static final String RESULT = "result";
    public static final String ACTION = "action";
    public static final String USUARIO = "usuario";
   

    public static final String HOME_EVENT = "HOME_EVENT";
    public static final String PALABRA_EVENT = "PALABRA_EVENT";
    public static final String FRASE_EVENT = "FRASE_EVENT";
    public static final String PARTIAL_EVENT = "PARTIAL_EVENT";

    public static final String LOCATION = "location";

    /**
     * *********************************** DB *********************************
     **/
    public static final String TABLA = "tabla";
    public static final String ROWS = "rows";
    /**
     * nombre
     */
    public static final String DB_NAME = "DB_EMAM";
    public static final String CLAVE = "clave";
    public static final String REGISTRO = "registro";
    public static final String USER = "usuario";

    /**
     * tablas
     */
    public static final String PALABRAS = "PALABRAS";
    public static final String USUARIOS = "USUARIOS";
    public static final String ALARMAS = "ALARMAS";

    /**
     * funciones
     */
    public static final String TRIGER1 = "TRIGER1";   // app armada
    public static final String TRIGER2 = "TRIGER2";   // app envio de localizacion
    public static final String TRIGER3 = "TRIGER3";   // app buscar y lanxar google

    /**
     * acciones
     */
    public static final String INSERT = "INSERT";
    public static final String SELECT = "SELECT";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";


    /**
     * mensajes de error
     */
    public static final String MSG_ERROR_GPS = "Debes activar el GPS para poder enviar mensajes de alerta";


    /**
     * ********************************   SERVIZE *********************************
     **/
    public static final String ON = "ON";
    public static final String OFF = "OFF";
    public static final String BIND = "BIND";
    public static final String UNBIND = "UNBIND";
    public static final String IS_RUNNING = "IS_RUNNING";
    public static final String MSG_ERROR_SERVIZE = "No se ha detenido correctamente el servicio";

    /**
     * ********************************   TTS  *********************************
     **/

    public static final String ESTOY_ESPERANDO = "Estoy esperando";
    public static final String DIME = "¿dime maestro? ";

    /**
     * ********************************   WIDGET  *********************************
     **/
    public static final int WIDGET_CLICKS = 5;
    public static final String ONE_CLICK_TO_SEND = "Estas a un toque de enviar la ubicación";

    /**
     * ********************************   CONTACTS  *********************************
     **/
    public static final int PICK_CONTACT = 10;
    public static final String NUMERO = "numero";
    public static final String MAIL = "email";
    public static final String CONTACTO = "contacto";


    public static final String LATITUD = "latitud";
    public static final String LONGITUD = "longitud";
    public static final String TRANSPORT_MODE = "transportMode";
}
