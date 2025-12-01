import {User} from "./user";
import {Sprint} from "./sprint";
import {Status} from "./status";

/**
 * Модель задачи (Task) в системе управления проектами.
 *
 * Представляет задачу, которая может быть назначена пользователю,
 * привязана к спринту и имеет определенный статус выполнения.
 *
 * @property id - Уникальный идентификатор задачи
 * @property name - Название задачи
 * @property storyPoints - Оценка сложности в Story Points (может быть null)
 * @property implementer - Исполнитель задачи (может быть null если не назначен)
 * @property sprint - Спринт, к которому привязана задача (может быть null)
 * @property status - Текущий статус задачи (To Do, In Progress, Done и т.д.)
 * @property priorityEnum - Приоритет задачи
 * @property createdBy - Пользователь, создавший задачу
 */
export interface Task {
  id: number;
  name: string;
  storyPoints: number | null;
  implementer: User | null;
  sprint: Sprint | null;
  status: Status;
  priorityEnum: TaskPriority;
  createdBy: User;
}

/**
 * Перечисление приоритетов задач.
 *
 * Определяет уровни важности задачи для приоритизации работы команды.
 *
 * @enum {string}
 * @property LOW - Низкий приоритет (не критичные улучшения)
 * @property MEDIUM - Средний приоритет (стандартные задачи)
 * @property CRITICAL - Критический приоритет (требует немедленного внимания)
 */
export enum TaskPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  CRITICAL = 'CRITICAL'
}
