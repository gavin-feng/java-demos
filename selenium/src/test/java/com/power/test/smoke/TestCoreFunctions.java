package com.power.test.smoke;

import com.power.page.base.HomePage;
import com.power.page.base.LoginPage;
import com.power.page.data.OrderControlPage;
import com.power.page.data.OrderForRepeatListPage;
import com.power.page.data.OrderSavePage;
import com.power.page.manufacture.BomPage;
import com.power.page.manufacture.ManufactureSignForPage;
import com.power.page.plan.PlanSchedulerPage;
import com.power.page.purchase.PurchaseApplyPage;
import com.power.page.warehouse.MaterialsApplyPage;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestCoreFunctions {
    public static Logger log = LoggerFactory.getLogger(TestCoreFunctions.class);
    private WebDriver driver;
    private StringBuffer verificationErrors = new StringBuffer();
    private static String customerCode = "280500";
    private static String customerModel = "MAS40-1002&1202";
    private static String url = "http://192.168.1.70:8080/PowerFramework/login.jsp";

    @Before
    public void setUp() throws Exception {
        // 需要配置 webdriver.chrome.driver 环境变量
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }
    
    /**
     * 新增订单、出货安排、生产安排、数据处理的冒烟测试
     */
    @Test
    public void testOrderAndDataProcess() {
        String username = "situo";
        String passwd = "123123";
        log.info("Start Smoke Testing of order-related actions");
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.login(url, username, passwd);
        OrderSavePage orderRegisterPage = 
                (OrderSavePage)homePage.switchToSubMenuPage(HomePage.SUBMENU_NEW_ORDER);
        String indentPart = orderRegisterPage.newOrder(customerCode, customerModel);
        PlanSchedulerPage planSchedulerPage = 
                (PlanSchedulerPage)homePage.switchToSubMenuPage(HomePage.SUBMENU_DISPATCH);
        planSchedulerPage.scheduleDelivery(indentPart);
        
        String processName = "（昆山）激光快速出货";
        planSchedulerPage = 
                (PlanSchedulerPage)homePage.switchToSubMenuPage(HomePage.SUBMENU_DISPATCH);
        planSchedulerPage.scheduleManufacture(indentPart, processName);
    }
    
    /**
     * 测试重复制作、生产签收订单
     */
    @Test
    public void testOrderRepeat() {
        String username = "szpwr";
        String passwd = "123456";
        log.info("Start Smoke Testing of order-related actions");
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.login(url, username, passwd);
        OrderForRepeatListPage repeatListPage = 
                (OrderForRepeatListPage)homePage.switchToSubMenuPage(HomePage.SUBMENU_ORDER_REPEAT);
        String indentPart = "1299-0764-02-01-1-1";
        OrderSavePage orderSavePage = repeatListPage.toRepeatOrderPage(indentPart);
        orderSavePage.repeatOrder();

        String processName = "自定义切割加";
        PlanSchedulerPage planSchedulerPage = 
                (PlanSchedulerPage)homePage.switchToSubMenuPage(HomePage.SUBMENU_DISPATCH);
        planSchedulerPage.scheduleManufacture(indentPart, processName);
        homePage.sleepSeconds(3);
        
        ManufactureSignForPage signForPage = 
                (ManufactureSignForPage)homePage.switchToSubMenuPage(HomePage.SUBMENU_SIGN_FOR);
        signForPage.acceptFirstNode(indentPart);
        homePage.sleepSeconds(3);
    }
    
    /**
     * 新增订单、生产安排、配料、修改订单（导致part#变更、但物料没变）、查询配料信息
     * 
     * 先验条件： 新增的订单，有自动配料信息
     * 
     */
    @Test
    public void testModifyOrderWithPartNumChanged() {
        String username = "situo";
        String passwd = "123123";
        log.info("Start Smoke Testing of order-changed for bom");
        LoginPage loginPage = new LoginPage(driver);
        // 登录
        HomePage homePage = loginPage.login(url, username, passwd);
        // 切换到订单登记
        OrderSavePage orderSavePage = 
                (OrderSavePage)homePage.switchToSubMenuPage(HomePage.SUBMENU_NEW_ORDER);
        String indentPart = orderSavePage.newOrder(customerCode, customerModel);
        // 生产安排
        String processName = "（昆山）激光快速出货";
        PlanSchedulerPage planSchedulerPage = 
                (PlanSchedulerPage)homePage.switchToSubMenuPage(HomePage.SUBMENU_DISPATCH);
        planSchedulerPage.scheduleManufacture(indentPart, processName);
        // 配料审批
        BomPage bomPage = (BomPage)homePage.switchToSubMenuPage(HomePage.SUBMENU_BOM);
        bomPage.approve(indentPart);
        // 验证审批后的领用信息，可以查到
        MaterialsApplyPage materialsApplyPage = 
                (MaterialsApplyPage)homePage.switchToSubMenuPage(HomePage.SUBMENU_MATERIALS_APPLY);
        int searchCount = materialsApplyPage.searchMaterialsApplyCountByWorkOrder(indentPart);
        assertTrue(searchCount>0);
        // 订单控制菜单下，修改订单
        OrderControlPage orderControlPage = 
                (OrderControlPage)homePage.switchToSubMenuPage(HomePage.SUBMENU_ORDER_CONTROL);
        orderSavePage = orderControlPage.toModifyOrder(indentPart);
        String newIndentPart = orderSavePage.editOrderWithPartNumChanged();
        // 使用旧part#验证，应该查不到领用信息
        materialsApplyPage = 
                (MaterialsApplyPage)homePage.switchToSubMenuPage(HomePage.SUBMENU_MATERIALS_APPLY);
        int searchOldCount = materialsApplyPage.searchMaterialsApplyCountByWorkOrder(indentPart);
        assertTrue(searchOldCount == 0);
        // 使用新part#验证，应该能查到领用信息
        int searchNewCount = materialsApplyPage.searchMaterialsApplyCountByWorkOrder(newIndentPart);
        assertTrue(searchNewCount > 0);
    }
    
    /**
     * 深圳请购、采购、入库、检验、出库的冒烟测试
     * 先验条件：
     *         1、邓刚峰、奚春燕、钟颂明的账号正常，且密码重置为123456
     *         2、请购、采购流程的权限配置OK
     */
    @Test
    public void testPurchaseStockInAndOut() {
        String userDGF = "pwr047";
        String userXCY = "pwr022";
        String userZSM = "pwr006";
        String passwd = "123456";
        LoginPage loginPage = new LoginPage(driver);
        // 深圳请购申请
        HomePage homePage = loginPage.login(url, userDGF, passwd);
        PurchaseApplyPage purchaseApplyPage = 
                (PurchaseApplyPage)homePage.switchToSubMenuPage(HomePage.SUBMENU_PURCHASE_APPLY);
        String applyCode = purchaseApplyPage.newPurchaseApply();
        purchaseApplyPage.waitForAjaxPresent(2);     // 等待后台处理完毕，防止退出发生在处理完毕之前，使得用户session仍然存在
        homePage.logout();
        
        // 主管审核
        //String applyCode = "SZ0120160524004";
        homePage = loginPage.login(url, userXCY, passwd);
        purchaseApplyPage = 
                (PurchaseApplyPage)homePage.switchToSubMenuPage(HomePage.SUBMENU_PURCHASE_APPLY_AUDIT);
        purchaseApplyPage.audit(applyCode);
        purchaseApplyPage.waitForAjaxPresent(2);     // 等待后台处理完毕
        homePage.logout();
        
        // 总经理审批
        homePage = loginPage.login(url, userZSM, passwd);
        purchaseApplyPage = 
                (PurchaseApplyPage)homePage.switchToSubMenuPage(HomePage.SUBMENU_PURCHASE_APPLY_AUDIT);
        purchaseApplyPage.audit(applyCode);
        purchaseApplyPage.waitForAjaxPresent(2);
    }

    @After
    public void tearDown() throws Exception {
      driver.quit();
      String verificationErrorString = verificationErrors.toString();
      if (!"".equals(verificationErrorString)) {
        fail(verificationErrorString);
      }
    }
}
