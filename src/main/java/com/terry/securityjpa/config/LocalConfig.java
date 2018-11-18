package com.terry.securityjpa.config;

import com.terry.securityjpa.config.annotation.ProfileLocal;
import org.h2.tools.Server;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@ProfileLocal
public class LocalConfig {

  /**
   * Local 개발시 사용하게 되는 H2 Memory DB에 IDE(Eclipse, IntelliJ 등)를 통해 접속할때 TCP Server
   * 를 통해서 접속이 되도록 설정해준다 Spring Boot 설정의 h2.tcpServer.enabled 설정값이 true 일 경우에만
   * Bean이 설정된다 이 설정값이 없을 경우엔 default로 false가 되기 때문에 Bean이 설정되지 않는다 Spring Boot
   * 설정의 h2.tcpServer.port 설정값으로 포트번호를 사용한다 이 값이 설정되어 있지 않을 경우 9092 포트를 사용한다
   *
   * @return
   * @throws SQLException
   */
  @Bean(name = "h2Server", initMethod = "start", destroyMethod = "stop")
  @ConditionalOnExpression("${h2.tcpServer.enabled:false}")
  public Server createTcpServer(@Value("${h2.tcpServer.port:9092}") String h2TcpPort) throws SQLException {
    return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", h2TcpPort);
  }

  /**
   * Local 개발시 사용하게 되는 H2 Memory DB에 Web Browser를 통해 접속할때 Web Server 를 통해서 접속이 되도록
   * 설정해준다 Spring Boot 에서 spring.h2.console.enabled = true 로 설정하는 것과 동일한 기능이 설정된다.
   * Spring Boot 설정의 h2.webServer.enabled 설정값이 true 일 경우에만 Bean이 설정된다 이 설정값이 없을
   * 경우엔 default로 true가 되기 때문에 Bean이 설정된다 Spring Boot 설정의 h2.webServer.port 설정값으로
   * 포트번호를 사용한다 이 값이 설정되어 있지 않을 경우 8082 포트를 사용한다 브라우저에서 http://localhost:8082 로
   * 접속하면 된다
   *
   * @return
   * @throws SQLException
   */
  @Bean(name = "h2WebServer", initMethod = "start", destroyMethod = "stop")
  @ConditionalOnExpression("${h2.webServer.enabled:true}")
  public Server createWebServer(@Value("${h2.webServer.port:8082}") String h2WebPort) throws SQLException {
    return Server.createWebServer("-web", "-webAllowOthers", "-webPort", h2WebPort);
  }

  /**
   * 클래스를 수정할 경우 devtools 로 인해 spring boot 어플리케이션이 재시작이 된다. 이 재시작이 되는 과정에서 H2
   * Memory Database를 이용할 경우 이 Memory Database가 shutdown 되지 않고 재시작이 되기 때문에 재시작 하는
   * 과정에서 schema 를 생성하는 부분에서 에러가 발생한다(내려가지 않았기 때문에 현재 schema가 존재하고 있는 상황에서 다시
   * 생성하려고 하기 때문이다) 그래서 다음의 Bean을 생성해서 DataSource가 Bean에서 해제가 될때 shutdown 명령을 강제로
   * 내리게 함으로써 H2 Database를 강제로 내려가게끔 했다
   * 
   * @param dataSource
   * @return
   */
  @Bean
  @DependsOn("dataSource")
  public DisposableBean embeddedDatabaseShutdownExecutor(DataSource dataSource) {
    /*
     * return new DisposableBean() {
     * 
     * @Override public void destroy() throws Exception {
     * dataSource.getConnection().createStatement().execute("SHUTDOWN"); }
     * 
     * };
     */

    DisposableBean disposableBean = () -> {
      dataSource.getConnection().createStatement().execute("SHUTDOWN");
    };

    return disposableBean;
  }
}
