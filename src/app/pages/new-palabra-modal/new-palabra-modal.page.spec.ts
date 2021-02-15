import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { IonicModule } from '@ionic/angular';

import { NewPalabraModalPage } from './new-palabra-modal.page';

describe('NewPalabraModalPage', () => {
  let component: NewPalabraModalPage;
  let fixture: ComponentFixture<NewPalabraModalPage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewPalabraModalPage ],
      imports: [IonicModule.forRoot()]
    }).compileComponents();

    fixture = TestBed.createComponent(NewPalabraModalPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
