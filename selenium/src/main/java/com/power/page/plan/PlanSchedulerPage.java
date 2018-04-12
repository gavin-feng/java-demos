package com.power.page.plan;

import com.power.page.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlanSchedulerPage extends BasePage {
    private static Logger log = LoggerFactory.getLogger(PlanSchedulerPage.class);
    
    public PlanSchedulerPage(WebDriver driver) {
        super(driver);
    }
    
    public void scheduleDelivery(String indentPart) {
        // 定位“工程处理订单”TAP页
        driver.findElement(By.xpath("//*[@id='planIndentTabs']/div[1]/div[3]/ul/li[1]/a/span[1]")).click();
        driver.findElement(By.id("qpartNum")).clear();
        driver.findElement(By.id("qpartNum")).sendKeys(indentPart);
        // 点击查询按钮
        driver.findElement(By.xpath("//*[@id='div_button']/a[1]/span/span")).click();
        super.waitForAjaxPresent(2);
        driver.findElement(By.linkText("出货安排")).click();
        // 定位“出货安排”frame
        WebElement iframeEle = driver.findElement(By.id("sendInfoIframe"));
        driver.switchTo().frame(iframeEle);
        // 输入计划出货时间
        String nowStr = getNowStr();
        driver.findElement(By.xpath("//*[@id='checkIndentSendInfo']/table/tbody/tr[1]/td[4]/span/input[1]")).clear();
        driver.findElement(By.xpath("//*[@id='checkIndentSendInfo']/table/tbody/tr[1]/td[4]/span/input[1]")).sendKeys(nowStr);
        // 输入要求出货时间
        driver.findElement(By.xpath("//*[@id='checkIndentSendInfo']/table/tbody/tr[4]/td[4]/span/input[1]")).clear();
        driver.findElement(By.xpath("//*[@id='checkIndentSendInfo']/table/tbody/tr[4]/td[4]/span/input[1]")).sendKeys(nowStr);
        driver.findElement(By.id("REMARK")).clear();
        driver.findElement(By.id("REMARK")).sendKeys("测试出货安排备注：\ntest");
        // 提交出货安排信息
        driver.findElement(By.xpath("//*[@id='editbtn3']/span/span")).click();
        log.info("提交出货安排信息成功！");
    }

    private String getNowStr() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(now);
    }
    
    public void scheduleManufacture(String indentPart, String name) {
        // 生产订单tab页
        driver.findElement(By.xpath("//span[contains(.,'生产订单')]")).click();
        // 等待点击该tab页之后的自动的搜索结束，避免先查出我们要的结果后被自动搜索的结果覆盖了
        super.waitForAjaxPresent(5);    
        driver.findElement(By.id("qpartNum1")).clear();
        driver.findElement(By.id("qpartNum1")).sendKeys(indentPart);
        // 查询
        driver.findElement(By.xpath("//*[@id='check2']/span/span")).click();
        super.waitForAjaxPresent(3);
        // 生产安排操作
        // 点击生产安排链接
        driver.findElement(By.xpath("//a[contains(.,'生产安排')]")).click();
        // 定位“生产安排”frame
        WebElement iframeEle = driver.findElement(By.id("planProduceIframe"));
        driver.switchTo().frame(iframeEle);
        // 计划完成时间
        driver.findElement(By.xpath("//*[@id='planProduceDetail']/table/tbody/tr[2]/td[2]/span/span/span")).click();
        driver.findElement(By.cssSelector("a.datebox-ok")).click(); // 点击弹出的日期选择中的“确定”
        // 选择生产流程
        driver.findElement(By.xpath("//*[@id='planProduceDetail']/table/tbody/tr[1]/td/span/span/span")).click();
        driver.findElement(By.xpath("//span[contains(.,'" + name +"')]")).click();
        //super.sleepSeconds(2);
        // 填写生产数量
        driver.findElement(By.id("PRODUCT_NUM")).clear();
        driver.findElement(By.id("PRODUCT_NUM")).sendKeys("1");
        // 生产安排后提交
        driver.findElement(By.id("editbtn3")).click();
        log.info("提交生产安排信息成功！");
    }
}
