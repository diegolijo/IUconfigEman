import { Injectable } from '@angular/core';
import { Plugins } from '@capacitor/core';
import { Constants } from './Constants';
const { NatPlugin } = Plugins;

@Injectable()
export class NativePlugin {

    public pluginListener: any;


    constructor(
    ) {
    }

    /******************************************** servicio *********************************************/
    public async startService() {
        const result = await NatPlugin.servizeOperations({ action: Constants.ON });
        return result;
    }

    public async stopService() {
        const result = await NatPlugin.servizeOperations({ action: Constants.OFF });
        return result;
    }

    public async bindService() {
        const result = await NatPlugin.servizeOperations({ action: Constants.BIND });
        return result;
    }

    public async unBindServize() {
        const result = await NatPlugin.servizeOperations({ action: Constants.UNBIND });
        return result;
    }

    /********************************************** listener ********************************************/

    public async startListener() {
        this.pluginListener = Plugins.NatPlugin.addListener(Constants.PLUGIN_EVENT, (info: any) => {
            this.resultFromNative(info);
        });
    }

    public async removeListener() {
        this.pluginListener.remove();
    }

    // resultados de la capa nativa
    public async resultFromNative(result) {
        result = result;
        // {action: 'RESPUESTA'};
    }


    /*********************************************** BD ************************************************/
    public async insertDB(table: string, row: any) {
        const result = await NatPlugin.insertDB({ tabla: table, registro: { row } });
        return result;
    }


    public async selectDB(table: string, key: any) {
        const result = await NatPlugin.selectDB({ tabla: table, clave: key });
        return result;
    }


    // palabras
    public async selectFuncion(func: any) {
        const result = await NatPlugin.selectFuncion({ funcion: func });
        return result;
    }





}

