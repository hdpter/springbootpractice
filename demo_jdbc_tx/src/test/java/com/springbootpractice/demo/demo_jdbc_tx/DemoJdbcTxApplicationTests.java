package com.springbootpractice.demo.demo_jdbc_tx;

import com.springbootpractice.demo.demo_jdbc_tx.biz.TxJdbcBiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionManager;
import org.springframework.util.Assert;

@SpringBootTest
class DemoJdbcTxApplicationTests {

    @Autowired
    private TxJdbcBiz txJdbcBiz;

    @Autowired
    private TransactionManager transactionManager;

    @Test
    void testInsertUserTest() {

        final int result = txJdbcBiz.insertUserLogin("monika.smith", "xxxx");

        Assert.isTrue(result > 0, "插入失败");


    }

    @Test
    void insertUserLoginTransactionTest() {
        final int result = txJdbcBiz.insertUserLoginTransaction("stefan.li", "hello transaction");
        Assert.isTrue(result > 0, "插入失败");

    }

    @Test
    void transactionManagerTest() {
        System.out.println(transactionManager.getClass().getName());
    }
}
