package com.power.page.manufacture;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

import com.power.page.BasePage;

public class BomPage extends BasePage {
    public BomPage(WebDriver driver) {
        super(driver);
    }
    
    // 查询的part#输入框
    @FindBy(id = "indentPartParam")
    @CacheLookup
    WebElement indentPartInput;
    
    // 查询按钮
    @FindBy(xpath = "//span[contains(.,'查询')]")
    @CacheLookup
    WebElement searchButton;
    
    //datagrid的在header上的checkbox
    @FindBy(css = "div.datagrid-header-check")
    @CacheLookup
    WebElement headerCheckBox;
    
    //状态的下拉框
    @FindBy(id = "flag")
    @CacheLookup
    WebElement statusSelectField;
    
    // '发料审批'的按钮
    @FindBy(xpath = "//span[contains(.,'发料审批')]")
    @CacheLookup
    WebElement approveButton;

    /**
     * 先验条件：该part对应的物料信息已经配置好了
     * 
     * @param indentPart
     */
    public void approve(String indentPart) {
        // 填写 part#
        super.clearAndTypeString(indentPartInput, indentPart);
        // 点击查询
        searchButton.click();
        super.waitForAjaxPresent(2);
        // 把查询出来的每一行都选中
        headerCheckBox.click();
        // 点击“发料审批”
        approveButton.click();
        
    }
    
	/**
	 * 搜索配料信息，目前的状态选项是 全部
	 * 
	 * @param indentPart part#
	 * @param color 用于匹配的颜色
	 * @return
	 */
	public int searchForResult(String indentPart, String color) {
		// 填写 part#
		super.clearAndTypeString(indentPartInput, indentPart);
		super.selectElementByText(statusSelectField, "全部");
		// 点击查询
		searchButton.click();
		super.waitForAjaxPresent(3);
		List<WebElement> elements = driver.findElements(By.xpath(buildSearchXPath(color)));
		return elements.size();
	}
    
    private String buildSearchXPath(String color) {
    	return "//td[@field='countNum'][contains(@style,'" + color + "')]";
    }
}


