import { TestBed } from '@angular/core/testing';

import { GameserverApiService } from './gameserver-api.service';

describe('GameserverApiService', () => {
  let service: GameserverApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GameserverApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
