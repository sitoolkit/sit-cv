import {Pipe, PipeTransform} from '@angular/core';

export type HideOption = 'HIDE_ALL' | 'PARAM_TYPE_ONLY';

@Pipe({
  name: 'hidePackage'
})
export class HidePackagePipe implements PipeTransform {

  transform(value: string, hideOption: HideOption = 'HIDE_ALL'): string {
    if (hideOption === 'PARAM_TYPE_ONLY') {
      return value.replace(/[(][^()]*[)]/g, this.hidePackageName);
    } else {
      return this.hidePackageName(value);
    }
  }

  hidePackageName(value: string) {
    return value.replace(/[a-zA-Z][^ ()<>,]+\.([A-Z])/g, '$1');
  }
}
