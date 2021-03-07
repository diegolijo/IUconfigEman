import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { TranslateModule } from '@ngx-translate/core';
import { NewPalabraModalPageRoutingModule } from './new-palabra-modal-routing.module';
import { NewPalabraModalPage } from './new-palabra-modal.page';




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
export class NewPalabraModalPageModule { }
