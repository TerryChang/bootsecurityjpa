package com.terry.securityjpa.security;

import com.terry.securityjpa.config.WithMockCustomUser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @WithMockCustomUser(로그인아이디)를 줌으로써 주어진 로그인 아이디로 로그인 한 상황을 만들어낼 수 있다
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
@Rollback
@Transactional
public class SampleTest {

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

  /**
   * 올바른 로그인 아이디와 비밀번호를 주었을때 /index.html로 이동하는지에 대한 테스트
   * @throws Exception
   */
  @Test
  public void login() throws Exception {
    mockMvc.perform(
        post("/login_proc.html")
        .param("loginId", "junmemberid")
        .param("password", "1234")
    ).andDo(print()).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/index.html"));
  }

  @Test
  public void loginInvalidId() throws Exception {
    mockMvc.perform(
        post("/login_proc.html")
            .param("loginId", "junmemberid1")
            .param("password", "1234")
    ).andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION.message", "로그인아이디 junmemberid1 가 없습니다."))
        .andExpect(redirectedUrl("/login.html?error=true"));
  }

  @Test
  @WithMockCustomUser("junmemberid")
  public void loginAssociateUserTest() throws Exception {
    mockMvc.perform(
        get("/associate/board/list.html")
    ).andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @WithMockCustomUser("jungmemberid")
  public void loginRegularUserTest() throws Exception {
    mockMvc.perform(
        get("/regular/board/list.html")
    ).andDo(print())
        .andExpect(status().isOk());
  }

  /**
   * 준회원 권한을 가진 사용자가 정회원 권한만 접근할 수 있는 URL에 대한 테스트
   * @throws Exception
   */
  @Test
  @WithMockCustomUser("junmemberid")
  public void loginMismatchRoleAndUrlTest() throws Exception {
    mockMvc.perform(
        get("/regular/board/list.html")
    ).andDo(print())
        .andExpect(status().is(403));
  }

}
