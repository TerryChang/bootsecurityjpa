package com.terry.securityjpa.config.converter;

import com.terry.securityjpa.dto.MemberDTO;
import com.terry.securityjpa.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class MemberToMemberDTOConverter implements CustomConverter<Member, MemberDTO> {

  @Autowired
  @Lazy
  ConversionService conversionService;

  @Override
  public MemberDTO convert(Member member) {
    return new MemberDTO(member);
  }

  @Override
  public List<MemberDTO> convertAll(List<Member> members) {
    List<MemberDTO> result = new ArrayList<>();
    for (Member member : members) {
      result.add(new MemberDTO(member));
    }
    return result;
  }

  @Override
  public Set<MemberDTO> convertAll(Set<Member> members) {
    Set<MemberDTO> result = new HashSet<>();
    for (Member member : members) {
      result.add(new MemberDTO(member));
    }
    return result;
  }

}
