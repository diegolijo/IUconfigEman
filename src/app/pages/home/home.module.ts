import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { HomePageRoutingModule } from './home-routing.module';
import { HomePage } from './home.page';
import { TranslateModule } from '@ngx-translate/core';
import { NewPalabraModalPageModule } from '../new-palabra-modal/new-palabra-modal.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    TranslateModule.forChild(),
    HomePageRoutingModule,
    NewPalabraModalPageModule
  ],
  declarations: [HomePage]
})
export class HomePageModule { }

