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
import net.alephdev.calendar.WebSocketHandler;
import net.alephdev.calendar.annotation.AuthorizedRequired;
import net.alephdev.calendar.annotation.PrivilegeRequired;
import net.alephdev.calendar.models.Role;
import net.alephdev.calendar.models.Status;
import net.alephdev.calendar.service.RoleService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
@AuthorizedRequired
@Tag(name = "Роли", description = "API для управления ролями пользователей")
@SecurityRequirement(name = "Bearer Authentication")
public class RoleController {

  private final RoleService roleService;
  private final StatusService statusService;
  private final WebSocketHandler webSocketHandler;

  @Autowired
  public RoleController(
      RoleService roleService, StatusService statusService, WebSocketHandler webSocketHandler) {
    this.roleService = roleService;
    this.statusService = statusService;
    this.webSocketHandler = webSocketHandler;
  }

  @Operation(summary = "Получить все роли", description = "Получение списка всех ролей в системе")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список ролей успешно получен",
            content = @Content(schema = @Schema(implementation = Role.class)))
      })
  @GetMapping
  public List<Role> getAllRoles() {
    return roleService.getAllRoles();
  }

  @Operation(summary = "Создать роль", description = "Создание новой роли (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Роль успешно создана",
            content = @Content(schema = @Schema(implementation = Role.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
      })
  @PrivilegeRequired
  @PostMapping
  public ResponseEntity<Role> createRole(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Данные для создания роли")
          @RequestBody
          Role role) {
    Role createdRole = roleService.createRole(role);
    webSocketHandler.notifyClients("role");
    return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
  }

  @Operation(
      summary = "Обновить роль",
      description = "Обновление существующей роли (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Роль успешно обновлена",
            content = @Content(schema = @Schema(implementation = Role.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Роль не найдена")
      })
  @PrivilegeRequired
  @PutMapping("/{id}")
  public ResponseEntity<Role> updateRole(
      @Parameter(description = "ID роли", example = "1") @PathVariable Integer id,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Обновленные данные роли")
          @RequestBody
          Role updatedRole) {
    Role role = roleService.updateRole(id, updatedRole);
    webSocketHandler.notifyClients("role", role.getId());
    return new ResponseEntity<>(role, HttpStatus.OK);
  }

  @Operation(summary = "Удалить роль", description = "Удаление роли (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Роль успешно удалена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Роль не найдена")
      })
  @PrivilegeRequired
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRole(
      @Parameter(description = "ID роли", example = "1") @PathVariable Integer id) {
    ResponseEntity<Void> result = roleService.deleteRole(id);
    webSocketHandler.notifyClients("role", id);
    return result;
  }

  @Operation(
      summary = "Получить статусы роли",
      description = "Получение списка статусов для конкретной роли")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список статусов успешно получен",
            content = @Content(schema = @Schema(implementation = Status.class)))
      })
  @GetMapping("/{id}/statuses")
  public List<Status> getStatuses(
      @Parameter(description = "ID роли", example = "1") @PathVariable Integer id) {
    return roleService.getStatuses(id);
  }

  @Operation(
      summary = "Добавить статус к роли",
      description = "Добавление статуса к роли (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Статус успешно добавлен к роли"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Роль или статус не найдены")
      })
  @PrivilegeRequired
  @PostMapping("/{id}/statuses")
  public ResponseEntity<Void> addRoleStatus(
      @Parameter(description = "ID роли", example = "1") @PathVariable Integer id,
      @Parameter(description = "ID статуса", example = "2") @RequestParam Integer statusId) {
    roleService.addRoleStatus(roleService.getRole(id), statusService.getStatus(statusId));
    webSocketHandler.notifyClients("role", id);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Operation(
      summary = "Удалить статус из роли",
      description = "Удаление статуса из роли (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Статус успешно удален из роли"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Роль или статус не найдены")
      })
  @PrivilegeRequired
  @DeleteMapping("/{id}/statuses")
  public ResponseEntity<Void> deleteRoleStatus(
      @Parameter(description = "ID роли", example = "1") @PathVariable Integer id,
      @Parameter(description = "ID статуса", example = "2") @RequestParam Integer statusId) {
    roleService.removeRoleStatus(roleService.getRole(id), statusService.getStatus(statusId));
    webSocketHandler.notifyClients("role", id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
