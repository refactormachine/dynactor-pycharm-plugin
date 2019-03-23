package util.tree;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import junit.framework.TestCase;
import logic.RefactoringSuggestion;
import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;

public class TreeSerializationTest extends TestCase {
    class A implements Serializable{
        public A(int a1, int a2){
            value1 = a1;
            value2 = a2;
        }

        double value1, value2;
    }

    @Test
    public void testSerialize(){
        validateTreeSerialization(1.23, 4.56);
    }

    @Test
    public void testSerializeWithGenerics(){
        validateTreeSerialization(new A(1, 2), new A(3, 4));
    }

    @Test
    public void testSerializeRefactoringSuggestion(){
        RefactoringSuggestion r1 = RefactoringSuggestion.create(new HashMap<>(),
                "description", "oneLine", 1.23,
                "id1");
        RefactoringSuggestion r2 = RefactoringSuggestion.exampleRefactoringSuggestion("abcd");
        validateTreeSerialization(r1, r2);
    }

    private <T extends Serializable> void validateTreeSerialization(T r1, T r2) {
        Gson serializer = new GsonBuilder().create();
        TreeNode<T> t1 = new TreeNode<>(
                r1
        );
        t1.addChild(r2);
        t1.addChild(null);
        String jsoned = serializer.toJson(t1);
        String s = serializer.toJson(t1);
        TreeNode<T> x = serializer.fromJson(s, TreeNode.class);
        String fromJsonedJson = serializer.toJson(x);
        Assert.assertEquals(jsoned, fromJsonedJson);
        System.out.println("s = " + s);
        Assert.assertTrue(jsoned.contains(serializer.toJson(r2)));
    }
}
