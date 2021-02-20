import { NgModule } from '@angular/core';
import { CssOnKeyboardShowDirective } from './css-on-keyboard-show-directive';
import { HideOnKeyboardShowDirective } from './hide-on-keyboard-show-directive';
import { LongPressDirective } from './long-press-directive';
import { SelectOnClickDirective } from './select-on-click-directive';


@NgModule({
    imports: [],
    providers: [],
    declarations: [
        SelectOnClickDirective,
        LongPressDirective,
        HideOnKeyboardShowDirective,
        CssOnKeyboardShowDirective
    ],
    exports: [
        SelectOnClickDirective,
        LongPressDirective,
        HideOnKeyboardShowDirective,
        CssOnKeyboardShowDirective
    ],
})

export class DirectivesModule { }

