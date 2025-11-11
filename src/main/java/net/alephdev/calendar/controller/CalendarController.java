package net.alephdev.calendar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.alephdev.calendar.annotation.AuthorizedRequired;
import net.alephdev.calendar.dto.MessageDto;
import net.alephdev.calendar.interfaces.CalendarServiceInterface;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/calendar")
@Cacheable("calendarData")
@Tag(name = "Календарь", description = "API для работы с календарными данными")
@SecurityRequirement(name = "Bearer Authentication")
public class CalendarController {
  private final CalendarServiceInterface calendarService;

  @Operation(
      summary = "Получить календарные данные",
      description = "Получение информации о выходных днях для указанного года")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Календарные данные успешно получены",
            content = @Content(schema = @Schema(implementation = MessageDto.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Ошибка сервера при получении данных",
            content = @Content(schema = @Schema(implementation = MessageDto.class)))
      })
  @GetMapping
  @AuthorizedRequired
  public ResponseEntity<MessageDto> getCalendar(
      @Parameter(description = "Год для получения календарных данных", example = "2024")
          @RequestParam
          int year) {
    try {
      String response = calendarService.getDayOffData(year);
      return ResponseEntity.ok(new MessageDto(response));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new MessageDto("Не удалось получить информацию для календаря"));
    }
  }
}
