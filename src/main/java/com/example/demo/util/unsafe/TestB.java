package com.example.demo.util.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Description：绕过安全的常用技术，直接修改内存变量。
 *       反射也可以实现相同的功能。但是 Unsafe 可以修改任何对象，甚至没有这些对象的引用。
 *       https://www.jianshu.com/p/2e5b92d0962e
 * Author；JinHuatao
 * Date: 2019/7/10 14:03
 */
public class TestB {

    private int ACCESS_ALLOWED = 1;

    public boolean giveAccess(){
        return 40 == ACCESS_ALLOWED;
    }


    public static void main(String[] args) {
        //constructor
        TestB constructorB = new TestB();
        System.out.println(constructorB.giveAccess());//print false

        //unsafe
        Field f = null;
        try {
            f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Unsafe unsafe = (Unsafe) f.get(null);
            TestB unsafeB = (TestB) unsafe.allocateInstance(TestB.class);
            Field unsafeBField = unsafeB.getClass().getDeclaredField("ACCESS_ALLOWED");
            //绕过安全的常用技术，直接修改内存变量
            unsafe.putInt(unsafeB, unsafe.objectFieldOffset(unsafeBField), 40);//memory corruption
            System.out.println(unsafeB.giveAccess());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
