-- Sequence 정의 시작
CREATE SEQUENCE SEQ_AUTHORITY
START WITH 1
INCREMENT BY 1
  NOCYCLE
  NOCACHE;

CREATE SEQUENCE SEQ_URL_RESOURCES
  START WITH 1
  INCREMENT BY 1
  NOCYCLE
  NOCACHE;

CREATE SEQUENCE SEQ_ROLE
  START WITH 1
  INCREMENT BY 1
  NOCYCLE
  NOCACHE;

CREATE SEQUENCE SEQ_MEMBER
  START WITH 1
  INCREMENT BY 1
  NOCYCLE
  NOCACHE;
  
CREATE SEQUENCE SEQ_BOARD
  START WITH 1
  INCREMENT BY 1
  NOCYCLE
  NOCACHE;
  
CREATE SEQUENCE SEQ_BOARD_FILE
  START WITH 1
  INCREMENT BY 1
  NOCYCLE
  NOCACHE;
-- Sequence 정의 끝

-- 테이블 정의 시작
CREATE TABLE AUTHORITY (
  IDX BIGINT DEFAULT SEQ_AUTHORITY.NEXTVAL PRIMARY KEY,
  AUTHORITY_NAME VARCHAR(40) NOT NULL,
  AUTHORITY_DESC VARCHAR(255) NOT NULL,
  CREATE_DT DATETIME NOT NULL,
  UPDATE_DT DATETIME
);

CREATE TABLE URL_RESOURCES (
  IDX BIGINT DEFAULT SEQ_URL_RESOURCES.NEXTVAL PRIMARY KEY,
  RESOURCE_NAME VARCHAR(40) NOT NULL,
  RESOURCE_PATTERN VARCHAR(255) NOT NULL,
  REQUEST_MATCHER_TYPE VARCHAR(20) NOT NULL DEFAULT 'ANT',
  ORDERNUM SMALLINT NOT NULL DEFAULT 1,
  CREATE_DT DATETIME NOT NULL,
  UPDATE_DT DATETIME
);

CREATE TABLE URL_RESOURCES_AUTHORITY (
  URL_RESOURCES_IDX BIGINT NOT NULL,
  AUTHORITY_IDX BIGINT NOT NULL
);

CREATE TABLE ROLE (
  IDX BIGINT DEFAULT SEQ_ROLE.NEXTVAL PRIMARY KEY,
  ROLE_NAME VARCHAR(40) NOT NULL,
  ROLE_DESC VARCHAR(255) NOT NULL,
  PARENT_ROLE_IDX BIGINT NULL,
  CREATE_DT DATETIME NOT NULL,
  UPDATE_DT DATETIME
);

CREATE TABLE ROLE_AUTHORITY (
  ROLE_IDX BIGINT NOT NULL,
  AUTHORITY_IDX BIGINT NOT NULL
);

CREATE TABLE MEMBER (
  IDX BIGINT DEFAULT SEQ_MEMBER.NEXTVAL PRIMARY KEY,
  NAME VARCHAR(40) NOT NULL,
  LOGIN_ID VARCHAR(255) NOT NULL,
  LOGIN_PASSWORD VARCHAR(255) NOT NULL,
  CREATE_DT DATETIME NOT NULL,
  UPDATE_DT DATETIME
);

CREATE TABLE MEMBER_ROLE (
  MEMBER_IDX BIGINT NOT NULL,
  ROLE_IDX BIGINT NOT NULL
);

CREATE TABLE MEMBER_AUTHORITY (
  MEMBER_IDX BIGINT NOT NULL,
  AUTHORITY_IDX BIGINT NOT NULL
);

CREATE TABLE BOARD (
  IDX BIGINT DEFAULT SEQ_BOARD.NEXTVAL PRIMARY KEY,
  MEMBER_IDX BIGINT NOT NULL,
  TITLE VARCHAR(100) NOT NULL,
  CONTENTS CLOB NOT NULL,
  CREATE_DT DATETIME NOT NULL,
  UPDATE_DT DATETIME
);

CREATE TABLE BOARD_FILE (
  IDX BIGINT DEFAULT SEQ_BOARD_FILE.NEXTVAL PRIMARY KEY,
  BOARD_IDX BIGINT NOT NULL,
  SAVE_PATH VARCHAR(100) NOT NULL,
  REAL_NAME VARCHAR(100) NOT NULL,
  ORG_NAME VARCHAR(100) NOT NULL,
  EXT VARCHAR(10) NULL,
  CREATE_DT DATETIME NOT NULL
);
-- 테이블 정의 끝

-- 제약조건(ex: Foreign Key) 정의 시작
ALTER TABLE URL_RESOURCES_AUTHORITY ADD PRIMARY KEY (URL_RESOURCES_IDX, AUTHORITY_IDX);
ALTER TABLE URL_RESOURCES_AUTHORITY
  ADD FOREIGN KEY (URL_RESOURCES_IDX)
REFERENCES URL_RESOURCES(IDX);
ALTER TABLE URL_RESOURCES_AUTHORITY
  ADD FOREIGN KEY (AUTHORITY_IDX)
REFERENCES AUTHORITY(IDX);

ALTER TABLE ROLE ADD FOREIGN KEY(PARENT_ROLE_IDX) REFERENCES ROLE(IDX);

ALTER TABLE ROLE_AUTHORITY ADD PRIMARY KEY (ROLE_IDX, AUTHORITY_IDX);
ALTER TABLE ROLE_AUTHORITY
  ADD FOREIGN KEY (ROLE_IDX)
REFERENCES ROLE(IDX);
ALTER TABLE ROLE_AUTHORITY
  ADD FOREIGN KEY (AUTHORITY_IDX)
REFERENCES AUTHORITY(IDX);

ALTER TABLE MEMBER_ROLE ADD PRIMARY KEY (MEMBER_IDX, ROLE_IDX);
ALTER TABLE MEMBER_ROLE
  ADD FOREIGN KEY (MEMBER_IDX)
REFERENCES MEMBER(IDX);
ALTER TABLE MEMBER_ROLE
  ADD FOREIGN KEY (ROLE_IDX)
REFERENCES ROLE(IDX);

ALTER TABLE MEMBER_AUTHORITY ADD PRIMARY KEY (MEMBER_IDX, AUTHORITY_IDX);
ALTER TABLE MEMBER_AUTHORITY
  ADD FOREIGN KEY (MEMBER_IDX)
REFERENCES MEMBER(IDX);
ALTER TABLE MEMBER_AUTHORITY
  ADD FOREIGN KEY (AUTHORITY_IDX)
REFERENCES AUTHORITY(IDX);

ALTER TABLE BOARD_FILE 
  ADD FOREIGN KEY(BOARD_IDX) 
REFERENCES BOARD(IDX);
-- 제약조건(ex: Foreign Key) 정의 끝