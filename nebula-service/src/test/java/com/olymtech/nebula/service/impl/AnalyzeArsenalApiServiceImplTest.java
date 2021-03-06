package com.olymtech.nebula.service.impl;

import com.olymtech.nebula.service.IAnalyzeArsenalApiService;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by Gavin on 2015-11-11 15:01.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.config.xml"})
@TransactionConfiguration(defaultRollback = false)
public class AnalyzeArsenalApiServiceImplTest extends TestCase {

    @Resource
    private IAnalyzeArsenalApiService analyzeArsenalApiService;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetSimpleHostListByProductAndModule() throws Exception {
//        List<ProductTree> productTrees = analyzeArsenalApiService.getSimpleHostListByProductAndModule("yjt2014","priceservice,schedulerboss,serviceportal,ebooking","test");
        System.out.println("success");
    }

    @Test
    public void testGetProductTreeListByPid() throws Exception {
        Map<String, Object> map = analyzeArsenalApiService.getSimpleHostListByProductAndModule("yjt2015", "freight-elcl-rest,freight-elcl-ds", "stage");
    }

    @Test
    public void testInstancesJsonArrayToProductTreeList() throws Exception {

    }
}