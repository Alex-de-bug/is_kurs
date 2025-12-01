import {Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {ApiService} from "./api.service";
import {catchError} from "rxjs/operators";
import {Tag} from "../../models/tag";
import { TagDto } from "../../models/dto/tag-dto";

/**
 * Сервис для управления тегами задач.
 */
@Injectable({
  providedIn: 'root'
})
export class TagService {
  private statusSubject = new Subject<{}>();
  tag$ = this.statusSubject.asObservable();

  constructor(private http: HttpClient, private apiService: ApiService) {}

  /**
   * Сигнализирует подписчикам об изменении данных тегов.
   */
  initiateUpdate() {
    this.statusSubject.next({});
  }

  /**
   * Получает все теги.
   */
  getAllTags(): Observable<Tag[] | HttpErrorResponse> {
    return this.http.get<Tag[]>(`${this.apiService.apiUrl}/tags`, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Создает новый тег.
   *
   * @param tag DTO тега
   */
  createTag(tag: TagDto): Observable<Tag | HttpErrorResponse> {
    return this.http.post<Tag>(`${this.apiService.apiUrl}/tags`, tag, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Обновляет существующий тег.
   *
   * @param id Идентификатор тега
   * @param updatedTag Обновлённые данные тега
   */
  updateTag(id: number, updatedTag: TagDto): Observable<Tag | HttpErrorResponse> {
    return this.http.put<Tag>(`${this.apiService.apiUrl}/tags/${id}`, updatedTag, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Удаляет тег.
   *
   * @param id Идентификатор тега
   */
  deleteTag(id: number): Observable<any | HttpErrorResponse> {
    return this.http.delete<any>(`${this.apiService.apiUrl}/tags/${id}`, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Добавляет тег к задаче.
   *
   * @param taskId Идентификатор задачи
   * @param tagId Идентификатор тега
   */
  addTagToTask(taskId: number, tagId: number): Observable<any | HttpErrorResponse> {
    return this.http.post<any>(`${this.apiService.apiUrl}/tags/task/${taskId}`, null, { params: { tagId }, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Удаляет тег из задачи.
   *
   * @param taskId Идентификатор задачи
   * @param tagId Идентификатор тега
   */
  removeTagFromTask(taskId: number, tagId: number): Observable<any | HttpErrorResponse> {
    return this.http.delete<any>(`${this.apiService.apiUrl}/tags/task/${taskId}`, { params: { tagId }, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Получает список тегов задачи.
   *
   * @param taskId Идентификатор задачи
   */
  getTagsForTask(taskId: number): Observable<Tag[] | HttpErrorResponse> {
    return this.http.get<Tag[]>(`${this.apiService.apiUrl}/tags/task/${taskId}`, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }
}
