package com.walmart.kharonte.utilities;

public class HiveYamlConstruction {

  public HiveYamlConstruction() {
  }

  public String yamlStagingFirstPart() {
    return
        "Description: \"Hive query to create stage and target table for $target_table $geo_region_cd inc load\"\n"
            + "HiveQuery : '\n"
            + "\n"
            + "SET hive.merge.smallfiles.avgsize=128000000;\n"
            + "SET hive.merge.size.per.task=128000000;\n"
            + "SET tez.grouping.max-size=128000000;\n"
            + "SET hive.tez.container.size=4096;\n"
            + "SET hive.tez.java.opts=-Xmx3400m;\n"
            + "SET hive.execution.engine=tez;\n"
            + "SET tez.queue.name=$queue;\n"
            + "\n"
            + "ADD JAR $libpath/$libfile;\n"
            + "\n"
            + "CREATE DATABASE IF NOT EXISTS stg_$target_schema;\n"
            + "\n"
            + "DROP TABLE IF EXISTS stg_$target_schema.$target_table;\n"
            + "\n"
            + "CREATE TABLE IF NOT EXISTS stg_$target_schema.$target_table\n"
            + "(";
  }

  public String yamlStagingSecondPart() {
    return ")\nSTORED AS ORC\n"
        + "LOCATION ''/user/$USER/$target_schema/staging/$target_table''\n"
        + "TBLPROPERTIES(''orc.compress''=''SNAPPY'',''transactional''=''false'');\n"
        + "INSERT INTO stg_$target_schema.$target_table\n"
        + "SELECT ";
  }

  public String yamlStagingFinalPart() {
    return "\n  ''$geo_region_cd'' as GEO_REGION_CD,\n"
        + "  ''$op_cmpny_cd'' as OP_CMPNY_CD\n"
        + "from raw_$target_schema.$source_table ;\n"
        + "dfs -chgrp -R $group /user/$USER/$target_schema/staging/$target_table;\n"
        + "dfs -chmod -R $permission /user/$USER/$target_schema/staging/$target_table;'";
  }

  public String yamlProdFirstPart() {
    return
        "Description: \"Hive query to create stage and target table for $target_table $geo_region_cd inc load\"\n"
            + "HiveQuery : '\n"
            + "\n"
            + "SET hive.merge.smallfiles.avgsize=128000000;\n"
            + "SET hive.merge.size.per.task=128000000;\n"
            + "SET tez.grouping.max-size=128000000;\n"
            + "SET hive.tez.container.size=4096;\n"
            + "SET hive.tez.java.opts=-Xmx3400m;\n"
            + "SET hive.execution.engine=tez;\n"
            + "SET tez.queue.name=$queue;\n"
            + "\n"
            + "ADD JAR $libpath/$libfile;\n"
            + "\n"
            + "CREATE DATABASE IF NOT EXISTS $target_schema;\n"
            + "\n"
            + "CREATE TABLE IF NOT EXISTS $target_schema.$target_table\n"
            + "(";
  }

  public String yamlProdSecondPart() {
    return ")\n"
        + "PARTITIONED BY (OP_CMPNY_CD VARCHAR(8), TRANS_DT DATE)\n"
        + "STORED AS ORC\n"
        + "LOCATION ''/user/$USER/$target_schema/catalog/$target_table''\n"
        + "TBLPROPERTIES(''orc.compress''=''SNAPPY'',''transactional''=''false'');\n"
        + "\n"
        + "INSERT INTO TABLE $target_schema.$target_table PARTITION( OP_CMPNY_CD , TRANS_DT)\n"
        + "SELECT";
  }

  public String yamlProdFinalPart() {
    return "\n  ''$geo_region_cd'' as GEO_REGION_CD,\n"
        + "  ''$op_cmpny_cd'' as OP_CMPNY_CD\n"
        + "\nfrom stg_$target_schema.$target_table ;\n"
        + "\n"
        + "dfs -chgrp -R $group /user/$USER/$target_schema/catalog/$target_table;\n"
        + "dfs -chmod -R $permission /user/$USER/$target_schema/catalog/$target_table;\n"
        + "'";
  }

}
