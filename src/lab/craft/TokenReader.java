package lab.craft;

public interface TokenReader {
    //返回Token流中的下一个Token，并从中取出，如果流为空，返回null
    public Token read();

    //返回Token流中的下一个Token，但不从中取出，如果流为空，返回null
    public Token peek();

    //Token流回退一步
    public void unread();

    //获取当前流的读取位置
    public int getPosition();

    //设置当前流读取的位置
    public void setPosition(int pos);
}
