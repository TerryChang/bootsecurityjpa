INSERT INTO AUTHORITY(AUTHORITY_NAME, AUTHORITY_DESC, CREATE_DT)
VALUES('ANNONYMOUS', '익명권한', CURRENT_TIMESTAMP()); -- 1
INSERT INTO AUTHORITY(AUTHORITY_NAME, AUTHORITY_DESC, CREATE_DT)
VALUES('BOARD_ASSOCIATE_READ', '준회원 게시판 읽기', CURRENT_TIMESTAMP()); -- 2
INSERT INTO AUTHORITY(AUTHORITY_NAME, AUTHORITY_DESC, CREATE_DT)
VALUES('BOARD_ASSOCIATE_WRITE', '준회원 게시판 쓰기', CURRENT_TIMESTAMP()); -- 3
INSERT INTO AUTHORITY(AUTHORITY_NAME, AUTHORITY_DESC, CREATE_DT)
VALUES('BOARD_REGULAR_READ', '정회원 게시판 읽기', CURRENT_TIMESTAMP()); -- 4
INSERT INTO AUTHORITY(AUTHORITY_NAME, AUTHORITY_DESC, CREATE_DT)
VALUES('BOARD_REGULAR_WRITE', '정회원 게시판 쓰기', CURRENT_TIMESTAMP()); -- 5
INSERT INTO AUTHORITY(AUTHORITY_NAME, AUTHORITY_DESC, CREATE_DT)
VALUES('BOARD_ADMIN_READ', '관리자 게시판 읽기', CURRENT_TIMESTAMP()); -- 6
INSERT INTO AUTHORITY(AUTHORITY_NAME, AUTHORITY_DESC, CREATE_DT)
VALUES('BOARD_ADMIN_WRITE', '관리자 게시판 쓰기', CURRENT_TIMESTAMP()); -- 7
INSERT INTO AUTHORITY(AUTHORITY_NAME, AUTHORITY_DESC, CREATE_DT)
VALUES('BOARD_NOTICE_READ', '공지사항 게시판 읽기', CURRENT_TIMESTAMP()); -- 8
INSERT INTO AUTHORITY(AUTHORITY_NAME, AUTHORITY_DESC, CREATE_DT)
VALUES('BOARD_NOTICE_WRITE', '공지사항 게시판 쓰기', CURRENT_TIMESTAMP()); -- 9

INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 1
VALUES('준회원 게시판 목록', '/board/associate/list.html', 'ANT', 1, CURRENT_TIMESTAMP());
INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 2
VALUES('준회원 게시판 상세화면', '/board/associate/view.html', 'ANT', 1, CURRENT_TIMESTAMP());
INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 3
VALUES('준회원 게시판 등록', '/board/associate/write.html', 'ANT', 1, CURRENT_TIMESTAMP());
INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 4
VALUES('준회원 게시판 수정', '/board/associate/update.html', 'ANT', 1, CURRENT_TIMESTAMP());
INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 5
VALUES('준회원 게시판 삭제', '/board/associate/delete.html', 'ANT', 1, CURRENT_TIMESTAMP());

INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 6
VALUES('정회원 게시판 목록', '/board/regular/list.html', 'ANT', 1, CURRENT_TIMESTAMP());
INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 7
VALUES('전회원 게시판 상세화면', '/board/regular/view.html', 'ANT', 1, CURRENT_TIMESTAMP());
INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 8
VALUES('정회원 게시판 등록', '/board/regular/write.html', 'ANT', 1, CURRENT_TIMESTAMP());
INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 9
VALUES('정회원 게시판 수정', '/board/regular/update.html', 'ANT', 1, CURRENT_TIMESTAMP());
INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 10
VALUES('정회원 게시판 삭제', '/board/regular/delete.html', 'ANT', 1, CURRENT_TIMESTAMP());

INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 11
VALUES('관리자 게시판 목록', '/board/admin/list.html', 'ANT', 1, CURRENT_TIMESTAMP());
INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 12
VALUES('관리자 게시판 상세화면', '/board/admin/view.html', 'ANT', 1, CURRENT_TIMESTAMP());
INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 13
VALUES('관리자 게시판 등록', '/board/admin/write.html', 'ANT', 1, CURRENT_TIMESTAMP());
INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 14
VALUES('관리자 게시판 수정', '/board/admin/update.html', 'ANT', 1, CURRENT_TIMESTAMP());
INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 15
VALUES('관리자 게시판 삭제', '/board/admin/delete.html', 'ANT', 1, CURRENT_TIMESTAMP());


INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 16
VALUES('공지사항 게시판 목록', '/board/notice/list.html', 'ANT', 1, CURRENT_TIMESTAMP());
INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 17
VALUES('공지사항 게시판 상세화면', '/board/notice/view.html', 'ANT', 1, CURRENT_TIMESTAMP());
INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 18
VALUES('공지사항 게시판 등록', '/board/notice/write.html', 'ANT', 1, CURRENT_TIMESTAMP());
INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 19
VALUES('공지사항 게시판 수정', '/board/notice/update.html', 'ANT', 1, CURRENT_TIMESTAMP());
INSERT INTO URL_RESOURCES(RESOURCE_NAME, RESOURCE_PATTERN, REQUEST_MATCHER_TYPE, ORDERNUM, CREATE_DT) -- 20
VALUES('공지사항 게시판 삭제', '/board/notice/delete.html', 'ANT', 1, CURRENT_TIMESTAMP());

INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(1, 2); -- 준회원 게시판 목록 / 준회원 게시판 읽기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(1, 3); -- 준회원 게시판 목록 / 준회원 게시판 쓰기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(2, 2); -- 준회원 게시판 상세화면 / 준회원 게시판 읽기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(2, 3); -- 준회원 게시판 상세화면 / 준회원 게시판 쓰기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(3, 3); -- 준회원 게시판 등록 / 준회원 게시판 쓰기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(4, 3); -- 준회원 게시판 수정 / 준회원 게시판 쓰기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(5, 3); -- 준회원 게시판 삭제 / 준회원 게시판 쓰기

INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(6, 4); -- 정회원 게시판 목록 / 정회원 게시판 읽기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(6, 5); -- 정회원 게시판 목록 / 정회원 게시판 쓰기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(7, 4); -- 정회원 게시판 상세화면 / 정회원 게시판 읽기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(7, 5); -- 정회원 게시판 상세화면 / 정회원 게시판 쓰기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(8, 5); -- 정회원 게시판 등록 / 정회원 게시판 쓰기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(9, 5); -- 정회원 게시판 수정 / 정회원 게시판 쓰기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(10, 5); -- 정회원 게시판 삭제 / 정회원 게시판 쓰기

INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(11, 6); -- 관리자 게시판 목록 / 관리자 게시판 읽기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(11, 7); -- 관리자 게시판 목록 / 관리자 게시판 쓰기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(12, 6); -- 관리자 게시판 상세화면 / 관리자 게시판 읽기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(12, 7); -- 관리자 게시판 상세화면 / 관리자 게시판 쓰기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(13, 7); -- 관리자 게시판 등록 / 관리자 게시판 쓰기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(14, 7); -- 관리자 게시판 수정 / 관리자 게시판 쓰기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(15, 7); -- 관리자 게시판 삭제 / 관리자 게시판 쓰기

INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(16, 8); -- 공지 게시판 목록 / 공지 게시판 읽기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(16, 9); -- 공지 게시판 목록 / 공지 게시판 쓰기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(17, 8); -- 공지 게시판 상세화면 / 공지 게시판 읽기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(17, 9); -- 공지 게시판 상세화면 / 공지 게시판 쓰기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(18, 9); -- 공지 게시판 등록 / 공지 게시판 쓰기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(19, 9); -- 공지 게시판 수정 / 공지 게시판 쓰기
INSERT INTO URL_RESOURCES_AUTHORITY(URL_RESOURCES_IDX, AUTHORITY_IDX)
VALUES(20, 9); -- 공지 게시판 삭제 / 공지 게시판 쓰기

INSERT INTO ROLE(ROLE_NAME, ROLE_DESC, PARENT_ROLE_IDX, CREATE_DT)
VALUES('ADMIN', '관리자', null, CURRENT_TIMESTAMP());             -- 1
INSERT INTO ROLE(ROLE_NAME, ROLE_DESC, PARENT_ROLE_IDX, CREATE_DT)
VALUES('ASSOCIATE_MEMBER', '준회원', null, CURRENT_TIMESTAMP());  -- 2
INSERT INTO ROLE(ROLE_NAME, ROLE_DESC, PARENT_ROLE_IDX, CREATE_DT)
VALUES('REGULAR_MEMBER', '정회원', null, CURRENT_TIMESTAMP());    -- 3
INSERT INTO ROLE(ROLE_NAME, ROLE_DESC, PARENT_ROLE_IDX, CREATE_DT)
VALUES('PUNISHMENT_MEMBER', '징계회원', null, CURRENT_TIMESTAMP());   -- 4
UPDATE ROLE SET PARENT_ROLE_IDX = 1 WHERE IDX=2; -- 준회원의 상급 ROLE을 관리자로 설정하는 UPDATE 문
UPDATE ROLE SET PARENT_ROLE_IDX = 1 WHERE IDX=3; -- 정회원의 상급 ROLE을 관리자로 설정하는 UPDATE 문
UPDATE ROLE SET PARENT_ROLE_IDX = 1 WHERE IDX=4; -- 징계회원의 상급 ROLE을 관리자로 설정하는 UPDATE 문

INSERT INTO ROLE_AUTHORITY(ROLE_IDX, AUTHORITY_IDX)
VALUES(2, 2); -- 준회원 / 준회원 게시판 읽기
INSERT INTO ROLE_AUTHORITY(ROLE_IDX, AUTHORITY_IDX)
VALUES(2, 3); -- 준회원 / 준회원 게시판 쓰기
INSERT INTO ROLE_AUTHORITY(ROLE_IDX, AUTHORITY_IDX)
VALUES(2, 8); -- 준회원 / 공지사항 게시판 읽기
INSERT INTO ROLE_AUTHORITY(ROLE_IDX, AUTHORITY_IDX)
VALUES(3, 4); -- 정회원 / 정회원 게시판 읽기
INSERT INTO ROLE_AUTHORITY(ROLE_IDX, AUTHORITY_IDX)
VALUES(3, 5); -- 정회원 / 정회원 게시판 쓰기
INSERT INTO ROLE_AUTHORITY(ROLE_IDX, AUTHORITY_IDX)
VALUES(3, 8); -- 정회원 / 공지사항 게시판 읽기
INSERT INTO ROLE_AUTHORITY(ROLE_IDX, AUTHORITY_IDX)
VALUES(4, 8); -- 징계회원 / 공지사항 게시판 읽기
INSERT INTO ROLE_AUTHORITY(ROLE_IDX, AUTHORITY_IDX)
VALUES(1, 6); -- 관리자 / 관리자 게시판 읽기
INSERT INTO ROLE_AUTHORITY(ROLE_IDX, AUTHORITY_IDX)
VALUES(1, 7); -- 관리자 / 관리자 게시판 쓰기
INSERT INTO ROLE_AUTHORITY(ROLE_IDX, AUTHORITY_IDX)
VALUES(1, 8); -- 관리자 / 공지사항 게시판 읽기(여기를 주석처리 해서 role hierachy 테스트 시도)
INSERT INTO ROLE_AUTHORITY(ROLE_IDX, AUTHORITY_IDX)
VALUES(1, 9); -- 관리자 / 공지사항 게시판 쓰기

INSERT INTO MEMBER(NAME, LOGIN_ID, LOGIN_PASSWORD, CREATE_DT)
VALUES('준회원이름', 'associate_id', '$2a$10$gkK3hCHFrKT.LJPlR7Bvj.KxSIYkcIOSqokvsHB80/rlGvKnmwBbq', CURRENT_TIMESTAMP());
INSERT INTO MEMBER(NAME, LOGIN_ID, LOGIN_PASSWORD, CREATE_DT)
VALUES('정회원이름', 'regular_id', '$2a$10$gkK3hCHFrKT.LJPlR7Bvj.KxSIYkcIOSqokvsHB80/rlGvKnmwBbq', CURRENT_TIMESTAMP());
INSERT INTO MEMBER(NAME, LOGIN_ID, LOGIN_PASSWORD, CREATE_DT)
VALUES('징계회원이름', 'punishment_id', '$2a$10$gkK3hCHFrKT.LJPlR7Bvj.KxSIYkcIOSqokvsHB80/rlGvKnmwBbq', CURRENT_TIMESTAMP());
INSERT INTO MEMBER(NAME, LOGIN_ID, LOGIN_PASSWORD, CREATE_DT)
VALUES('관리자이름', 'admin_id', '$2a$10$gkK3hCHFrKT.LJPlR7Bvj.KxSIYkcIOSqokvsHB80/rlGvKnmwBbq', CURRENT_TIMESTAMP());

INSERT INTO MEMBER_ROLE(MEMBER_IDX, ROLE_IDX)
VALUES(1, 2); -- 준회원 이름 / 준회원
INSERT INTO MEMBER_ROLE(MEMBER_IDX, ROLE_IDX)
VALUES(2, 3); -- 정회원 이름 / 정회원
INSERT INTO MEMBER_ROLE(MEMBER_IDX, ROLE_IDX)
VALUES(4, 1); -- 관리자 이름 / 관리자

/*
INSERT INTO MEMBER_AUTHORITY(MEMBER_IDX, AUTHORITY_IDX)
VALUES(2, 6);
INSERT INTO MEMBER_AUTHORITY(MEMBER_IDX, AUTHORITY_IDX)
VALUES(2, 7);
*/

INSERT INTO BOARD(MEMBER_IDX, TITLE, CONTENTS, BOARD_TYPE, CREATE_DT, UPDATE_DT)
VALUES(1, '준회원 게시판 작성 테스트1 제목', '<p>준회원 게시판 작성 테스트1</p><p>내용</p>', 1, CURRENT_TIMESTAMP(), NULL);
INSERT INTO BOARD(MEMBER_IDX, TITLE, CONTENTS, BOARD_TYPE, CREATE_DT, UPDATE_DT)
VALUES(1, '준회원 게시판 작성 테스트2 제목', '<p>준회원 게시판 작성 테스트2</p><p>내용</p>', 1, CURRENT_TIMESTAMP(), NULL);
INSERT INTO BOARD(MEMBER_IDX, TITLE, CONTENTS, BOARD_TYPE, CREATE_DT, UPDATE_DT)
VALUES(1, '준회원 게시판 작성 테스트3 제목', '<p>준회원 게시판 작성 테스트3</p><p>내용</p>', 1, CURRENT_TIMESTAMP(), NULL);
INSERT INTO BOARD(MEMBER_IDX, TITLE, CONTENTS, BOARD_TYPE, CREATE_DT, UPDATE_DT)
VALUES(1, '준회원 게시판 작성 테스트4 제목', '<p>준회원 게시판 작성 테스트4</p><p>내용</p>', 1, CURRENT_TIMESTAMP(), NULL);
INSERT INTO BOARD(MEMBER_IDX, TITLE, CONTENTS, BOARD_TYPE, CREATE_DT, UPDATE_DT)
VALUES(1, '준회원 게시판 작성 테스트5 제목', '<p>준회원 게시판 작성 테스트5</p><p>내용</p>', 1, CURRENT_TIMESTAMP(), NULL);
INSERT INTO BOARD(MEMBER_IDX, TITLE, CONTENTS, BOARD_TYPE, CREATE_DT, UPDATE_DT)
VALUES(1, '준회원 게시판 작성 테스트6 제목', '<p>준회원 게시판 작성 테스트6</p><p>내용</p>', 1, CURRENT_TIMESTAMP(), NULL);
INSERT INTO BOARD(MEMBER_IDX, TITLE, CONTENTS, BOARD_TYPE, CREATE_DT, UPDATE_DT)
VALUES(1, '준회원 게시판 작성 테스트7 제목', '<p>준회원 게시판 작성 테스트7</p><p>내용</p>', 1, CURRENT_TIMESTAMP(), NULL);
INSERT INTO BOARD(MEMBER_IDX, TITLE, CONTENTS, BOARD_TYPE, CREATE_DT, UPDATE_DT)
VALUES(1, '준회원 게시판 작성 테스트8 제목', '<p>준회원 게시판 작성 테스트8</p><p>내용</p>', 1, CURRENT_TIMESTAMP(), NULL);
INSERT INTO BOARD(MEMBER_IDX, TITLE, CONTENTS, BOARD_TYPE, CREATE_DT, UPDATE_DT)
VALUES(1, '준회원 게시판 작성 테스트9 제목', '<p>준회원 게시판 작성 테스트9</p><p>내용</p>', 1, CURRENT_TIMESTAMP(), NULL);
INSERT INTO BOARD(MEMBER_IDX, TITLE, CONTENTS, BOARD_TYPE, CREATE_DT, UPDATE_DT)
VALUES(1, '준회원 게시판 작성 테스트10 제목', '<p>준회원 게시판 작성 테스트10</p><p>내용</p>', 1, CURRENT_TIMESTAMP(), NULL);
INSERT INTO BOARD(MEMBER_IDX, TITLE, CONTENTS, BOARD_TYPE, CREATE_DT, UPDATE_DT)
VALUES(1, '준회원 게시판 작성 테스트11 제목', '<p>준회원 게시판 작성 테스트11</p><p>내용</p>', 1, CURRENT_TIMESTAMP(), NULL);
INSERT INTO BOARD(MEMBER_IDX, TITLE, CONTENTS, BOARD_TYPE, CREATE_DT, UPDATE_DT)
VALUES(1, '준회원 게시판 작성 테스트12 제목', '<p>준회원 게시판 작성 테스트12</p><p>내용</p>', 1, CURRENT_TIMESTAMP(), NULL);
INSERT INTO BOARD(MEMBER_IDX, TITLE, CONTENTS, BOARD_TYPE, CREATE_DT, UPDATE_DT)
VALUES(1, '준회원 게시판 작성 테스트13 제목', '<p>준회원 게시판 작성 테스트13</p><p>내용</p>', 1, CURRENT_TIMESTAMP(), NULL);
INSERT INTO BOARD(MEMBER_IDX, TITLE, CONTENTS, BOARD_TYPE, CREATE_DT, UPDATE_DT)
VALUES(1, '준회원 게시판 작성 테스트14 제목', '<p>준회원 게시판 작성 테스트14</p><p>내용</p>', 1, CURRENT_TIMESTAMP(), NULL);



