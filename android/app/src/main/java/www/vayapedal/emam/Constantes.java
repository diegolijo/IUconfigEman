package www.vayapedal.emam;

public class Constantes {


    /**
     * permisos
     */
    public static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    public static final int PERMISSIONS_REQUEST_CALL_PHONE = 2;
    public static final int PERMISSIONS_REQUEST_GPS = 3;
    public static final int PERMISSIONS_REQUEST_LOCATION = 4;
    public static final int PERMISSIONS_REQUEST_SMS = 5;

    public static final int VIRAR_CORTO = 100;
    public static final int VIBRAR_MEDIO = 500;
    public static final int VIBRAR_LARGO = 1000;

    /**
     * periodo en segundos que triger2 esta escuchando
     */
    public static final long PERIODO_EN_ALERTA = 30;


    public static final String PATH = "EMAN";

    public static final String W_MAPS = "https://www.google.es/maps/search/";
    public static final String PERMITIR_APPS_POCO_SEGURAS = "https://myaccount.google.com/lesssecureapps";
    public static final String FRASE_ALERTA = "ok google";

    public static final String MSG_GPA_DISABLED = "El GPS esta desactivado, Es necesario para el funcionamiento";

    public static final String NOTIFICACION_PARCIAL = "PARCIAL";
    public static final String NOTIFICACION_PALABRA = "PALABRA";
    public static final String NOTIFICACION_FRASE = "FRASE";
    public static final String NOTIFICACION_SERVICIO = "NOTIFICACION_GENERAL";
    public static final String NOTIFICACION_GPS_DISABLE = "GPS";
    public static final String ON_TOGGLE = "ON_TOGGLE";
    public static final String ON_WIDGET = "ON_WIDGET";
    public static final String ON_CONFIG = "ON_CONFIG";
    public static final String OFF_SERVICIO = "SERVICIO_FINALIZADO";
    public static final String ON_BOTONES = "ON_BOTONES";
    public static final int ID_SERVICIO = 10000;
    public static final int ID_SERVICIO_BIND = 10001;
    public static final String PACKAGE_NAME = "com.vayapedal.EMAN";
    public static final String ORIGEN_INTENT = "ORIGEN";
    public static final String RECEIVER = "RECEIVER";
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;


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
    public static final String TRIGER1 = "TRIGER1";
    public static final String TRIGER2 = "TRIGER2";

    /**
     * acciones
     */
    public static final String INSERT = "INSERT";
    public static final String SELECT = "SELECT";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";

    public static final String RESULT = "result";

    /**
     * mensajes de error
     */


    /**
     * ********************************  NAT PLUGIN SERVIZE *********************************
     **/
    public static final String PLUGIN_EVENT = "PLUGIN_EVENT";
    public static final String HOME_EVENT = "HOME_EVENT";
    public static final String PALABRA_EVENT = "PALABRA_EVENT";
    public static final String PARTIAL_EVENT = "PARTIAL_EVENT";
    public static final String ACTION = "action";

    public static final String ON = "ON";
    public static final String OFF = "OFF";
    public static final String BIND = "BIND";
    public static final String UNBIND = "UNBIND";


}
