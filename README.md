# Project - Trilo
- 웹 서버(프론트엔드 서버) : [링크](http://cosain-trilo.com/)
- api 문서(백엔드 서버 API 문서) : [링크](http://api.cosain-trilo.com/docs/)

## 팀 구성 👪

|                   BackEnd                    |                   BackEnd                    |                  FrontEnd                  |                       FrontEnd                       |                                                  Design                                                   |
|:--------------------------------------------:|:--------------------------------------------:|:------------------------------------------:|:----------------------------------------------------:|:---------------------------------------------------------------------------------------------------------:|
| ![](https://github.com/ttasjwi.png?size=200) | ![](https://github.com/pia2011.png?size=200) | ![](https://github.com/jthw1005.png?size=200) | ![](https://github.com/bangdler.png?size=200) | ![](https://github.com/teamCoSaIn/trilo-be/assets/53935439/a14a6b7d-ed57-4a4c-89c1-a31cde1cefa7?size=200) |
|      [땃쥐](https://gihtub.com/ttasjwi)      |     [LUKAS](https://github.com/pia2011)      |   [‍♂Oliver](https://github.com/jthw1005)   |         [Bangtae](https://github.com/bangdler)          |                                                    Joy                                                    |

## 프로젝트 간단 시연 📺

<details>
   <summary> 확인하기 (👈 클릭)</summary>
<br />

### 로그인 

![1 로그인](https://github.com/teamCoSaIn/trilo-be/assets/53935439/b55ef318-78a6-4e4c-89fe-095025dd7a12)

### 여행 생성 및 이미지 업로드

![2 여행 생성 및 이미지 업로드](https://github.com/teamCoSaIn/trilo-be/assets/53935439/28a765ff-2f5e-4e53-ac2d-6a7bca065eb1)

### 여행 기간 수정

![3 여행 기간 수립](https://github.com/teamCoSaIn/trilo-be/assets/53935439/3d1f171a-8fd8-468e-bc0d-a56174708992)

### 여행 일정 추가

![4 여행 일정 추가](https://github.com/teamCoSaIn/trilo-be/assets/53935439/21d1076f-644b-4998-a20d-140cf663ac26)

### 여행 일정 이동

![5 여행 일정 이동](https://github.com/teamCoSaIn/trilo-be/assets/53935439/4a790cac-2858-437a-b94f-1d825ef36bee)

### 일정 목록 조회

![6 일정 목록 조회](https://github.com/teamCoSaIn/trilo-be/assets/53935439/600a1f6d-9ce7-45dc-9aa9-e7434336e0c8)

### Day 색상 변경

![7 Day 색상 변경](https://github.com/teamCoSaIn/trilo-be/assets/53935439/46825d3b-6ca8-409c-9dba-5a7c549d2aa3)

### 여행 기간 수정 & 기간안에 포함되지 않는 일정들 일괄 임시보관함 이동

![8 여행 기간 변경에 따른 일괄 임시보관함 이동](https://github.com/teamCoSaIn/trilo-be/assets/53935439/03eedb85-4e22-4248-a210-3a0125221eca)


</details>

## 프로젝트 설명
- 사용자는 google, naver, kakao 소셜 서비스와 연동하여 우리 서비스에 로그인할 수 있습니다.
- 각각의 사용자는 '여행'을 생성합니다. 이 때 여행의 기간을 지정할 수 있고, 변경할 수 있습니다.
  - 여행의 특정 날짜에 일정을 추가할 수 있고, 일정을 다른 날짜로 드래그 앤 드롭 기능을 통해 이동시킬 수 있습니다.
  - 특정 날짜 외에도, 임시보관함에 임시적으로 일정을 등록해둘 수 있습니다.

## 사용 기술
- 언어 : Java 17
- 프레임워크 : Spring Boot 3.0
- 데이터베이스 : MySQL 8
- 데이터베이스 접근 기술 : Spring Data JPA, Querydsl
- CI/CD : Github Actions
- API 문서화 : Spring Rest Docs

<!-- ## 프로젝트 배포 방법 -->

<!-- ## 프로젝트 로컬환경 실행법  -->

## 협업 전략
### 브랜치
- `dev` : 배포 브랜치. PR 이후 병합되는 브랜치입니다.
- `TRL-xxx__...` : 기능별 브랜치. PR 이후 dev 브랜치에 병합됩니다.

### 리포지토리 명칭
- `upstream` : tril-be의 [공식 github 리포지토리](https://github.com/teamCoSaIn/trilo-be)
  - `upstream`은 정말 필요한 경우가 아닌 이상, 직접적인 merge를 금지합니다.
- `origin` : 개발자 각각이 자신의 github에 fork한 리포지토리
- `local` : 개발자 각각의 로컬 환경 git 리포지토리

개발자는 fork한 origin 리포지토리를 로컬환경에서 git clone 명령어를 통해 clone해오고, upstream을 리포지토리 등록하여 개발하면 됩니다.

### 커밋 컨벤션
```text
[TRL-티켓번호] Type: 커밋 제목 

커밋에 대한 설명
```
| Type     | 설명                                          |
|----------|---------------------------------------------|
| feat     | 새로운 기능 추가, 확장                               |
| test     | 테스트 코드 작성, 테스트 리팩토링(프로덕션 코드 변경 X)           |
| refactor | 코드 리팩토링                                     |
| docs     | 문서(API 문서, README.md) 작업, 수정                |
| fix      | 버그를 고쳐야하는 경우, 요구사항 변경으로 인해 코드를 일부 수정해야하는 경우 |
| infra    | CI/CD 및 인프라(외부 아키텍처) 관련 스크립트                |
| chore    | 잡다한 작업들(메서드 명, 순서 변경, 의존성 추가 등...)          |

### JIRA를 통한 티켓(이슈) 관리
- JIRA를 사용하여 티켓(이슈)를 관리합니다. 새로 추가하거나 수정하여 반영해야하는 기능을 정하고 JIRA에서 해당 기능에 대응하는 티켓을 발행합니다.
- 개발자는 자신이 맡은 기능의 브랜치를 local에서 생성합니다. 이 때 이름은 JIRA 티켓 번호를 가져와서 `TRL-xxx__...`와 같이 짓습니다.
- `TRL-xxx_...` 브랜치에서 기능을 작업하고 커밋 1개 단위로 `origin`에 push 합니다.
  - 처음 push 할 때 PR을 생성합니다. 이제 커밋 push시마다 해당 PR에 커밋이 하나씩 붙습니다.
  - 이 과정에서 CI 빌드테스트의 문제가 있을 시 해당 커밋을 로컬에서 수정하여 `amend`하고 `push --force`를 통해 덮어씌워 정상적으로 빌드되도록 합니다.
- 코드리뷰를 거쳐서 해당 브랜치를 dev에 병합해도 된다고 허락받으면 PR을 게시자 스스로 squash-merge 합니다.
- 이후 작업자들은 작업 브랜치를 해당 PR 뒤에 리베이스하여 올립니다. squash-merge를 하기 때문에 충돌이 나지 않는다면 rebase를 하지 않아도 되지만,
충돌이 날 경우에는 필수적으로 rebase하면서 충돌을 해결하야합니다. (가장 이상적인건 충돌이 나지 않도록 일감(티켓, 이슈)을 잘 나누는 것입니다.)

### 버전 관리 사고 수습
- 로컬에서 작업 후 커밋을 했는데 직전 커밋의 문제점을 발견했을 경우 `git commit --amend` 명령어를 통해 새로운 커밋으로 덮어씌웁니다.
- 해당 티켓 브랜치 커밋 이력을 수정하려면 `rebase -i` 명령어를 이용해 커밋 순서를 조작하거나, 커밋명을 변경하거나 복수의 커밋을 병합하는 것을 적극적으로 사용하세요.
    - 혹시 `origin` 브랜치에 이미 작업물을 올렸다면 `push --force` 를 사용하세요. 이 명령어는 기존 origin 에 있던 브랜치 대신 지금 로컬에서의 브랜치를 강제로 덮어씌우는 것으로 보시면 됩니다.
      단, 해당 브랜치의 PR을 다른 코드리뷰어가 리뷰 진행 중일 경우 사전 조율이 필요합니다.
- 브랜치를 특정 버전으로 다시 되돌리고 싶다면 `reset` 명령어를 사용합니다. `--hard` 옵션은 해당 커밋 이후의 변경점을 모두 지워버리고, `mixed`는 추가 작업물을 로컬에 그대로 남긴채 되돌립니다.

<!-- ## 외부 아키텍처 -->

<!-- ## 내부 아키텍처 -->

---
