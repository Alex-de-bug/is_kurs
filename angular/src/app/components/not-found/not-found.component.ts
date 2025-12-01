import { Component } from '@angular/core';
import {LoaderService} from "../../services/loader.service";

/**
 * Страница 404 (маршрут не найден).
 *
 * Скрывает глобальный лоадер и отображает сообщение о том, что ресурс не найден.
 */
@Component({
  selector: 'app-not-found',
  templateUrl: './not-found.component.html',
  standalone: true
})
export class NotFoundComponent {
  constructor(private loaderService: LoaderService) {
    this.loaderService.loader(false);
  }
}
