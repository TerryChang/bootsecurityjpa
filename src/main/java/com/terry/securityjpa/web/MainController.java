package com.terry.securityjpa.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MainController {

  @Autowired
  PasswordEncoder passwordEncoder;

  @GetMapping(value = { "/", "/index" })
  public String index(Model model) {
    logger.info("access index.html");
    model.addAttribute("test", "");
    return "/index";
  }

  @GetMapping(value = "/login")
  public String login() {
    return "/login";
  }

  @GetMapping(value = "/errorpage/sessionError")
  public String sessionError(@RequestParam(value = "error") String error, Model model) {
    if (error == null)
      error = "etc";
    model.addAttribute("error", error);
    return "/errorpage/sessionError";
  }

  @GetMapping(value = "/password")
  public String password(@RequestParam(value = "password") String password, Model model) {
    String encodedPassword = passwordEncoder.encode(password);

    model.addAttribute("password", password);
    model.addAttribute("encodedPassword", encodedPassword);
    return "/password";
  }
}
