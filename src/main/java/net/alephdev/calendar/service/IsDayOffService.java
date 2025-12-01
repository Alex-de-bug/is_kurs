package net.alephdev.calendar.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.alephdev.calendar.interfaces.CalendarServiceInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class IsDayOffService implements CalendarServiceInterface {
  @Value("${service.calendar.url}")
  private String calendarUrl;

  private final RestTemplate restTemplate;

  public IsDayOffService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public String getDayOffData(int year) {
    String url = String.format(calendarUrl, year);
    return restTemplate.getForObject(url, String.class);
  }
}
