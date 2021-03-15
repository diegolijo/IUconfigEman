import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Platform } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { Constants } from './../../services/Constants';
import { Helper } from './../../services/Helper';
import { NativePlugin } from './../../services/NativePlugin';

@Component({
  selector: 'app-register',
  templateUrl: './register.page.html',
  styleUrls: ['./register.page.scss'],
})
export class RegisterPage implements OnInit {

  // view
  public PassTypeText = 'password';
  public username = '';
  public password = '';
  public password2 = '';
  public email = '';
  public emailPass = '';

  constructor(
    private translate: TranslateService,
    private nativePlugin: NativePlugin,
    private platform: Platform,
    public helper: Helper,
    private router: Router
  ) { }

  ngOnInit() {
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


  public async onClickSaveUser() {
    this.registerUser();
  }


  /************************************************ registro ************************************************/
  /**
   * compueba si no existe el usuario
   * inserta el registro en la BD
   */
  public async registerUser() {

    if (this.platform.is('cordova')) {
      if (this.password2 === this.password) {
        const res = await this.nativePlugin.selectDB(Constants.USUARIOS, this.username, null);
        if (!res.result) {
          const user = {
            usuario: this.username,
            loginPass: this.password,
            mailFrom: this.email,
            mailPass: this.emailPass
          };
          const result = await this.nativePlugin.insertDB(Constants.USUARIOS, user);
          if (result.result) {
            this.helper.showMessage(await this.translate.get('REGISTER.DONE').toPromise());
            history.back();
          } else {
            this.helper.showMessage(await this.translate.get('REGISTER.FAIL').toPromise());
          }
        } else {
          this.helper.showMessage(await this.translate.get('REGISTER.FAILURE').toPromise());
        }
      } else {

        this.helper.showMessage(await this.translate.get('REGISTER.DIFERENT_PASSWORDS').toPromise());
      }
    } else {
      // todo codigo plataforma pc


    }
  }

}
