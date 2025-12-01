import {Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {ApiService} from "./api.service";
import {catchError} from "rxjs/operators";
import {Sprint} from "../../models/sprint";
import {SprintDto} from "../../models/dto/sprint-dto";
import {SprintTeamDto} from "../../models/dto/sprint-team-dto";
import {UserStoryPointsDto} from "../../models/dto/user-story-points-dto";
import {Release} from "../../models/release";
import {Page} from "../../models/misc/page";

/**
 * Сервис для управления спринтами.
 *
 * Поддерживает CRUD операции, фильтрацию по году и команде,
 * получение сторипоинтов и релизов спринта.
 */
@Injectable({
  providedIn: 'root'
})
export class SprintService {
  private sprintSubject = new Subject<{}>();
  sprint$ = this.sprintSubject.asObservable();

  constructor(private http: HttpClient, private apiService: ApiService) {}

  /**
   * Сигнализирует подписчикам об изменении данных спринтов.
   */
  initiateUpdate() {
    this.sprintSubject.next({});
  }

  /**
   * Получает список спринтов с пагинацией и опциональными фильтрами.
   *
   * @param page Номер страницы (0‑based)
   * @param majorVersion Фильтр по версии спринта
   * @param teamId Фильтр по команде
   */
  getAllSprints(page: number = 0, majorVersion?: string, teamId?: number): Observable<Page<Sprint> | HttpErrorResponse> {
    let params: { page: string, majorVersion?: string, teamId?: string } = { page: page.toString() };
    if(majorVersion) params.majorVersion = majorVersion;
    if(teamId) params.teamId = teamId.toString();

    return this.http.get<Page<Sprint>>(`${this.apiService.apiUrl}/sprints`, { params, headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }

  /**
   * Получает спринт по идентификатору.
   *
   * @param id Идентификатор спринта
   */
  getSprint(id: number): Observable<Sprint | HttpErrorResponse> {
    return this.http.get<Sprint>(`${this.apiService.apiUrl}/sprints/${id}`, { headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }

  /**
   * Создает новый спринт.
   *
   * @param sprintDto Данные нового спринта
   */
  createSprint(sprintDto: SprintDto): Observable<Sprint | HttpErrorResponse> {
    return this.http.post<Sprint>(`${this.apiService.apiUrl}/sprints`, sprintDto, { headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }


  /**
   * Обновляет существующий спринт.
   *
   * @param id Идентификатор спринта
   * @param updatedSprint Обновлённые данные спринта
   */
  updateSprint(id: number, updatedSprint: SprintDto): Observable<Sprint | HttpErrorResponse> {
    return this.http.put<Sprint>(`${this.apiService.apiUrl}/sprints/${id}`, updatedSprint, { headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }

  /**
   * Удаляет спринт.
   *
   * @param id Идентификатор спринта
   */
  deleteSprint(id: number): Observable<any | HttpErrorResponse> {
    return this.http.delete<any>(`${this.apiService.apiUrl}/sprints/${id}`, { headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }

  /**
   * Получает спринты по году и названию команды.
   *
   * @param year Год
   * @param teamName Название команды
   */
  getSprintsByYearAndTeam(year: number, teamName: string): Observable<SprintTeamDto[] | HttpErrorResponse> {
    return this.http.get<SprintTeamDto[]>(`${this.apiService.apiUrl}/sprints/filtered`, { params: { year: year.toString(), teamName: teamName }, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }


  /**
   * Возвращает количество сторипоинтов по пользователям в спринте.
   *
   * @param sprintId Идентификатор спринта
   */
  getStoryPointsPerUser(sprintId: number): Observable<UserStoryPointsDto[] | HttpErrorResponse> {
    return this.http.get<UserStoryPointsDto[]>(`${this.apiService.apiUrl}/sprints/${sprintId}/story-points`, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }


  /**
   * Возвращает список релизов, связанных со спринтом.
   *
   * @param id Идентификатор спринта
   */
  getSprintReleases(id: number): Observable<Release[] | HttpErrorResponse> {
    return this.http.get<Release[]>(`${this.apiService.apiUrl}/sprints/${id}/releases`, { headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }
}
