package com.walmart.kharonte;

import com.walmart.kharonte.exceptions.AttributeException;
import com.walmart.kharonte.model.Attributes;
import com.walmart.kharonte.utilities.HiveYamlConstruction;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class YamlMainGenerator {

  public static void main(String args[]) throws IOException, AttributeException {
    try {
      InputStream inputStream = new FileInputStream("src/main/resources/datamodel.yaml");
      Yaml yaml = new Yaml();
      Map<String, Map<String, Map<String, String>>> data = yaml.load(inputStream);

      for (Map.Entry<String, Map<String, Map<String, String>>> fields : data.entrySet()) {
        LinkedHashMap<String, Attributes> tableFields = new LinkedHashMap<>();
        String tableName = fields.getKey();
        for (Map.Entry<String, Map<String, String>> field : fields.getValue().entrySet()) {
          String fieldName = field.getKey();
          Attributes att = new Attributes();
          for (Map.Entry<String, String> attributes : field.getValue().entrySet()) {
            if ("type".equalsIgnoreCase(attributes.getKey())) {
              att.setType(attributes.getValue());
            } else if ("source".equalsIgnoreCase(attributes.getKey())) {
              att.setSource(attributes.getValue());
            } else if ("desc".equalsIgnoreCase(attributes.getKey())) {
              att.setDesc(attributes.getValue());
            } else {
              throw new AttributeException("Valor del attributo no es type, source o desc");
            }
          }
          tableFields.put(fieldName, att);
        }
        //codigo para generar el script por tabla
        String table = createStagingTable(tableFields);
        File txt = new File("src/main/resources/stg/" + tableName + ".yaml");
        FileWriter writer = new FileWriter(txt);
        writer.write(table.replace(",)",")"));
        writer.close();

        table = createProdTable(tableFields);
        txt = new File("src/main/resources/prod/" + tableName + ".yaml");
        writer = new FileWriter(txt);
        writer.write(table.replace(",)",")"));
        writer.close();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private static String createStagingTable(Map tableFields) {
    HiveYamlConstruction yamlC = new HiveYamlConstruction();
    String createTable = "";
    String ingestTable = "";
    int count =0;
    for (Object campo : tableFields.keySet()) {
      if(count +2 < tableFields.keySet().size()) {
        Attributes a = (Attributes) tableFields.get(campo);
        createTable = createTable + "\n  " + campo + " " + a.getType() + ",";
        ingestTable = ingestTable + "\n  " + a.getSource() + " as " + campo + ",";
        count++;
      }
    }
    return yamlC.yamlStagingFirstPart() + createTable + yamlC.yamlStagingSecondPart() + ingestTable
        + yamlC.yamlStagingFinalPart();
  }

  private static String createProdTable(Map tableFields) {
    HiveYamlConstruction yamlC = new HiveYamlConstruction();
    String createTable = "";
    String ingestTable = "";
    int count =0;
    for (Object campo : tableFields.keySet()) {
      if(count +2 < tableFields.keySet().size()) {
        Attributes a = (Attributes) tableFields.get(campo);
        createTable = createTable + "\n  " + campo + " " + a.getType() + ",";
        ingestTable = ingestTable + "\n  " + a.getSource() + " as " + campo + ",";
        count++;
      }
    }
    return yamlC.yamlProdFirstPart() + createTable + yamlC.yamlProdSecondPart() + ingestTable
        + yamlC.yamlProdFinalPart();
  }

}
