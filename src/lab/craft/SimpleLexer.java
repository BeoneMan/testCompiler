package lab.craft;


import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 手写词法分析器
 */
public class SimpleLexer {
    public static void main(String[] args) throws IOException {
        SimpleLexer lexer = new SimpleLexer();

        // String script = "int age = 45;";
        String script = "int age = 45 ; ";
        System.out.println("parse :" + script);
        SimpleTokenReader tokenReader = lexer.tokenize(script);
        dump(tokenReader);
         /*  //测试inta的解析
        script = "inta age = 45;";
        System.out.println("\nparse :" + script);
        tokenReader = lexer.tokenize(script);
        dump(tokenReader);

        //测试in的解析
        script = "in age = 45;";
        System.out.println("\nparse :" + script);
        tokenReader = lexer.tokenize(script);
        dump(tokenReader);

        //测试>=的解析
        script = "age >= 45;";
        System.out.println("\nparse :" + script);
        tokenReader = lexer.tokenize(script);
        dump(tokenReader);

        //测试>的解析
        script = "age > 45;";
        System.out.println("\nparse :" + script);
        tokenReader = lexer.tokenize(script);
        dump(tokenReader);*/
    }

    private List<Token> tokens = null;//保存解析出来的Token
    private SimpleToken token = null;//正在解析的Token
    private StringBuffer tokenText = null;//临时保存Token的文本

    //是否是字母
    private boolean isAlpha(int ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z';
    }

    //是否是数字
    private boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    //是否是空白字符
    private boolean isBlank(int ch) {
        return ch == ' ' || ch == '\t' || ch == '\n';
    }

    public SimpleTokenReader tokenize(String script) throws IOException {
        CharArrayReader reader = new CharArrayReader(script.toCharArray());
        int ich = 0;
        char ch = 0;
        tokenText = new StringBuffer();
        token = new SimpleToken();
        tokens = new ArrayList<>();
        DfaState state = DfaState.Initial;
        while ((ich = reader.read()) != -1) {
            ch = (char) ich;
            switch (state) {
                case Initial:
                    state = initToken(ch);//重新确定后续状态
                    break;
                case Id:
                    if (isAlpha(ch) || isDigit(ch)) {
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Id_int1:
                    if (isAlpha(ch) || isDigit(ch)) {
                        if ('n' == ch) {
                            state = DfaState.Id_int2;
                        } else {
                            state = DfaState.Id;
                        }
                        tokenText.append(ch);
                    }
                    break;

                case Id_int2:
                    if (isAlpha(ch) || isDigit(ch)) {
                        if ('t' == ch) {
                            state = DfaState.Id_int3;
                        } else {
                            state = DfaState.Id;
                        }
                        tokenText.append(ch);
                    }
                    break;

                case Id_int3:
                    if (isBlank(ch)) {
                        state = DfaState.Int;
                    } else {
                        state = initToken(ch);
                    }
                    tokenText.append(ch);
                    break;

                case Int:
                    token.type = TokenType.Int;
                    state = initToken(ch);
                    break;

                case Assignment:
                    state = initToken(ch);
                    break;
                case IntLiteral:
                    if (isDigit(ch)) {
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case SemiColon:
                    state = initToken(ch);
                    break;
            }

        }

        return new SimpleTokenReader(tokens);
    }

    private DfaState initToken(char ch) {
        if (tokenText.length() > 0) {
            token.text = tokenText.toString();
            tokens.add(token);
            token = new SimpleToken();
            tokenText = new StringBuffer();
        }
        DfaState state = DfaState.Initial;
        if (isAlpha(ch)) {
            if ('i' == ch) {
                state = DfaState.Id_int1;
            } else {
                state = DfaState.Id;
            }
            tokenText.append(ch);
            token.type = TokenType.Identifier;
        } else if ('=' == ch) {
            state = DfaState.Assignment;
            token.type = TokenType.Assignment;
            tokenText.append(ch);
        } else if (isDigit(ch)) {
            state = DfaState.IntLiteral;
            token.type = TokenType.IntLiteral;
            tokenText.append(ch);
        } else if (';' == ch) {
            state = DfaState.SemiColon;
            token.type = TokenType.SemiColon;
            tokenText.append(ch);
        }


        return state;

    }

    private final class SimpleToken implements Token {
        //Token类型
        private TokenType type = null;

        //文本值
        private String text = null;

        @Override
        public TokenType getType() {
            return type;
        }

        @Override
        public String getText() {
            return text;
        }
    }


    private class SimpleTokenReader implements TokenReader {
        private List<Token> tokens = null;
        private int pos = 0;

        public SimpleTokenReader(List<Token> tokens) {
            this.tokens = tokens;
        }

        @Override
        public Token read() {
            if (pos < tokens.size()) {
                return tokens.get(pos++);
            }
            return null;
        }

        @Override
        public Token peek() {
            if (pos < tokens.size()) {
                return tokens.get(pos);
            }
            return null;
        }

        @Override
        public void unread() {
            if (pos > 0) pos--;
        }

        @Override
        public int getPosition() {
            return pos;
        }

        @Override
        public void setPosition(int pos) {
            if (pos >= 0 && pos < tokens.size()) {
                this.pos = pos;
            }
        }
    }

    //有限自动机的各种状态
    private enum DfaState {
        Initial,

        If, Id_if1, Id_if2, Else, Id_else1, Id_else2, Id_else3, Id_else4, Int, Id_int1, Id_int2, Id_int3, Id, GT, GE,

        Assignment,

        Plus, Minus, Star, Slash,

        SemiColon,
        LeftParen,
        RightParen,

        IntLiteral
    }

    //遍历所有Token
    private static void dump(SimpleTokenReader tokenReader) {
        System.out.println("text\ttype");
        Token token = null;
        while ((token = tokenReader.read()) != null) {
            System.out.println(token.getText() + "\t\t" + token.getType());
        }
    }
}
