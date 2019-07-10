package com.example.demo.util.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Description：当想要绕过对象构造方法、安全检查器或者没有 public 的构造方法时，allocateInstance() 方法变得非常有用。
 *  https://www.jianshu.com/p/2e5b92d0962e
 * Author；JinHuatao
 * Date: 2019/7/10 13:52
 */
public class TestA {
    private int a = 0;

    private TestA(){
        a = 1;
    }

    public int getA(){
        return a;
    }

    public static void main(String[] args) {
        // constructor  构造方法
        TestA constructorA = new TestA();
        System.out.println(constructorA.getA());//print 1


        // reflection  反射方法
        try {
            TestA reflectionA = TestA.class.newInstance();
            System.out.println(reflectionA.getA());//print 1
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // unsafe   绕过对象构造方法、安全检查器或者没有 public 的构造方法
        Field f = null;
        try {
            f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Unsafe unsafe = (Unsafe) f.get(null);
            TestA unsafeA = (TestA) unsafe.allocateInstance(TestA.class);
            System.out.println(unsafeA.getA()); //print 0   此时绕过TestA的构造方法，直接访问了变量a的初始化值。
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }
}
