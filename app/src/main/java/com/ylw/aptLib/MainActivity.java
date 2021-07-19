package com.ylw.aptLib;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * author : liwen15
 * date : 2021/7/18
 * description :
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //以下全是能够造成崩溃的方法，增加了@SafeRun注解，自测能够防止崩溃
        Test.test2("123");
        System.out.println("test...getName " + Test.getName());
        System.out.println("test...getName " + Test.test23(""));
        System.out.println("test...getAge " + Test.getAge());
        if (Test.getPerson() != null) {
            System.out.println("test...getPerson " + Test.getPerson().toString());
        }
    }
}
