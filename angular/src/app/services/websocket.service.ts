import {Injectable} from "@angular/core";
import {Subject} from "rxjs";
import {WebsocketMessage} from "../models/misc/websocket-message";
import { webSocket } from 'rxjs/webSocket';
import {environment} from "../environments/environment";
import {AlertService} from "./alert.service";

/**
 * Сервис для управления WebSocket соединением с сервером.
 *
 * Обеспечивает двустороннюю связь с сервером в реальном времени для:
 * - Получения уведомлений об изменениях данных
 * - Автоматического обновления интерфейса при изменениях
 * - Синхронизации состояния между клиентами
 *
 * Сообщения содержат информацию о модели и идентификаторе изменённого объекта,
 * что позволяет компонентам реагировать только на релевантные изменения.
 */
@Injectable({ providedIn: 'root' })
export class WebsocketService {
  /** Subject для распространения WebSocket сообщений */
  private wsSubject = new Subject<WebsocketMessage>();
  
  /** Observable для подписки на WebSocket сообщения */
  ws$ = this.wsSubject.asObservable();

  /** URL WebSocket сервера из переменных окружения */
  url = environment.wsUrl;

  /**
   * Создает экземпляр WebsocketService и устанавливает соединение.
   *
   * При инициализации:
   * - Устанавливает WebSocket соединение с сервером
   * - Обрабатывает входящие сообщения
   * - Отображает уведомление об ошибке если соединение не удалось
   *
   * @param alertService - Сервис для отображения уведомлений об ошибках
   */
  constructor(private alertService: AlertService) {
    let subject = webSocket(this.url);
    subject.subscribe(
      (message) => this.call(message as WebsocketMessage),
      (error) => {
        if(!localStorage.getItem("hideErrorWS"))
          this.alertService.showAlert('danger', 'Не удалось подключиться к сервису обновлений. Перезагрузите страницу.');
        console.error(error);
      },
      () => console.log('complete')
    );
    console.log('Websocket connected!');
  }

  /**
   * Обрабатывает входящее WebSocket сообщение.
   *
   * Передает сообщение всем подписчикам через Subject.
   *
   * @param message - WebSocket сообщение от сервера
   * @private
   */
  private call(message: WebsocketMessage) {
    this.wsSubject.next(message);
  }
}
