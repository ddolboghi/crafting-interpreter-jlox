package jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jlox.TokenType.*;

/**
 * 소스 코드 문자열을 읽어 lexeme을 찾아 토큰으로 만드는 스캐너 역할을 하는 클래스
 */
class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0; // 스캔 중인 렉심의 첫 번째 문자 위치를 가리키는 오프셋
    private int current = 0; // 현재 처리 중인 문자 위치를 가리키는 오프셋
    private int line = 1; // current가 위치한 소스 줄 번호

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd())
                        advance(); // (한 줄짜리)주석은 줄 끝까지 소비하지만 토큰으로 처리하지 않음
                } else {
                    addToken((SLASH));
                }
                break;

            case ' ':
            case '\r':
            case '\t':
                // 공백 문자 무시
                break;

            case '\n':
                line++;
                break;

            case '"':
                string();
                break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
                // lox에서 안쓰는 문자라면 에러 발생시킴(lexical error)
                Lox.error(line, "Unexpected character. <-- this is a lexical error!");
                break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek()))
            advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null)
            type = IDENTIFIER;
        addToken(type);
    }

    private void number() {
        while (isDigit(peek()))
            advance();

        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek()))
                advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n')
                line++;
            advance();
        }

        if (isAtEnd()) { // 방금 소비한 문자가 큰따옴표일때 소스 길이는 current보다 항상 큼
            Lox.error(line, "Unterminated string.");
            return;
        }

        // 문자열 리터럴을 닫는 큰따옴표
        advance();

        String value = source.substring(start + 1, current - 1); // 앞 뒤 큰따옴표 제거하여 인터프리터가 사용할 실제 문자열 생성
        // escape 시퀀스를 지원하려면 문자열에 unescape 처리해야함
        addToken(STRING, value);
    }

    private boolean match(char expected) {
        if (isAtEnd())
            return false;
        if (source.charAt(current) != expected)
            return false;

        current++;
        return true;
    }

    /**
     * lookahead를 하는 함수로, advance()와 비슷하지만 문자를 소비하지 않는다.
     * lookahead 문자 개수가 적을수록 스캐너가 더 빨리 작동된다.
     * 
     * @return 현재 current가 source에서 가리키는 char 타입 문자
     */
    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    /**
     * peek() 함수에 lookahead할 문자 개수를 매개변수로 정의하면 lookahead의 임의성(arbitarity)가 커져
     * lookahead 문자 개수를 마음대로 늘릴 수 있다.
     * peekNext() 함수를 따로 구현함으로써 lookahead 문자 개수는 최대 2개임을 명시한다.
     * 
     * @return
     */
    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++); // 현재 current 값을 사용한 뒤 1 증가
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}