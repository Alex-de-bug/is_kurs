package net.alephdev.calendar.controller;

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
public class StatusController {

  private final StatusService statusService;
  private final WebSocketHandler webSocketHandler;

  @Autowired
  public StatusController(StatusService statusService, WebSocketHandler webSocketHandler) {
    this.statusService = statusService;
    this.webSocketHandler = webSocketHandler;
  }

  @GetMapping
  public List<Status> getAllStatuses() {
    return statusService.getAllStatuses();
  }

  @PrivilegeRequired
  @PostMapping
  public ResponseEntity<Status> createStatus(
          @RequestBody
          Status status) {
    Status createdStatus = statusService.createStatus(status);
    webSocketHandler.notifyClients("status");
    return new ResponseEntity<>(createdStatus, HttpStatus.CREATED);
  }

  @PrivilegeRequired
  @PutMapping("/{id}")
  public ResponseEntity<Status> updateStatus(
      @PathVariable Integer id,
          @RequestBody
          Status updatedStatus) {
    Status status = statusService.updateStatus(id, updatedStatus);
    webSocketHandler.notifyClients("status", id);
    return new ResponseEntity<>(status, HttpStatus.OK);
  }

  @PrivilegeRequired
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteStatus(
      @PathVariable Integer id) {
    ResponseEntity<Void> result = statusService.deleteStatus(id);
    webSocketHandler.notifyClients("status", id);
    return result;
  }
}
