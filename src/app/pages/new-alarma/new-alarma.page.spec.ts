import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { IonicModule } from '@ionic/angular';

import { NewAlarmaPage } from './new-alarma.page';

describe('NewAlarmaPage', () => {
  let component: NewAlarmaPage;
  let fixture: ComponentFixture<NewAlarmaPage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewAlarmaPage ],
      imports: [IonicModule.forRoot()]
    }).compileComponents();

    fixture = TestBed.createComponent(NewAlarmaPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
