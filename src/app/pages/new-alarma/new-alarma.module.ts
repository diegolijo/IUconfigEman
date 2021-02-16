import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { TranslateModule } from '@ngx-translate/core';
import { NewAlarmaPageRoutingModule } from './new-alarma-routing.module';
import { NewAlarmaPage } from './new-alarma.page';




@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    TranslateModule.forChild(),
    NewAlarmaPageRoutingModule
  ],
  declarations: [NewAlarmaPage]
})
export class NewAlarmaPageModule { }
