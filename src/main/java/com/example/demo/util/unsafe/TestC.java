package com.example.demo.util.unsafe;

import sun.misc.Unsafe;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;

/**
 * Description：可以在运行时创建一个类
 * Author；JinHuatao
 * Date: 2019/7/10 14:21
 */
public class TestC {

    public static void main(String[] args) {
        // dynamic create a class
        byte[] classContents = new byte[0];
        try {
            classContents = getClassContent();
            Class c = getUnsafe().defineClass(null, classContents, 0, classContents.length, TestC.class.getClassLoader(), TestC.class.getProtectionDomain());
            System.out.println(c.getMethod("getA").invoke(c.newInstance(), null));//print 1
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Unsafe getUnsafe(){
        Field f = null;
        Unsafe unsafe = null;
        try {
            f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return unsafe;
    }

    private static byte[] getClassContent() throws Exception {
        File f = new File("E:/github/sample-demo/target/classes/com/example/demo/util/unsafe/TestD.class");
        FileInputStream input = new FileInputStream(f);
        byte[] content = new byte[(int)f.length()];
        input.read(content);
        input.close();
        return content;
    }

}
