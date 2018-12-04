package com.terry.securityjpa.web;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.terry.securityjpa.config.WithMockCustomUser;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
@Rollback
@Transactional
public class BoardControllerTest {

  private MockMvc mockMvc;

  // @Autowired
  // private FilterChainProxy filterChainProxy;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void before() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
  }

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  
  @Test
  @WithMockCustomUser("associate_id")
  public void 준회원_게시판_목록_파라미터_바인딩_테스트() throws Exception {
    mockMvc.perform(get("/board/associate/list.html"))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  @WithMockCustomUser("regular_id")
  public void PreAuthorize_customHasRole_정회원_테스트() throws Exception {
    mockMvc.perform(get("/test/board/list.html"))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @WithMockCustomUser("associate_id")
  public void PreAuthorize_customHasRole_준회원_테스트() throws Exception {
    mockMvc.perform(get("/test/board/list.html"))
        .andDo(print())
        .andExpect(status().is(403));
  }
}
