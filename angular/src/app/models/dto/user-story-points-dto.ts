/**
 * DTO с количеством сторипоинтов пользователя в спринте.
 *
 * @property userLogin - Логин пользователя
 * @property totalStoryPoints - Общее количество сторипоинтов
 */
export interface UserStoryPointsDto {
  userLogin: string;
  totalStoryPoints: number;
}
