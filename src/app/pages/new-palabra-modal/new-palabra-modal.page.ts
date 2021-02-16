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
    { id: Constants.TRIGER2 }
  ];

  public isBindService = false;

  constructor(
    private translate: TranslateService,
    private platform: Platform,
    private modalCtrl: ModalController,
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
        const result = await this.selectPalabras();
        for (const palabra of result.rows) {
          this.palabras.push(this.modelCreator.getIPalabra(palabra));
        }
        await this.onClickRefresh();
      } else {
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
    } else {

    }
  }


  public async onClickRecButton() {
    if (this.platform.is('cordova')) {
      if (!this.isBindService) {
        this.bindServize();
      } else {
        this.unBindServize();
      }
    } else {
      if (!this.isBindService) {
        this.isBindService = true;
      } else {
        this.isBindService = false;
      }
    }
  }



  public async onClickRefresh() {
    if (this.platform.is('cordova')) {
      this.palabras = [];
      const result = await this.selectPalabras();
      for (const palabra of result.rows) {
        this.palabras.push(this.modelCreator.getIPalabra(palabra));
      }
    } else {
      const palabra: IPalabra = { clave: 'caca', funcion: 'triger_de_primer_nivel', fecha: '', descripcion: '', usuario: '' };
      for (let i = 0; i < 7; i++) {
        this.palabras.push(this.modelCreator.getIPalabra(palabra));
      }
    }
  }

  public async onClickAddPalabra() {
    if (this.platform.is('cordova')) {
      if (this.newPalabra.clave !== '') {
        this.newPalabra.usuario = this.appUser.usuario;
        const result = await this.insertPalabra(this.newPalabra);
        if (result.result) {
          await this.onClickRefresh();
        } else {
          this.helper.showMessage(await this.translate.get('NEW_PALABRA_MODAL.NO_INSERT').toPromise());
        }

      }
    } else {

    }
  }

  public async onClickDeletePalabra(palabra: IPalabra) {
    if (this.platform.is('cordova')) {
      const result = await this.deletePalabra(palabra);
      await this.onClickRefresh();
    } else {

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
}
