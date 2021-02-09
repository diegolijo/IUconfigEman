import { NativePlugin } from './../../../services/NativePlugin';
import { Constants } from './../../../services/Constants';
import { IPalabra } from './../../../interfaces/i-db-models';
import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { Plugins } from '@capacitor/core';
import { ModalController, Platform } from '@ionic/angular';
const { NatPlugin } = Plugins;

@Component({
  selector: 'app-new-palabra-modal',
  templateUrl: './new-palabra-modal.page.html',
  styleUrls: ['./new-palabra-modal.page.scss'],
})
export class NewPalabraModalPage implements OnInit, OnDestroy {




  public resultText = '';
  public pluginListener: any;

  public newPalabra: IPalabra =
    {
      clave: '',
      funcion: '',
      fecha: ''
    };

  public funciones = [
    { id: Constants.TRIGER1 },
    { id: Constants.TRIGER2 }
  ];

  constructor(
    private platform: Platform,
    private modalCtrl: ModalController,
    private nativePlugin: NativePlugin,
    private changeDetectorRef: ChangeDetectorRef
  ) { }

  ngOnInit() {
    if (this.platform.is('cordova')) {
      this.startListener();
    } else {

    }
  }


  ngOnDestroy() {
    this.unBindServize();
    this.removeListener();
  }




  public onClickRecButton() {
    if (this.platform.is('cordova')) {
      this.bindServize();
    } else {
      this.resultText += ' ' + this.resultText;
    }
  }


  public async onClickClose(msg) {
    try {
      if (msg) {
        await this.close(msg);
      }
    } catch (error) {
      await this.close(error.message);
    }
  }





  /************************************************* page ************************************************** */
  public async close(msg: any) {
    if (msg === 'success') {
      await this.insertPalabra(this.newPalabra);
    }
    await this.modalCtrl.dismiss({
      result: msg
    });
  }






  /************************************************ servize ************************************************/
  public async bindServize() {
    const result = await this.nativePlugin.bindService();
  }

  public async unBindServize() {
    const result = await this.nativePlugin.unBindServize();
  }




  /********************************************** listener ********************************************/

  public startListener() {
    this.pluginListener = Plugins.NatPlugin.addListener(Constants.PALABRA_EVENT, (info: any) => {
      this.resultFromNative(info);
    });
  }

  public removeListener() {
    this.pluginListener.remove();
  }

  // resultados de la capa nativa -> result: "agua"
  public resultFromNative(result) {
    this.newPalabra.clave = result.result;
    this.changeDetectorRef.detectChanges();
  }

  /***********************************************  DB  ****************************************************/

  public async insertPalabra(newPalabra: IPalabra) {
    /*     const palabra = {
          usuario: this.username,
          loginPass: this.password,
          mailFrom: this.email,
          mailPass: this.emailPass
        }; */
    const result = await this.nativePlugin.insertDB(Constants.PALABRAS, newPalabra);
    if (result.result) {

      const resul = await this.nativePlugin.selectFuncion(Constants.TRIGER1);
      const resu = await this.nativePlugin.selectFuncion(Constants.TRIGER2);
      debugger;
    }
  }


}
