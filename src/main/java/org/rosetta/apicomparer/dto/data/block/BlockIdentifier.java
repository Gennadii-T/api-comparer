package org.rosetta.apicomparer.dto.data.block;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BlockIdentifier {

  private Integer index;

  private String hash;
}
