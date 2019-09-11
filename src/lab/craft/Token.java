package lab.craft;
/**
 * 一个简单的Token。
 * 只有类型和文本值两个属性。
 */
public interface Token {
    //Token类型
    public TokenType getType();

    //Token文本信息
    public String getText();

}
