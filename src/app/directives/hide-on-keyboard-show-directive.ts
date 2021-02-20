import { Directive, ElementRef, Input, AfterViewInit, NgZone, EventEmitter, Output } from '@angular/core';
import { Plugins, KeyboardInfo } from '@capacitor/core';
import { Helper } from '../services/Helper';
const { Keyboard } = Plugins;


@Directive({
    selector: '[appHideOnKeyboardShow]'
})
export class HideOnKeyboardShowDirective implements AfterViewInit {

    public el = this.elementRef.nativeElement;

    constructor(
        private elementRef: ElementRef,
    ) { }

    ngAfterViewInit() {
        this.initListener();
    }

    initListener() {
        Keyboard.addListener('keyboardWillShow', (info: KeyboardInfo) => {
            this.el.style.display = 'none';
        });


        Keyboard.addListener('keyboardWillHide', () => {
            this.el.style.display = 'block';
            try {
                this.el.setRangeText(this.el.value, 0, this.el.value.length, 'end');
            } catch (err) {

            }
        });

    }

}
