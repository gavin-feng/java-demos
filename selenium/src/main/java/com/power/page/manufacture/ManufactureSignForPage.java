package com.power.page.manufacture;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

import com.power.page.BasePage;

public class ManufactureSignForPage extends BasePage {
    
    // 接收工位任务的Tab
    @FindBy(xpath = "//span[contains(.,'接收工位任务')]")
    @CacheLookup
    WebElement signForTab;
    
    // 查询的part#输入框
    @FindBy(id = "partNo1")
    @CacheLookup
    WebElement indentPartInput;
    
    // 查询按钮
    @FindBy(xpath = "//span[contains(.,'查询')]")
    @CacheLookup
    WebElement searchButton;
    
    // 接收工位任务的Tab
    @FindBy(linkText = "接受订单")
    @CacheLookup
    WebElement acceptOrderLink;

    public ManufactureSignForPage(WebDriver driver) {
        super(driver);
    }

    public void acceptFirstNode(String indentPart) {
        signForTab.click();
        super.waitForAjaxPresent(3);
        super.clearAndTypeString(indentPartInput, indentPart);
        searchButton.click();
        super.waitForAjaxPresent(3);
        acceptOrderLink.click();
        super.alertClickOK(2);
    }
}
