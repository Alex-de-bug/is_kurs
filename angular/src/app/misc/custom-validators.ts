import {AbstractControl, ValidationErrors} from "@angular/forms";

/**
 * Набор кастомных валидаторов для Angular форм.
 */
export class CustomValidators {
  /**
   * Валидатор, запрещающий ввод только пробельных символов.
   *
   * @returns Функция‑валидатор для AbstractControl
   */
  public static noWhitespace() {
    return (control: AbstractControl): ValidationErrors | null => {
      const isWhitespace = (control.value || '').trim().length === 0;
      const isValid = !isWhitespace;
      return isValid ? null : {pattern: true};
    };
  }
}
