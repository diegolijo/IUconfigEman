import { Injectable } from '@angular/core';

@Injectable()
export class Constants {

    constructor() {
    }

    /*********************************************** APP ***********************************************/
    public static APP_VERSION = '2.0.1';
    public static CARACTERES_POR_LINEA = 45;

    /********************************************** login **********************************************/
    public static PAGES = {
        LOGIN: 'LOGIN',
        REGISTER: 'REGISTER'
    };

    /*********************************************** BD ************************************************/
    // tablas
    public static PALABRAS = 'PALABRAS';
    // palabras[funciones]
            public static TRIGER1 = 'TRIGER1';
            public static TRIGER2 = 'TRIGER2';

    public static USUARIOS = 'USUARIOS';
    public static ALARMAS = 'ALARMAS';

    // acciones
    public static INSERT = 'INSERT';
    public static SELECT = 'SELECT';
    public static UPDATE = 'UPDATE';
    public static DELETE = 'DELETE';
    // clave

    /******************************************** servicio *********************************************/
    public static ON = 'ON';
    public static OFF = 'OFF';
    public static BIND = 'BIND';
    public static UNBIND = 'UNBIND';

    /******************************************** capacitor *********************************************/
    public static PLUGIN_EVENT = 'PLUGIN_EVENT';
    public static HOME_EVENT = 'HOME_EVENT';
    public static PALABRA_EVENT = 'PALABRA_EVENT';
    public static PARTIAL_EVENT = 'PARTIAL_EVENT';
}

