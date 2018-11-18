package com.terry.securityjpa.config.converter.attribute;

import javax.persistence.AttributeConverter;

public class BoardTypeConverter implements AttributeConverter<String, Integer> {

  @Override
  public Integer convertToDatabaseColumn(String attribute) {
    // TODO Auto-generated method stub
    int result = 0;
    switch(attribute) {
    case "associate" :
      result = 1;
      break;
    case "regular" :
      result = 2;
      break;
    case "admin" :
      result = 99;
      break;
    default :
      result = 0;
    }
    return result;
  }

  @Override
  public String convertToEntityAttribute(Integer dbData) {
    // TODO Auto-generated method stub
    String result = null;
    switch(dbData) {
    case 1:
      result = "associate";
      break;
    case 2:
      result = "regular";
      break;
    case 99:
      result = "admin";
      break;
    default :
      result = null;
    }
    return result;
  }

}
