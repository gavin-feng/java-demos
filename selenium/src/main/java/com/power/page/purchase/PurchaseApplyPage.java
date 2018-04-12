package com.power.page.purchase;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.power.page.BasePage;

public class PurchaseApplyPage extends BasePage {
    private static Logger log = LoggerFactory.getLogger(PurchaseApplyPage.class);
    
    public PurchaseApplyPage(WebDriver driver) {
        super(driver);
    }
    
    public String newPurchaseApply(){
        // 点击“请购申请” editBtn
        driver.findElement(By.id("editBtn")).click();
        // 获取“请购编码” applyCode
        String purchaseApplyCode = driver.findElement(By.id("applyCode")).getAttribute("value");
        // 输入“请购原因” remark
        driver.findElement(By.id("remark")).sendKeys("blabla...");
        
        // 选择物料 钢片0.10*600*570
        String matName = "钢片0.10*600*570";
        driver.findElement(By.id("editBtn1")).click();
        String searchAeraXPath = "//input[@placeholder='物料名称/编码']";
        super.waitForAjaxPresent(3);
        driver.findElement(By.xpath(searchAeraXPath)).clear();
        driver.findElement(By.xpath(searchAeraXPath)).sendKeys(matName);
        driver.findElement(By.xpath(searchAeraXPath)).sendKeys(Keys.ENTER); // 回车即查询
        String matchMatName = "-" + matName + ",";  // 确保唯一
        By by = By.xpath("//span[contains(.,'"+ matchMatName +"')]");
        super.waitForWebElementClickable(by);   // 等待搜索结果的出现
        driver.findElement(by).click();
        driver.findElement(By.xpath("//span[contains(.,'确定添加')]")).click();
        // 选择物料 钢片网框736*736/40*30/斜边/无螺孔/空心
        
        // 点击提交 submitApplies
        driver.findElement(By.id("submitApplies")).click();
        log.info("请购申请成功： "+purchaseApplyCode);
        return purchaseApplyCode;
    }
    
    /**
     * 测试的先验条件：登录的用户确实要审批该订单的这一环节
     * 
     * @param applyCode
     */
    public void audit(String applyCode) {
        // 输入请购编号 applyNum
        driver.findElement(By.id("applyNum")).clear();
        driver.findElement(By.id("applyNum")).sendKeys(applyCode);
        // 点击查询
        driver.findElement(By.xpath("//span[contains(.,'查询')]")).click();
        super.waitForAjaxPresent(1);
        // 点击同意
        driver.findElement(By.linkText("同意")).click();
        // 点击提交
        driver.findElement(By.id("confirmSubmit")).click();
        log.info("审批成功： " + applyCode);
    }
}
