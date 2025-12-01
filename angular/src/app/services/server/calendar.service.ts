import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {MessageDto} from "../../models/dto/message-dto";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {ApiService} from "./api.service";
import {catchError} from "rxjs/operators";

/**
 * Сервис для получения календаря рабочих/нерабочих дней.
 */
@Injectable({
  providedIn: 'root'
})
export class CalendarService {
  constructor(private http: HttpClient, private apiService: ApiService) {}

  /**
   * Возвращает календарь за указанный год.
   *
   * Использует localStorage в качестве простого кэша.
   *
   * @param year Год, для которого запрашивается календарь
   * @returns DTO с данными календаря или ошибку
   */
  getCalendar(year: number): Observable<MessageDto | HttpErrorResponse> {
    const cachedData = localStorage.getItem(`calendar${year}`);
    if (cachedData) {
      return new Observable<MessageDto>(observer => observer.next(JSON.parse(cachedData)));
    }
    return this.http.get<MessageDto>(`${this.apiService.apiUrl}/calendar`, { params: { year: year.toString() }, headers: this.apiService.getHeaders() }).pipe(
      catchError(this.apiService.handleError)
    );
  }
}
