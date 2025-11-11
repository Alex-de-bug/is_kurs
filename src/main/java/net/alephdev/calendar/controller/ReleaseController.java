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
import net.alephdev.calendar.WebSocketHandler;
import net.alephdev.calendar.annotation.AuthorizedRequired;
import net.alephdev.calendar.annotation.PrivilegeRequired;
import net.alephdev.calendar.dto.ReleaseDto;
import net.alephdev.calendar.models.Release;
import net.alephdev.calendar.service.ReleaseService;
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
@RequestMapping("/api/releases")
@AuthorizedRequired
@RequiredArgsConstructor
@Tag(name = "Релизы", description = "API для управления релизами")
@SecurityRequirement(name = "Bearer Authentication")
public class ReleaseController {

  private final ReleaseService releaseService;
  private final WebSocketHandler webSocketHandler;

  @Operation(summary = "Получить все релизы", description = "Получение списка всех релизов")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список релизов успешно получен",
            content = @Content(schema = @Schema(implementation = Page.class)))
      })
  @GetMapping
  public Page<Release> getAllReleases(
      @Parameter(description = "Номер страницы", example = "0") @RequestParam @DefaultValue("0")
          int page) {
    return releaseService.getAllReleases(page);
  }

  @Operation(
      summary = "Получить релизы спринта",
      description = "Получение списка релизов для конкретного спринта")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список релизов спринта успешно получен",
            content = @Content(schema = @Schema(implementation = Page.class)))
      })
  @GetMapping("/sprint/{sprintId}")
  public Page<Release> getAllReleasesBySprint(
      @Parameter(description = "ID спринта", example = "1") @PathVariable Integer sprintId,
      @Parameter(description = "Номер страницы", example = "0") @RequestParam @DefaultValue("0")
          int page) {
    return releaseService.getAllReleasesBySprint(sprintId, page);
  }

  @Operation(
      summary = "Создать релиз",
      description = "Создание нового релиза (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Релиз успешно создан",
            content = @Content(schema = @Schema(implementation = Release.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа")
      })
  @PrivilegeRequired
  @PostMapping
  public ResponseEntity<?> createRelease(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Данные для создания релиза")
          @RequestBody
          ReleaseDto releaseDto) {
    Release createdRelease = releaseService.createRelease(releaseDto);
    webSocketHandler.notifyClients("release", createdRelease.getSprint().getId());
    return new ResponseEntity<>(createdRelease, HttpStatus.CREATED);
  }

  @Operation(
      summary = "Обновить релиз",
      description = "Обновление существующего релиза (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Релиз успешно обновлен",
            content = @Content(schema = @Schema(implementation = Release.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Релиз не найден")
      })
  @PrivilegeRequired
  @PutMapping("/{id}")
  public ResponseEntity<?> updateRelease(
      @Parameter(description = "ID релиза", example = "1") @PathVariable Integer id,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Обновленные данные релиза")
          @RequestBody
          ReleaseDto updatedRelease) {
    Release release = releaseService.updateRelease(id, updatedRelease);
    webSocketHandler.notifyClients("release", release.getSprint().getId());
    return new ResponseEntity<>(release, HttpStatus.OK);
  }

  @Operation(summary = "Удалить релиз", description = "Удаление релиза (требуются привилегии)")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Релиз успешно удален"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
        @ApiResponse(responseCode = "404", description = "Релиз не найден")
      })
  @PrivilegeRequired
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRelease(
      @Parameter(description = "ID релиза", example = "1") @PathVariable Integer id) {
    ResponseEntity<Void> result = releaseService.deleteRelease(id);
    webSocketHandler.notifyClients("release");
    return result;
  }
}
