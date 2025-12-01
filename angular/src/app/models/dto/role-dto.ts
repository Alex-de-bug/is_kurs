/**
 * DTO для создания или обновления роли.
 *
 * @property name - Название роли
 * @property responsibilities - Описание обязанностей и прав
 */
export interface RoleDto {
    name: string;
    responsibilities: string;
}
