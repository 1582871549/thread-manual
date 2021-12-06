import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionTests {

    @Test
    public void method1() {

        Map<String, String> map = new HashMap<>();
        map.put("k-1", "v-1");
        map.put("k-2", "v-2");
        map.put("k-3", "v-3");

        List<String> list = new ArrayList<>(map.values());

        System.out.println("map: " + map);
        System.out.println("list: " + list);
        System.out.println("------");

        map.clear();
        System.out.println("map: " + map);
        System.out.println("list: " + list);

    }



}
