import { Component, OnInit } from '@angular/core';

import { Platform } from '@ionic/angular';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { TranslateService } from '@ngx-translate/core';


@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss']
})
export class AppComponent implements OnInit {
  darkMode = true;


  constructor(
    private translate: TranslateService,
    private platform: Platform,
    private splashScreen: SplashScreen,
    private statusBar: StatusBar
  ) {
    this.initializeApp();

    /*   This line would incorporate the language detection */
    let userLang = navigator.language.split('-')[0];
    userLang = (userLang === 'en' || userLang === 'es') ? userLang : 'es';
    this.translate.use(userLang);

  }

  ngOnInit() {
  }

  async initializeApp() {
    try {

      const res = await this.platform.ready();
      const prefersDark = window.matchMedia('(prefers-color-scheme: light)');
      this.darkMode = prefersDark.matches;
      this.setAppTheme(this.darkMode);
      this.statusBar.styleDefault();
      this.splashScreen.hide();
    }
    catch (error) {

    }
  }

  public toggleTheme(event: any) {
    this.darkMode = event.detail.checked;
    this.setAppTheme(this.darkMode);
  }

  public setAppTheme(dark: boolean) {
    if (dark) {
      document.body.setAttribute('color-theme', 'dark');
      return;
    }
    document.body.setAttribute('color-theme', 'light');
  }
}
