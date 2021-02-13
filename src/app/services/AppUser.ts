import { Injectable } from '@angular/core';
import { IUsuario } from '../interfaces/i-db-models';
import { ModelCreator } from './model_ceator';

@Injectable()
export class AppUser {
    subscription: any;

    public appUser: IUsuario;



    constructor(
        private modelCreator: ModelCreator
    ) {

    }

    public emptyAppUser() {
        this.appUser = this.modelCreator.emptyIUsuario();
    }

    public setAppUser(user: IUsuario) {
        this.appUser = user;
    }

    public getAppUser() {
        return this.appUser;
    }
}
