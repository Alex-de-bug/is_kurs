package net.alephdev.calendar.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.alephdev.calendar.WebSocketHandler;
import net.alephdev.calendar.annotation.AuthorizedRequired;
import net.alephdev.calendar.annotation.CurrentUser;
import net.alephdev.calendar.annotation.PrivilegeRequired;
import net.alephdev.calendar.dto.TaskDto;
import net.alephdev.calendar.models.Task;
import net.alephdev.calendar.models.User;
import net.alephdev.calendar.service.TaskService;
import net.alephdev.calendar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@AuthorizedRequired
@Tag(name = "Задачи", description = "API для управления задачами")
@SecurityRequirement(name = "Bearer Authentication")
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class TaskController {

  private final TaskService taskService;
  private final UserService userService;
  private final WebSocketHandler webSocketHandler;

  @Autowired
  public TaskController(
      TaskService taskService, UserService userService, WebSocketHandler webSocketHandler) {
    this.taskService = taskService;
    this.userService = userService;
    this.webSocketHandler = webSocketHandler;
  }

  @Operation(
      summary = "Получить все задачи",
      description =
          "Получение списка задач с возможностью фильтрации по статусу, исполнителю, спринту и"
              + " тегу")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список задач успешно получен",
            content = @Content(schema = @Schema(implementation = Page.class)))
      })
  @GetMapping
  public Page<Task> getAllTasks(
      @Parameter(description = "Номер страницы", example = "0") @RequestParam(defaultValue = "0")
          int page,
      @Parameter(description = "ID статуса для фильтрации", example = "1")
          @RequestParam(required = false)
          Integer statusId,
      @Parameter(description = "Логин исполнителя для фильтрации", example = "john.doe")
          @RequestParam(required = false)
          String implementerLogin,
      @Parameter(description = "ID спринта для фильтрации", example = "1")
          @RequestParam(required = false)
          Integer sprintId,
      @Parameter(description = "ID тега для фильтрации", example = "1")
          @RequestParam(required = false)
          Integer tagId) {

    User implementer =
        implementerLogin != null ? userService.getUserByLogin(implementerLogin) : null;

    return taskService.getFilteredTasks(statusId, sprintId, implementer, tagId, page);
  }

  @Operation(
      summary = "Получить задачу по ID",
      description = "Получение информации о конкретной задаче")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Задача найдена",
            content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "404", description = "Задача не найдена")
      })
  @GetMapping("/{id}")
  public ResponseEntity<Task> getTask(
      @Parameter(description = "ID задачи", example = "1") @PathVariable Integer id) {
    Task task = taskService.getTask(id);
    return new ResponseEntity<>(task, HttpStatus.OK);
  }

  @Operation(summary = "Создать задачу", description = "Создание новой задачи")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Задача успешно создана",
            content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для создания задачи")
      })
  @PostMapping
  public ResponseEntity<Task> createTask(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Данные для создания задачи")
          @RequestBody
          TaskDto task,
      @Parameter(hidden = true) @CurrentUser User user) {
    if (taskService.canCreateTask(user)) {
      Task createdTask = taskService.createTask(task, user);
      webSocketHandler.notifyClients("task");
      return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @Operation(summary = "Обновить задачу", description = "Обновление существующей задачи")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Задача успешно обновлена",
            content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Недостаточно прав для редактирования задачи"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена")
      })
  @PutMapping("/{id}")
  public ResponseEntity<Task> updateTask(
      @Parameter(description = "ID задачи", example = "1") @PathVariable Integer id,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Обновленные данные задачи")
          @RequestBody
          TaskDto updatedTask,
      @Parameter(hidden = true) @CurrentUser User user) {
    Task existingTask = taskService.getTask(id);

    if (taskService.canEditTask(user, existingTask)) {
      Task task = taskService.updateTask(id, updatedTask);
      webSocketHandler.notifyClients("task", id);
      return new ResponseEntity<>(task, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @Operation(summary = "Удалить задачу", description = "Удаление задачи (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена")
      })
  @PrivilegeRequired
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(
      @Parameter(description = "ID задачи", example = "1") @PathVariable Integer id) {
    ResponseEntity<Void> result = taskService.deleteTask(id);
    webSocketHandler.notifyClients("task", id);
    return result;
  }

  @Operation(summary = "Назначить исполнителя", description = "Назначение исполнителя для задачи")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Исполнитель успешно назначен",
            content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Недостаточно прав для назначения исполнителя"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена")
      })
  @PutMapping("/{taskId}/implementer")
  public ResponseEntity<Task> assignImplementer(
      @Parameter(description = "ID задачи", example = "1") @PathVariable Integer taskId,
      @Parameter(description = "Логин исполнителя", example = "john.doe") @RequestParam
          String implementerLogin,
      @Parameter(hidden = true) @CurrentUser User user) {
    Task existingTask = taskService.getTask(taskId);

    if ((user.getRole() != null && userService.isPrivileged(user))
        || (existingTask.getImplementer() != null
            && existingTask.getImplementer().getLogin().equals(user.getLogin()))
        || existingTask.getCreatedBy().getLogin().equals(user.getLogin())) {
      Task task = taskService.assignImplementer(taskId, implementerLogin);
      webSocketHandler.notifyClients("task", taskId);
      return new ResponseEntity<>(task, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @Operation(summary = "Обновить статус задачи", description = "Изменение статуса задачи")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Статус задачи успешно обновлен",
            content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "404", description = "Задача не найдена")
      })
  @PutMapping("/{taskId}/status")
  public ResponseEntity<?> updateStatus(
      @Parameter(description = "ID задачи", example = "1") @PathVariable Integer taskId,
      @Parameter(description = "ID нового статуса", example = "2") @RequestParam Integer statusId,
      @Parameter(hidden = true) @CurrentUser User user) {
    Task task = taskService.updateStatus(taskId, statusId, user);
    webSocketHandler.notifyClients("task", taskId);
    return new ResponseEntity<>(task, HttpStatus.OK);
  }

  @Operation(
      summary = "Назначить спринт",
      description = "Назначение спринта для задачи (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Спринт успешно назначен",
            content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена")
      })
  @PrivilegeRequired
  @PutMapping("/{taskId}/sprint")
  public ResponseEntity<Task> assignSprint(
      @Parameter(description = "ID задачи", example = "1") @PathVariable Integer taskId,
      @Parameter(description = "ID спринта", example = "1") @RequestParam Integer sprintId) {
    Task task = taskService.assignSprint(taskId, sprintId);
    webSocketHandler.notifyClients("task", taskId);
    return new ResponseEntity<>(task, HttpStatus.OK);
  }
}
