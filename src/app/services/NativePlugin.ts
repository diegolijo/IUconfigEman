import { Injectable } from '@angular/core';
import { Plugins } from '@capacitor/core';
import { Platform } from '@ionic/angular';
import { Constants } from './Constants';
const { NatPlugin } = Plugins;

@Injectable()
export class NativePlugin {


    public pluginListener: any;


    constructor(
        private platform: Platform,
    ) {
    }

    /******************************************** servicio *********************************************/
    public async startService() {
        if (this.platform.is('cordova')) {
            const result = await NatPlugin.servizeOperations({ action: Constants.ON });
            return result;
        } else { }

    }

    public async stopService() {
        if (this.platform.is('cordova')) {
            const result = await NatPlugin.servizeOperations({ action: Constants.OFF });
            return result;
        } else { }

    }

    public async bindService() {
        if (this.platform.is('cordova')) {
            const result = await NatPlugin.servizeOperations({ action: Constants.BIND });
            return result;
        } else { }

    }

    public async unBindServize() {
        if (this.platform.is('cordova')) {
            const result = await NatPlugin.servizeOperations({ action: Constants.UNBIND });
            return result;
        } else { }


    }

    /********************************************** listener ********************************************/

    public async startListener() {
        if (this.platform.is('cordova')) {
            this.pluginListener = Plugins.NatPlugin.addListener(Constants.PLUGIN_EVENT, (info: any) => {
                this.resultFromNative(info);
            });
        } else { }

    }

    public async removeListener() {
        if (this.platform.is('cordova')) { this.pluginListener.remove(); } else { }

    }

    // resultados de la capa nativa
    public async resultFromNative(result) {
        if (this.platform.is('cordova')) {
            result = result;
            // {action: 'RESPUESTA'}; } else {}

        } else { }

    }


    /*********************************************** BD ************************************************/
    public async insertDB(table: string, row: any) {
        if (this.platform.is('cordova')) {
            const result = await NatPlugin.insertDB({ tabla: table, registro: { row } });
            return result;
        } else { }

    }


    public async selectDB(table: string, key: any, user: string) {
        if (this.platform.is('cordova')) {
            const result = await NatPlugin.selectDB({ tabla: table, usuario: user, clave: key });
            return result;
        } else { }

    }

    public async deleteDB(table: string, key: string, user: string) {
        if (this.platform.is('cordova')) {
            const result = await NatPlugin.deleteDB({ tabla: table, usuario: user, clave: key });
            return result;
        } else { }

    }

    // palabras
    public async selectFuncion(func: any) {
        if (this.platform.is('cordova')) {
            const result = await NatPlugin.selectFuncion({ funcion: func });
            return result;
        } else { }

    }





}

