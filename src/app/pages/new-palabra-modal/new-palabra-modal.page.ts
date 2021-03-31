import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { Plugins } from '@capacitor/core';
import { ModalController, Platform } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { IPalabra, IUsuario } from '../../interfaces/i-db-models';
import { AppUser } from '../../services/AppUser';
import { Constants } from '../../services/Constants';
import { Helper } from '../../services/Helper';
import { ModelCreator } from '../../services/model_ceator';
import { NativePlugin } from '../../services/NativePlugin';
const { NatPlugin } = Plugins;

@Component({
  selector: 'app-new-palabra-modal',
  templateUrl: './new-palabra-modal.page.html',
  styleUrls: ['./new-palabra-modal.page.scss'],
})
export class NewPalabraModalPage implements OnInit, OnDestroy {


  public pluginPartialListener: any;
  public appUser: IUsuario;
  public palabras: IPalabra[] = [];
  public newPalabra: IPalabra;


  public funciones = [
    { id: Constants.TRIGER1 },
    { id: Constants.TRIGER2 },
    { id: Constants.TRIGER3 }
  ];

  public isBindService = false;

  constructor(
    private translate: TranslateService,
    private platform: Platform,
    private nativePlugin: NativePlugin,
    private changeDetectorRef: ChangeDetectorRef,
    public helper: Helper,
    public proAppUser: AppUser,
    public modelCreator: ModelCreator
  ) {
  }

  async ngOnInit() {
    try {
      if (this.platform.is('cordova')) {
        this.newPalabra = this.modelCreator.emptyIPalabra();
        this.appUser = this.proAppUser.getAppUser();
        this.startPartialListener();
        await this.refreshPalabras();
      }
      if (!this.platform.is('cordova')) {
        this.appUser = this.proAppUser.getAppUser();
        this.newPalabra = this.modelCreator.emptyIPalabra();
      }
    } catch (err) {
      this.helper.showException('ngOnInit :' + err);
    }
  }


  async ngOnDestroy() {
    if (this.platform.is('cordova')) {
      this.removePartialListener();
      this.unBindServize();
    }
    if (!this.platform.is('cordova')) {

    }
  }


  public async onClickRecButton() {
    if (this.platform.is('cordova')) {
      if (!this.isBindService) {
        this.bindServize();
      } else {
        this.unBindServize();
      }
    }
    if (!this.platform.is('cordova')) {
      if (!this.isBindService) {
        this.isBindService = true;
      } else {
        this.isBindService = false;
      }
    }
  }



  public async onClickRefresh() {
    await this.refreshPalabras();
  }



  public async onClickAddPalabra() {
    if (this.platform.is('cordova')) {
      if (this.newPalabra.clave !== '') {
        this.newPalabra.usuario = this.appUser.usuario;
        const result = await this.insertPalabra(this.newPalabra);
        if (result.result) {
          await this.refreshPalabras();
        } else {
          this.helper.showMessage(await this.translate.get('NEW_PALABRA_MODAL.NO_INSERT').toPromise());
        }

      }
    }
    if (!this.platform.is('cordova')) {

    }
  }

  public async onClickDeletePalabra(palabra: IPalabra) {
    if (this.platform.is('cordova')) {
      const result = await this.deletePalabra(palabra);
      await this.refreshPalabras();
    }
    if (!this.platform.is('cordova')) {

    }
  }


  private async refreshPalabras() {
    if (this.platform.is('cordova')) {
      const result = await this.selectPalabrasFuncion(this.newPalabra.funcion);
      this.palabras = [];
      for (const palabra of result.rows) {
        this.palabras.push(this.modelCreator.getIPalabra(palabra));
      }
    }
    if (!this.platform.is('cordova')) {

    }
  }


  /************************************************ servize ************************************************/
  public async bindServize() {
    this.isBindService = true;
    const result = await this.nativePlugin.bindService();
  }

  public async unBindServize() {
    this.isBindService = false;
    const result = await this.nativePlugin.unBindServize();
  }

  /********************************************** listener ********************************************/

  public startPartialListener() {
    this.pluginPartialListener = Plugins.NatPlugin.addListener(Constants.PARTIAL_EVENT, (info: any) => {
      this.resultFromNative(info);
    });

  }

  public removePartialListener() {
    this.pluginPartialListener.remove();
  }

  // resultados de la capa nativa -> result: "agua"
  public resultFromNative(result) {
    this.newPalabra.clave = result.result;
    this.changeDetectorRef.detectChanges();
  }

  /***********************************************  DB  ****************************************************/

  public async insertPalabra(newPalabra: IPalabra) {
    const result = await this.nativePlugin.insertDB(Constants.PALABRAS, newPalabra);
    return result;
  }

  public async deletePalabra(palabra: IPalabra) {
    const result = await this.nativePlugin.deleteDB(Constants.PALABRAS, palabra.clave, this.appUser.usuario);
    return result;
  }

  public async selectPalabras() {
    const result = await this.nativePlugin.selectDB(Constants.PALABRAS, '', this.appUser.usuario);
    return result;
  }

  public async selectPalabrasFuncion(funcion) {
    const result = await this.nativePlugin.selectDB(Constants.PALABRAS, funcion, this.appUser.usuario);
    return result;
  }


}
