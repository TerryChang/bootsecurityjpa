package com.terry.securityjpa.config.security;

import com.terry.securityjpa.dto.MemberDTO;
import com.terry.securityjpa.entity.Member;
import com.terry.securityjpa.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class LoginUserDetailsService implements UserDetailsService {

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  MessageSource messageSource;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    Member member = memberRepository.findMemberByLoginId(username);

    if (member == null) {
      // 로그인 하는 과정에 있어서 사용자를 발견하지 못하면 로그인 한 아이디가 존재하지 않는건지 아니면 비밀번호가 맞지 않아 못찾는건지 확인한다
      String message = "";
      List<String> messageObjects = new ArrayList<>();
      messageObjects.add(username);
      if (memberRepository.countMemberByLoginId(username) == 0) {
        message = messageSource.getMessage("spring.security.usernameNotFoundException.NotExistUser",
            messageObjects.toArray(new String[messageObjects.size()]), Locale.getDefault());
        throw new UsernameNotFoundException(message);
      }
    }

    MemberDTO memberDTO = new MemberDTO(member);
    return memberDTO;
  }
}
