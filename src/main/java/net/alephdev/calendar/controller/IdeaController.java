package net.alephdev.calendar.controller;

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
import net.alephdev.calendar.dto.IdeaDto;
import net.alephdev.calendar.models.Idea;
import net.alephdev.calendar.models.User;
import net.alephdev.calendar.service.IdeaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ideas")
@AuthorizedRequired
@Tag(name = "Идеи", description = "API для управления идеями")
@SecurityRequirement(name = "Bearer Authentication")
public class IdeaController {

  private final IdeaService ideaService;
  private final WebSocketHandler webSocketHandler;

  @Autowired
  public IdeaController(IdeaService ideaService, WebSocketHandler webSocketHandler) {
    this.ideaService = ideaService;
    this.webSocketHandler = webSocketHandler;
  }

  @Operation(
      summary = "Получить все идеи",
      description = "Получение списка идей с возможностью фильтрации по статусу")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список идей успешно получен",
            content = @Content(schema = @Schema(implementation = Page.class)))
      })
  @GetMapping
  public Page<Idea> getAllIdeas(
      @Parameter(description = "Номер страницы", example = "0") @RequestParam @DefaultValue("0")
          int page,
      @Parameter(description = "Статус идеи для фильтрации", example = "PENDING")
          @RequestParam(required = false)
          Idea.Status status) {
    if (status != null) {
      return ideaService.getAllIdeasByStatus(status, page);
    }
    return ideaService.getAllIdeas(page);
  }

  @Operation(summary = "Создать идею", description = "Создание новой идеи")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Идея успешно создана",
            content = @Content(schema = @Schema(implementation = Idea.class)))
      })
  @PostMapping
  public ResponseEntity<Idea> createIdea(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Данные для создания идеи")
          @RequestBody
          IdeaDto idea,
      @Parameter(hidden = true) @CurrentUser User user) {
    Idea createdIdea = ideaService.createIdea(idea, user);
    webSocketHandler.notifyClients("idea");
    return new ResponseEntity<>(createdIdea, HttpStatus.CREATED);
  }

  @Operation(summary = "Обновить идею", description = "Обновление существующей идеи")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Идея успешно обновлена",
            content = @Content(schema = @Schema(implementation = Idea.class))),
        @ApiResponse(responseCode = "404", description = "Идея не найдена")
      })
  @PutMapping("/{id}")
  public ResponseEntity<Idea> updateIdea(
      @Parameter(description = "ID идеи", example = "1") @PathVariable Integer id,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Обновленные данные идеи")
          @RequestBody
          IdeaDto updatedIdea,
      @Parameter(hidden = true) @CurrentUser User user) {
    Idea idea = ideaService.updateIdea(id, updatedIdea, user);
    webSocketHandler.notifyClients("idea", id);
    return new ResponseEntity<>(idea, HttpStatus.OK);
  }

  @Operation(
      summary = "Обработать идею",
      description = "Изменение статуса идеи (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Статус идеи успешно изменен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Идея не найдена")
      })
  @PrivilegeRequired
  @PutMapping("/{id}/status")
  public ResponseEntity<Void> processIdea(
      @Parameter(description = "ID идеи", example = "1") @PathVariable Integer id,
      @Parameter(description = "Новый статус идеи", example = "APPROVED") @RequestParam
          Idea.Status status) {
    ideaService.processIdea(id, status.toString());
    webSocketHandler.notifyClients("idea");
    return ResponseEntity.ok().build();
  }
}
