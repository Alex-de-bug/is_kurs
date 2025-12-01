import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, throwError, BehaviorSubject, Subscription} from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import {JwtRequestDto} from "../../models/dto/jwt-request-dto";
import {JwtResponseDto} from "../../models/dto/jwt-response-dto";
import {User} from "../../models/user";
import {WebsocketService} from "../websocket.service";
import {environment} from "../../environments/environment";

/**
 * Сервис аутентификации и управления пользовательской сессией.
 *
 * Отвечает за:
 * - Аутентификацию пользователей (вход/выход)
 * - Хранение и управление JWT токеном
 * - Управление состоянием текущего пользователя
 * - Автоматическую валидацию токена при загрузке приложения
 * - Реактивное обновление данных пользователя через WebSocket
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService implements OnDestroy {
  private apiUrl = environment.apiUrl;
  
  /** BehaviorSubject для хранения JWT токена */
  private tokenSubject = new BehaviorSubject<string | null>(null);
  
  /** BehaviorSubject для хранения данных текущего пользователя */
  private userSubject = new BehaviorSubject<User | null>(null);
  
  /** Observable для подписки на изменения токена */
  token$: Observable<string | null> = this.tokenSubject.asObservable();
  
  /** Observable для подписки на изменения данных пользователя */
  user$: Observable<User | null> = this.userSubject.asObservable();

  /** Подписка на WebSocket сообщения для обновления данных пользователя */
  wss: Subscription;

  /**
   * Создает экземпляр AuthService.
   *
   * При инициализации:
   * - Загружает сохраненный токен и данные пользователя из localStorage
   * - Валидирует токен если он существует
   * - Подписывается на WebSocket для автоматического обновления данных
   *
   * @param http - HttpClient для выполнения HTTP запросов
   * @param websocketService - Сервис WebSocket для получения обновлений в реальном времени
   */
  constructor(private http: HttpClient,
              private websocketService: WebsocketService) {
    const savedToken = localStorage.getItem('token');
    const savedUser = localStorage.getItem('user');

    if (savedToken) {
      this.tokenSubject.next(savedToken);
      this.validate(savedToken);
    }
    if (savedUser) this.userSubject.next(JSON.parse(savedUser));

    this.wss = this.websocketService.ws$.subscribe(message => {
      if(
        (message.model == 'user' && message.id == this.userSubject.value?.login) ||
        (message.model == 'userWipe' && message.id == this.userSubject.value?.login) ||
        (message.model == 'team' && message.id == this.userSubject.value?.team?.id.toString()) ||
        (message.model == 'role' && message.id == this.userSubject.value?.role?.id.toString())
      ) {
        this.initiateUpdate();
      }
    })
  }

  /**
   * Очищает подписку на WebSocket при уничтожении сервиса
   */
  ngOnDestroy() {
    this.wss.unsubscribe();
  }

  /**
   * Инициирует обновление данных текущего пользователя.
   *
   * Вызывается при получении WebSocket сообщения об изменении
   * связанных с пользователем данных (роль, команда и т.д.).
   */
  initiateUpdate() {
    this.validate(this.getToken() || '');
  }

  /**
   * Выполняет вход пользователя в систему.
   *
   * При успешной аутентификации:
   * - Сохраняет токен в localStorage и состояние
   * - Сохраняет данные пользователя
   *
   * @param loginRequest - Данные для входа (логин и пароль)
   * @returns Observable с JWT токеном и данными пользователя
   */
  login(loginRequest: JwtRequestDto): Observable<JwtResponseDto> {
    return this.http.post<JwtResponseDto>(`${this.apiUrl}/auth/login`, loginRequest).pipe(
      tap((response) => {
        this.setToken(response.token);
        this.setUser(response.user);
      }),
      catchError((error) => {
        console.error('Login error:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Валидирует токен и обновляет данные текущего пользователя.
   *
   * Отправляет запрос на сервер для получения актуальных данных пользователя.
   * При ошибке (невалидный токен) выполняет выход из системы.
   *
   * @param token - JWT токен для валидации
   */
  validate(token: string): void {
    this.http.get<User>(`${this.apiUrl}/users/current`,
      { headers: new HttpHeaders({
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        })
      }).pipe(catchError(error => {
        this.logout();
        return throwError(() => new Error('Требуется повторная авторизация'));
    })).subscribe(user => {
      this.setUser(user);
    });
  }

  /**
   * Сохраняет JWT токен в localStorage и состояние
   * 
   * @param token - JWT токен для сохранения
   */
  setToken(token: string) {
    localStorage.setItem('token', token);
    this.tokenSubject.next(token);
  }

  /**
   * Сохраняет данные пользователя в localStorage и состояние
   * 
   * @param user - Объект пользователя для сохранения
   */
  setUser(user: User) {
    localStorage.setItem('user', JSON.stringify(user));
    this.userSubject.next(user);
  }

  /**
   * Возвращает текущий JWT токен
   * 
   * @returns JWT токен или null если пользователь не авторизован
   */
  getToken(): string | null {
    return this.tokenSubject.value;
  }

  /**
   * Возвращает данные текущего пользователя
   * 
   * @returns Объект пользователя или null если не авторизован
   */
  getUser(): User | null {
    return this.userSubject.value;
  }

  /**
   * Выполняет выход пользователя из системы.
   *
   * Очищает JWT токен и данные пользователя из localStorage и состояния.
   */
  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.tokenSubject.next(null);
    this.userSubject.next(null);
  }
}
