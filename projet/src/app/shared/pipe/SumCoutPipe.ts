import { Pipe, PipeTransform } from '@angular/core';
import { CoutPrev } from 'app/services/cout-prev.service';

@Pipe({ name: 'sumCout', standalone: true })
export class SumCoutPipe implements PipeTransform {
  transform(charges: CoutPrev[]): number {
    return charges.reduce((s, c) => s + (c.coutPrev || 0), 0);
  }
}