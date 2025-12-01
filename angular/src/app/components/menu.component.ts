import {Component, OnInit} from "@angular/core";
import {initFlowbite} from "flowbite";
import {SideItemBinding} from "./bindings/side-item.binding";
import {
  faBolt,
  faCalendar,
  faChartSimple,
  faLightbulb,
  faListCheck,
  faStar,
  faUser, faUsers, faPaperclip
} from "@fortawesome/free-solid-svg-icons";
import {AuthService} from "../services/server/auth.service";
import {User} from "../models/user";
import {AsyncPipe, NgIf} from "@angular/common";
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {RouterLink} from "@angular/router";
import {CreateUserModalComponent} from "../views/users/create-user/create-user-modal.component";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";

/**
 * Компонент бокового меню навигации приложения.
 *
 * Главное меню приложения, которое предоставляет:
 * - Навигацию по разделам системы (Задачи, Спринты, Пользователи и т.д.)
 * - Отображение информации о текущем пользователе
 * - Доступ к настройкам профиля
 * - Функцию выхода из системы
 *
 * Использует Flowbite для визуальных эффектов и анимаций.
 * Автоматически обновляет данные пользователя при изменениях через AuthService.
 */
@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [
    SideItemBinding,
    AsyncPipe,
    NgIf,
    FontAwesomeModule,
    RouterLink
  ],
  templateUrl: './menu.component.html',
})
export class MenuComponent implements OnInit {
  /** Данные текущего авторизованного пользователя */
  currentUser: User | null = null;

  /**
   * Создает экземпляр MenuComponent
   * 
   * @param authService - Сервис аутентификации для доступа к данным пользователя
   * @param modalService - Сервис NgBootstrap для открытия модальных окон
   */
  constructor(private authService: AuthService,
              private modalService: NgbModal) {}

  /**
   * Инициализирует компонент при создании.
   *
   * - Инициализирует Flowbite для UI эффектов
   * - Загружает данные текущего пользователя
   * - Подписывается на изменения данных пользователя
   */
  ngOnInit(): void {
    initFlowbite();
    this.loadUserData();
    this.authService.user$.subscribe(this.loadUserData.bind(this));
  }

  /**
   * Загружает данные текущего пользователя из AuthService.
   *
   * Вызывается при инициализации и при изменении данных пользователя.
   *
   * @private
   */
  private loadUserData(): void {
    this.currentUser = this.authService.getUser();
  }

  /**
   * Открывает модальное окно настроек пользователя.
   *
   * Позволяет пользователю редактировать свой профиль:
   * - Имя и фамилия
   * - Пароль
   * - Другие личные данные
   */
  openSettings() {
    const modalRef = this.modalService.open(CreateUserModalComponent, { size: 'lg' });
    modalRef.componentInstance.user = this.currentUser;
  }

  /**
   * Выполняет выход пользователя из системы.
   *
   * Очищает токен авторизации и данные пользователя,
   * перенаправляя на страницу входа.
   */
  logout(): void {
    this.authService.logout();
  }

  protected readonly faCalendar = faCalendar;
  protected readonly faListCheck = faListCheck;
  protected readonly faUser = faUser;
  protected readonly faLightbulb = faLightbulb;
  protected readonly faChartSimple = faChartSimple;
  protected readonly faStar = faStar;
  protected readonly faBolt = faBolt;
  protected readonly faUsers = faUsers;
  protected readonly faPaperclip = faPaperclip;
}
