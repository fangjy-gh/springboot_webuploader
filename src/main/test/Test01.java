import org.junit.Test;

import java.time.LocalDate;

/**
 * @创建人 fangjy
 * @创建时间 2019/10/11
 * 描述
 */
public class Test01 {
    @Test
    public void first(){
        System.out.println(22);
        String property = System.getProperty("os.name");
        System.out.println(property);
    }
    @Test
    public void two(){
        LocalDate now = LocalDate.now();
        System.out.println(now.getYear()+"/"+now.getMonthValue()+"/"+now.getDayOfMonth());
    }




}
