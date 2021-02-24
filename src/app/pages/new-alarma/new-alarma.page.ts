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



  public isBindService = false;

  constructor(
    private translate: TranslateService,
    private platform: Platform,
    private nativePlugin: NativePlugin,
    public helper: Helper,
    public proAppUser: AppUser,
    private contacts: Contacts,
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
    const contacts = await this.getContacts();
    contacts.forEach(element => {
      try {
        console.log(element.displayName + ': ' + element.phoneNumbers[0].value);
      } catch (err) {
        try {
          console.log(element.displayName + ' -');
        } catch (err) {
          console.log('*****************************');
        }
      }
      const res = element.displayName;
    });
    const resp = contacts[0].phoneNumbers[0].value;
    console.log(resp);
  }

  /*********************************************** contactos ***************************************************/

  /*   Contact object
  rawId: "712"
  _objectInstance: Contact
  addresses: null
  birthday: null
  categories: null
  displayName: "susu"
  emails: null
  id: "699"
  ims: null
  name:
  formatted: "susu "
  givenName: "susu"
  __proto__: Object
  nickname: null
  note: null
  organizations: null
  phoneNumbers: Array(1)
  0: {id: "3615", pref: false, value: "‪+34 662 01 66 60‬", type: "mobile"}
  length: 1
  __proto__: Array(0)
  photos: null
  rawId: null
  urls: null */


  private getContacts() {
    return new Promise<any>((resolve, reject) => {
      const cont = this.contacts.find(['phoneNumbers']);
      resolve(cont);
    });
  }

  /************************************************ servize ************************************************/
  /*   public async bindServize() {
      this.isBindService = true;
      const result = await this.nativePlugin.bindService();
    }
    public async unBindServize() {
      this.isBindService = false;
      const result = await this.nativePlugin.unBindServize();
    } */

  /********************************************** listener ********************************************/
  /*
    public startPartialListener() {
      this.pluginPartialListener = Plugins.NatPlugin.addListener(Constants.PARTIAL_EVENT, (info: any) => {
        this.resultFromNative(info);
      });
    }
    public removePartialListener() {
      this.pluginPartialListener.remove();
    }
   */
  // resultados de la capa nativa -> result: "agua"

  /* public resultFromNative(result) {
    this.newAlarma.clave = result.result;
  } */

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
