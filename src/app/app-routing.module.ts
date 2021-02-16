import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadChildren: () => import('./pages/login/login.module').then(m => m.LoginPageModule)
  },
  {
    path: 'home',
    //  canActivate: [AuthGuard],
    loadChildren: () => import('./pages/home/home.module').then(m => m.HomePageModule)
  },
  {
    path: 'register',
    loadChildren: () => import('./pages/register/register.module').then(m => m.RegisterPageModule)
  },
  {
    path: 'new-palabra',
    loadChildren: () => import('./pages/new-palabra-modal/new-palabra-modal.module').then(m => m.NewPalabraModalPageModule)
  },
  {
    path: 'new-alarma',
    loadChildren: () => import('./pages/new-alarma/new-alarma.module').then( m => m.NewAlarmaPageModule)
  },
  {
    path: 'new-alarma',
    loadChildren: () => import('./new-alarma/new-alarma.module').then( m => m.NewAlarmaPageModule)
  },


];
@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
