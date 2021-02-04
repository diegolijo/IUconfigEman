import { Component } from '@angular/core';
import { Plugins } from '@capacitor/core';
const { NatPlugin } = Plugins;

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss']
})
export class HomePage {

  public username: string;
  public password: string;
  public submitted: boolean;
  public version: string;
  public visibility = 'password';

  constructor() {

    this.username = '';
    this.password = '';
    this.submitted = true;
    this.version = '';
  }

  public async toAndroidService() {
    //  debugger;
    const result = await NatPlugin.customCall({ message: 'CUSTOM MESSAGE' });
    this.username = result;
    this.submitted = result;
    this.version = result;
    const res = await NatPlugin.getPalabra({ message: 'CUSTOM MESSAGE' });
  }


  switchVisibility() {
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








}

