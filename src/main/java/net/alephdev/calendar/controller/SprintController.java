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
import net.alephdev.calendar.dto.SprintDto;
import net.alephdev.calendar.dto.functional.SprintTeamDto;
import net.alephdev.calendar.dto.functional.UserStoryPointsDto;
import net.alephdev.calendar.models.Release;
import net.alephdev.calendar.models.Sprint;
import net.alephdev.calendar.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/sprints")
@AuthorizedRequired
@Tag(name = "Спринты", description = "API для управления спринтами")
@SecurityRequirement(name = "Bearer Authentication")
public class SprintController {

  private final SprintService sprintService;
  private final WebSocketHandler webSocketHandler;

  @Autowired
  public SprintController(SprintService sprintService, WebSocketHandler webSocketHandler) {
    this.sprintService = sprintService;
    this.webSocketHandler = webSocketHandler;
  }

  @Operation(
      summary = "Получить все спринты",
      description = "Получение списка спринтов с возможностью фильтрации по версии и команде")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список спринтов успешно получен",
            content = @Content(schema = @Schema(implementation = Page.class)))
      })
  @GetMapping
  public Page<Sprint> getAllSprints(
      @Parameter(description = "Номер страницы", example = "0") @RequestParam @DefaultValue("0")
          int page,
      @Parameter(description = "Основная версия для фильтрации", example = "1.0")
          @RequestParam(required = false)
          String majorVersion,
      @Parameter(description = "ID команды для фильтрации", example = "1")
          @RequestParam(required = false)
          Integer teamId) {
    if (majorVersion != null) {
      if (teamId != null) {
        return sprintService.getSprintsByMajorVersionAndTeam(page, majorVersion, teamId);
      }
      return sprintService.getSprintsByMajorVersion(page, majorVersion);
    }
    return sprintService.getAllSprints(page);
  }

  @Operation(
      summary = "Получить спринт по ID",
      description = "Получение информации о конкретном спринте")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Спринт найден",
            content = @Content(schema = @Schema(implementation = Sprint.class))),
        @ApiResponse(responseCode = "404", description = "Спринт не найден")
      })
  @GetMapping("/{id}")
  public ResponseEntity<Sprint> getSprint(
      @Parameter(description = "ID спринта", example = "1") @PathVariable Integer id) {
    Sprint sprint = sprintService.getSprint(id);
    return new ResponseEntity<>(sprint, HttpStatus.OK);
  }

  @Operation(
      summary = "Создать спринт",
      description = "Создание нового спринта (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Спринт успешно создан",
            content = @Content(schema = @Schema(implementation = Sprint.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
      })
  @PrivilegeRequired
  @PostMapping
  public ResponseEntity<Sprint> createSprint(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Данные для создания спринта")
          @RequestBody
          SprintDto sprint) {
    Sprint createdSprint = sprintService.createSprint(sprint);
    webSocketHandler.notifyClients("sprints");
    return new ResponseEntity<>(createdSprint, HttpStatus.CREATED);
  }

  @Operation(
      summary = "Обновить спринт",
      description = "Обновление существующего спринта (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Спринт успешно обновлен",
            content = @Content(schema = @Schema(implementation = Sprint.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Спринт не найден")
      })
  @PrivilegeRequired
  @PutMapping("/{id}")
  public ResponseEntity<Sprint> updateSprint(
      @Parameter(description = "ID спринта", example = "1") @PathVariable Integer id,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Обновленные данные спринта")
          @RequestBody
          SprintDto updatedSprint) {
    Sprint sprint = sprintService.updateSprint(id, updatedSprint);
    webSocketHandler.notifyClients("sprints", id);
    return new ResponseEntity<>(sprint, HttpStatus.OK);
  }

  @Operation(summary = "Удалить спринт", description = "Удаление спринта (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Спринт успешно удален"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Спринт не найден")
      })
  @PrivilegeRequired
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSprint(
      @Parameter(description = "ID спринта", example = "1") @PathVariable Integer id) {
    ResponseEntity<Void> result = sprintService.deleteSprint(id);
    webSocketHandler.notifyClients("sprints", id);
    return result;
  }

  @Operation(
      summary = "Получить спринты по году и команде",
      description = "Получение отфильтрованных спринтов по году и названию команды")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список спринтов успешно получен",
            content = @Content(schema = @Schema(implementation = SprintTeamDto.class)))
      })
  @GetMapping("/filtered")
  public ResponseEntity<List<SprintTeamDto>> getSprintsByYearAndTeam(
      @Parameter(description = "Год для фильтрации", example = "2024") @RequestParam Integer year,
      @Parameter(description = "Название команды для фильтрации", example = "Frontend Team")
          @RequestParam(required = false)
          String teamName) {
    return ResponseEntity.ok(sprintService.getSprintsByYearAndTeam(year, teamName));
  }

  @Operation(
      summary = "Получить story points по пользователям",
      description = "Получение статистики story points для каждого пользователя в спринте")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Статистика story points успешно получена",
            content = @Content(schema = @Schema(implementation = UserStoryPointsDto.class)))
      })
  @GetMapping("/{sprintId}/story-points")
  public ResponseEntity<List<UserStoryPointsDto>> getStoryPointsPerUser(
      @Parameter(description = "ID спринта", example = "1") @PathVariable Integer sprintId) {
    return ResponseEntity.ok(sprintService.getStoryPointsPerUser(sprintId));
  }

  @Operation(
      summary = "Получить релизы спринта",
      description = "Получение списка релизов для конкретного спринта")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список релизов успешно получен",
            content = @Content(schema = @Schema(implementation = Release.class)))
      })
  @GetMapping("/{id}/releases")
  public List<Release> getSprintReleases(
      @Parameter(description = "ID спринта", example = "1") @PathVariable Integer id) {
    return sprintService.getSprintReleases(id);
  }
}
