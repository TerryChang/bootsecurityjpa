package com.terry.securityjpa.config;

import com.terry.securityjpa.config.cache.CacheService;
import com.terry.securityjpa.dto.BoardDTO;
import com.terry.securityjpa.dto.BoardFileDTO;
import com.terry.securityjpa.entity.Board;
import com.terry.securityjpa.entity.BoardFile;
import com.terry.securityjpa.repository.RoleRepository;
import com.terry.securityjpa.repository.UrlResourcesRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

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

    // BoardFileDTO -> BoardFile 클래스 변환
    modelMapper.addMappings(new PropertyMap<BoardFileDTO, BoardFile>() {
      @Override
      protected void configure() {
        map().setSavePath(source.getSavePath());
        map().setOrgFileName(source.getOrgFileName());
        map().setRealFileName(source.getRealFileName());
        map().setExtension(source.getExtension());
        map().setSize(source.getSize());
      }
    });

    // BoardFile -> BoardFileDTO 클래스 변환
    modelMapper.addMappings(new PropertyMap<BoardFile, BoardFileDTO>() {
      @Override
      protected void configure() {
        map().setIdx(source.getIdx());
        map().setSavePath(source.getSavePath());
        map().setOrgFileName(source.getOrgFileName());
        map().setRealFileName(source.getRealFileName());
        map().setExtension(source.getExtension());
        map().setSize(source.getSize());
      }
    });

    // BoardDTO -> Board 클래스 변환
    modelMapper.addMappings(new PropertyMap<BoardDTO, Board>() {
      @Override
      protected void configure() {
        map().setTitle(source.getTitle());
        map().setContents(source.getContents());
        map().setBoardType(source.getBoardType());
      }
    });
    // Board -> BoardDTO 클래스 변환
    modelMapper.addMappings(new PropertyMap<Board, BoardDTO>() {
      @Override
      protected void configure() {
        map().setIdx(source.getIdx());
        map().setLoginId(source.getMember().getLoginId());
        map().setTitle(source.getTitle());
        map().setContents(source.getContents());
        map().setBoardType(source.getBoardType());
        map().setCreateDT(source.getCreateUpdateDT().getCreateDT());
        map().setUpdateDT(source.getCreateUpdateDT().getUpdateDT());
      }
    });


    return modelMapper;
  }
}
