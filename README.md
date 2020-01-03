# Datastation API v0.01 - 20200103

- 로그인이 필요할경우 명시
- parameter 특이사항 명시
- 웹/SPSS 종류 명시
- SPSS 를 제외한 나머지 항목중 download 기능은 모두 로그인 필요
- User 및 데이터 정보 DB - 192.168.210.142
- API 정보 - 192.168.210.131:7578
- Swagger - http://192.168.210.131:7578/swagger-ui.html#

### /api/auth/signin

웹 / 로그인 API

- Swagger 내 로그인방법 - API 요청후 반환되는 값 token_type, access_token 을 복사후 Swagger 내 우측 상단에 Authorize 버튼 클릭후 "{token_type} {access_token}" 형태로 paste(두 값 사이에 공백).



### /api/auth/signup

웹 / 가입 API

- v0.01 기준 탈퇴/조회 기능없음
- 가입정보 수정 필요시 DB 내에서 삭제후 재가입



### /calendar/calendar/download

웹 / 달력 다운로드 / 로그인 필요



### /calendar/spss/download

SPSS / 달력 다운로드 



### /dataset/latest

웹 / 최신 데이터셋 리스트



### /dataset/popular

웹 / 인기 데이터셋 리스트



### /dataset/retrieve

- ctgrcode - datastation_a.category_info 테이블내 ctgr_code
- dscode - datastation_a.dataset_info 테이블내 ds_code 
  - 하나 이상 dscode 가능



### /dataset/search

웹 / 데이터셋 검색

- 웹 내 최근 1년 데이터 다운로드 버튼 및 개별 데이터셋 다운로드 endpoint 반환되는 형태



### /news/count/spss/download

SPSS / 키워드로 검색된 문서 수 



### /news/entity

웹 / 개체명 검색 결과 세부 페이지

- page, size - from: (page-1)*size, size: size
  - ex) page 0, size 10 -> page 1, size 10 -> ...

- entity parameter 가능항목
  - locationNamedEntity
  - organizationNamedEntity
  - etcNamedEntity
  - totalNamedEntity
  - personNamedEntity - v0.01 기준 반환되는 값없음

- sort parameter 가능항목
  - date.desc,entity.desc, date.asc, entity.asc 항목중 date, entity 2개 조합해서 사용
    - date - 뉴스기사 날짜, entity - 개체명 갯수
    - ex) date.desc,entity.desc 가장 최근뉴스순, entity 개수가 많이 추출된 수 순으로 정렬
  - default - date.desc,entity.desc 
    - ex) http://192.168.210.131:7578/news/entity?entity=etcNamedEntity&from=20180101&page=0&query=test&size=10&sort=date.desc,entity.desc&to=20180211
  - Swagger 상에서는 bug로 date.desc,entity.desc default 값으로 자동 적용(수정불가)



### /news/entity/download

웹 / 개체명 검색 결과 세부 페이지내 분석결과 다운로드 버튼 / 로그인 필요

/news/entity 와 동일



### /news/entity/spss/download

SPSS / 개체명 검색 결과 SPSS 다운로드



### /news/entity/summary

웹 / 개체명 프론트 페이지



### /news/related

웹 / 관련 키워드 세부 페이지

- 임의로 date.desc 로 지정; parameter 로 조정 불가



### /news/related/download

웹/ 관련 키워드 세부 페이지내 관련 키워드 다운로드 버튼 / 로그인 필요



### /news/related/rank/download

웹 / 관련 키워드 세부 페이지내 전체 일자별 관련 키워드 TOP10 다운로드 버튼 / 로그인 필요

- size - TOP N size; ex) size - 10, 전체 일자별 관련 키워드 TOP 10 다운로드



### /news/related/spss/download

SPSS / 관련 키워드 SPSS 다운로드



### /news/timeline

웹 / 뉴스 문서변화량 분석 결과 그래프



### /news/timeline/download

웹 / 뉴스 문서변화량 분석 결과 값 다운로드 / 로그인 필요



### /news/timeline/summary

웹 / 뉴스 문서변화량 프론트 페이지 



### /news/topic

웹 / 주제어 세부 페이지



### /news/topic/download

웹 / 주제어 세부 페이지내 주제어 다운로드 버튼 / 로그인 필요



### /news/topic/rank/download

웹 / 주제어 세부 페이지내 전체 일자별 관련 키워드 TOP10 다운로드 버튼 / 로그인 필요



### /news/topic/spss/download

웹 / 주제어 SPSS 다운로드



### /weather/{dataset}/download

웹 / 날씨 데이터셋 다운로드 API / 로그인 필요

- dataset 가능값(142번 MySQL datastation_a.dataset_info 테이블내 날씨 데이터 ds_code 참고) - w-avg-ta, w-max-ta, w-min-ta, w-prob-rn, w-sum-rn, w-dd-mefs, w-max-wd, w-avg-ws, w-avg-rhm, w-avg-tca, a-pm10, a-pm25, a-o3, s-01-swind, s-02-hrain, s-03-cold, s-04-dry, s-05-ssurge, s-06-hsea, s-07-typoon, s-08-hsnow, s-09-ydust, s-10-resv

- areacode - >= 10
  - ex) 10 일경우 1000 >= and <= 1099  데이터 모두 반환; 142번 DB dataset_a.weather_area_info 테이블내 area_code 참고



### /weather/download

웹 / 날씨 데이터셋 다운로드 API / 로그인 필요



### /weather/spss/download

SPSS / 날씨 데이터셋 다운로드 



