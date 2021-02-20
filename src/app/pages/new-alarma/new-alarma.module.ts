import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { TranslateModule } from '@ngx-translate/core';
import { DirectivesModule } from '../../directives/directives-module';
import { NewAlarmaPageRoutingModule } from './new-alarma-routing.module';
import { NewAlarmaPage } from './new-alarma.page';




@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    DirectivesModule,
    TranslateModule.forChild(),
    NewAlarmaPageRoutingModule
  ],
  declarations: [NewAlarmaPage]
})
export class NewAlarmaPageModule { }
