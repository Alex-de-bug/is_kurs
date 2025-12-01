import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

/**
 * Сервис для управления системными уведомлениями (алертами).
 *
 * Предоставляет централизованный механизм для отображения уведомлений
 * пользователю. Используется для:
 * - Сообщений об ошибках
 * - Уведомлений об успешных операциях
 * - Предупреждений
 * - Информационных сообщений
 */
@Injectable({ providedIn: 'root' })
export class AlertService {
  /** Subject для распространения алертов */
  private alertSubject = new Subject<{ type: string; message: string }>();
  
  /** Observable для подписки на алерты */
  alert$ = this.alertSubject.asObservable();

  /**
   * Отображает уведомление пользователю
   * 
   * @param type - Тип уведомления (success, danger, warning, info)
   * @param message - Текст сообщения для отображения
   */
  showAlert(type: string, message: string) {
    this.alertSubject.next({ type, message });
  }
}
