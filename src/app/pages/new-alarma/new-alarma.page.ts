import { Component, OnDestroy, OnInit } from '@angular/core';
import { Platform } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { AppUser } from 'src/app/services/AppUser';
import { Constants } from 'src/app/services/Constants';
import { Helper } from 'src/app/services/Helper';
import { ModelCreator } from 'src/app/services/model_ceator';
import { NativePlugin } from 'src/app/services/NativePlugin';
import { IAlarma, IPalabra, IUsuario } from './../../interfaces/i-db-models';

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
    { id: Constants.TRIGER1 },
    { id: Constants.TRIGER2 }
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

  async ngOnInit() {
    try {
      if (this.platform.is('cordova')) {
        this.newAlarma = this.modelCreator.emptyIAlarma();
        this.appUser = this.proAppUser.getAppUser();

        const result = await this.selectAlarmas();
        for (const alarma of result.rows) {
          this.alarmas.push(this.modelCreator.getIAlarma(alarma));
        }
        await this.onClickRefresh();
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
    if (this.platform.is('cordova')) {
      this.alarmas = [];
      const result = await this.selectAlarmas();
      for (const alarma of result.rows) {
        this.alarmas.push(this.modelCreator.getIAlarma(alarma));
      }
    } else {
      const alarma: IAlarma = {
        usuario: 'user',
        clave: 'clave',
        numTlfTo: '662023955',
        enable: true,
        mailTo: 'deigonalgas@hotmail.com'
      };
      for (let i = 0; i < 7; i++) {
        this.alarmas.push(this.modelCreator.getIAlarma(alarma));
      }
    }
  }

  public async onClickAddAlarma() {
    if (this.platform.is('cordova')) {
      if (this.newAlarma.clave !== '') {
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
    const result = await this.nativePlugin.deleteDB(Constants.ALARMAS, alarma.clave, this.appUser.usuario);
    return result;
  }


  public async selectAlarmas() {
    const result = await this.nativePlugin.selectDB(Constants.ALARMAS, '', this.appUser.usuario);
    return result;
  }
}
