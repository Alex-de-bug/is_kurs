import {Component, Input} from "@angular/core";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {UiButtonComponent} from "../ui/ui-button.component";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faClose, faExclamationTriangle} from "@fortawesome/free-solid-svg-icons";
import {NgIf} from "@angular/common";

/**
 * Универсальный компонент модального окна подтверждения.
 * 
 * Переиспользуемый компонент для отображения диалогов подтверждения действий:
 * - Удаление элементов
 * - Подтверждение критичных операций
 * - Предупреждения пользователю
 * 
 * Настраивается через входные параметры:
 * - Иконка и цвета кнопок
 * - Основной текст и предупреждение
 * 
 * Возвращает результат через promise при закрытии модального окна.
 */
@Component({
  selector: 'app-delete-modal',
  standalone: true,
  imports: [
    UiButtonComponent,
    FaIconComponent,
    NgIf
  ],
  templateUrl: 'confirm-modal.component.html'
})
export class ConfirmModalComponent {
  /** Иконка для отображения в модальном окне */
  @Input() icon = faExclamationTriangle;

  /** Цвет кнопки отмены */
  @Input() rejectColor = 'primary';
  
  /** Цвет кнопки подтверждения */
  @Input() confirmColor = 'danger';

  /** Основной текст сообщения */
  @Input() content = "";
  
  /** Дополнительное предупреждение (отображается красным) */
  @Input() warning : string | null = null;
  
  /**
   * Создает экземпляр ConfirmModalComponent
   * 
   * @param activeModal - Сервис NgBootstrap для управления модальным окном
   */
  constructor(private activeModal: NgbActiveModal) {
  }
  
  /**
   * Закрывает модальное окно с результатом.
   * 
   * При вызове с 'confirm' — действие подтверждено.
   * При вызове с null или без параметра — действие отменено.
   * 
   * @param val - Значение результата ('confirm' или null для отмены)
   */
  closeModal(val : string | null = null) {
    this.activeModal.close(val);
  }

  protected readonly faExclamationTriangle = faExclamationTriangle;
  protected readonly faClose = faClose;
}
