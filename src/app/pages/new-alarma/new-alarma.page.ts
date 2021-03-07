import { Component, OnDestroy, OnInit } from '@angular/core';
import { Contacts, Contact, ContactField, ContactName } from '@ionic-native/contacts/ngx';
import { Platform } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { AppUser } from 'src/app/services/AppUser';
import { Constants } from 'src/app/services/Constants';
import { Helper } from 'src/app/services/Helper';
import { ModelCreator } from 'src/app/services/model_ceator';
import { NativePlugin } from 'src/app/services/NativePlugin';
import { IAlarma, IUsuario } from './../../interfaces/i-db-models';
import { Plugins, KeyboardInfo } from '@capacitor/core';
const { Keyboard } = Plugins;


@Component({
  selector: 'app-new-alarma',
  templateUrl: './new-alarma.page.html',
  styleUrls: ['./new-alarma.page.scss'],
})
export class NewAlarmaPage implements OnInit, OnDestroy {

  public pluginPartialListener: any;
  public appUser: IUsuario;
  public alarmas: IAlarma[] = [];
  public newAlarma: IAlarma;


  public funciones = [
    { id: Constants.TRIGER2 },
    { id: Constants.TRIGER3 }
  ];

  public isBindService = false;

  constructor(
    private translate: TranslateService,
    private platform: Platform,
    private nativePlugin: NativePlugin,
    public helper: Helper,
    public proAppUser: AppUser,
    public modelCreator: ModelCreator
  ) {
  }

  /************************************************ eventos ************************************************/
  async ngOnInit() {
    try {
      if (this.platform.is('cordova')) {
        this.newAlarma = this.modelCreator.emptyIAlarma();
        this.newAlarma.funcion = Constants.TRIGER2;
        this.appUser = this.proAppUser.getAppUser();
        const result = await this.selectAlarmas();
        for (const alarma of result.rows) {
          this.alarmas.push(this.modelCreator.getIAlarma(alarma));
        }
        await this.refreshViewFromDb();
      } else {
        this.appUser = this.proAppUser.getAppUser();
        this.newAlarma = this.modelCreator.emptyIAlarma();
      }

    } catch (err) {
      this.helper.showException('ngOnInit :' + err);
    }
  }

  async ngOnDestroy() {
    if (this.platform.is('cordova')) {

    } else {

    }
  }

  public async onClickRefresh() {
    await this.refreshViewFromDb();
  }

  private async refreshViewFromDb() {
    if (this.platform.is('cordova')) {
      const result = await this.selectAlarmas();
      this.alarmas = [];
      for (const alarma of result.rows) {
        this.alarmas.push(this.modelCreator.getIAlarma(alarma));
      }
    } else {
      const alarma: IAlarma = {
        usuario: 'user',
        funcion: 'clave',
        numTlfTo: '662023955',
        enable: true,
        mailTo: 'diegonalgas@hotmail.com'
      };
      for (let i = 0; i < 7; i++) {
        this.alarmas.push(this.modelCreator.getIAlarma(alarma));
      }
    }
  }

  public async onClickAddAlarma() {
    if (this.platform.is('cordova')) {
      if (this.newAlarma.funcion !== '') {
        this.newAlarma.usuario = this.appUser.usuario;
        const result = await this.insertAlarma(this.newAlarma);
        if (result.result) {
          await this.onClickRefresh();
        } else {
          this.helper.showMessage(await this.translate.get('NEW_ALARMA.NO_INSERT').toPromise());
        }

      }
    } else {

    }
  }

  public async onClickDeleteAlarma(alarma: IAlarma) {
    if (this.platform.is('cordova')) {
      const result = await this.deleteAlarma(alarma);
      await this.onClickRefresh();
    } else {

    }
  }

  public async onClickContactos() {

  }



  private getContacts() {

  }

  /************************************************ servize ************************************************/

  /********************************************** listener ********************************************/


  /***********************************************  DB  ****************************************************/

  public async insertAlarma(newAlarma: IAlarma) {
    const result = await this.nativePlugin.insertDB(Constants.ALARMAS, newAlarma);
    return result;
  }

  public async deleteAlarma(alarma: IAlarma) {
    const result = await this.nativePlugin.deleteDB(Constants.ALARMAS, alarma.numTlfTo, this.appUser.usuario);
    return result;
  }


  public async selectAlarmas() {
    const result = await this.nativePlugin.selectDB(Constants.ALARMAS, '', this.appUser.usuario);
    return result;
  }
}
