import com.rice.meng.common.util.RandomUtils;
import com.rice.meng.service.decorator.Computable;
import com.rice.meng.service.decorator.Memoizer;
import com.rice.meng.service.decorator.UserComputer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class DecoratorTests {

    @Test
    public void testDecorator() {
        Computable<String, Integer> computer = new Memoizer<>(new UserComputer());

        int total = 10000;
        int count = 10000;
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int nextInt = RandomUtils.nextInt(0, 10);
            list.add(nextInt);
        }

        int[] ints = reverse_IntStream(list).toArray();

        IntStream.of(ints)
                .parallel()
                .forEach(i -> {
                    Integer compute = computer.compute(String.valueOf(i));
                    Assertions.assertEquals(i, compute);
                });

        Assertions.assertEquals(total, count);
    }

    public static IntStream reverse_IntStream(List<Integer> list) {
        if (list == null) {
            throw new IllegalArgumentException("list can't be null");
        }
        int size = list.size();
        return IntStream.iterate(size - 1, el -> el - 1).limit(size).map(list::get);
    }
}
