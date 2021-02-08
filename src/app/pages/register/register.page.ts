import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Platform } from '@ionic/angular';
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
    if (this.password2 === this.password) {
      if (this.platform.is('cordova')) {
        const res = await this.nativePlugin.selectDB(Constants.USUARIOS, this.username);
        if (!res.result) {
          const user = {
            usuario: this.username,
            loginPass: this.password,
            mailFrom: this.email,
            mailPass: this.emailPass
          };
          const result = await this.nativePlugin.insertDB(Constants.USUARIOS, user);
          if (result.result) {
            // TODO alert creado ok

          } else {

          }
        } else {
          // todo alert user existe

        }
      } else {
        // todo codigo plataforma pc

      }
    } else {
      // todo alert pass diferentes

    }
  }

}
