package org.rosetta.apicomparer.dto;

import lombok.Builder;
import lombok.Data;
import org.json.JSONObject;

@Builder
@Data
public class BalanceInfo {

  private String symbol;

  private int decimals;

  private String value;

  private String policyId;

  private int count;

  public String toString() {
    return symbol + ":" + decimals + ":" + value + ":" + policyId;
  }

  public JSONObject toJSONObject() {
    JSONObject obj = new JSONObject();
    obj.put("symbol", symbol);
    obj.put("decimals", decimals);
    obj.put("value", value);
    obj.put("policyId", policyId);
    return obj;
  }

  public void incrementCount() {
    count++;
  }
}
