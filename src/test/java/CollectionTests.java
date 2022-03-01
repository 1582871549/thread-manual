import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class CollectionTests {

    @Test
    public void testListSort() {

        List<Integer> list = IntStream.range(0, 100)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        log.info("before sort, orderedList: {}", list);
        log.info("-------------------------");

        Comparator<Integer> comparator = (a, b) -> b - a;

        List<Integer> orderedList = list.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        log.info("after sort, orderedList: {}", orderedList);
        log.info("-------------------------");

        int index = Collections.binarySearch(orderedList, 15, comparator);
        log.info("index: [{}], key: [{}]", index, orderedList.get(index));

        Assertions.assertEquals(0, Collections.binarySearch(orderedList, 99, comparator));
        Assertions.assertEquals(1, Collections.binarySearch(orderedList, 98, comparator));
        Assertions.assertEquals(84, Collections.binarySearch(orderedList, 15, comparator));
        Assertions.assertEquals(15, Collections.binarySearch(orderedList, 84, comparator));
        Assertions.assertEquals(99, Collections.binarySearch(orderedList, 0, comparator));
    }

    @Test
    public void testFlatMap() {

        List<String> list1 = new ArrayList<>();
        list1.add("dubbo");
        list1.add("spring cloud");

        List<String> list2 = new ArrayList<>();
        list2.add("java");
        list2.add("golang");
        list2.add("python");

        List<String> list3 = new ArrayList<>();
        list3.add("js");
        list3.add("es");
        list3.add("ts");

        List<List<String>> lists = Stream.of(list1, list2, list3)
                .collect(Collectors.toList());

        log.info("lists: {}", lists);

        List<String> collect = lists.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        log.info("collect: {}", collect);
    }


}
