package com.gabby.core.test;

import com.gabby.core.SaveState;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class SaveStateTest {
    @Test
    public void testFileOps() {
        SaveState s1 = new SaveState();
        
        // set some random values
        s1.a = 1;
        s1.c = 2;
        s1.sp = 5;
        s1.pc = 10;
        s1.memory[200] = 20;
        s1.memory[300] = 10;

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("test.sav"));
            out.writeObject(s1);
            out.close();

            ObjectInputStream in = new ObjectInputStream(new FileInputStream("test.sav"));
            SaveState s2 = (SaveState) in.readObject();

            // Could test more stuff, but if these all check out, I imagine the rest will

            Assert.assertEquals(s1.a, s2.a);
            Assert.assertEquals(s1.c, s2.c);
            Assert.assertEquals(s1.sp, s2.sp);
            Assert.assertEquals(s1.pc, s2.pc);
            Assert.assertEquals(s1.memory[200], s2.memory[200]);
            Assert.assertEquals(s1.memory[300], s2.memory[300]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
