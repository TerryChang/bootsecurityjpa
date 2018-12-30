package com.terry.securityjpa.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terry.securityjpa.config.web.support.LoginMember;
import com.terry.securityjpa.dto.*;
import com.terry.securityjpa.entity.Board;
import com.terry.securityjpa.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
public class BoardController {

  /*
   * @RequestMapping(value="/{member_type}/board/list") public String
   * list(Authentication authentication, Model model, @PathVariable(value =
   * "member_type") String member_type) { MemberDTO memberDTO =
   * (MemberDTO)authentication.getPrincipal(); // 로그인 계정정보 // memberDTO 변수를 이용해서
   * ROLE을 얻어온다 Set<String> roleSet = memberDTO.getRoleSet(); return "board/list";
   * }
   */

  private BoardService boardService;

  private ObjectMapper objectMapper;

  @Value("${app.summernote.imagesDir}")
  private String appSummernoteImagesDir;

  @Autowired
  public BoardController(BoardService boardService, ObjectMapper objectMapper, @Value("${app.summernote.imagesDir}") String appSummernoteImagesDir) {
    this.boardService = boardService;
    this.objectMapper = objectMapper;
    this.appSummernoteImagesDir = appSummernoteImagesDir;
  }
  
  /**
   * @PageDefault 어노테이션 사용시엔 PageableHandlerMethodArgumentResolver 클래스의 oneIndexedParameter 속성값 설정과는 상관없이 1페이지를 볼려면 page 속성을 0으로 주어야 한다
   * 
   * @param boardType
   * @param model
   * @param searchDTO
   * @return
   */
  @GetMapping(value = "/board/{boardType}/list")
  public String list(@PathVariable(value = "boardType") String boardType, Model model, @PageableDefault(page=0, size=10) SearchDTO searchDTO) {
    Page<BoardDTO> result = boardService.list(boardType, searchDTO);
    model.addAttribute("searchDTO", searchDTO);
    model.addAttribute("boardType", boardType);
    model.addAttribute("result", result);
    return "board/list";
  }

  @GetMapping(value = "/board/{boardType}/view")
  public String view(@PathVariable(value = "boardType") String boardType, Model model, @RequestParam(value="idx") Long idx) {
    Board board = boardService.view(idx);
    model.addAttribute("boardType", boardType);
    model.addAttribute("board", board);
    return "board/view";
  }

  @GetMapping(value = "/board/{boardType}/write")
  public String write(@PathVariable(value = "boardType") String boardType, @LoginMember MemberDTO loginUser, Model model) {
    model.addAttribute("boardType", boardType);
    return "board/write";
  }

  @PostMapping(value = "/board/{boardType}/write")
  @ResponseBody
  public String writePost(@PathVariable(value = "boardType") String boardType
      , @LoginMember MemberDTO loginUser
      , BoardDTO boardDTO
      , @RequestParam(value = "boardFile", required = false) List<BoardFileDTO> boardFileDTOList, Model model) throws Exception {
    boardDTO.setBoardType(boardType);
    boardDTO.setBoardFileDTOList(boardFileDTOList);
    boardService.write(boardDTO, loginUser);
    Result result = new Result("00", "");
    String jsonResult = objectMapper.writeValueAsString(result);
    return jsonResult;
  }

  @GetMapping(value = "/board/{boardType}/update")
  public String update(@PathVariable(value = "boardType") String boardType, @LoginMember MemberDTO loginUser, @RequestParam(value="idx") long idx, SearchDTO searchDTO, Model model) {
    Board board = boardService.view(idx);
    model.addAttribute("boardType", boardType);
    model.addAttribute("board", board);
    model.addAttribute("searchDTO", searchDTO);
    return "board/update";
  }

  @PostMapping(value = "/board/{boardType}/update")
  public String updatePost(@PathVariable(value = "boardType") String boardType, @LoginMember MemberDTO loginUser, @ModelAttribute(name="board") BoardDTO boardDTO, SearchDTO searchDTO, Model model) throws IOException {
    boardService.update(boardDTO, loginUser);
    model.addAttribute("boardType", boardType);
    String paramQueryString = "&pageNo=" + searchDTO.getPageNo() + "&pageSize=" + searchDTO.getPageSize() + "&searchType=" + searchDTO.getSearchType() + "&searchWord=" + searchDTO.getSearchWord();
    return "redirect:/board/" + boardType + "/update.html?idx=" + boardDTO.getIdx() + paramQueryString;
  }

  /*
  @RequestMapping(value="/board/imageUpload")
  @ResponseBody
  public String imageUpload(@RequestParam(value="image") FileDTO fileDTO) throws IOException {

    String imageUrl = FileUtils.makeSummernoteImage(appSummernoteImagesDir, fileDTO);
    return imageUrl;
  }
  */
}
