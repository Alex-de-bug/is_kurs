import {User} from "./user";
import {Task} from "./task";

/**
 * Модель идеи (предложения/фичи) в системе.
 *
 * Идея может быть связана с задачей и иметь статус модерации.
 *
 * @property id - Уникальный идентификатор идеи
 * @property description - Описание идеи
 * @property authorLogin - Автор идеи
 * @property statusEnumId - Статус идеи
 * @property task - Связанная задача (если идея принята в работу)
 */
export interface Idea {
  id: number;
  description: string;
  authorLogin: User;
  statusEnumId: IdeaStatus;
  task: Task | null;
}

/**
 * Статусы идеи.
 *
 * @enum {string}
 * @property PENDING - Ожидает решения
 * @property REJECTED - Отклонена
 * @property APPROVED - Принята
 */
export enum IdeaStatus {
  PENDING = 'PENDING',
  REJECTED = 'REJECTED',
  APPROVED = 'APPROVED'
}
