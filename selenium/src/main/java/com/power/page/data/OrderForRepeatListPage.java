package com.power.page.data;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

import com.power.page.BasePage;

public class OrderForRepeatListPage extends BasePage{
    
    // 查询的part#输入框
    @FindBy(id = "indent_part")
    @CacheLookup
    WebElement indentPartInput;
    
    // 查询按钮
    @FindBy(xpath = "//span[contains(.,'查询')]")
    @CacheLookup
    WebElement searchButton;
    
    // 重复制作的链接
    @FindBy(linkText = "重复制作")
    @CacheLookup
    WebElement repeatLink;

    public OrderForRepeatListPage(WebDriver driver) {
        super(driver);
    }

    public OrderSavePage toRepeatOrderPage(String indentPart) {
        super.clearAndTypeString(indentPartInput, indentPart);
        searchButton.click();
        super.waitForAjaxPresent(3);
        repeatLink.click();
        super.waitForAjaxPresent(30);
        OrderSavePage savePage = new OrderSavePage(driver);
        savePage.initPage();
        return savePage;
    }
}
