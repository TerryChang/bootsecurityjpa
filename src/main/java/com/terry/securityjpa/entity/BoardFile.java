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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
@Entity
@Table(name = "BOARD_FILE")
@SequenceGenerator(name="BoardFileSequenceGenerator", sequenceName="SEQ_BOARD_FILE", initialValue=1, allocationSize=1)
@Access(AccessType.FIELD)
public class BoardFile {
	private Long idx;
	
	@Column(name="SAVE_PATH")
	private String savePath;
	
	@Column(name="REAL_NAME")
	private String realName;
	
	@Column(name="ORG_NAME")
	private String orgName;
	
	@Column(name="EXT")
	private String ext;
	
	@Column(name="CREATE_DT", nullable = false, updatable = false)
    private LocalDateTime createDT = LocalDateTime.now();
	
	@Id
	@Column(name="IDX")
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="BoardSequenceGenerator")
	@Access(AccessType.PROPERTY)
	public Long getIdx() {
	  return idx;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="BOARD_IDX")
	@ToString.Exclude
	private Board board;
	
	public void setBoard(Board board) {
	  this.board = board;
	  if(board != null && !board.getBoardFileSet().contains(this)) {
			board.getBoardFileSet().add(this);
	  }
	}
}
