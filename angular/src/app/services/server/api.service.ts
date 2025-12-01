import { Injectable } from '@angular/core';
import {HttpErrorResponse, HttpHeaders} from '@angular/common/http';

import { AuthService } from './auth.service';
import {Observable, throwError} from "rxjs";
import {AlertService} from "../alert.service";
import {environment} from "../../environments/environment";

/**
 * Базовый сервис для взаимодействия с REST API.
 * 
 * Предоставляет общие методы для работы с HTTP-запросами:
 * - Формирование заголовков с токеном авторизации
 * - Централизованная обработка ошибок
 * - Интеграция с сервисом аутентификации
 */
@Injectable({
  providedIn: 'root'
})
export class ApiService {
  /** URL базового API из переменных окружения */
  apiUrl = environment.apiUrl;

  /**
   * Создает экземпляр ApiService
   * 
   * @param authService - Сервис аутентификации для получения токена
   * @param alertService - Сервис для отображения уведомлений об ошибках
   */
  constructor(private authService: AuthService, private alertService: AlertService) {
    this.handleError = this.handleError.bind(this);
  }

  /**
   * Формирует HTTP заголовки с токеном авторизации
   * 
   * @returns HttpHeaders с Content-Type и Authorization
   */
  getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    })
  }

  /**
   * Централизованный обработчик HTTP ошибок
   * 
   * @param error - HTTP ошибка от Angular HttpClient
   * @returns Observable с ошибкой
   * 
   * Обрабатывает различные типы ошибок:
   * - ErrorEvent: клиентские ошибки
   * - 401: автоматический выход из системы
   * - Прочие: отображение уведомления пользователю
   */
  handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred.';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else if (error.status === 401) {
      this.authService.logout();
    } else {
      if (error.error && error.error.message) {
        errorMessage = error.error.message;
        this.alertService.showAlert('danger', errorMessage);
      } else {
        errorMessage = `Error Status: ${error.status}`;
      }
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
