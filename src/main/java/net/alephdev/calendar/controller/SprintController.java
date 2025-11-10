package net.alephdev.calendar.controller;

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
public class SprintController {

  private final SprintService sprintService;
  private final WebSocketHandler webSocketHandler;

  @Autowired
  public SprintController(SprintService sprintService, WebSocketHandler webSocketHandler) {
    this.sprintService = sprintService;
    this.webSocketHandler = webSocketHandler;
  }

  @GetMapping
  public Page<Sprint> getAllSprints(
      @RequestParam @DefaultValue("0")
          int page,
      
          @RequestParam(required = false)
          String majorVersion,
      
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

  @GetMapping("/{id}")
  public ResponseEntity<Sprint> getSprint(
      @PathVariable Integer id) {
    Sprint sprint = sprintService.getSprint(id);
    return new ResponseEntity<>(sprint, HttpStatus.OK);
  }

  @PrivilegeRequired
  @PostMapping
  public ResponseEntity<Sprint> createSprint(
          @RequestBody
          SprintDto sprint) {
    Sprint createdSprint = sprintService.createSprint(sprint);
    webSocketHandler.notifyClients("sprints");
    return new ResponseEntity<>(createdSprint, HttpStatus.CREATED);
  }

  @PrivilegeRequired
  @PutMapping("/{id}")
  public ResponseEntity<Sprint> updateSprint(
      @PathVariable Integer id,
          @RequestBody
          SprintDto updatedSprint) {
    Sprint sprint = sprintService.updateSprint(id, updatedSprint);
    webSocketHandler.notifyClients("sprints", id);
    return new ResponseEntity<>(sprint, HttpStatus.OK);
  }

  @PrivilegeRequired
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSprint(
      @PathVariable Integer id) {
    ResponseEntity<Void> result = sprintService.deleteSprint(id);
    webSocketHandler.notifyClients("sprints", id);
    return result;
  }

  @GetMapping("/filtered")
  public ResponseEntity<List<SprintTeamDto>> getSprintsByYearAndTeam(
      @RequestParam Integer year,
      
          @RequestParam(required = false)
          String teamName) {
    return ResponseEntity.ok(sprintService.getSprintsByYearAndTeam(year, teamName));
  }

  @GetMapping("/{sprintId}/story-points")
  public ResponseEntity<List<UserStoryPointsDto>> getStoryPointsPerUser(
      @PathVariable Integer sprintId) {
    return ResponseEntity.ok(sprintService.getStoryPointsPerUser(sprintId));
  }

  @GetMapping("/{id}/releases")
  public List<Release> getSprintReleases(
      @PathVariable Integer id) {
    return sprintService.getSprintReleases(id);
  }
}
