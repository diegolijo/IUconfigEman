import { Injectable } from '@angular/core';
import { IAlarma, IPalabra, IUsuario } from '../interfaces/i-db-models';

@Injectable()
export class ModelCreator {


    constructor(
    ) { }


    public getIPalabra(palabra: any): IPalabra {
        return {
            clave: palabra.clave,
            funcion: palabra.funcion,
            fecha: palabra.fecha,
            descripcion: palabra.descripcion,
            usuario: palabra.usuario
        };
    }

    public emptyIPalabra(): IPalabra {
        return {
            clave: '',
            funcion: '',
            fecha: '',
            descripcion: '',
            usuario: ''
        };
    }

    public getIUsuario(usuario: any): IUsuario {
        return {
            usuario: usuario.usuario,
            loginPass: usuario.loginPass,
            mailFrom: usuario.mailFrom,
            mailPass: usuario.mailPass
        };
    }

    public emptyIUsuario(): IUsuario {
        return {
            usuario: '',
            loginPass: '',
            mailFrom: '',
            mailPass: ''
        };
    }

    public getIAlarma(alarma: any): IAlarma {
        return {
            usuario: alarma.usuario,
            funcion: alarma.funcion,
            numTlfTo: alarma.numTlfTo,
            mailTo: alarma.mailTo,
            enable: alarma.enable
        };
    }


    emptyIAlarma(): IAlarma {
        return {
            usuario: '',
            funcion: '',
            numTlfTo: '',
            enable: false,
            mailTo: ''
        };
    }

}
