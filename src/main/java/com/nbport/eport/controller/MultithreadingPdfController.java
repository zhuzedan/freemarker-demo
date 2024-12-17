package com.nbport.eport.controller;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.nbport.eport.utils.PdfTemplateUtil2;
import com.nbport.eport.utils.PdfTemplateUtil3;
import com.nbport.eport.vo.PdfDataTest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author zella
 */
@RestController
@RequestMapping("/pdf2")
public class MultithreadingPdfController {

    /**
     * @description : 有返回值的线程
     **/
    @RequestMapping("/export")
    public void exportPdf(HttpServletResponse response) throws Exception {
        OutputStream out = null;
        ByteArrayOutputStream baos = null;

        // 模板中的数据，实际运用从数据库中查询
        Map<String, Object> data = getStringObjectMap();

        int pressure = 10;
        //并发开启100个线程调用/rearrange_sale/rearrange接口，查看更新update操作锁表可能引起的异常。
        ExecutorService executorService = new ThreadPoolExecutor(10, 50, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024),
                new ThreadFactoryBuilder().setNameFormat("index-thread-%d").build(),
                new ThreadPoolExecutor.AbortPolicy());

        List<Future<ByteArrayOutputStream>> futureList = new ArrayList<>(pressure);
        Future<ByteArrayOutputStream> result = null;

        try {
            //利用线程池，开启多线程模式
            for (int idx = 0; idx < pressure; idx++) {
                result = (Future<ByteArrayOutputStream>) executorService.submit(new PdfTemplateUtil2(data, "test.ftl"));
                futureList.add(result);
            }
            //调用future方法阻塞当前线程，直至所有的分线程执行完毕
            for (Future<ByteArrayOutputStream> future : futureList) {
                System.out.println("future.get(): " + future.get());

            }
            baos = result.get();
            // 设置响应消息头，告诉浏览器当前响应是一个PDF文档，并且应该在浏览器中内联显示
            response.setContentType("application/pdf");
            // 告诉浏览器，当前响应数据要求用户干预保存到文件中，以及文件名是什么 如果文件名有中文，必须URL编码
            response.setHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode("xxx报告.pdf", "UTF-8"));
            out = response.getOutputStream();
            baos.writeTo(out);
            executorService.shutdown();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                baos.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }


    /**
     * @description : 无返回值的线程
     **/
    @RequestMapping("/export1")
    public void exportPdf1(HttpServletResponse response) throws Exception {
        // 模板中的数据，实际运用从数据库中查询
        Map<String, Object> data = getStringObjectMap();
        String fileName = URLEncoder.encode("xxx报告.pdf", "UTF-8");

        int pressure = 10;
        ExecutorService executorService = new ThreadPoolExecutor(10, 50, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024),
                new ThreadFactoryBuilder().setNameFormat("index-thread-%d").build(),
                new ThreadPoolExecutor.AbortPolicy());

        try {
            //利用线程池，开启多线程模式
            for (int idx = 0; idx < pressure; idx++) {
                executorService.execute(new PdfTemplateUtil3(data, "test.ftl", fileName, response));
            }
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> getStringObjectMap() {
        // 模板中的数据，实际运用从数据库中查询
        Map<String, Object> data = new HashMap<>();
        data.put("curr", 1);
        data.put("one", 2);
        data.put("two", 1);
        data.put("three", 6);
        List<PdfDataTest> detailList = new ArrayList<>();
        detailList.add(new PdfDataTest(123456, "测试", "测试", "测试", "测试"));
        detailList.add(new PdfDataTest(111111, "测试", "测试", "测试", "测试"));
        detailList.add(new PdfDataTest(222222, "测试", "测试", "测试", "测试"));
        data.put("detailList", detailList);
        return data;
    }

}
 