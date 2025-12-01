import {Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {ApiService} from "./api.service";
import {catchError} from "rxjs/operators";
import {User} from "../../models/user";
import {UserDto} from "../../models/dto/user-dto";
import {Page} from "../../models/misc/page";

/**
 * Сервис для управления пользователями.
 */
@Injectable({
  providedIn: 'root'
})
export class UserService {
  private userSubject = new Subject<{}>();
  user$ = this.userSubject.asObservable();

  constructor(private http: HttpClient, private apiService: ApiService) {}

  /**
   * Сигнализирует подписчикам об изменении данных пользователей.
   */
  initiateUpdate() {
    this.userSubject.next({});
  }

  /**
   * Получает список пользователей с пагинацией и фильтрами.
   *
   * @param page Номер страницы (0‑based)
   * @param login Фильтр по логину
   * @param team Фильтр по идентификатору команды
   * @param onlyActive Показывать только активных пользователей
   */
  getAllUsers(page: number = 0, login: string, team: number = 0, onlyActive: boolean | null = true): Observable<Page<User> | HttpErrorResponse> {
    let params : { page: string, login: string, team: number, onlyActive?: boolean } = { page: page.toString(), login, team};
    if(onlyActive) params.onlyActive = onlyActive;

    return this.http.get<Page<User>>(`${this.apiService.apiUrl}/users`, {params, headers: this.apiService.getHeaders()}).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Получает данные текущего пользователя.
   */
  getCurrentUser(): Observable<User | HttpErrorResponse> {
    return this.http.get<User>(`${this.apiService.apiUrl}/users/current`, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Регистрирует нового пользователя.
   *
   * @param userDto Данные пользователя
   */
  registerUser(userDto: UserDto): Observable<User | HttpErrorResponse> {
    return this.http.post<User>(`${this.apiService.apiUrl}/users/register`, userDto, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Обновляет роль пользователя.
   *
   * @param login Логин пользователя
   * @param roleId Идентификатор роли
   */
  updateUserRole(login: string, roleId: number): Observable<User | HttpErrorResponse> {
    return this.http.put<User>(`${this.apiService.apiUrl}/users/${login}/role`, null, { params: {roleId: roleId.toString()}, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }


  /**
   * Обновляет команду пользователя.
   *
   * @param login Логин пользователя
   * @param teamId Идентификатор команды
   */
  updateUserTeam(login: string, teamId: number): Observable<User | HttpErrorResponse> {
    let params = {teamId: teamId.toString()};

    return this.http.put<User>(`${this.apiService.apiUrl}/users/${login}/team`, null, { params, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }


  /**
   * Обновляет данные пользователя.
   *
   * @param login Логин пользователя
   * @param userDto Обновлённые данные
   */
  updateUser(login: string, userDto: UserDto): Observable<User | HttpErrorResponse> {
    return this.http.put<User>(`${this.apiService.apiUrl}/users/${login}`, userDto, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Полностью удаляет пользователя.
   *
   * @param login Логин пользователя
   */
  wipeUser(login: string): Observable<void | HttpErrorResponse> {
    return this.http.delete<void>(`${this.apiService.apiUrl}/users/${login}`, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }
}
