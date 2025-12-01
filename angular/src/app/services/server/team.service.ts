import {Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {ApiService} from "./api.service";
import {catchError} from "rxjs/operators";
import {ObjectDto} from "../../models/dto/object-dto";
import {Team} from "../../models/team";

/**
 * Сервис для управления командами разработки.
 */
@Injectable({
  providedIn: 'root'
})
export class TeamService {
  private teamSubject = new Subject<{}>();
  team$ = this.teamSubject.asObservable();

  constructor(private http: HttpClient, private apiService: ApiService) {}

  /**
   * Сигнализирует подписчикам об изменении данных команд.
   */
  initiateUpdate() {
    this.teamSubject.next({});
  }

  /**
   * Получает список команд.
   *
   * @param onlyActive Флаг фильтрации только по активным командам
   */
  getAllTeams(onlyActive: boolean): Observable<Team[] | HttpErrorResponse> {
    return this.http.get<Team[]>(`${this.apiService.apiUrl}/teams`, {params: {onlyActive: onlyActive?.toString()}, headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }

  /**
   * Создает новую команду.
   *
   * @param team Данные команды
   */
  createTeam(team: Team): Observable<Team | HttpErrorResponse> {
    return this.http.post<Team>(`${this.apiService.apiUrl}/teams`, team, { headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }

  /**
   * Обновляет существующую команду.
   *
   * @param id Идентификатор команды
   * @param updatedTeam Обновлённые данные команды
   */
  updateTeam(id: number, updatedTeam: Team): Observable<Team | HttpErrorResponse> {
    return this.http.put<Team>(`${this.apiService.apiUrl}/teams/${id}`, updatedTeam, { headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }

  /**
   * Возвращает загрузку команды в рамках спринта.
   *
   * @param teamId Идентификатор команды
   * @param sprintId Идентификатор спринта
   */
  getTeamLoad(teamId: number, sprintId: number): Observable<ObjectDto | HttpErrorResponse> {
    return this.http.get<ObjectDto>(`${this.apiService.apiUrl}/teams/load`, { params: { teamId: teamId.toString(), sprintId: sprintId.toString() }, headers: this.apiService.getHeaders() }).pipe(catchError(this.apiService.handleError));
  }

  /**
   * Удаляет команду.
   *
   * @param id Идентификатор команды
   */
  deleteTeam(id: number): Observable<any | HttpErrorResponse> {
    return this.http.delete<any>(`${this.apiService.apiUrl}/teams/${id}`, { headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }
}
