import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { NewAlarmaPageRoutingModule } from './new-alarma-routing.module';

import { NewAlarmaPage } from './new-alarma.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    NewAlarmaPageRoutingModule
  ],
  declarations: [NewAlarmaPage]
})
export class NewAlarmaPageModule {}
