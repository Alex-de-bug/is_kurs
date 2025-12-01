import {Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {ApiService} from "./api.service";
import {catchError} from "rxjs/operators";
import {Risk} from "../../models/risk";
import {TopRiskDto} from "../../models/dto/top-risk-dto";
import {Page} from "../../models/misc/page";
import { RiskDto } from "../../models/dto/risk-dto";

/**
 * Сервис для управления рисками проекта.
 *
 * Поддерживает CRUD операции, привязку рисков к задачам и идеям,
 * а также получение топ‑10 наиболее критичных рисков.
 */
@Injectable({
  providedIn: 'root'
})
export class RiskService {
  private riskSubject = new Subject<{}>();
  risk$ = this.riskSubject.asObservable();
  
  constructor(private http: HttpClient, private apiService: ApiService) {}

  /**
   * Сигнализирует подписчикам о том, что данные рисков обновились.
   */
  initiateUpdate() {
    this.riskSubject.next({});
  }

  /**
   * Получает список рисков с пагинацией и фильтром по описанию.
   *
   * @param page Номер страницы (0‑based)
   * @param description Подстрока для поиска в описании риска
   */
  getAllRisks(page: number = 0, description: string): Observable<Page<Risk> | HttpErrorResponse> {
    let params : { page: string, description: string } = { page: page.toString(), description};
    return this.http.get<Page<Risk>>(`${this.apiService.apiUrl}/risks`, { params, headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }

  /**
   * Создает новый риск.
   *
   * @param risk DTO с данными риска
   */
  createRisk(risk: RiskDto): Observable<Risk | HttpErrorResponse> {
    return this.http.post<Risk>(`${this.apiService.apiUrl}/risks`, risk, { headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }

  /**
   * Обновляет существующий риск.
   *
   * @param id Идентификатор риска
   * @param updatedRisk Обновленные данные риска
   */
  updateRisk(id: number, updatedRisk: RiskDto): Observable<Risk | HttpErrorResponse> {
    return this.http.put<Risk>(`${this.apiService.apiUrl}/risks/${id}`, updatedRisk, { headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }

  /**
   * Удаляет риск.
   *
   * @param id Идентификатор риска
   */
  deleteRisk(id: number): Observable<any | HttpErrorResponse> {
    return this.http.delete<any>(`${this.apiService.apiUrl}/risks/${id}`, { headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }

  /**
   * Привязывает риск к задаче.
   *
   * @param taskId Идентификатор задачи
   * @param riskId Идентификатор риска
   */
  addRiskToTask(taskId: number, riskId: number): Observable<any | HttpErrorResponse> {
    return this.http.post<any>(`${this.apiService.apiUrl}/risks/task/${taskId}`, null, { params: {riskId}, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Удаляет привязку риска к задаче.
   *
   * @param taskId Идентификатор задачи
   * @param riskId Идентификатор риска
   */
  removeRiskFromTask(taskId: number, riskId: number): Observable<any | HttpErrorResponse> {
    return this.http.delete<any>(`${this.apiService.apiUrl}/risks/task/${taskId}`, { params: {riskId}, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Получает все риски, привязанные к задаче.
   *
   * @param taskId Идентификатор задачи
   */
  getRisksForTask(taskId: number): Observable<Risk[] | HttpErrorResponse> {
    return this.http.get<Risk[]>(`${this.apiService.apiUrl}/risks/task/${taskId}`, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Привязывает риск к идее.
   *
   * @param ideaId Идентификатор идеи
   * @param riskId Идентификатор риска
   */
  addRiskToIdea(ideaId: number, riskId: number): Observable<any | HttpErrorResponse> {
    return this.http.post<any>(`${this.apiService.apiUrl}/risks/idea/${ideaId}`, null, { params: {riskId}, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Удаляет привязку риска к идее.
   *
   * @param ideaId Идентификатор идеи
   * @param riskId Идентификатор риска
   */
  removeRiskFromIdea(ideaId: number, riskId: number): Observable<any | HttpErrorResponse> {
    return this.http.delete<any>(`${this.apiService.apiUrl}/risks/idea/${ideaId}`, { params: {riskId}, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }


  /**
   * Получает все риски, привязанные к идее.
   *
   * @param ideaId Идентификатор идеи
   */
  getRisksForIdea(ideaId: number): Observable<Risk[] | HttpErrorResponse> {
    return this.http.get<Risk[]>(`${this.apiService.apiUrl}/risks/idea/${ideaId}`, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Возвращает список из топ‑10 наиболее критичных рисков.
   */
  getTop10Risks(): Observable<TopRiskDto[] | HttpErrorResponse> {
    return this.http.get<TopRiskDto[]>(`${this.apiService.apiUrl}/risks/top10`, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }
}
