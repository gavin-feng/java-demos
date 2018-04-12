package com.power.page.warehouse;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

import com.power.page.BasePage;

public class MaterialsApplyPage extends BasePage {
    @FindBy(id = "workOrder")
    @CacheLookup
    WebElement workOrderInput;

    // 查询按钮
    @FindBy(xpath = "//span[contains(.,'查询')]")
    @CacheLookup
    WebElement searchButton;

    public MaterialsApplyPage(WebDriver driver) {
        super(driver);
    }
    
    public int searchMaterialsApplyCountByWorkOrder(String workOrder) {
        super.clearAndTypeString(workOrderInput, workOrder);
        searchButton.click();
        super.waitForAjaxPresent(3);
        String matchXPath = "//div[@class='datagrid-cell datagrid-cell-c1-work_order'][contains(.,'"
                + workOrder + "')]";
        List<WebElement> elements = driver.findElements(By.xpath(matchXPath));
        return elements.size();
    }

}
