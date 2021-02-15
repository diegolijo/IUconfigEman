import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { NewPalabraModalPage } from './new-palabra-modal.page';

const routes: Routes = [
  {
    path: '',
    component: NewPalabraModalPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NewPalabraModalPageRoutingModule {}
