import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { TranslateModule } from '@ngx-translate/core';
import { DirectivesModule } from './../../directives/directives-module';
import { LoginPageRoutingModule } from './login-routing.module';
import { LoginPage } from './login.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    DirectivesModule,
    TranslateModule.forChild(),
    LoginPageRoutingModule
  ],
  declarations: [LoginPage]
})


export class LoginPageModule { }


