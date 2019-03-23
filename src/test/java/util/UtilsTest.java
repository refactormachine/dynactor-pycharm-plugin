package util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

public class UtilsTest {

    @Test
    public void testToArray() {
        assertArrayEquals(new Integer[]{1, 2, 3},
                Utils.toArrayNoPolymorphism(Arrays.asList(1, 2, 3)));
        assertArrayEquals(new Integer[]{},
                Utils.toArrayNoPolymorphism(new ArrayList<Integer>()));
    }
}