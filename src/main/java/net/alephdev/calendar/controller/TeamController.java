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
import net.alephdev.calendar.dto.ObjectDto;
import net.alephdev.calendar.models.Team;
import net.alephdev.calendar.service.TeamService;
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
@RequestMapping("/api/teams")
@AuthorizedRequired
@Tag(name = "Команды", description = "API для управления командами")
@SecurityRequirement(name = "Bearer Authentication")
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class TeamController {

  private final TeamService teamService;
  private final WebSocketHandler webSocketHandler;

  @Autowired
  public TeamController(TeamService teamService, WebSocketHandler webSocketHandler) {
    this.teamService = teamService;
    this.webSocketHandler = webSocketHandler;
  }

  @Operation(
      summary = "Получить все команды",
      description = "Получение списка команд с возможностью фильтрации по активности")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список команд успешно получен",
            content = @Content(schema = @Schema(implementation = Team.class)))
      })
  @GetMapping
  public List<Team> getAllTeams(
      @Parameter(description = "Показывать только активные команды", example = "true")
          @RequestParam(required = false)
          boolean onlyActive) {
    if (onlyActive) {
      return teamService.getActiveTeams();
    }
    return teamService.getAllTeams();
  }

  @Operation(
      summary = "Создать команду",
      description = "Создание новой команды (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Команда успешно создана",
            content = @Content(schema = @Schema(implementation = Team.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
      })
  @PrivilegeRequired
  @PostMapping
  public ResponseEntity<Team> createTeam(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Данные для создания команды")
          @RequestBody
          Team team) {
    Team createdTeam = teamService.createTeam(team);
    webSocketHandler.notifyClients("team");
    return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
  }

  @Operation(
      summary = "Обновить команду",
      description = "Обновление существующей команды (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Команда успешно обновлена",
            content = @Content(schema = @Schema(implementation = Team.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Команда не найдена")
      })
  @PrivilegeRequired
  @PutMapping("/{id}")
  public ResponseEntity<Team> updateTeam(
      @Parameter(description = "ID команды", example = "1") @PathVariable Integer id,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Обновленные данные команды")
          @RequestBody
          Team updatedTeam) {
    Team team = teamService.updateTeam(id, updatedTeam);
    webSocketHandler.notifyClients("team", id);
    return new ResponseEntity<>(team, HttpStatus.OK);
  }

  @Operation(
      summary = "Получить нагрузку команды",
      description = "Получение информации о нагрузке команды в спринте")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Нагрузка команды успешно получена",
            content = @Content(schema = @Schema(implementation = ObjectDto.class)))
      })
  @GetMapping("/load")
  public ResponseEntity<ObjectDto> getTeamLoad(
      @Parameter(description = "ID команды", example = "1") @RequestParam Integer teamId,
      @Parameter(description = "ID спринта", example = "1") @RequestParam Integer sprintId) {
    return ResponseEntity.ok(new ObjectDto(teamService.getTeamLoad(teamId, sprintId)));
  }

  @Operation(summary = "Удалить команду", description = "Удаление команды (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Команда успешно удалена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Команда не найдена")
      })
  @PrivilegeRequired
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTeam(
      @Parameter(description = "ID команды", example = "1") @PathVariable Integer id) {
    ResponseEntity<Void> result = teamService.deleteTeam(id);
    webSocketHandler.notifyClients("team", id);
    return result;
  }
}
