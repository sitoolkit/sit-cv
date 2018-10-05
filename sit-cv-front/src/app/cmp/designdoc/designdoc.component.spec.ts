import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DesignDocComponent } from './designdoc.component';

describe('DesignDocComponent', () => {
  let component: DesignDocComponent;
  let fixture: ComponentFixture<DesignDocComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DesignDocComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignDocComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
