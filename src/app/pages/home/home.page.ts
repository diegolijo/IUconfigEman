import { Component, OnDestroy, OnInit, ViewChildren, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { Helper } from './../../services/Helper';
import { NativePlugin } from './../../services/NativePlugin';
import { Plugins } from '@capacitor/core';
import { Constants } from 'src/app/services/Constants';
import { Platform } from '@ionic/angular';
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

  constructor(
    private platform: Platform,
    private nativePlugin: NativePlugin,
    public helper: Helper,
    private changeDetectorRef: ChangeDetectorRef

  ) { }

  ngOnInit() {
    if (this.platform.is('cordova')) {
      this.startListener();
    } else {

    }
  }



  ngOnDestroy() {
    this.removeListener();
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
