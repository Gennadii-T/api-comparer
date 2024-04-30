package org.rosetta.apicomparer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfig {

  @Bean(name = "restTemplateTS")
  public RestTemplate restTemplateTS(RestTemplateBuilder builder, ApiTSProperties apiTSProperties) {
    return builder
        .rootUri(apiTSProperties.getBaseUrl())
        .build();
  }

  @Bean(name = "restTemplateJava")
  public RestTemplate restTemplateJava(RestTemplateBuilder builder, ApiJavaProperties apiJavaProperties) {
    return builder
        .rootUri(apiJavaProperties.getBaseUrl())
        .build();
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    return mapper;
  }
}
