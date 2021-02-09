import { Component, OnDestroy, OnInit, ViewChildren, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { Helper } from './../../services/Helper';
import { NativePlugin } from './../../services/NativePlugin';
import { Plugins } from '@capacitor/core';
import { Constants } from 'src/app/services/Constants';
import { ModalController, Platform } from '@ionic/angular';
import { NewPalabraModalPage } from '../modals/new-palabra-modal/new-palabra-modal.page';
const { NatPlugin } = Plugins;

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss'],
})
export class HomePage implements OnInit, OnDestroy {

  // @ViewChildren('textArea') textArea;

  public resultText = '';
  public pluginListener: any;
  private user: any;

  constructor(
    private platform: Platform,
    private nativePlugin: NativePlugin,
    public helper: Helper,
    private changeDetectorRef: ChangeDetectorRef,
    private modalCtrl: ModalController

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


  public async onClickNewPalabraModal() {
    try {
      const newPalabraModal = await this.modalCtrl.create({
        component: NewPalabraModalPage,
        componentProps: {
          user: this.user
        }
      });
      await newPalabraModal.present();
      newPalabraModal.onDidDismiss().then(res => {
        if (res.data.result === 'success') {

        } else if (res.data.result === 'cancelled') {

        } else {
          this.helper.showMessage(res);
        }
      });
    } catch (error) {
      this.helper.showMessage(error);
    }
  }


  public onClickStartServize() {
    if (this.platform.is('cordova')) {
      this.bindServize();
    } else {
      this.resultText += ' ' + this.resultText;
    }
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
    this.pluginListener = Plugins.NatPlugin.addListener(Constants.HOME_EVENT, (info: any) => {
      this.resultFromNative(info);
    });
  }

  public removeListener() {
    this.pluginListener.remove();
  }

  // resultados de la capa nativa -> result: "agua"
  public resultFromNative(result) {
    this.resultText += ' ' + result.result;
    this.changeDetectorRef.detectChanges();
  }



}
