import { Constants } from './../../services/Constants';
import { NativePlugin } from './../../services/NativePlugin';
import { Component, ViewChildren } from '@angular/core';
import { Platform } from '@ionic/angular';



@Component({
  selector: 'app-login',
  templateUrl: 'login.page.html',
  styleUrls: ['login.page.scss']
})
export class HomePage {

  @ViewChildren('iNusername') iNusername;
  @ViewChildren('iNpassword') iNpassword;

  public LOGIN = Constants.PAGES.LOGIN;
  public REGISTER = Constants.PAGES.REGISTER;
  public pageMode = this.LOGIN;

  // login
  public version = Constants.APP_VERSION;
  public username = '';
  public password = '';
  public password2 = '';
  public email = '';
  public submitted = false;
  public PassTypeText = 'password';
  emailPass: any;
  // register


  constructor(
    private nativePlugin: NativePlugin,
    private platform: Platform,
  ) {
  }

  async ionViewDidEnter() {
  }


  switchPassVisibility() {
    switch (this.PassTypeText) {
      case 'text':
        this.PassTypeText = 'password';
        break;
      case 'password':
        this.PassTypeText = 'text';
        break;
      default:
        break;
    }
  }


  // llamar desde el click y (ionBlur)= o (focusout)=
  public selectOnClick(parIonInput) {
    const inputHtmlNat = parIonInput.el.firstElementChild;
    if (inputHtmlNat.value !== '') {
      if (inputHtmlNat.setSelect !== true) {
        inputHtmlNat.setRangeText(inputHtmlNat.value, 0, inputHtmlNat.value.length, 'select');
        const prop = 'setSelect';
        inputHtmlNat[prop] = true;
      } else {
        inputHtmlNat.setRangeText(inputHtmlNat.value, 0, inputHtmlNat.value.length, 'end');
        inputHtmlNat.setSelect = false;
      }
    }
  }

  public async onClickLogin() {
    if (this.platform.is('cordova')) {
      const result = await this.nativePlugin.selectDB(Constants.USUARIOS, this.username);
    } else {
      this.submitted = true;
    }
  }


  public onClickRegistro() {
    this.pageMode = this.REGISTER;

  }


  public async onClickSaveUser() {
    const user = {
      usuario: this.username,
      loginPass: this.password,
      mailFrom: this.email,
      mailPass: this.emailPass
    };
    const result = await this.nativePlugin.insertDB(Constants.USUARIOS, user);
    if (result) {
      this.pageMode = this.LOGIN;
    }
  }



}

