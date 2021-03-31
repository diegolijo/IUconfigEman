import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { AlertController, MenuController, Platform } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { AppUser } from './services/AppUser';
import { Helper } from './services/Helper';



@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss']
})
export class AppComponent implements OnInit {

  darkMode = true;
  languages = ['GAL', 'ES'];
  language: string;


  constructor(
    private translate: TranslateService,
    private platform: Platform,
    private splashScreen: SplashScreen,
    private statusBar: StatusBar,
    private appUser: AppUser,
    private alertCtrl: AlertController,
    private router: Router,
    private menuController: MenuController,
    private helper: Helper
  ) {
    this.initializeApp();
    /* This line would incorporate the language detection */
    let userLang = navigator.language.split('-')[0];
    userLang = (userLang === 'gal') ? userLang : 'es';
    this.translate.use(userLang);

  }

  /************************** events *************************/


  ngOnInit() {
    this.appUser.emptyAppUser();
    this.setLanguage('GAL');
  }

  public toggleTheme(value: boolean) {
    this.darkMode = value;
    this.setAppTheme(this.darkMode);
  }

  public onSelectLanguage(value: string) {
    const lenguage = value;
    this.setLanguage(lenguage);
  }


  public onClickLogout() {
    this.logout();
  }

  /*************************************************************/

  async initializeApp() {
    try {
      const res = await this.platform.ready();
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)');
      this.darkMode = prefersDark.matches;
      this.setAppTheme(this.darkMode);
      this.statusBar.styleDefault();
      this.splashScreen.hide();
    }
    catch (error) {

    }
  }

  public setAppTheme(dark: boolean) {
    document.body.setAttribute('color-theme', 'light');
    if (dark) {
      document.body.setAttribute('color-theme', 'dark');
    }
  }

  private setLanguage(lenguage: string) {
    switch (lenguage) {
      case 'ES':
        this.translate.use('es');
        break;
      case 'GAL':
        this.translate.use('gal');
        break;
      default:
        this.translate.use('gal');
        break;
    }
  }

  async logout() {
    try {
      const alert = await this.alertCtrl.create({
        header: await this.translate.get('LOGOUT.TITLE').toPromise(),
        subHeader: await this.translate.get('LOGOUT.TEXT').toPromise(),
        message: '',
        buttons: [{
          text: await this.translate.get('NO').toPromise(),
          role: 'cancel',
          cssClass: 'btn-cancel'
        }, {
          text: await this.translate.get('SI').toPromise(),
          handler: async () => {
            try {
              // await this.authenticationService.logout();
              this.appUser.emptyAppUser();
              await this.menuController.close();
              this.router.navigateByUrl('/login');
            } catch (error) {
              throw (error);
            }
          },
          cssClass: 'btn-success'
        }]
      });
      await alert.present();
      await alert.onDidDismiss();
    } catch (error) {
      this.helper.showMessage(error.message);
    }
  }



}
