# study-spring-reactive

Spring Boot Reactive를 개인적인 공부 목적으로 만든 레포지토리입니다. (완강)

다음 강의를 참고하여 프로젝트를 진행했습니다.

- [Udemy String Boot Reactive 강의](https://www.udemy.com/course/build-reactive-restful-apis-using-spring-boot-webflux/)

# 실행 방법

1. gradle 도커 플러그인으로 각 모듈에 대한 도커 이미지를 만들어줍니다.
   ```
   $ ./gradlew docker
   ```
2. `docker-compose.prod.yml` 파일로 docker-compose를 실행합니다. (그냥 docker-compsoe.yml은 개발용입니다.)
   ```
   $ docker compose -f ./docker-compose.prod.yml up -d
   ```
3. docker running이 완료되면 intellij의 Http Client를 이용하여 다음 테스트 파일로 테스트를 해보시면 됩니다.
   ```
   # intellij test code path: movies/src/test/http/movies_review_flow.http
   ```
