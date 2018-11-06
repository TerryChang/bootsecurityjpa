package com.terry.securityjpa.config.converter;


import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.Set;

public interface CustomConverter<S, T> extends Converter<S, T> {
  List<T> convertAll(List<S> s);
  Set<T> convertAll(Set<S> s);
}
