import { Directive, ElementRef, Input, AfterViewInit, NgZone, EventEmitter, Output } from '@angular/core';
import { GestureController } from '@ionic/angular';
import { Constants } from '../services/Constants';

@Directive({
    selector: '[appPress]'
})
export class LongPressDirective implements AfterViewInit {

    @Output() appPress = new EventEmitter();
    @Input() delay = Constants.LONG_PRESS_THRESHOLD;
    action: any;

    private longPressActive = false;

    constructor(
        private elementRef: ElementRef,
        private gestureCtrl: GestureController,
        private zone: NgZone
    ) { }

    ngAfterViewInit() {
        this.loadLongPressOnElement();
    }

    loadLongPressOnElement() {

        const gesture = this.gestureCtrl.create({
            el: this.elementRef.nativeElement,
            threshold: 0,
            gestureName: 'long-press',
            onStart: ev => {
                this.longPressActive = true;
                this.longPressAction();
            },
            onEnd: ev => {
                this.longPressActive = false;
            }
        });
        gesture.enable(true);
    }

    private longPressAction() {
        if (this.action) {
            clearInterval(this.action);
        }
        this.action = setTimeout(() => {
            this.zone.run(() => {
                if (this.longPressActive === true) {
                    this.longPressActive = false;
                    this.appPress.emit();
                }
            });
        }, this.delay);
    }
}
