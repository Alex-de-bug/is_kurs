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
import java.util.List;
import net.alephdev.calendar.WebSocketHandler;
import net.alephdev.calendar.annotation.AuthorizedRequired;
import net.alephdev.calendar.annotation.PrivilegeRequired;
import net.alephdev.calendar.models.Status;
import net.alephdev.calendar.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statuses")
@AuthorizedRequired
@Tag(name = "Статусы", description = "API для управления статусами")
@SecurityRequirement(name = "Bearer Authentication")
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class StatusController {

  private final StatusService statusService;
  private final WebSocketHandler webSocketHandler;

  @Autowired
  public StatusController(StatusService statusService, WebSocketHandler webSocketHandler) {
    this.statusService = statusService;
    this.webSocketHandler = webSocketHandler;
  }

  @Operation(
      summary = "Получить все статусы",
      description = "Получение списка всех статусов в системе")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список статусов успешно получен",
            content = @Content(schema = @Schema(implementation = Status.class)))
      })
  @GetMapping
  public List<Status> getAllStatuses() {
    return statusService.getAllStatuses();
  }

  @Operation(
      summary = "Создать статус",
      description = "Создание нового статуса (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Статус успешно создан",
            content = @Content(schema = @Schema(implementation = Status.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
      })
  @PrivilegeRequired
  @PostMapping
  public ResponseEntity<Status> createStatus(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Данные для создания статуса")
          @RequestBody
          Status status) {
    Status createdStatus = statusService.createStatus(status);
    webSocketHandler.notifyClients("status");
    return new ResponseEntity<>(createdStatus, HttpStatus.CREATED);
  }

  @Operation(
      summary = "Обновить статус",
      description = "Обновление существующего статуса (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Статус успешно обновлен",
            content = @Content(schema = @Schema(implementation = Status.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Статус не найден")
      })
  @PrivilegeRequired
  @PutMapping("/{id}")
  public ResponseEntity<Status> updateStatus(
      @Parameter(description = "ID статуса", example = "1") @PathVariable Integer id,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Обновленные данные статуса")
          @RequestBody
          Status updatedStatus) {
    Status status = statusService.updateStatus(id, updatedStatus);
    webSocketHandler.notifyClients("status", id);
    return new ResponseEntity<>(status, HttpStatus.OK);
  }

  @Operation(summary = "Удалить статус", description = "Удаление статуса (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Статус успешно удален"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Статус не найден")
      })
  @PrivilegeRequired
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteStatus(
      @Parameter(description = "ID статуса", example = "1") @PathVariable Integer id) {
    ResponseEntity<Void> result = statusService.deleteStatus(id);
    webSocketHandler.notifyClients("status", id);
    return result;
  }
}
