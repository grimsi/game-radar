import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {MatPaginatorModule, MatSortModule, MatTableModule} from '@angular/material';

import {GameserverTableComponent} from './gameserver-table.component';

describe('ServerTableComponent', () => {
  let component: GameserverTableComponent;
  let fixture: ComponentFixture<GameserverTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [GameserverTableComponent],
      imports: [
        NoopAnimationsModule,
        MatPaginatorModule,
        MatSortModule,
        MatTableModule,
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GameserverTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should compile', () => {
    expect(component).toBeTruthy();
  });
});
