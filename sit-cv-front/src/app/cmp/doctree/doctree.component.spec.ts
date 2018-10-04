import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DoctreeComponent } from './doctree.component';

describe('DoctreeComponent', () => {
  let component: DoctreeComponent;
  let fixture: ComponentFixture<DoctreeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DoctreeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DoctreeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
