export interface IPalabra {
    clave: string;          // palabra
    funcion: string;        // funcion que se ejecuta al ser reconocida
    fecha: string;          // fecha de ultimo reconocimiento
    descripcion: string;    // clave de la descripcion
    usuario: string;
}

export interface IUsuario {
    usuario: string;
    loginPass: string;
    mailFrom: string;
    mailPass: string;
}

export interface IAlarma {
    usuario: string;
    funcion: string;
    numTlfTo: string;
    enable: boolean;
    mailTo: string;
}
