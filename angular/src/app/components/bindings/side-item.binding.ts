import {Component, Input} from "@angular/core";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {IconDefinition} from "@fortawesome/fontawesome-svg-core";

/**
 * Боковой пункт меню с иконкой.
 *
 * Используется в сайдбаре навигации.
 */
@Component({
  selector: 'li[side-item]',
  standalone: true,
  imports: [
    FaIconComponent
  ],
  template: `
    <a
      class="flex items-center p-2 text-base font-medium text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group transition-all duration-200 cursor-pointer"
    >
      <fa-icon [icon]="icon" style="width: 24px; height: 24px; font-size: 24px; text-align: center"/>
      <span class="ml-3"><ng-content></ng-content></span>
    </a>
  `
})
export class SideItemBinding {
  /**
   * Иконка, отображаемая слева от текста.
   */
  @Input() icon !: IconDefinition;
}
