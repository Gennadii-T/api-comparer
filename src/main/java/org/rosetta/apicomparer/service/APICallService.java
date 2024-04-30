package org.rosetta.apicomparer.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class APICallService {
  
  private static final String SEPARATOR = "---------------------";

  private final ConstructionApiService constructionApiService;
  private final DataApiService dataApiService;

  @Value("${api.block.index}")
  private int startBlock;
  
  @PostConstruct
  public void apiCallsAfterStartup() {
    log.info("Construction APIs:");
    log.info(SEPARATOR);
    constructionApiService.compareDeriveResponses();
    log.info(SEPARATOR);
    constructionApiService.compareHashResponses();
    log.info(SEPARATOR);
    constructionApiService.compareMetadataResponses();
    log.info(SEPARATOR);
    constructionApiService.compareSimplePreprocessResponses();
    log.info(SEPARATOR);
    constructionApiService.compareWithdrawalPreprocessResponses();
    log.info(SEPARATOR);
    constructionApiService.compareSimplePayloadResponses();
    log.info(SEPARATOR);
    constructionApiService.comparePayloadWithMultipleAssetsResponses();
    log.info(SEPARATOR);
    constructionApiService.compareCombineResponses();
    log.info(SEPARATOR);
    constructionApiService.compareParseSignedMultiassetResponses();
    log.info(SEPARATOR);
    constructionApiService.compareParseDepositResponses();
    log.info(SEPARATOR);
    constructionApiService.compareParseRefundResponses();
    log.info(SEPARATOR);
    constructionApiService.compareParseSignedMultiassetResponses();
    log.info(SEPARATOR);

    log.info(SEPARATOR);
    log.info("Data APIs:");
    log.info(SEPARATOR);

    dataApiService.compareNetworkListResponses();
    log.info(SEPARATOR);
    dataApiService.compareNetworkStatusResponses();
    log.info(SEPARATOR);
    dataApiService.compareNetworkOptionsResponses();
    log.info(SEPARATOR);
    dataApiService.compareAccountBalanceResponses();
    log.info(SEPARATOR);
    dataApiService.compareAccountCoinsResponses();
    log.info(SEPARATOR);
    dataApiService.compareBlockResponses(startBlock);
    log.info(SEPARATOR);
  }
}
