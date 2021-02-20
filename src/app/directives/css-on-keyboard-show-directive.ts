import { AfterViewInit, Directive, ElementRef, Input, OnDestroy } from '@angular/core';
import { KeyboardInfo, Plugins } from '@capacitor/core';
import { GestureController } from '@ionic/angular';
const { Keyboard } = Plugins;


@Directive({
    selector: '[appClassKey]'
})
export class CssOnKeyboardShowDirective implements AfterViewInit, OnDestroy {
    @Input('appClassKey') cssClass: any;


    public el = this.elementRef.nativeElement;

    public isClicled = false;

    constructor(
        private elementRef: ElementRef,
        private gestureCtrl: GestureController,

    ) { }

    ngAfterViewInit() {
        this.initListener();
    }

    initListener() {




        Keyboard.addListener('keyboardWillShow', (info: KeyboardInfo) => {
            this.el.classList.add(this.cssClass);
        });

        Keyboard.addListener('keyboardWillHide', () => {
            this.el.classList.remove(this.cssClass);
        });


        /*function myFunction() {
                          var element, name, arr;
                          element = document.getElementById("myDIV");
                          name = "mystyle";
                          arr = element.className.split(" ");
                          if (arr.indexOf(name) == -1) {
                            element.className += " " + name;
                            .activeElement.name
                            .activeElement.type
                          }
                        } */
    }



    onClick() {
        const gesture = this.gestureCtrl.create({
            el: this.elementRef.nativeElement,
            threshold: 0,
            gestureName: 'on-click',
            onStart: ev => {
                const g = this.el.name;
                this.el.classList.add(this.cssClass);
            },
            onEnd: ev => {

            }
        });
        gesture.enable(true);
    }






    ngOnDestroy(): void {
        Keyboard.removeAllListeners();
    }

}

