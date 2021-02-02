import { Component } from '@angular/core';
import { Plugins } from '@capacitor/core';
const { NatPlugin } = Plugins;

@Component({
  selector: 'app-tabs',
  templateUrl: 'tabs.page.html',
  styleUrls: ['tabs.page.scss']
})
export class TabsPage {


  constructor() { }

  public async toAndroidService() {
    const result = await NatPlugin.customCall({ message: 'CUSTOM MESSAGE' });
    const res = await NatPlugin.customFunction();
  }


}

