import { Injectable } from '@angular/core';

@Injectable()
export class Constants {




    constructor() {
    }

    /*********************************************** APP ***********************************************/
    public static APP_VERSION = '2.0.1';

    /********************************************** login **********************************************/
    public static PAGES = {
        LOGIN: 'LOGIN',
        REGISTER: 'REGISTER'
    };

    /*********************************************** BD ************************************************/
    // tablas
    public static PALABRAS = 'PALABRAS';
    public static USUARIOS = 'USUARIOS';
    public static ALARMAS = 'ALARMAS';
    // acciones
    public static INSERT = 'INSERT';
    public static SELECT = 'SELECT';
    public static UPDATE = 'UPDATE';
    public static DELETE = 'DELETE';

    /******************************************** servicio *********************************************/
    public static ON = 'ON';
    public static OFF = 'OFF';
    public static BIND = 'BIND';
    public static UNBIND = 'UNBIND';
    static PLUGIN_EVENT = 'PLUGIN_EVENT';

}

