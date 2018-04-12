package com.power.page.data;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.power.page.BasePage;

public class OrderControlPage extends BasePage {

    public OrderControlPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * 跳转到订单修改的页面
     * 
     * @param indentPart
     * @return OrderSavePage 订单修改保存的页面对象
     */
    public OrderSavePage toModifyOrder(String indentPart) {
        // 输入查询的part#
        super.clearAndTypeString(getWebElement(By.id("qpartNum")), indentPart);
        // 点击“查询”
        getWebElement(By.xpath("//span[contains(.,'查询')]")).click();
        super.waitForAjaxPresent(2);
        // 点击订单信息列的“修改”
        getWebElement(By.partialLinkText("修改")).click();
        // 选中“修改订单”
        getWebElement(By.xpath("//input[@value='update']")).click();
        // 点击“确定”
        getWebElement(By.xpath("//input[@value='确定']")).click();
        // "你确定对订单执行此操作?"对话框的确认
        getWebElement(By.xpath("//span[contains(.,'确定')]")).click();
        super.waitForAjaxPresent(30);
        OrderSavePage orderSavePage = new OrderSavePage(driver);
        orderSavePage.initPage();
        return orderSavePage;
    }
}
