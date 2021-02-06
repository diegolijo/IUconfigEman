import { Component, ViewChildren } from '@angular/core';
import { Plugins } from '@capacitor/core';
import { IonInput } from '@ionic/angular';
const { NatPlugin } = Plugins;

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss']
})
export class HomePage {

  @ViewChildren('iNusername') iNusername;
  @ViewChildren('iNpassword') iNpassword;
  @ViewChildren('form') form;
  @ViewChildren('textarea') textarea;


  public username: string;
  public password: string;
  public submitted: boolean;
  public version: string;
  public visibility = 'password';
  //  public inputHtmlNat: any;
  public textArea: string;

  constructor() {
    this.username = 'xfbxfbx';
    this.password = 'dfgbdx';
    this.submitted = true;
    this.version = '';
  }

  async ionViewDidEnter() {
    //   this.inputHtmlNat = this.iNusername.first.el.firstElementChild;
    // tslint:disable-next-line: no-string-literal
  }



  public async toAndroidService() {
    //  debugger;
    const result = await NatPlugin.customCall({ message: 'CUSTOM MESSAGE' });
    this.username = result;
    this.submitted = result;
    this.version = result;
    const res = await NatPlugin.getPalabra({ message: 'CUSTOM MESSAGE' });
  }


  switchPassVisibility() {
    switch (this.visibility) {
      case 'text':
        this.visibility = 'password';
        break;
      case 'password':
        this.visibility = 'text';
        break;
      default:
        break;
    }
  }

  getVisibility() {

  }

// llamar desde el click y (ionBlur)= o (focusout)=
  selectOnClick(parIonInput) {
    const inputHtmlNat = parIonInput.el.firstElementChild;
    if (inputHtmlNat.value !== '') {
      if (inputHtmlNat.setSelect !== true) {
        inputHtmlNat.setRangeText(inputHtmlNat.value, 0, inputHtmlNat.value.length, 'select');
        // tslint:disable-next-line: no-string-literal
        inputHtmlNat['setSelect'] = true;
      } else {
        inputHtmlNat.setRangeText(inputHtmlNat.value, 0, inputHtmlNat.value.length, 'end');
        inputHtmlNat.setSelect = false;
      }
    }
  }





}

