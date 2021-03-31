import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { TranslateModule } from '@ngx-translate/core';
import { MapsPageRoutingModule } from './maps-routing.module';
import { MapsPage } from './maps.page';




@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    TranslateModule.forChild(),
    MapsPageRoutingModule
  ],
  declarations: [MapsPage]
})
export class MapsPageModule {}
