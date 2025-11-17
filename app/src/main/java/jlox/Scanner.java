package jlox;

import java.util.ArrayList;
import java.util.List;

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

            default:
                // lox에서 안쓰는 문자라면 에러 발생시킴(lexical error)
                Lox.error(line, "Unexpected character. <-- this is a lexical error!");
                break;
        }
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
