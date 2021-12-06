import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockTests {

    private final Lock lock = new ReentrantLock();

    @Test
    public void method1() {

        lock.lock();

        try{
            something();

        }catch(Exception e){
            lock.unlock();
        }

    }

    private void something() {

        String a = "12";
        int b = 2;
        String c = "1" + b;

        System.out.println(a == c);

    }


}
