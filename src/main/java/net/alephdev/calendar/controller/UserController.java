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
import jakarta.validation.Valid;
import net.alephdev.calendar.WebSocketHandler;
import net.alephdev.calendar.annotation.AuthorizedRequired;
import net.alephdev.calendar.annotation.CurrentUser;
import net.alephdev.calendar.annotation.PrivilegeRequired;
import net.alephdev.calendar.dto.UserDto;
import net.alephdev.calendar.models.User;
import net.alephdev.calendar.service.TaskService;
import net.alephdev.calendar.service.UserService;
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
@RequestMapping("/api/users")
@AuthorizedRequired
@Tag(name = "Пользователи", description = "API для управления пользователями")
@SecurityRequirement(name = "Bearer Authentication")
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class UserController {

  private final UserService userService;
  private final TaskService taskService;
  private final WebSocketHandler webSocketHandler;

  @Autowired
  public UserController(
      UserService userService, TaskService taskService, WebSocketHandler webSocketHandler) {
    this.userService = userService;
    this.taskService = taskService;
    this.webSocketHandler = webSocketHandler;
  }

  @Operation(
      summary = "Получить всех пользователей",
      description =
          "Получение списка пользователей с возможностью фильтрации по логину, команде и"
              + " активности")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список пользователей успешно получен",
            content = @Content(schema = @Schema(implementation = Page.class)))
      })
  @GetMapping
  public Page<User> getAllUsers(
      @Parameter(description = "Номер страницы", example = "0") @RequestParam @DefaultValue("0")
          int page,
      @Parameter(description = "Частичный логин для поиска", example = "john")
          @RequestParam(required = false)
          String login,
      @Parameter(description = "ID команды для фильтрации", example = "1")
          @RequestParam
          @DefaultValue("0")
          int team,
      @Parameter(description = "Показывать только активных пользователей", example = "true")
          @RequestParam(required = false)
          @DefaultValue("true")
          boolean onlyActive) {
    if (login != null) {
      if (team != 0) return userService.getAllUsersWithPartialLoginAndTeam(page, login, team);
      if (onlyActive) return userService.getAllUsersWithPartialLoginActive(page, login);
      return userService.getAllUserWithPartialLogin(page, login);
    }
    return userService.getAllUsers(page);
  }

  @Operation(
      summary = "Получить текущего пользователя",
      description = "Получение информации о текущем аутентифицированном пользователе")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Информация о пользователе получена",
            content = @Content(schema = @Schema(implementation = User.class)))
      })
  @GetMapping("/current")
  public User getCurrentUser(@Parameter(hidden = true) @CurrentUser User user) {
    user.setCanCreateTasks(taskService.canCreateTask(user));
    return user;
  }

  @Operation(
      summary = "Регистрация пользователя",
      description = "Создание нового пользователя (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Пользователь успешно создан",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
      })
  @PrivilegeRequired
  @PostMapping("/register")
  public ResponseEntity<User> registerUser(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Данные для регистрации пользователя")
          @Valid
          @RequestBody
          UserDto userDto) {
    User user = userService.register(userDto);
    webSocketHandler.notifyClients("user");
    return new ResponseEntity<>(user, HttpStatus.CREATED);
  }

  @Operation(
      summary = "Обновить роль пользователя",
      description = "Изменение роли пользователя (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Роль пользователя успешно обновлена",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
      })
  @PrivilegeRequired
  @PutMapping("/{login}/role")
  public ResponseEntity<User> updateUserRole(
      @Parameter(description = "Логин пользователя", example = "john.doe") @PathVariable
          String login,
      @Parameter(description = "ID новой роли", example = "2") @RequestParam Integer roleId) {
    User user = userService.updateRole(login, roleId);
    webSocketHandler.notifyClients("user", login);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @Operation(
      summary = "Обновить команду пользователя",
      description = "Изменение команды пользователя (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Команда пользователя успешно обновлена",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
      })
  @PrivilegeRequired
  @PutMapping("/{login}/team")
  public ResponseEntity<User> updateUserTeam(
      @Parameter(description = "Логин пользователя", example = "john.doe") @PathVariable
          String login,
      @Parameter(description = "ID новой команды", example = "1") @RequestParam(required = false)
          Integer teamId) {
    User user = userService.updateUserTeam(login, teamId);
    webSocketHandler.notifyClients("user", login);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @Operation(
      summary = "Обновить данные пользователя",
      description = "Обновление информации о пользователе")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Данные пользователя успешно обновлены",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для редактирования"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
      })
  @PutMapping("/{login}")
  public ResponseEntity<User> updateUser(
      @Parameter(description = "Логин пользователя", example = "john.doe") @PathVariable
          String login,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Обновленные данные пользователя")
          @Valid
          @RequestBody
          UserDto userDto,
      @Parameter(hidden = true) @CurrentUser User currentUser) {
    if (currentUser.getLogin().equals(login) || userService.isPrivileged(currentUser)) {
      User user = userService.updateUser(login, userDto);
      webSocketHandler.notifyClients("user", login);
      return new ResponseEntity<>(user, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @Operation(
      summary = "Удалить пользователя",
      description = "Удаление пользователя из системы (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно удален"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
      })
  @DeleteMapping("/{login}")
  public ResponseEntity<Void> wipeUser(
      @Parameter(description = "Логин пользователя", example = "john.doe") @PathVariable
          String login,
      @Parameter(hidden = true) @CurrentUser User currentUser) {
    if (userService.isPrivileged(currentUser)) {
      userService.wipeUser(login);
      webSocketHandler.notifyClients("userWipe", login);
      return new ResponseEntity<>(HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }
}
