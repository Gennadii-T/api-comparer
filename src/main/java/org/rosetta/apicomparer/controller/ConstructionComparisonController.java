package org.rosetta.apicomparer.controller;

import lombok.RequiredArgsConstructor;
import org.rosetta.apicomparer.service.ConstructionApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/construction/compare")
@RequiredArgsConstructor
public class ConstructionComparisonController {

  private final ConstructionApiService apiService;

  @GetMapping("/hash")
  public void compareNetworkList() {
    apiService.compareHashResponses();
  }

  @GetMapping("/metadata")
  public void compareMetadata() {
    apiService.compareMetadataResponses();
  }

  @GetMapping("/derive")
  public void compareDerive() {
    apiService.compareDeriveResponses();
  }

  @GetMapping("/preprocess")
  public void comparePreprocess() {
    apiService.compareSimplePreprocessResponses();
  }

  @GetMapping("/preprocess/withdrawal")
  public void compareWithdrawalPreprocess() {
    apiService.compareWithdrawalPreprocessResponses();
  }

  @GetMapping("/preprocess/pool_registration")
  public void comparePoolRegistrationPreprocess() {
    apiService.comparePoolRegistrationPreprocessResponses();
  }

  @GetMapping("/payloads/simple")
  public void compareSimplePayload() {
    apiService.compareSimplePayloadResponses();
  }

  @GetMapping("/payloads/assets")
  public void comparePayloadWithMultipleAssets() {
    apiService.comparePayloadWithMultipleAssetsResponses();
  }

  @GetMapping("/combine")
  public void compareCombine() {
    apiService.compareCombineResponses();
  }

  @GetMapping("/parse/deposit")
  public void compareParseDeposit() {
    apiService.compareParseDepositResponses();
  }

  @GetMapping("/parse/refund")
  public void compareParseRefund() {
    apiService.compareParseRefundResponses();
  }

  @GetMapping("/parse/signed_multiasset")
  public void compareParseSignedMultiasset() {
    apiService.compareParseSignedMultiassetResponses();
  }

  @GetMapping("/submit")
  public void compareSubmit() {
    apiService.comparePSubmitResponses();
  }
}
