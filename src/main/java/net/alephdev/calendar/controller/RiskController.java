package net.alephdev.calendar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.alephdev.calendar.WebSocketHandler;
import net.alephdev.calendar.annotation.AuthorizedRequired;
import net.alephdev.calendar.annotation.CurrentUser;
import net.alephdev.calendar.annotation.PrivilegeRequired;
import net.alephdev.calendar.dto.functional.TopRiskDto;
import net.alephdev.calendar.models.Idea;
import net.alephdev.calendar.models.Risk;
import net.alephdev.calendar.models.Task;
import net.alephdev.calendar.models.User;
import net.alephdev.calendar.service.IdeaService;
import net.alephdev.calendar.service.RiskService;
import net.alephdev.calendar.service.TaskService;
import org.springframework.boot.context.properties.bind.DefaultValue;
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
@RequestMapping("/api/risks")
@AuthorizedRequired
@RequiredArgsConstructor
@Tag(name = "Риски", description = "API для управления рисками")
@SecurityRequirement(name = "Bearer Authentication")
public class RiskController {

  private final RiskService riskService;
  private final TaskService taskService;
  private final IdeaService ideaService;
  private final WebSocketHandler webSocketHandler;

  @Operation(
      summary = "Получить все риски",
      description = "Получение списка рисков с возможностью фильтрации по описанию")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список рисков успешно получен",
            content = @Content(schema = @Schema(implementation = Page.class)))
      })
  @GetMapping
  public Page<Risk> getAllRisks(
      @Parameter(description = "Номер страницы", example = "0") @RequestParam @DefaultValue("0")
          int page,
      @Parameter(description = "Описание для поиска", example = "технический") @RequestParam
          String description) {
    return riskService.getAllRisks(page, description);
  }

  @Operation(summary = "Создать риск", description = "Создание нового риска (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Риск успешно создан",
            content = @Content(schema = @Schema(implementation = Risk.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
      })
  @PrivilegeRequired
  @PostMapping
  public ResponseEntity<Risk> createRisk(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Данные для создания риска")
          @RequestBody
          Risk risk) {
    Risk createdRisk = riskService.createRisk(risk);
    webSocketHandler.notifyClients("risk");
    return new ResponseEntity<>(createdRisk, HttpStatus.CREATED);
  }

  @Operation(
      summary = "Обновить риск",
      description = "Обновление существующего риска (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Риск успешно обновлен",
            content = @Content(schema = @Schema(implementation = Risk.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Риск не найден")
      })
  @PrivilegeRequired
  @PutMapping("/{id}")
  public ResponseEntity<Risk> updateRisk(
      @Parameter(description = "ID риска", example = "1") @PathVariable Integer id,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Обновленные данные риска")
          @RequestBody
          Risk updatedRisk) {
    Risk risk = riskService.updateRisk(id, updatedRisk);
    webSocketHandler.notifyClients("risk");
    return new ResponseEntity<>(risk, HttpStatus.OK);
  }

  @Operation(summary = "Удалить риск", description = "Удаление риска (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Риск успешно удален"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Риск не найден")
      })
  @PrivilegeRequired
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRisk(
      @Parameter(description = "ID риска", example = "1") @PathVariable Integer id) {
    ResponseEntity<Void> result = riskService.deleteRisk(id);
    webSocketHandler.notifyClients("risk");
    return result;
  }

  @Operation(summary = "Добавить риск к задаче", description = "Связывание риска с задачей")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Риск успешно добавлен к задаче"),
        @ApiResponse(responseCode = "404", description = "Задача или риск не найдены")
      })
  @PostMapping("/task/{taskId}")
  public ResponseEntity<?> addRiskToTask(
      @Parameter(description = "ID задачи", example = "1") @PathVariable Integer taskId,
      @Parameter(description = "ID риска", example = "1") @RequestParam Integer riskId,
      @Parameter(hidden = true) @CurrentUser User user) {
    Task task = taskService.getTask(taskId);
    Risk risk = riskService.getRisk(riskId);

    riskService.addRiskToTask(task, risk, user);
    webSocketHandler.notifyClients("task", taskId);
    webSocketHandler.notifyClients("risk");
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/task/{taskId}")
  @Operation(summary = "Удалить риск из задачи", description = "Удаление связи риска с задачей")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Риск успешно удален из задачи"),
        @ApiResponse(responseCode = "404", description = "Задача или риск не найдены")
      })
  public ResponseEntity<?> removeRiskFromTask(
      @Parameter(description = "ID задачи", example = "1") @PathVariable Integer taskId,
      @Parameter(description = "ID риска", example = "1") @RequestParam Integer riskId,
      @Parameter(hidden = true) @CurrentUser User user) {
    Task task = taskService.getTask(taskId);
    Risk risk = riskService.getRisk(riskId);

    riskService.removeRiskFromTask(task, risk, user);
    webSocketHandler.notifyClients("task", taskId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/task/{taskId}")
  @Operation(
      summary = "Получить риски задачи",
      description = "Получение списка рисков, связанных с задачей")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список рисков задачи успешно получен",
            content = @Content(schema = @Schema(implementation = Risk.class)))
      })
  public ResponseEntity<List<Risk>> getRisksForTask(
      @Parameter(description = "ID задачи", example = "1") @PathVariable Integer taskId) {
    Task task = taskService.getTask(taskId);
    List<Risk> risks = riskService.getRisksForTask(task);

    return ResponseEntity.ok(risks);
  }

  @PostMapping("/idea/{ideaId}")
  @Operation(summary = "Добавить риск к идее", description = "Связывание риска с идеей")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Риск успешно добавлен к идее"),
        @ApiResponse(responseCode = "404", description = "Идея или риск не найдены")
      })
  public ResponseEntity<?> addRiskToIdea(
      @Parameter(description = "ID идеи", example = "1") @PathVariable Integer ideaId,
      @Parameter(description = "ID риска", example = "1") @RequestParam Integer riskId,
      @Parameter(hidden = true) @CurrentUser User user) {
    Idea idea = ideaService.getIdea(ideaId);
    Risk risk = riskService.getRisk(riskId);

    riskService.addRiskToIdea(idea, risk, user);
    webSocketHandler.notifyClients("idea", ideaId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/idea/{ideaId}")
  @Operation(summary = "Удалить риск из идеи", description = "Удаление связи риска с идеей")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Риск успешно удален из идеи"),
        @ApiResponse(responseCode = "404", description = "Идея или риск не найдены")
      })
  public ResponseEntity<?> removeRiskFromIdea(
      @Parameter(description = "ID идеи", example = "1") @PathVariable Integer ideaId,
      @Parameter(description = "ID риска", example = "1") @RequestParam Integer riskId,
      @Parameter(hidden = true) @CurrentUser User user) {
    Idea idea = ideaService.getIdea(ideaId);
    Risk risk = riskService.getRisk(riskId);

    riskService.removeRiskFromIdea(idea, risk, user);
    webSocketHandler.notifyClients("idea", ideaId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/idea/{ideaId}")
  @Operation(
      summary = "Получить риски идеи",
      description = "Получение списка рисков, связанных с идеей")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список рисков идеи успешно получен",
            content = @Content(schema = @Schema(implementation = Risk.class)))
      })
  public ResponseEntity<List<Risk>> getRisksForIdea(
      @Parameter(description = "ID идеи", example = "1") @PathVariable Integer ideaId) {
    Idea idea = ideaService.getIdea(ideaId);
    List<Risk> risks = riskService.getRisksForIdea(idea);

    return ResponseEntity.ok(risks);
  }

  @Operation(
      summary = "Получить топ-10 рисков",
      description = "Получение списка топ-10 наиболее часто встречающихся рисков в задачах")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список топ-10 рисков успешно получен",
            content = @Content(schema = @Schema(implementation = TopRiskDto.class)))
      })
  @GetMapping("/top10")
  public ResponseEntity<List<TopRiskDto>> getTop10TaskRisks() {
    return ResponseEntity.ok(riskService.getTop10TaskRisks());
  }
}
