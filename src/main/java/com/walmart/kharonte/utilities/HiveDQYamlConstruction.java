package com.walmart.kharonte.utilities;

public class HiveDQYamlConstruction {

  public HiveDQYamlConstruction() {
  }

  public String yamlDQFirstPart() {
    return
        "component: \"Data Quality Framework\"\n"
            + "info:\n"
            + "  description: \"Data Quality checks to be validated for k2_cl_pos_trans\"\n"
            + "  hiveTableName: \"stg_$target_schema.$target_table\"\n"
            + "rules:\n";
  }


}
