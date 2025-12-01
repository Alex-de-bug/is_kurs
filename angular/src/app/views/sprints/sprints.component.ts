import {Component, OnDestroy, OnInit} from "@angular/core";
import {HeaderItemBinding} from "../../components/bindings/header-item.binding";
import {SprintsCalendarComponent} from "./sprints-calendar.component";
import {UiDropdownComponent} from "../../components/ui/ui-dropdown.component";
import {FullCalendarModule} from "@fullcalendar/angular";
import {PrimaryButtonBinding} from "../../components/bindings/primary-button.binding";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faPlus} from "@fortawesome/free-solid-svg-icons";
import {SprintsTableComponent} from "./sprints-table.component";
import { TeamService } from "../../services/server/team.service";
import { Team } from "../../models/team";
import { SprintService } from "../../services/server/sprint.service";
import { HttpErrorResponse } from "@angular/common/http";
import { AuthService } from "../../services/server/auth.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {CreateSprintModalComponent} from "./create-sprint/create-sprint-modal.component";
import {NgIf} from "@angular/common";
import {LoaderService} from "../../services/loader.service";
import {WebsocketService} from "../../services/websocket.service";
import {Subscription} from "rxjs";

/**
 * Компонент для управления спринтами.
 * 
 * Главный компонент раздела спринтов, предоставляющий:
 * - Два режима отображения: календарь и таблица
 * - Фильтрацию спринтов по командам
 * - Создание новых спринтов (для администраторов)
 * - Автоматическое обновление через WebSocket
 * - Сохранение предпочтений пользователя (режим отображения)
 * 
 * Режим отображения сохраняется в localStorage и восстанавливается
 * при следующем посещении страницы.
 */
@Component({
  selector: 'app-sprints',
  standalone: true,
  imports: [
    HeaderItemBinding,
    SprintsCalendarComponent,
    UiDropdownComponent,
    FullCalendarModule,
    PrimaryButtonBinding,
    FaIconComponent,
    SprintsTableComponent,
    NgIf
  ],
  templateUrl: './sprints.component.html'
})
export class SprintsComponent implements OnInit, OnDestroy {
  /** Флаг табличного режима отображения (false = календарь, true = таблица) */
  tableView = false;
  
  /** Список всех команд */
  teams: Team[] = [];
  
  /** Опции для выпадающего списка команд (название -> название) */
  teamOptions: { [key: string]: string } = {};
  
  /** Текущий авторизованный пользователь */
  currentUser = this.authService.getUser();

  /** Выбранная команда для фильтрации спринтов */
  _selectedTeam: string = '';

  /** Подписка на WebSocket сообщения */
  wss: Subscription;

  constructor(private teamService : TeamService,
              private sprintService: SprintService,
              private authService: AuthService,
              private modalService: NgbModal,
              private loaderService: LoaderService,
              private websocketService: WebsocketService,
  ) {
    this.authService.user$.subscribe(this.loadUserData.bind(this));
    this.loaderService.loader(true);
    this.wss = this.websocketService.ws$.subscribe(message => {
      if(message.model == 'sprints') {
        this.sprintService.initiateUpdate();
      }
      if(message.model == 'team') {
        this.loadTeams(this.teamOptions[this.selectedTeam]);
      }
    })
  }

  ngOnDestroy() {
    this.wss.unsubscribe();
  }

  /**
   * Загружает данные текущего пользователя.
   * 
   * Вызывается при изменении данных пользователя через AuthService.
   */
  loadUserData() {
    this.currentUser = this.authService.getUser();
  }

  /**
   * Проверяет, является ли текущий пользователь администратором
   * 
   * @returns true если пользователь - администратор (role.id === 1)
   */
  get isAdmin() : boolean {
    return this.currentUser && this.currentUser.role && this.currentUser.role.id === 1 || false;
  }

  /**
   * Устанавливает режим отображения спринтов.
   * 
   * Сохраняет выбор пользователя в localStorage.
   * 
   * @param view - true для табличного режима, false для календаря
   */
  setView(view: boolean) {
    this.tableView = view;
    localStorage.setItem("lastView", String(view));
  }

  /**
   * Геттер для выбранной команды
   * @returns Название выбранной команды
   */
  get selectedTeam(): string {
    return this._selectedTeam;
  }

  /**
   * Сеттер для выбранной команды.
   * 
   * Автоматически обновляет список спринтов при изменении.
   * 
   * @param value - Название команды для выбора
   */
  set selectedTeam(value: string) {
    if(value !== this._selectedTeam && value != null) {
      this._selectedTeam = value;
      setTimeout(this.sprintService.initiateUpdate.bind(this.sprintService), 0);
    }
  }

  /**
   * Инициализирует компонент.
   * 
   * - Восстанавливает режим отображения из localStorage
   * - Загружает список команд
   */
  ngOnInit() {
    const lastView = localStorage.getItem("lastView");
    this.tableView = lastView === "true";

    this.loadTeams();
  }

  /**
   * Загружает список команд с сервера.
   * 
   * При первой загрузке автоматически выбирает команду пользователя.
   * Если передан параметр preselected, выбирает указанную команду.
   * 
   * @param preselected - Название команды для предварительного выбора (опционально)
   */
  loadTeams(preselected?: string) {
    const user = this.authService.getUser();
    this.teamService.getAllTeams(true).subscribe({
      next: (teams) => {
        if (!(teams instanceof HttpErrorResponse)) {
          this.teams = teams;
          this.teamOptions = teams.reduce((acc, team) => {
            acc[team.name] = team.name;
            return acc;
          }, {} as { [key: string]: string });

          if (user && !preselected) {
            this.selectedTeam = this.teams.find(
              team => team.id.toString() === user?.team?.id?.toString() || ''
            )?.name || this.teams[0]?.name || '';
          }
          if(preselected) {
            this.selectedTeam = this.teams.find(team => team.name === preselected)?.name || this.teams[0]?.name || '';
          }
        }
      },
      error: (error) => {
        console.error('Error loading teams:', error);
        this.teams = [];
        this.teamOptions = {};
      }
    });
  }

  protected readonly faPlus = faPlus;

  openCreateModal() {
    this.modalService.open(CreateSprintModalComponent, {
      size: 'lg'
    });
  }
}
