package org.rosetta.apicomparer.dto.data.block;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NetworkIdentifier {

  private String blockchain;

  private String network;
}
