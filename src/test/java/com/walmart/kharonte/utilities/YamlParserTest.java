package com.walmart.kharonte.utilities;

import com.walmart.kharonte.model.Attributes;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class YamlParserTest {

  @Test
  public void test() throws IOException {
    InputStream inputStream = new FileInputStream("src/test/resources/test.yaml");
    Yaml yaml = new Yaml();
    Map<String, Map<String, Map<String, String>>> data = yaml.load(inputStream);
    Map<String, Attributes> tableFields = new HashMap<>();

    for (Map.Entry<String, Map<String, Map<String, String>>> fields : data.entrySet()) {
      String tableName = fields.getKey();
      for (Map.Entry<String, Map<String, String>> field : fields.getValue().entrySet()) {
        String fieldName = field.getKey();
        Attributes att = new Attributes();
        for (Map.Entry<String, String> attributes : field.getValue().entrySet()) {
          if("type".equalsIgnoreCase(attributes.getKey())){
            att.setType(attributes.getValue());
          }else if ("source".equalsIgnoreCase(attributes.getKey())){
            att.setSource(attributes.getValue());
          }else if ("desc".equalsIgnoreCase(attributes.getKey())){
            att.setDesc(attributes.getValue());
          }
        }
        tableFields.put(fieldName,att);
      }
      //codigo para generar el script por tabla
      String table = createStagingTable(tableFields);
      File txt = new File("src/test/resources/stg/"+tableName+".yaml");
      FileWriter writer = new FileWriter(txt);
      writer.write(table);
      writer.close();

      table = createProdTable(tableFields);
      txt = new File("src/test/resources/prod/"+tableName+".yaml");
      writer = new FileWriter(txt);
      writer.write(table);
      writer.close();
    }
  }

  private String createStagingTable( Map tableFields){
    HiveYamlConstruction yamlC = new HiveYamlConstruction();
    String createTable = "";
    String ingestTable = "";
    for(Object campo : tableFields.keySet()){
      Attributes a = (Attributes) tableFields.get(campo);
      createTable = createTable + "\n  " + campo + " " + a.getType() + ",";
      ingestTable = ingestTable + "\n  " + a.getSource() + " as " + campo +",";
    }
    return yamlC.yamlStagingFirstPart() + createTable + yamlC.yamlStagingSecondPart() + ingestTable + yamlC.yamlStagingFinalPart();
  }

  private String createProdTable( Map tableFields){
    HiveYamlConstruction yamlC = new HiveYamlConstruction();
    String createTable = "";
    String ingestTable = "";
    for(Object campo : tableFields.keySet()){
      Attributes a = (Attributes) tableFields.get(campo);
      createTable = createTable + "\n  " + campo + " " + a.getType() + ",";
      ingestTable = ingestTable + "\n  " + a.getSource() + " as " + campo +",";
    }
    return yamlC.yamlProdFirstPart() + createTable + yamlC.yamlProdSecondPart() + ingestTable + yamlC.yamlProdFinalPart();
  }


}


