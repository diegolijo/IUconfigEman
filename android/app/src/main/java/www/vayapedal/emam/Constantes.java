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
     *   periodo en segundos que triger2 esta escuchando
     */
    public static final long PERIODO_EN_ALERTA = 30;



    public static final int MENSAJE = 1;


    public static final int PUNSACIONES_EMERGENCIA = 4;
    public static final String TRIGER1 = "triger_de_primer_nivel";
    public static final String TRIGER2 = "triger_de_segundo_nivel";

    public static final String PATH = "EMAN";

    public static final String FILE_TXT = "palabras.txt";
    public static final long TIEMPO_GPS = 1000*30;
    public static final float DISTANCIA_GPS = 50;

    public static final String W_MAPS = "https://www.google.es/maps/search/";
    public static final String PERMITIR_APPS_POCO_SEGURAS = "https://myaccount.google.com/lesssecureapps";
    public static final String FRASE_ALERTA = "ok google";

    public static final String MSG_GPA_DISABLED = "El GPS esta desactivado, Es necesario para el funcionamiento";


    public static final String NOTIFICACION_PARCIAL = "PARCIAL";
    public static final String NOTIFICACION_PALABRA = "PALABRA";
    public static final String NOTIFICACION_FRASE = "FRASE" ;
    public static final String NOTIFICACION_SERVICIO = "NOTIFICACION_GENERAL";
    public static final String NOTIFICACION_GPS_DISABLE = "GPS";
    public static final String ON_TOGGLE = "ON_TOGGLE";
    public static final String ON_WIDGET = "ON_WIDGET";
    public static final String ON_CONFIG = "ON_CONFIG";
    public static final String OFF_SERVICIO =  "SERVICIO_FINALIZADO";
    public static final String ON_BOTONES = "ON_BOTONES";
    public static final int ID_SERVICIO = 10000;

    public static final String PACKAGE_NAME = "com.vayapedal.EMAN";
    public static final String ORIGEN_INTENT = "ORIGEN";
    public static final String RECEIVER = "RECEIVER";
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;


}
