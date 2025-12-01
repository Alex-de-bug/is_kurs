import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {ApiService} from "./api.service";
import {catchError} from "rxjs/operators";
import {Task} from "../../models/task";
import {TaskDto} from "../../models/dto/task-dto";
import {Page} from "../../models/misc/page";

/**
 * Сервис для управления задачами (tasks).
 *
 * Предоставляет методы для:
 * - CRUD операций с задачами
 * - Фильтрации и пагинации списка задач
 * - Назначения исполнителей
 * - Изменения статусов задач
 * - Привязки задач к спринтам
 *
 * Все методы возвращают Observable с данными или HttpErrorResponse в случае ошибки.
 */
@Injectable({
  providedIn: 'root'
})
export class TaskService {
  /**
   * Создает экземпляр TaskService
   * 
   * @param http - HttpClient для выполнения HTTP запросов
   * @param apiService - Базовый API сервис для заголовков и обработки ошибок
   */
  constructor(private http: HttpClient, private apiService: ApiService) {}

  /**
   * Получает список задач с пагинацией и фильтрацией
   * 
   * @param page - Номер страницы (начиная с 0)
   * @param statusId - Фильтр по статусу (опционально)
   * @param implementerLogin - Фильтр по логину исполнителя (опционально)
   * @param sprintId - Фильтр по спринту (опционально)
   * @param tagId - Фильтр по тегу (опционально)
   * @returns Observable со страницей задач или ошибкой
   */
  getAllTasks(page: number = 0,
              statusId?: number,
              implementerLogin?: string,
              sprintId?: number,
              tagId?: number
  ): Observable<Page<Task> | HttpErrorResponse> {
    let params: { page: string, statusId?: string, implementerLogin?: string, sprintId?: string, tagId?: string } = { page: page.toString() };
    if(statusId) params.statusId = statusId.toString();
    if(implementerLogin) params.implementerLogin = implementerLogin;
    if(sprintId) params.sprintId = sprintId.toString();
    if(tagId) params.tagId = tagId.toString();

    return this.http.get<Page<Task>>(`${this.apiService.apiUrl}/tasks`, { params, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Получает задачу по идентификатору
   * 
   * @param id - Идентификатор задачи
   * @returns Observable с задачей или ошибкой
   */
  getTask(id: number) : Observable<Task | HttpErrorResponse>{
    return this.http.get<Task>(`${this.apiService.apiUrl}/tasks/${id}`, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Создает новую задачу
   * 
   * @param taskDto - Данные для создания задачи
   * @returns Observable с созданной задачей или ошибкой
   */
  createTask(taskDto: TaskDto): Observable<Task | HttpErrorResponse> {
    return this.http.post<Task>(`${this.apiService.apiUrl}/tasks`, taskDto, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Обновляет существующую задачу
   * 
   * @param id - Идентификатор задачи
   * @param updatedTask - Обновленные данные задачи
   * @returns Observable с обновленной задачей или ошибкой
   */
  updateTask(id: number, updatedTask: TaskDto): Observable<Task | HttpErrorResponse> {
    return this.http.put<Task>(`${this.apiService.apiUrl}/tasks/${id}`, updatedTask, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Удаляет задачу
   * 
   * @param id - Идентификатор задачи для удаления
   * @returns Observable с результатом или ошибкой
   */
  deleteTask(id: number): Observable<any | HttpErrorResponse> {
    return this.http.delete<any>(`${this.apiService.apiUrl}/tasks/${id}`, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Назначает исполнителя на задачу
   * 
   * @param taskId - Идентификатор задачи
   * @param implementerLogin - Логин исполнителя
   * @returns Observable с обновленной задачей или ошибкой
   */
  assignImplementer(taskId: number, implementerLogin: string): Observable<Task | HttpErrorResponse> {
    return this.http.put<Task>(`${this.apiService.apiUrl}/tasks/${taskId}/implementer`, null, { params: { implementerLogin }, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Обновляет статус задачи.
   *
   * Используется для перемещения задачи между колонками на доске (To Do, In Progress, Done и т.д.).
   *
   * @param taskId - Идентификатор задачи
   * @param statusId - Идентификатор нового статуса
   * @returns Observable с обновленной задачей или ошибкой
   */
  updateTaskStatus(taskId: number, statusId: number): Observable<Task | HttpErrorResponse> {
    return this.http.put<Task>(`${this.apiService.apiUrl}/tasks/${taskId}/status`, null, { params: { statusId: statusId.toString() }, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Привязывает задачу к спринту
   * 
   * @param taskId - Идентификатор задачи
   * @param sprintId - Идентификатор спринта
   * @returns Observable с обновленной задачей или ошибкой
   */
  assignSprintToTask(taskId: number, sprintId: number): Observable<Task | HttpErrorResponse> {
    return this.http.put<Task>(`${this.apiService.apiUrl}/tasks/${taskId}/sprint`, null, { params: { sprintId: sprintId.toString() }, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }
}
