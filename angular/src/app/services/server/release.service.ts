import {Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {ApiService} from "./api.service";
import {catchError} from "rxjs/operators";
import {Release} from "../../models/release";
import {ReleaseDto} from "../../models/dto/release-dto";
import {Page} from "../../models/misc/page";

/**
 * Сервис для управления релизами.
 *
 * Работает с релизами как со связанными с спринтами сущностями.
 */
@Injectable({
  providedIn: 'root'
})
export class ReleaseService {
  private releaseSubject = new Subject<{}>();
  release$ = this.releaseSubject.asObservable();

  constructor(private http: HttpClient, private apiService: ApiService) {}

  /**
   * Сигнализирует подписчикам о том, что данные по релизам нужно обновить.
   */
  initiateUpdate() {
    this.releaseSubject.next({});
  }

  /**
   * Получает страницу всех релизов.
   *
   * @param page Номер страницы (0‑based)
   */
  getAllReleases(page: number = 0): Observable<Page<Release> | HttpErrorResponse> {
    return this.http.get<Page<Release>>(`${this.apiService.apiUrl}/releases`, { params: {page: page.toString()}, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Получает релизы, относящиеся к конкретному спринту.
   *
   * @param sprintId Идентификатор спринта
   * @param page Номер страницы (0‑based)
   */
  getReleasesBySprint(sprintId: number, page: number = 0): Observable<Page<Release> | HttpErrorResponse> {
    return this.http.get<Page<Release>>(`${this.apiService.apiUrl}/releases/sprint/${sprintId}`, { params: {page: page.toString()}, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Создает новый релиз.
   *
   * @param releaseDto Данные нового релиза
   */
  createRelease(releaseDto: ReleaseDto): Observable<Release | HttpErrorResponse> {
    return this.http.post<Release>(`${this.apiService.apiUrl}/releases`, releaseDto, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Обновляет существующий релиз.
   *
   * @param id Идентификатор релиза
   * @param releaseDto Обновленные данные релиза
   */
  updateRelease(id: number, releaseDto: ReleaseDto): Observable<Release | HttpErrorResponse> {
    return this.http.put<Release>(`${this.apiService.apiUrl}/releases/${id}`, releaseDto, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Удаляет релиз.
   *
   * @param id Идентификатор релиза
   */
  deleteRelease(id: number): Observable<any | HttpErrorResponse> {
    return this.http.delete<any>(`${this.apiService.apiUrl}/releases/${id}`, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }
}
