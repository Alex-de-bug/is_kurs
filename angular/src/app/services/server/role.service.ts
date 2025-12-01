import {Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {ApiService} from "./api.service";
import {catchError} from "rxjs/operators";
import {Role} from "../../models/role";
import {Status} from "../../models/status";
import { RoleDto } from "../../models/dto/role-dto";

/**
 * Сервис для управления ролями пользователей и их доступными статусами задач.
 */
@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private roleSubject = new Subject<{}>();
  role$ = this.roleSubject.asObservable();

  constructor(private http: HttpClient, private apiService: ApiService) {}

  /**
   * Сигнализирует подписчикам об изменении данных ролей.
   */
  initiateUpdate() {
    this.roleSubject.next({});
  }

  /**
   * Получает все роли.
   */
  getAllRoles(): Observable<Role[] | HttpErrorResponse> {
    return this.http.get<Role[]>(`${this.apiService.apiUrl}/roles`, { headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }

  /**
   * Создает новую роль.
   *
   * @param role Данные роли
   */
  createRole(role: RoleDto): Observable<Role | HttpErrorResponse> {
    return this.http.post<Role>(`${this.apiService.apiUrl}/roles`, role, { headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }

  /**
   * Обновляет существующую роль.
   *
   * @param id Идентификатор роли
   * @param updatedRole Обновлённые данные роли
   */
  updateRole(id: number, updatedRole: RoleDto): Observable<Role | HttpErrorResponse> {
    return this.http.put<Role>(`${this.apiService.apiUrl}/roles/${id}`, updatedRole, { headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }


  /**
   * Удаляет роль.
   *
   * @param id Идентификатор роли
   */
  deleteRole(id: number): Observable<any | HttpErrorResponse> {
    return this.http.delete<any>(`${this.apiService.apiUrl}/roles/${id}`, { headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }

  /**
   * Получает список статусов, доступных для указанной роли.
   *
   * @param id Идентификатор роли
   */
  getRoleStatuses(id: number): Observable<Status[] | HttpErrorResponse> {
    return this.http.get<Status[]>(`${this.apiService.apiUrl}/roles/${id}/statuses`, { headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }

  /**
   * Добавляет статус в список разрешённых для роли.
   *
   * @param roleId Идентификатор роли
   * @param statusId Идентификатор статуса
   */
  addRoleStatus(roleId: number, statusId: number): Observable<any | HttpErrorResponse> {
    return this.http.post<any>(`${this.apiService.apiUrl}/roles/${roleId}/statuses`, null, { params: {statusId}, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }


  /**
   * Удаляет статус из списка разрешённых для роли.
   *
   * @param roleId Идентификатор роли
   * @param statusId Идентификатор статуса
   */
  deleteRoleStatus(roleId: number, statusId: number): Observable<any | HttpErrorResponse> {
    return this.http.delete<any>(`${this.apiService.apiUrl}/roles/${roleId}/statuses`, { params: {statusId}, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }
}
