package org.rosetta.apicomparer.dto.data.block;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BlockDTO {

  private NetworkIdentifier networkIdentifier;

  private BlockIdentifier blockIdentifier;
}
