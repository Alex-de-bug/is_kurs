import {Injectable} from "@angular/core";
import {Subject} from "rxjs";

/**
 * Сервис для управления глобальным индикатором загрузки.
 *
 * Предоставляет централизованный механизм для отображения/скрытия
 * индикатора загрузки (спиннера) во время выполнения асинхронных операций.
 *
 * Используется для улучшения UX при:
 * - Загрузке данных с сервера
 * - Выполнении длительных операций
 * - Переходах между страницами
 */
@Injectable({ providedIn: 'root' })
export class LoaderService {
  /** Subject для управления состоянием загрузчика */
  private loaderSubject = new Subject<{ show: boolean }>();
  
  /** Observable для подписки на изменения состояния загрузчика */
  loader$ = this.loaderSubject.asObservable();

  /**
   * Управляет отображением глобального индикатора загрузки.
   *
   * @param show - true для отображения, false для скрытия индикатора
   */
  loader(show: boolean) {
    this.loaderSubject.next({ show });
  }
}
