import org.junit.jupiter.api.Test;

public class TestLanguage {


    /**
     *
     */
    @Test
    public void testConcatLong(){
        Long x =(long)9;
        String concat = "aa".concat("." + x);
        Long x1 =(long)100;
        String concat1 = "aa".concat("." + x1);
        System.out.println(concat);
        System.out.println(concat1);
    }
}
