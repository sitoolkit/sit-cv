import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FunctionModelComponent } from './function-model.component';

describe('FunctionModelComponent', () => {
  let component: FunctionModelComponent;
  let fixture: ComponentFixture<FunctionModelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FunctionModelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FunctionModelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
