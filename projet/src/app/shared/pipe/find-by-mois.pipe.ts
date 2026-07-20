import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'findByMoisPrev', standalone: true })
export class FindByMoisPrevPipe implements PipeTransform {
  transform(charges: any[], mois: string): number {
    return charges?.find(c => c.mois === mois)?.coutPrev || 0;
  }
}

@Pipe({ name: 'findByMoisReel', standalone: true })
export class FindByMoisReelPipe implements PipeTransform {
  transform(pointages: any[], mois: string): number {
    return pointages?.find(p => p.mois === mois)?.coutReel || 0;
  }
}