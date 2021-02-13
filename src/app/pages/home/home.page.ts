import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Plugins } from '@capacitor/core';
import { ModalController, Platform } from '@ionic/angular';
import { NewPalabraModalPage } from '../modals/new-palabra-modal/new-palabra-modal.page';
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

  // servicio
  public isBindService = false;
  public resultText = '';
  public pluginListener: any;

  constructor(
    private platform: Platform,
    private nativePlugin: NativePlugin,
    public helper: Helper,
    private changeDetectorRef: ChangeDetectorRef,
    public appComponent: AppComponent,
    public modelCreator: ModelCreator,
    private ProAppUser: AppUser,
    private router: Router,
    private modalCtrl: ModalController

  ) { }

  ngOnInit() {

  }

  async ionViewWillEnter() {
    this.appUser = this.ProAppUser.getAppUser();

    if (this.platform.is('cordova')) {
      if (this.appUser.usuario === '') {
        this.router.navigate(['login']);
      }

    } else {
      this.resultText += ' ' + this.resultText;
    }


  }



  async ngOnDestroy() {
    await this.unBindServize();
    await this.removeListener();
  }


  public async onClickNewPalabraModal() {
    try {
      const newPalabraModal = await this.modalCtrl.create({
        component: NewPalabraModalPage,
        componentProps: {
          user: this.appUser
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
      // TODO arranca servicio en primer plano 
    } else {
      this.resultText += ' ' + this.resultText;
    }
  }

  public async onClickBindServize() {
    if (!this.isBindService) {
      await this.startListener();
      await this.bindServize();
    } else {
      await this.unBindServize();
      await this.removeListener();

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
