package org.rosetta.apicomparer.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public abstract class ApiProperties {

  @Value("${api.network.id}")
  private String networkId;
}
