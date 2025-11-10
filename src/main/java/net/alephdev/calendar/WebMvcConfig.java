package net.alephdev.calendar;

import java.util.List;
import net.alephdev.calendar.annotation.implementation.AuthInterceptor;
import net.alephdev.calendar.annotation.implementation.CurrentUserArgumentResolver;
import net.alephdev.calendar.annotation.implementation.PrivilegeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  @Autowired private AuthInterceptor authInterceptor;

  @Autowired private PrivilegeInterceptor privilegeInterceptor;

  @Autowired private CurrentUserArgumentResolver currentUserArgumentResolver;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(currentUserArgumentResolver);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authInterceptor);
    registry.addInterceptor(privilegeInterceptor);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOrigins("http://localhost:4200")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true);
  }
}
