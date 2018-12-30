package com.terry.securityjpa.entity;

import java.time.LocalDateTime;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.terry.securityjpa.config.jpa.listener.CreateUpdateDTAuditEntityListener;
import com.terry.securityjpa.entity.embed.CreateUpdateDT;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
@Entity
@Table(name = "BOARD_FILE")
@SequenceGenerator(name = "BoardFileSequenceGenerator", sequenceName = "SEQ_BOARD_FILE", initialValue = 1, allocationSize = 1)
@Access(AccessType.FIELD)
public class BoardFile {
  private Long idx;

  @Column(name = "SAVE_PATH")
  private String savePath;

  @Column(name = "ORG_FILE_NAME")
  private String orgFileName;

  @Column(name = "REAL_FILE_NAME")
  private String realFileName;

  @Column(name = "EXTENSION")
  private String extension;

  @Column(name = "SIZE")
  private Long size;

  @Column(name = "CREATE_DT", nullable = false, updatable = false)
  private LocalDateTime createDT;

  @Id
  @Column(name = "IDX")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BoardFileSequenceGenerator")
  @Access(AccessType.PROPERTY)
  public Long getIdx() {
    return idx;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "BOARD_IDX")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Board board;

  public void setBoard(Board board) {
    this.board = board;
    if (board != null && !board.getBoardFileList().contains(this)) {
      board.getBoardFileList().add(this);
    }
  }
}