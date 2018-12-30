package com.terry.securityjpa.entity;

import com.terry.securityjpa.config.converter.attribute.BoardTypeConverter;
import com.terry.securityjpa.config.jpa.listener.CreateUpdateDTAuditEntityListener;
import com.terry.securityjpa.entity.audit.CreateUpdateDTAuditable;
import com.terry.securityjpa.entity.embed.CreateUpdateDT;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
@Entity
@EntityListeners(value = { CreateUpdateDTAuditEntityListener.class })
@Table(name = "BOARD")
@SequenceGenerator(name = "BoardSequenceGenerator", sequenceName = "SEQ_BOARD", initialValue = 1, allocationSize = 1)
@Access(AccessType.FIELD)
public class Board implements CreateUpdateDTAuditable {

  private Long idx;

  @Column(name = "TITLE")
  private String title;

  @Column(name = "CONTENTS")
  @Lob
  private String contents;
  
  @Column(name="BOARD_TYPE")
  @Convert(converter = BoardTypeConverter.class)
  private String boardType;

  @Embedded
  @EqualsAndHashCode.Exclude
  private CreateUpdateDT createUpdateDT;

  @Id
  @Column(name = "IDX")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BoardSequenceGenerator")
  @Access(AccessType.PROPERTY)
  public Long getIdx() {
    return idx;
  }

  @ManyToOne
  @JoinColumn(name = "MEMBER_IDX", nullable = false)
  @ToString.Exclude
  private Member member;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "board", cascade = CascadeType.ALL)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<BoardFile> boardFileList = new ArrayList<>();

/*
@Builder 어노테이션을 사용하면 객체를 생성할때 다음가 같이 해야 한다
Board.builder().member(member).title(title).contents(contents).boardType(boardType).build();
 */

  public void addBoardFile(BoardFile boardFile) {
    boardFileList.add(boardFile);
    if (boardFile.getBoard() != this) {
      boardFile.setBoard(this);
    }
  }

  public void removeBoardFile(BoardFile boardFile) {
    if (boardFile.getBoard() == this) {
      boardFileList.remove(boardFile);
    }
    boardFile.setBoard(null);
  }
}
