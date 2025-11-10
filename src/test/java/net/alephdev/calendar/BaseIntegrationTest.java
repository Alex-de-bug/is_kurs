package net.alephdev.calendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import net.alephdev.calendar.dto.JwtRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
  @Autowired protected MockMvc mockMvc;

  @Autowired protected ObjectMapper objectMapper;

  protected String getJwtToken(String username, String password) throws Exception {
    JwtRequestDto loginRequest = new JwtRequestDto(username, password);

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andReturn();

    return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
  }
}
