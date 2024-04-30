package org.rosetta.apicomparer.utility;

import org.rosetta.apicomparer.dto.data.block.BlockDTO;
import org.rosetta.apicomparer.dto.data.block.BlockIdentifier;
import org.rosetta.apicomparer.dto.data.block.NetworkIdentifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DTOBuilder {

  @Value("${api.blockchain.type}")
  private String blockchainType;

  @Value("${api.network.id}")
  private String networkId;

  public BlockDTO createBlockDTO(int startBlock) {
    return BlockDTO.builder()
        .networkIdentifier(createNetworkIdentifier())
        .blockIdentifier(createBlockIdentifier(startBlock))
        .build();
  }

  private BlockIdentifier createBlockIdentifier(int startBlock) {
    return BlockIdentifier.builder()
        .index(startBlock)
        .build();
  }

  private NetworkIdentifier createNetworkIdentifier() {
    return NetworkIdentifier.builder()
        .blockchain(blockchainType)
        .network(networkId)
        .build();
  }
}
