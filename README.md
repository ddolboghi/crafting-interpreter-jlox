# 개요

이 프로젝트는 책 'Crafting Interpreter/로버스 나이스트롬'의 Java interpreter 구현 실습입니다.

[공식 GitHub](https://github.com/munificent/craftinginterpreters)

[원서의 웹 문서 버전](https://craftinginterpreters.com/contents.html)

책에서는 lox라는 언어를 만듭니다.

- `null`처럼 값이 없음을 나타내는 데이터 타입으로 `nil`을 사용합니다.
- 모든 숫자는 런타임에 부동 소수점입니다.
- 선행 소수점이나 후행 소수점을 허용하지 않습니다.
- lox에는 비트 연산자, 시프트 연산자, 모듈로 연산자, 조건부 연산자가 없습니다.
- `print`를 코어 라이브러리 함수로 만드는 대신, 언어에 내장시켰습니다.(저자는 함수 호출 구현을 개발할때 유리하다고 합니다)
- 함수, 클래스를 first-class(일급)로 취급합니다.
- 클래스의 메서드를 선언할때 `fun` 키워드를 사용하지 않습니다.
- 인스턴스의 필드에 값을 할당할때 필드가 없으면 알아서 생성됩니다.
- 객체 생성 시 객체 유효성을 검증하는 생성자로 `init()` 메서드를 사용합니다.
- 클래스간 상속은 `<`으로 표현합니다.
- 클래스는 생성자를 상속합니다.
- 블록(`{ }`)으로 감싸서 여러 문장을 한 문장처럼 작동시킵니다.

# 실습을 끝내고 직접 해볼만한 것

- 비트 연산자, 시프트 연산자, 모듈로 연산자, 조건부 연산자 구현
- for-in 루프 구현(66p)
- 에러 처리: interactive debugger, static analyzer

# .java 파일 실행 방법

1. `gradlew.bat build`로 빌드
2. `java -cp app/build/classes/java/main <모듈명>.<클래스명> <인자>`
    e.g. `java -cp app/build/classes/java/main jlox.Lox`
