import { element } from 'protractor';
import { Constants } from './Constants';
import { Injectable } from '@angular/core';
import { Platform } from '@ionic/angular';

@Injectable()
export class Helper {
  subscription: any;




  constructor(
    private platform: Platform,) {
  }


  // llamar desde el click y (ionBlur)= o (focusout)=
  public selectOnClick(parIonInput) {
    const inputHtmlNat = parIonInput.el.firstElementChild;
    if (inputHtmlNat.value !== '') {
      if (inputHtmlNat.setSelect !== true) {
        inputHtmlNat.setRangeText(inputHtmlNat.value, 0, inputHtmlNat.value.length, 'select');
        const prop = 'setSelect';
        inputHtmlNat[prop] = true;
      } else {
        inputHtmlNat.setRangeText(inputHtmlNat.value, 0, inputHtmlNat.value.length, 'end');
        inputHtmlNat.setSelect = false;
      }
    }
  }

  // devuelve los saltos de linea en el texto proporcionado
  public getTextLineBreaks(text: string) {
    const exp = /\n/g;
    const matches = text.split(exp);
    if (!matches) {
      return 2;
    } else {
      if (matches.length < 2) {
        return 2 + this.getLineBreaks(matches);
      }
    }
    return matches.length + 1 + this.getLineBreaks(matches);
  }


  public getLineBreaks(matches) {
    let saltos = 0;
    for (const line of matches) {
      if (line.length > Constants.CARACTERES_POR_LINEA) {
        saltos += line.length / Constants.CARACTERES_POR_LINEA;
      }
    }
    saltos = Math.trunc(saltos);
    return saltos;
  }



  public async getRemoteObservable(variable) {
  
 
  }




}

