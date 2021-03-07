import { Component, ViewChildren } from '@angular/core';
import { NavigationExtras, Router } from '@angular/router';
import { Platform } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { AppUser } from './../../services/AppUser';
import { Constants } from './../../services/Constants';
import { Helper } from './../../services/Helper';
import { NativePlugin } from './../../services/NativePlugin';



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
    private translate: TranslateService,
    private nativePlugin: NativePlugin,
    private platform: Platform,
    public helper: Helper,
    public proAppUser: AppUser,
    private router: Router
  ) {
  }

  async ionViewDidEnter() {
    const user = this.proAppUser.getAppUser();
    if (user.usuario !== '') {
      this.goTo('home');
    }
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
   * compueba usuario
   * compara password
   * TODO sacar esto de aqui, encapsular en un metodo privado en la capa nativa
   */
  public async checkLogin() {
    if (this.platform.is('cordova')) {
      const result = await this.nativePlugin.selectDB(Constants.USUARIOS, this.username, null);
      if (result.result) {
        if (result.registro.loginPass === this.password) {
          this.proAppUser.setAppUser(result.registro);
          this.goTo('home');
        } else {
          this.submitted = true;
          const message = await this.translate.get('LOGIN.FAILURE').toPromise();
          this.helper.showMessage(message);
        }
      } else {
        this.submitted = true;
        const message = await this.translate.get('LOGIN.FAILURE').toPromise();
        this.helper.showMessage(message);
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

