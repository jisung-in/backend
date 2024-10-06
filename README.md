![jisung_image](https://github.com/user-attachments/assets/7957e811-8bee-4b4c-8ee0-a96048ada2dc)

## 📋 프로젝트 개요

새로운 책을 읽으려고 하는데, 내용을 공유하거나 이미 읽어본 사람들과 이야기를 나누면 좋지 않을까 ?  
책을 읽고, 책에 대한 리뷰를 남기고, 다른 사람들과 소통할 수 있는 서비스를 만들어보자 !

- 원하는 도서를 토크방을 통해 사람들과 토론을 진행할 수 있습니다.
- 토크방을 입장하는데 간단한 제약 조건이 있을 수 있습니다.
    - ex) 해당 책을 [읽고 있는 사람 / 읽은 사람]
- [도서 평가 / 별점 부여 / 독서 상태 기록 / 의견 작성]이 가능합니다

<br>

## 📘 프로젝트 인원 / 기간

- **개발 인원**: [FE 2명 / BE 3명]
- **개발 기간**: [2024.03 ~ 2024.05]

### 🖥️ 백엔드 역할

|                                       김도형                                       |                                       박정우                                       |                                       안윤기                                        |
|:-------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------:|
| <img width="160px" src="https://avatars.githubusercontent.com/u/58456758?v=4"/> | <img width="160px" src="https://avatars.githubusercontent.com/u/87631442?v=4"/> | <img width="160px" src="https://avatars.githubusercontent.com/u/121776373?v=4"/> |
|                     [pdohyung](https://github.com/pdohyung)                     |                        [jwooo](https://github.com/jwooo)                        |                     [AHNYUNKI](https://github.com/AHNYUNKI)                      |
|                            **[회원 / 리뷰 / 별점]** 도메인 개발                            |                              **[도서 / 서재]** 도메인 개발                               |                              **[토크룸 / 의견]** 도메인 개발                               |

<br>

## 🛠️ 기술 스택

### 📦 Language & Framework

![Java](https://img.shields.io/badge/java-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![JUnit5](https://img.shields.io/badge/junit5-25A162?style=for-the-badge&logo=JUnit5&logoColor=white)
![Mockito](https://img.shields.io/badge/mockito-25A162?style=for-the-badge&logo=&logoColor=white)
![Spring_Boot](https://img.shields.io/badge/spring_boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring_Rest_Docs](https://img.shields.io/badge/spring_rest_docs-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring_Data_JPA](https://img.shields.io/badge/spring_data_jpa-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![QueryDSL](https://img.shields.io/badge/querydsl-6DB33F?style=for-the-badge&logo=spring&logoColor=white)

### 🛢️ Database

![MySQL](https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=MySQL&logoColor=white)
![Redis](https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white)

### ☁️ Infra

![AWS_EC2](https://img.shields.io/badge/aws_ec2-FF9900.svg?style=for-the-badge&logo=amazonec2&logoColor=white)
![AWS_RDS](https://img.shields.io/badge/aws_rds-527FFF.svg?style=for-the-badge&logo=amazonrds&logoColor=white)
![AWS_S3](https://img.shields.io/badge/aws_s3-569A31.svg?style=for-the-badge&logo=amazons3&logoColor=white)
![GitHub_Actions](https://img.shields.io/badge/github_actions-2088FF.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![Docker](https://img.shields.io/badge/docker-2496ED.svg?style=for-the-badge&logo=docker&logoColor=white)

### 🌱 프로젝트 환경

> - Java 17
> - Spring Boot 3.1.9
> - Gradle 8.5
> - Git Flow [Feature &rarr; Develop &rarr; Main]

<br>

## 🚀 핵심 기능

### 회원

> 카카오 소셜로그인으로 로그인과 회원가입 제공합니다.  
> 회원 정보를 조회할 수 있습니다.  
> 회원이 생성한 [별점 / 한줄평 / 토크방 / 독서 상태]를 조회할 수 있습니다.

### 도서

> 도서 검색하고 결과를 조회할 수 있습니다.  
> 도서에 대한 상세 정보를 제공합니다.  
> 도서에 대한 별점과 독서 상태를 기록할 수 있습니다.  
> 도서 베스트셀러를 조회할 수 있습니다.

### 리뷰 (한줄평)

> 도서에 대한 한줄평을 작성하고 조회할 수 있습니다.  
> 한줄평에 좋아요를 남길 수 있습니다.

### 별점

> 도서에 대한 별점을 부여할 수 있습니다.  
> 별점의 평균을 조회할 수 있습니다.

### 토크룸

> 도서별 토크룸을 생성하고 참가할 수 있습니다.  
> 토크룸 [정보 / 이미지 / 참여 조건]을 설정할 수 있습니다.  
> 토크룸에 좋아요를 남길 수 있습니다.

### 의견

> 토크방에 의견을 작성할 수 있습니다.  
> 의견에 이미지를 추가할 수 있습니다.  
> 의견에 좋아요를 남길 수 있습니다.

<br>

## ⚡ 주요 작업 / PR

- [코드 리뷰를 통해 팀원들이 개발한 기능 검토 및 피드백 제공](https://github.com/jisung-in/backend/pull/13)
- [테스트 작성 및 커버리지 분석을 통한 기능 안정성 검증과 품질 향상](https://github.com/jisung-in/backend/pull/127)
- [도서 베스트셀러 데이터 조회 기능에 비동기 처리 적용으로 성능 개선](https://jwooo.tistory.com/2)
- [팬텀 리드 문제를 유니크 인덱스를 통해 해결하여 좋아요 중복 생성 방지](https://trysolve.tistory.com/36)
- [토크룸 조회 쿼리 분리를 통한 성능 개선](https://github.com/jisung-in/backend/pull/18)

<br>

## 🏗️ Architecture

<img width="1583" alt="jisungin_architecture" src="https://github.com/user-attachments/assets/525130f8-931e-4782-959a-dcae571741f3">

<br>

## 📐 ERD

![jisungin](https://github.com/user-attachments/assets/35684e3a-0e1a-4964-831b-ea8c991dbdc4)

<br>
