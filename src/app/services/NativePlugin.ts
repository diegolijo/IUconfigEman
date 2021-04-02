import { AppUser } from './AppUser';
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
        private appUser: AppUser
    ) {
    }

    /******************************************** servicio *********************************************/
    public async startService() {
        if (this.platform.is('cordova')) {
            const user = this.appUser.getAppUser();
            const result = await NatPlugin.servizeOperations({ action: Constants.ON, usuario: user.usuario });
            return result;
        }
        if (!this.platform.is('cordova')) {

        }
    }

    public async stopService() {
        if (this.platform.is('cordova')) {
            const result = await NatPlugin.servizeOperations({ action: Constants.OFF });
            return result;
        }
        if (!this.platform.is('cordova')) {

        }

    }

    public async bindService() {
        if (this.platform.is('cordova')) {
            const user = this.appUser.getAppUser();
            const result = await NatPlugin.servizeOperations({ action: Constants.BIND, usuario: user.usuario });
            return result;
        }
        if (!this.platform.is('cordova')) {

        }

    }

    public async unBindServize() {
        if (this.platform.is('cordova')) {
            const result = await NatPlugin.servizeOperations({ action: Constants.UNBIND });
            return result;
        }
        if (!this.platform.is('cordova')) {

        }
    }

    public async isServizeRunning() {
        if (this.platform.is('cordova')) {
            const result = await NatPlugin.servizeOperations({ action: Constants.IS_RUNNING });
            return result;
        }
        if (!this.platform.is('cordova')) {

        }
    }
    /********************************************** listener ********************************************/

    public async startListener() {
        if (this.platform.is('cordova')) {
            this.pluginListener = Plugins.NatPlugin.addListener(Constants.PLUGIN_EVENT, (info: any) => {
                this.resultFromNative(info);
            });
        }
        if (!this.platform.is('cordova')) {

        }

    }

    public async removeListener() {
        if (this.platform.is('cordova')) {
            this.pluginListener.remove();
        }
        if (!this.platform.is('cordova')) {

        }

    }

    // resultados de la capa nativa
    public resultFromNative(result) {
        if (this.platform.is('cordova')) {
            result = result;
        }
        if (!this.platform.is('cordova')) {

        }

    }


    /*********************************************** BD ************************************************/
    public async insertDB(table: string, row: any) {
        if (this.platform.is('cordova')) {
            const result = await NatPlugin.insertDB({ tabla: table, registro: { row } });
            return result;
        }
        if (!this.platform.is('cordova')) {

        }

    }


    public async selectDB(table: string, key: any, user: string) {
        if (this.platform.is('cordova')) {
            const result = await NatPlugin.selectDB({ tabla: table, usuario: user, clave: key });
            return result;
        }
        if (!this.platform.is('cordova')) {

        }
    }

    public async deleteDB(table: string, key: string, user: string) {
        if (this.platform.is('cordova')) {
            const result = await NatPlugin.deleteDB({ tabla: table, usuario: user, clave: key });
            return result;
        }
        if (!this.platform.is('cordova')) {
        }

    }

    // palabras
    public async selectFuncion(func: any) {
        if (this.platform.is('cordova')) {
            const result = await NatPlugin.selectFuncion({ funcion: func });
            return result;
        }
        if (!this.platform.is('cordova')) {

        }

    }

    // ******************************************** contactos *********************************************/
    public async getContacts() {
        if (this.platform.is('cordova')) {
            const result = await NatPlugin.getContacts();
            return result;
        }
        if (!this.platform.is('cordova')) {

        }
    }


    // ******************************************** contactos *********************************************/
    public async getLocation() {
        if (this.platform.is('cordova')) {
            const result = await NatPlugin.getLocation();
            return result;
        }
        if (!this.platform.is('cordova')) {

        }
    }


}

