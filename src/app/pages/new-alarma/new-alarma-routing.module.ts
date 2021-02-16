import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { NewAlarmaPage } from './new-alarma.page';

const routes: Routes = [
  {
    path: '',
    component: NewAlarmaPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NewAlarmaPageRoutingModule {}
