package net.alephdev.calendar.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.alephdev.calendar.WebSocketHandler;
import net.alephdev.calendar.annotation.AuthorizedRequired;
import net.alephdev.calendar.annotation.CurrentUser;
import net.alephdev.calendar.annotation.PrivilegeRequired;
import net.alephdev.calendar.models.Tag;
import net.alephdev.calendar.models.Task;
import net.alephdev.calendar.models.User;
import net.alephdev.calendar.service.TagService;
import net.alephdev.calendar.service.TaskService;
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
@RequestMapping("/api/tags")
@AuthorizedRequired
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Теги", description = "API для управления тегами")
@SecurityRequirement(name = "Bearer Authentication")
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class TagController {

  private final TagService tagService;
  private final TaskService taskService;
  private final WebSocketHandler webSocketHandler;

  @Operation(summary = "Получить все теги", description = "Получение списка всех тегов в системе")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список тегов успешно получен",
            content = @Content(schema = @Schema(implementation = Tag.class)))
      })
  @GetMapping
  public List<Tag> getAllTags() {
    return tagService.getAllTags();
  }

  @Operation(summary = "Создать тег", description = "Создание нового тега (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Тег успешно создан",
            content = @Content(schema = @Schema(implementation = Tag.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
      })
  @PrivilegeRequired
  @PostMapping
  public ResponseEntity<Tag> createTag(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Данные для создания тега")
          @RequestBody
          Tag tag) {
    Tag createdTag = tagService.createTag(tag);
    webSocketHandler.notifyClients("tag");
    return new ResponseEntity<>(createdTag, HttpStatus.CREATED);
  }

  @Operation(
      summary = "Обновить тег",
      description = "Обновление существующего тега (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Тег успешно обновлен",
            content = @Content(schema = @Schema(implementation = Tag.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Тег не найден")
      })
  @PrivilegeRequired
  @PutMapping("/{id}")
  public ResponseEntity<Tag> updateTag(
      @Parameter(description = "ID тега", example = "1") @PathVariable Integer id,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Обновленные данные тега")
          @RequestBody
          Tag updatedTag) {
    Tag tag = tagService.updateTag(id, updatedTag);
    webSocketHandler.notifyClients("tag", id);
    return new ResponseEntity<>(tag, HttpStatus.OK);
  }

  @Operation(summary = "Удалить тег", description = "Удаление тега (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Тег успешно удален"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Тег не найден")
      })
  @PrivilegeRequired
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTag(
      @Parameter(description = "ID тега", example = "1") @PathVariable Integer id) {
    ResponseEntity<Void> result = tagService.deleteTag(id);
    webSocketHandler.notifyClients("tag", id);
    return result;
  }

  @Operation(summary = "Добавить тег к задаче", description = "Связывание тега с задачей")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Тег успешно добавлен к задаче"),
        @ApiResponse(responseCode = "404", description = "Задача или тег не найдены")
      })
  @PostMapping("/task/{taskId}")
  public ResponseEntity<?> addTagToTask(
      @Parameter(description = "ID задачи", example = "1") @PathVariable Integer taskId,
      @Parameter(description = "ID тега", example = "1") @RequestParam Integer tagId,
      @Parameter(hidden = true) @CurrentUser User user) {
    Task task = taskService.getTask(taskId);
    Tag tag = tagService.getTag(tagId);

    tagService.addTagToTask(task, tag, user);
    webSocketHandler.notifyClients("task", taskId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/task/{taskId}")
  public ResponseEntity<?> removeTagFromTask(
      @PathVariable Integer taskId, @RequestParam Integer tagId, @CurrentUser User user) {
    Task task = taskService.getTask(taskId);
    Tag tag = tagService.getTag(tagId);

    tagService.removeTagFromTask(task, tag, user);
    webSocketHandler.notifyClients("task", taskId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      summary = "Получить теги задачи",
      description = "Получение списка тегов для конкретной задачи")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список тегов задачи успешно получен",
            content = @Content(schema = @Schema(implementation = Tag.class)))
      })
  @GetMapping("/task/{taskId}")
  public ResponseEntity<List<Tag>> getTagsForTask(
      @Parameter(description = "ID задачи", example = "1") @PathVariable Integer taskId) {
    Task task = taskService.getTask(taskId);
    List<Tag> tags = tagService.getTagsForTask(task);
    return new ResponseEntity<>(tags, HttpStatus.OK);
  }
}
