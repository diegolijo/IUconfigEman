import { Constants } from './../../services/Constants';
import { NativePlugin } from './../../services/NativePlugin';
import { Component, ViewChildren } from '@angular/core';
import { Platform } from '@ionic/angular';
import { Helper } from './../../services/Helper';
import { NavigationExtras, Router } from '@angular/router';



@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss']
})
export class LoginPage {

  @ViewChildren('iNusername') iNusername;
  @ViewChildren('iNpassword') iNpassword;

  // view
  public version = Constants.APP_VERSION;
  public PassTypeText = 'password';
  public submitted = false;

  // login
  public username = '';
  public password = '';



  constructor(
    private nativePlugin: NativePlugin,
    private platform: Platform,
    public helper: Helper,
    private router: Router
  ) {
  }

  async ionViewDidEnter() {
  }


  /************************************************ View ************************************************/

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

  public onClickLogin() {
    this.checkLogin();
  }


  public onClickGoToRegistro() {
    this.goTo('register');
  }


  /************************************************ Login ************************************************/


  /**
   * compueba select usuario
   * compara passwords
   */
  public async checkLogin() {
    if (this.platform.is('cordova')) {
      const result = await this.nativePlugin.selectDB(Constants.USUARIOS, this.username);
      if (result.result) {
        if (result.registro.loginPass === this.password) {
          // todo alert autenticacion ok
          this.goTo('home');
        }
      } else {
        // todo alert no se encuentra el usuario
      }
    } else {
      // codigo plataforma pc
      this.goTo('home');
    }
  }


  /************************************************ Routing ************************************************/

  goTo(path: any) {
    let navigationExtras: NavigationExtras;
    switch (path) {
      case 'register':
        this.router.navigate(['register']);
        break;
      case 'home':
        navigationExtras = { state: { user: this.username } };
        this.router.navigateByUrl('home', navigationExtras);
        break;
      default:
        this.router.navigate(['no-found']);
        break;
    }
  }

}

