// sum-charge.pipe.ts
import { Pipe, PipeTransform } from '@angular/core';
import { CoutPrev } from 'app/services/cout-prev.service';

@Pipe({ name: 'sumCharge', standalone: true })
export class SumChargePipe implements PipeTransform {
  transform(charges: CoutPrev[]): number {
    return charges.reduce((s, c) => s + (c.chargePrevuM || 0), 0);
  }
}

