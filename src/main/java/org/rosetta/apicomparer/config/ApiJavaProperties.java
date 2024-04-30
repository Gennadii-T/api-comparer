package org.rosetta.apicomparer.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ApiJavaProperties extends ApiProperties{

  @Value("${api.base-url-java}")
  private String baseUrl;
}
