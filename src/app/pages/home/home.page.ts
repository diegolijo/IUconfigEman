import { Component } from '@angular/core';
import { Plugins } from '@capacitor/core';
const { NatPlugin } = Plugins;

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss']
})
export class HomePage {

  public submitted: boolean;
  public password: string;
  public version: string;
  public username: string;


  constructor() {

    this.submitted = true;
    this.password = '';
    this.version = '';
    this.username = '';
  }

  public async toAndroidService() {
    debugger;
    const result = await NatPlugin.customCall({ message: 'CUSTOM MESSAGE' });
    this.username = result;
    this.submitted = result;
    this.version =result;
    const res = await NatPlugin.getPalabra({ message: 'CUSTOM MESSAGE' });
  }













}

