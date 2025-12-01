import { Component, EventEmitter, Input, Output } from '@angular/core';
import {faArrowLeft, faArrowRight, faPlus} from '@fortawesome/free-solid-svg-icons';
import {NgForOf, NgIf} from "@angular/common";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {UiButtonComponent} from "../ui/ui-button.component";
import {PageInfo} from "../../models/misc/page";

/**
 * Универсальный компонент таблицы с пагинацией.
 * 
 * Переиспользуемый компонент для отображения табличных данных с поддержкой:
 * - Динамических колонок
 * - Пагинации с умной навигацией
 * - Кнопки создания новых элементов
 * - Настраиваемой высоты
 * - Заголовка таблицы
 * 
 * Использует content projection для вставки строк таблицы через ng-content.
 */
@Component({
  selector: 'app-table-component',
  templateUrl: './table.component.html',
  standalone: true,
  imports: [
    NgIf,
    FaIconComponent,
    NgForOf,
    UiButtonComponent
  ],
  styleUrls: ['table.component.css']
})
export class TableComponent {
  /** Включает отображение кнопки создания */
  @Input() creationEnabled = false;
  
  /** Текст на кнопке создания */
  @Input() creationText = 'Создать';
  
  /** Массив заголовков колонок */
  @Input() columns: string[] = [];
  
  /** Текст заголовка таблицы */
  @Input() headerText: string | null = null;
  
  /** Максимальная высота таблицы (с прокруткой) */
  @Input() maxHeight: string | null = null;

  /** Информация о пагинации (текущая страница, общее количество) */
  @Input() pageInfo: PageInfo | null = null;

  /** Событие нажатия на кнопку создания */
  @Output() creationClick = new EventEmitter<void>();
  
  /** Событие изменения страницы */
  @Output() pageChange = new EventEmitter<number>();

  faPlus = faPlus;

  /** Текущая отображаемая страница */
  currentPage = 0;

  /**
   * Обновляет текущую страницу при изменении входных данных
   */
  ngOnChanges() {
    if (this.pageInfo) {
      this.currentPage = this.pageInfo.number;
    }
  }

  /**
   * Обрабатывает переход на другую страницу.
   * 
   * Проверяет валидность номера страницы и эмитит событие pageChange.
   * 
   * @param newPage - Номер новой страницы или строка '...' (игнорируется)
   */
  changePage(newPage: number | string) {
    if(typeof newPage === 'string') return;
    newPage = Number(newPage);
    if (newPage >= 0 && newPage < this.pageInfo!.totalPages) {
      this.currentPage = newPage;
      this.pageChange.emit(this.currentPage);
    }
  }

  /**
   * Преобразует номер страницы для отображения (добавляет 1)
   * 
   * @param page - Номер страницы (0-based) или строка '...'
   * @returns Строка с номером страницы для отображения (1-based)
   */
  addOne(page: number | string) : string {
    if(typeof page === 'string') return page;
    page = Number(page);
    return (page + 1).toString();
  }

  /**
   * Вычисляет массив номеров страниц для отображения в пагинаторе.
   * 
   * Реализует "умную" пагинацию:
   * - Показывает первую и последнюю страницы
   * - Показывает текущую страницу и соседние
   * - Использует '...' для пропуска промежуточных страниц
   * 
   * @returns Массив номеров страниц и символов '...' для сокращения
   */
  get pages(): (number | string)[] {
    if (!this.pageInfo) {
      return [];
    }

    const totalPages = this.pageInfo.totalPages - 1;

    if (totalPages <= 4) {
      return Array.from({ length: totalPages + 1 }, (_, i) => i);
    }

    const currentPage = this.currentPage;
    const pages: (number | string)[] = [];

    pages.push(0);
    if(currentPage + 1 > 2)
      pages.push('...');

    let start = currentPage;
    let end = currentPage + 2;

    if (start < 1) {
      start = 1;
      end = 2;
    }
    if (end >= totalPages) {
      start = totalPages - 3;
      end = totalPages;
    }

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    if(currentPage + 1 < totalPages - 2)
      pages.push('...');
    if(!pages.includes(totalPages))
      pages.push(totalPages);

    return pages;
  }

  protected readonly Math = Math;
  protected readonly faArrowRight = faArrowRight;
  protected readonly faArrowLeft = faArrowLeft;
}
