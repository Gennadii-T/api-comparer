package org.rosetta.apicomparer.controller;

import org.rosetta.apicomparer.service.DataApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data/compare")
@RequiredArgsConstructor
public class DataComparisonController {

  private final DataApiService dataApiService;

  @GetMapping("/network/list")
  public void compareNetworkList() {
      dataApiService.compareNetworkListResponses();
  }

  @GetMapping("/network/status")
  public void compareNetworkStatus() {
    dataApiService.compareNetworkStatusResponses();
  }

  @GetMapping("/network/options")
  public void compareNetworkOption() {
    dataApiService.compareNetworkOptionsResponses();
  }

  @GetMapping("/block")
  public void compareBlock() {
    dataApiService.compareBlockResponses(2134261);
  }

  @GetMapping("/account/balance")
  public void compareAccountBalance() {
    dataApiService.compareAccountBalanceResponses();
  }

  @GetMapping("/account/coins")
  public void compareAccountCoins() {
    dataApiService.compareAccountCoinsResponses();
  }
}
