package com.power.page.data;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.power.page.BasePage;

public class OrderSavePage extends BasePage {
    private static Logger log = LoggerFactory.getLogger(OrderSavePage.class);
    
    // '加工方式'的下拉框
    @FindBy(xpath = "//*[@id='div_jbxx3']/table/tbody/tr[3]/td[4]/span/span/span")
    @CacheLookup
    WebElement processWaySelect;
    // '加工方式'的下拉框的"激光"选项
    @FindBy(xpath = "/html/body/div[3]/div/div[1]")
    @CacheLookup
    WebElement processWaySelectLaser;                   
    // '加工方式'的下拉框的"蚀刻"选项
    @FindBy(xpath = "/html/body/div[3]/div/div[2]")
    @CacheLookup
    WebElement processWaySelectEtchOption;
    //'产品类型'的下拉框                                                       ---新添加的
    @FindBy(xpath ="//*[@id='div_jbxx3']/table/tbody/tr[2]/td[4]/span/span/span")  
    @CacheLookup
    WebElement productTypeCombo;
    //'产品类型'的下拉框的"Glue"选项                       ---新添加的
    @FindBy(xpath = "/html/body/div[47]/div/div[3]")         
    @CacheLookup
    WebElement productTypeGlueOption;
    //'工艺类型'的下拉框                                                        ---新添加的
    @FindBy(xpath ="//*[@id='div_jbxx4']/table/tbody/tr[1]/td[2]/span/span/span")   
    @CacheLookup
    WebElement technicalTypeCombo;
    //'工艺类型'的下拉框的"STEP+电铸"选项                ---新添加的                    
    @FindBy(xpath ="/html/body/div[45]/div/div[16]")    
    @CacheLookup                                   
    WebElement technicalTypeStepEFOption;              
   //'工艺类型'的下拉框的"蚀刻模板"选项                       ---新添加的         
    @FindBy(xpath ="/html/body/div[44]/div/div[2]")        
    @CacheLookup
    WebElement technicalTypeEtchOption;
   // "工艺模板"  的下拉框
    @FindBy(xpath ="//*[@id='div_jbxx3']/table/tbody/tr[1]/td[4]/span/span/span")        
    @CacheLookup
    WebElement craftCombo;
    //  "工艺模板"  的下拉框的"77885" 
    @FindBy(xpath ="/html/body/div[50]/div/div/div/div/div[2]/div[2]/table/tbody/tr[2]/td/div")
    @CacheLookup
    WebElement craftCombo77885Option;
    // '查询part#'的按钮
    @FindBy(xpath = "//input[@value='查询Part#']")
    @CacheLookup
    WebElement partNumFetchButton;
    // 订单数量的输入框
    @FindBy(id = "count_num")
    @CacheLookup
    WebElement indentCountField;
    // 提交数据的按钮
    @FindBy(id = "saveButton")
    @CacheLookup
    WebElement saveButton;
    
    public OrderSavePage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * 新增订单
     * 
     * @param customerCode 客户代码
     * @param customerModel 客户型号
     * @return
     */
    public String newOrder(String customerCode, String customerModel) {
        // 输入客户代码
        driver.findElement(By.id("customer_code")).clear();
        driver.findElement(By.id("customer_code")).sendKeys(customerCode);
        driver.findElement(By.id("customer_code")).sendKeys(Keys.ENTER);
        // 客户型号
        driver.findElement(
                By.xpath("//*[@id='div_jbxx3']/table/tbody/tr[1]/td[2]/span/input[1]")).sendKeys(
                        customerModel);
        driver.findElement(
                By.xpath("//*[@id='div_jbxx3']/table/tbody/tr[1]/td[2]/span/input[1]")).sendKeys(
                        Keys.ENTER);
        super.waitForAjaxPresent(1);
        driver.findElement(
                By.xpath("/html/body/div[35]/div/div/div/div[1]/div[2]/div[2]/table/tbody/tr[1]/td[1]/div")).click();
        // 定位工艺模板的下拉框
        craftCombo.click();
        // 选择736*736激光模板
        driver.findElement(
                By.xpath("/html/body/div[51]/div/div/div/div/div[2]/div[2]/table/tbody/tr/td/div")).click();
        // 厚度
        driver.findElement(By.id("steel_th")).clear();
        driver.findElement(By.id("steel_th")).sendKeys("0.12");
       //super.sleepSeconds(10);
       // 定位产品类型的下拉框                                           ---新添加的
        productTypeCombo.click();
        // 选择产品类型- Gule                 ---新添加的
        driver.findElement(
                By.xpath("/html/body/div[47]/div/div[3]")).click();
        // super.sleepSeconds(10);
        
        // 定位加工方式 的下拉框
        processWaySelect.click();
        // 选择加工方式 - 激光
        processWaySelectLaser.click();
        // 数量1
        super.clearAndTypeString(indentCountField, "1");
        // 定义工艺类型                                              ---新添加的
        technicalTypeCombo.click();
       // 选择工艺类型 -STEP+电铸                        ---新添加的
        driver.findElement(
                By.xpath("/html/body/div[45]/div/div[16]")).click();   
        technicalTypeCombo.click();
    	technicalTypeStepEFOption.click();
        // 将页面滚动条拖到底部
        String js = "var q=document.documentElement.scrollTop=10000";
        ((JavascriptExecutor) driver).executeScript(js);
        // 输入出货清单备注信息
        driver.findElement(By.id("shipment_accessory")).clear();
        driver.findElement(By.id("shipment_accessory")).sendKeys("测试备注出货清单：TEST；\n出货备注信息");
        // 输入备注特殊信息
        driver.findElement(By.id("special_req")).clear();
        driver.findElement(By.id("special_req")).sendKeys("测试特殊要求备注：TEST；\n特殊备注");
        // 获取part#
        String indentPart = clickToFetchPartNum();
        // 点击提交订单按钮
        saveButton.click();
        // 当前订单流程选择为:[本地处理模式]确认下单吗?，点击确定
        driver.findElement(By.cssSelector("span.l-btn-text.l-btn-focus")).click(); 
        log.info("下单成功：" + indentPart);
        return indentPart;
    }
    
    public void repeatOrder() {
        // 数量1
        super.clearAndTypeString(indentCountField, "1");
        saveButton.click();
        // "确定修改当前的订单?"对话框的确认
        driver.findElement(By.xpath("//span[contains(.,'确定')]")).click();
        log.info("重复制作生成成功！");
    }
    
    private String clickToFetchPartNum() {
        // 点击查询part按钮
        partNumFetchButton.click();
        // 获取提示的part#
        String indentPart = driver.findElement(By.xpath("//div[@class='panel window messager-window']/div[2]/div[2]")).getText();
        driver.findElement(By.cssSelector("span.l-btn-text.l-btn-focus")).click();
        return indentPart;
    }
    
    /**
     * 修改订单，part号变更
     * 
     * @return
     */
    public String editOrderWithPartNumChanged() {
        processWaySelect.click();
        processWaySelectEtchOption.click();
        String indentPart = clickToFetchPartNum();
        saveButton.click();
        // "确定修改当前的订单?"对话框的确认
        driver.findElement(By.xpath("//span[contains(.,'确定')]")).click();
        log.info("修改成功：" + indentPart);
        return indentPart;
    }
    
    /**
     * 修改订单，part号不变但配料信息变更
     * 
     * @return
     */
    public String editOrderWithPartNumUnchangedButBomChanged() {
    	craftCombo.click();
    	craftCombo77885Option.click();
        String newIndentPart = clickToFetchPartNum();
        saveButton.click();
        super.waitForAjaxPresent(3);
        // "确定修改当前的订单?"对话框的确认
        driver.findElement(By.xpath("//span[contains(.,'确定')]")).click();
        super.sleepSeconds(3);
        // "你确定修改当前的订单？"对话框的确定          
        getWebElement(By.xpath("//span[contains(.,'确定')]")).click();
        super.sleepSeconds(3);
        log.info("修改成功：" + newIndentPart);
        return newIndentPart;
    }

    /**
     * 修改订单，part号不变但配料类型变更
     * 
     * @return
     */
    public String editOrderWithPartNumUnchangedButBomTypeChanged() {    
    	technicalTypeCombo.click();
  	    technicalTypeEtchOption.click();
        String IndentPart = clickToFetchPartNum();
        saveButton.click();
        super.waitForAjaxPresent(3);
        //"您当前所下的订单的Part#在24小时内被登记过,是否继续?"对话框的确认
        getWebElement(By.xpath("//span[contains(.,'确定')]")).click();   
        super.sleepSeconds(3);
        // "你确定修改当前的订单？"对话框的确定            
        getWebElement(By.xpath("//span[contains(.,'确定')]")).click();  
        super.sleepSeconds(3); 
        log.info("修改成功：" + IndentPart);
        return IndentPart;
    }
}  







