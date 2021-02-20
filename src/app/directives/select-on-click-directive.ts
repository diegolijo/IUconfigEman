import { Directive, ElementRef, AfterViewInit } from '@angular/core';
import { GestureController } from '@ionic/angular';


@Directive({
    selector: '[appSelectOnClick]'
})


export class SelectOnClickDirective implements AfterViewInit {

    constructor(
        public elementRef: ElementRef,
        private gestureCtrl: GestureController,
    ) { }


    ngAfterViewInit() {
        this.loadselectOnClickOnElement();
    }


    loadselectOnClickOnElement() {
        const natElement = this.elementRef.nativeElement;
        const gesture = this.gestureCtrl.create({
            el: natElement,
            threshold: 0,
            gestureName: 'select-on-click',
            onStart: ev => {
                this.selectOnClick(natElement);
            },
            onEnd: ev => {
            }
        });
        gesture.enable(true);
    }



    public selectOnClick(el) {
        const inputHtmlNat = el.firstElementChild;
        if (inputHtmlNat.value !== '') {
            if (inputHtmlNat.setSelect !== true) {
                inputHtmlNat.select();
                const prop = 'setSelect';
                inputHtmlNat[prop] = true;
            } else {
                inputHtmlNat.focus();
                inputHtmlNat.setSelect = false;
            }
        }
    }



}
