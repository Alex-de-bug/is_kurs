/**
 * DTO для создания или обновления тега.
 *
 * @property name - Название тега
 * @property description - Описание тега
 */
export interface TagDto {
    name: string;
    description: string | null;
}
