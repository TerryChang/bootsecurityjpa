package com.terry.securityjpa.config;

import com.terry.securityjpa.config.cache.CacheService;
import com.terry.securityjpa.dto.BoardDTO;
import com.terry.securityjpa.entity.Board;
import com.terry.securityjpa.repository.RoleRepository;
import com.terry.securityjpa.repository.UrlResourcesRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class MainConfig {

  @Bean
  public CacheService cacheService(UrlResourcesRepository urlResourcesRepository, RoleRepository roleRepository) {
    CacheService cacheService = new CacheService(urlResourcesRepository, roleRepository);
    return cacheService;
  }

  /**
   * Class 와 Class 간의 변환을 담당하는 ModelMapper 클래스 객체 Bean 선언
   * @return
   */
  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();

    // BoardDTO -> Board 클래스 변환
    modelMapper.addMappings(new PropertyMap<BoardDTO, Board>() {
      @Override
      protected void configure() {

      }
    });
    // Board -> BoardDTO 클래스 변환
    modelMapper.addMappings(new PropertyMap<Board, BoardDTO>() {
      @Override
      protected void configure() {
        map().setLoginId(source.getMember().getLoginId());
        map().setCreateDT(source.getCreateUpdateDT().getCreateDT());
        map().setUpdateDT(source.getCreateUpdateDT().getUpdateDT());
      }
    });
    return modelMapper;
  }
}
