import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { NewPalabraModalPageRoutingModule } from './new-palabra-modal-routing.module';

import { NewPalabraModalPage } from './new-palabra-modal.page';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    TranslateModule.forChild(),
    NewPalabraModalPageRoutingModule
  ],
  declarations: [NewPalabraModalPage]
})
export class NewPalabraModalPageModule {}
