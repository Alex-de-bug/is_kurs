/**
 * Модель дня в календаре.
 *
 * Используется для представления рабочей/нерабочей загрузки в календаре спринтов.
 *
 * @property date - Дата
 * @property full - Признак полного рабочего дня (true) или сокращённого/нерабочего (false)
 */
export class Day {
  date: Date;
  full: boolean;

  /**
   * Создаёт экземпляр Day.
   *
   * @param date Дата
   * @param full Флаг полного рабочего дня
   */
  constructor(date: Date, full: boolean) {
    this.date = date;
    this.full = full;
  }
}
