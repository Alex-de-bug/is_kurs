package net.alephdev.calendar.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.alephdev.calendar.dto.JwtRequestDto;
import net.alephdev.calendar.dto.JwtResponseDto;
import net.alephdev.calendar.dto.MessageDto;
import net.alephdev.calendar.jwt.JwtUtils;
import net.alephdev.calendar.models.User;
import net.alephdev.calendar.service.TaskService;
import net.alephdev.calendar.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "API для аутентификации пользователей")
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final UserService userService;
  private final JwtUtils jwtUtils;
  private final TaskService taskService;

  @Operation(
      summary = "Вход в систему",
      description = "Аутентификация пользователя и получение JWT токена")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Успешная аутентификация",
            content = @Content(schema = @Schema(implementation = JwtResponseDto.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Неверные учетные данные",
            content = @Content(schema = @Schema(implementation = MessageDto.class)))
      })
  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@Valid @RequestBody JwtRequestDto authenticationRequest) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  authenticationRequest.getUsername(), authenticationRequest.getPassword()));

      if (authentication.isAuthenticated()) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userService.getUserByLogin(username);
        user.setCanCreateTasks(taskService.canCreateTask(user));

        String token = jwtUtils.generateToken(user);
        return ResponseEntity.ok(new JwtResponseDto(token, user));
      } else {
        return new ResponseEntity<>(
            new MessageDto("Неверный логин или пароль"), HttpStatus.UNAUTHORIZED);
      }
    } catch (BadCredentialsException e) {
      return new ResponseEntity<>(
          new MessageDto("Неверный логин или пароль"), HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      return new ResponseEntity<>(new MessageDto(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }
  }
}
