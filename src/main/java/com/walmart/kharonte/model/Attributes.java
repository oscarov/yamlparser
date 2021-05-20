package com.walmart.kharonte.model;

public class Attributes {

  String type;
  String source;
  String desc;
  String pii;
  String nullable;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getPii() {
    return pii;
  }

  public void setPii(String pii) {
    this.pii = pii;
  }

  public String getNullable() {
    return nullable;
  }

  public void setNullable(String nullable) {
    this.nullable = nullable;
  }
}
