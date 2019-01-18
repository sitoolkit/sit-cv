import { Injectable } from "@angular/core";

@Injectable({ providedIn: 'root' })
export class EnumUtils {
  values<T>(enumType: any): T[] {
    return <T[]>Object.values(enumType).filter(v => typeof v === "string");
  }
}