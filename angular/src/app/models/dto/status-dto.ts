/**
 * DTO для создания или обновления статуса.
 *
 * @property name - Название статуса
 * @property description - Описание статуса
 */
export interface StatusDto {
    name: string;
    description: string | null;
}
