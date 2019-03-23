package logic;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class FileDiffTest extends TestCase {

    @Test
    public void testValidDiff() {
        Assert.assertTrue(FileDiff.create("abc", "").isDiffValid(
                "abc"
        ));
        Assert.assertTrue(FileDiff.create("abc\n \n  ", "").isDiffValid(
                "abc\n  "
        ));
        Assert.assertTrue(FileDiff.create("\nabc", "").isDiffValid(
                "abc\n"
        ));
        Assert.assertTrue(FileDiff.create("abc", "").isDiffValid(
                "\n\n\nabc"
        ));
        Assert.assertTrue(FileDiff.create("abc", "").isDiffValid(
                "\nabc\n  "
        ));
    }

    @Test
    public void testInvalidDiff() {
        Assert.assertFalse(FileDiff.create("abc", "").isDiffValid(
                "abc\na"
        ));
        Assert.assertFalse(FileDiff.create("", "").isDiffValid(
                "abc\n\n"
        ));
        Assert.assertFalse(FileDiff.create("abc", "").isDiffValid(
                "abc \n\n"
        ));
        Assert.assertFalse(FileDiff.create("abc", "").isDiffValid(
                "\n"
        ));
        Assert.assertFalse(FileDiff.create("abc", "").isDiffValid(
                ""
        ));
    }
}