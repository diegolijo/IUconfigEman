import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { NavigationExtras, Router } from '@angular/router';
import { Plugins } from '@capacitor/core';
import { Platform } from '@ionic/angular';
import { AppComponent } from './../../app.component';
import { AppUser } from './../../services/AppUser';
import { Constants } from './../../services/Constants';
import { Helper } from './../../services/Helper';
import { ModelCreator } from './../../services/model_ceator';
import { NativePlugin } from './../../services/NativePlugin';
const { NatPlugin } = Plugins;

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss'],
})
export class HomePage implements OnInit, OnDestroy {

  // @ViewChildren('textArea') textArea;


  private appUser: any;
  public guardianServize = false;

  // servicio
  public isBindService = false;
  public resultText = [];
  public pluginListener: any;

  constructor(
    private platform: Platform,
    private nativePlugin: NativePlugin,
    public helper: Helper,
    private changeDetectorRef: ChangeDetectorRef,
    public appComponent: AppComponent,
    public modelCreator: ModelCreator,
    private ProAppUser: AppUser,
    private router: Router

  ) { }

  ngOnInit() {
    if (this.platform.is('cordova')) {



    } else {  // PC

    }
  }

  async ionViewDidEnter() {
    this.appUser = this.ProAppUser.getAppUser();
    if (this.platform.is('cordova')) {
      if (this.appUser.usuario === '') {
        this.router.navigate(['login']);
      }
      this.guardianServize = await this.isServiceRuning();

    } else {  // PC

    }
  }


  async ngOnDestroy() {
    this.unBindServize();
    this.removeListener();
  }


  /************************************************ eventos ************************************************/

  public async onClickGoTo(page: string) {
    try {
      switch (page) {
        case 'new-palabra':
          if (this.isBindService) {
            this.unBindServize();
            this.removeListener();
          }
          this.router.navigateByUrl(page);
          break;
        case 'new-alarma':
          if (this.isBindService) {
            this.unBindServize();
            this.removeListener();          }
          this.router.navigateByUrl(page);
          break;

        default:
          break;
      }
    } catch (error) {
      this.helper.showException('onClickGoTo: ' + error);

    }
  }

  public async onToggleServize(event) {
    if (this.platform.is('cordova')) {
      const result = await this.isServiceRuning();
      if (event.detail.checked) {
        if (!result) {
          await this.startServize();
        }
      } else {
        if (result) {
          await this.stopServize();
        }
      }
    } else {
      if (event.detail.checked) { }

    }
  }

  public async onClickBindServize() {
    if (!this.isBindService) {
      this.startListener();
      this.bindServize();
    } else {
      this.unBindServize();
      this.removeListener();

    }
  }

  private async isServiceRuning() {
    const result = await this.nativePlugin.isServizeRunning();
    let running = false;
    if (result.result) {
      running = true;
    }
    return running;
  }


  /************************************************ servize ************************************************/

  public async startServize() {
    const result = await this.nativePlugin.startService();
  }

  public async stopServize() {
    const result = await this.nativePlugin.stopService();
  }

  public async bindServize() {
    this.isBindService = true;
    const result = await this.nativePlugin.bindService();
  }

  public async unBindServize() {
    this.isBindService = false;
    const result = await this.nativePlugin.unBindServize();
  }

  public async isServizeRunning() {
    const result = await this.nativePlugin.isServizeRunning();
  }


  /********************************************** listener ********************************************/

  public startListener() {
    this.pluginListener = Plugins.NatPlugin.addListener(Constants.HOME_EVENT, (info: any) => {
      this.resultFromNative(info);
    });
  }

  public removeListener() {
    try {
      if (typeof this.pluginListener !== 'undefined') {
        this.pluginListener.remove();
      }
    } catch (err) {
      this.helper.showException('removeListener' + err);
    }
  }

  // resultados de la capa nativa -> result: "agua"
  public resultFromNative(result) {
    this.resultText.push(result.result);
    this.changeDetectorRef.detectChanges();
  }



}
