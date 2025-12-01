import { Pipe, PipeTransform } from '@angular/core';
import {IdeaStatus} from "../models/idea";

/**
 * Преобразует статус идеи в локализованную строку.
 */
@Pipe({
  standalone: true,
  name: 'statusParser'
})
export class StatusParserPipe implements PipeTransform {
  /**
   * Возвращает человекочитаемое название статуса.
   *
   * @param value Статус идеи
   */
  transform(value: IdeaStatus): string {
    switch (value) {
      case IdeaStatus.PENDING:
        return 'Ожидает';
      case IdeaStatus.REJECTED:
        return 'Отклонена';
      case IdeaStatus.APPROVED:
        return 'Принята';
      default:
        return 'Неизвестен';
    }
  }
}
