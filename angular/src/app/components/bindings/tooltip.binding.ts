import {Component, HostBinding} from "@angular/core";

/**
 * Директива‑обёртка для тултипов.
 *
 * Добавляет необходимые ARIA‑атрибуты и CSS‑классы.
 */
@Component({
  selector: 'div[tooltip]',
  standalone: true,
  template: `<ng-content></ng-content>`
})
export class TooltipBinding {
  /** Базовые классы для тултипа. */
  @HostBinding('class') classes = 'absolute z-10 invisible inline-block px-3 py-2 mb-1 text-sm font-medium text-white transition-opacity duration-300 bg-gray-600 rounded-lg shadow-sm opacity-0 tooltip dark:bg-gray-700';
  /** Роль ARIA для вспомогательного текста. */
  @HostBinding('role') role = 'tooltip';
}
