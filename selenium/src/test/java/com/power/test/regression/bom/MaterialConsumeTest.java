package com.power.test.regression.bom;

import com.power.page.base.HomePage;
import com.power.page.base.LoginPage;
import com.power.page.data.OrderControlPage;
import com.power.page.data.OrderSavePage;
import com.power.page.manufacture.BomPage;
import com.power.page.plan.PlanSchedulerPage;
import com.power.test.smoke.TestCoreFunctions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MaterialConsumeTest {
    
    private WebDriver driver;
    private StringBuffer verificationErrors = new StringBuffer();
    private static String url = "http://192.168.1.70:8080/PowerFramework/login.jsp";
    public static Logger log = LoggerFactory.getLogger(TestCoreFunctions.class);
    private static String customerCode = "280500";
    private static String customerModel = "S250801P";     
    @Before
    public void setUp() throws Exception {
        // 需要配置 webdriver.chrome.driver 环境变量
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }
    
    @After
    public void tearDown() throws Exception {
      driver.quit();
      String verificationErrorString = verificationErrors.toString();
      if (!"".equals(verificationErrorString)) {
        fail(verificationErrorString);
      }
    }    

    // 已审核订单变更的测试 1到3的状态变更。
    // @Test
    public void testModifyOrderWithPartNumChanged() {
        String username = "situo";
        String passwd = "123123";
        log.info("Start Smoke Testing of order-changed for bom");
        LoginPage loginPage = new LoginPage(driver);
        // 登录
        HomePage homePage = loginPage.login(url, username, passwd);
        // 切换到订单登记
        OrderSavePage orderSavePage = (OrderSavePage) homePage.switchToSubMenuPage(HomePage.SUBMENU_NEW_ORDER);
        String indentPart2 = orderSavePage.newOrder(customerCode, customerModel);
        // 生产安排
        PlanSchedulerPage planSchedulerPage = (PlanSchedulerPage) homePage
                .switchToSubMenuPage(HomePage.SUBMENU_DISPATCH);
        planSchedulerPage.scheduleManufacture(indentPart2, "（昆山）激光快速出货");
        // 配料审批
        BomPage bomPage = (BomPage) homePage.switchToSubMenuPage(HomePage.SUBMENU_BOM);
        bomPage.approve(indentPart2);
        // 订单控制菜单下，修改订单,保持part号不变
        OrderControlPage orderControlPage = (OrderControlPage) homePage
                .switchToSubMenuPage(HomePage.SUBMENU_ORDER_CONTROL);
        // String newIndentPart = "2805-0005-85-01-1-1";
        orderSavePage = orderControlPage.toModifyOrder(indentPart2);
        String newIndentPart = orderSavePage.editOrderWithPartNumUnchangedButBomChanged();
        // 订单物料查询,颜色的变更
        BomPage bompage = (BomPage) homePage.switchToSubMenuPage(HomePage.SUBMENU_BOM);
        int searchResultCount = bompage.searchForResult(newIndentPart, "yellow");
        assertTrue(searchResultCount == 2);
        // 页面等待时间
        bompage.sleepSeconds(10);
    }

    // 未审核状态下，配料信息1条变更为2条信息的测试
    @Test
    public void testModifyOrderWithPartNumChanged1() {
        String username = "situo";
        String passwd = "123123";
        log.info("Start Smoke Testing of order-changed for bom");
        LoginPage loginPage = new LoginPage(driver);
        // 登录
        HomePage homePage = loginPage.login(url, username, passwd);
        // 切换到订单登记
        OrderSavePage orderSavePage = (OrderSavePage) homePage.switchToSubMenuPage(HomePage.SUBMENU_NEW_ORDER);
        String indentPart = orderSavePage.newOrder(customerCode, customerModel);
        // 生产安排
        PlanSchedulerPage planSchedulerPage = (PlanSchedulerPage) homePage
                .switchToSubMenuPage(HomePage.SUBMENU_DISPATCH);
        planSchedulerPage.scheduleManufacture(indentPart, "（昆山）激光快速出货");
        // 订单控制菜单下，修改订单,保持part号不变
        OrderControlPage orderControlPage = (OrderControlPage) homePage
                .switchToSubMenuPage(HomePage.SUBMENU_ORDER_CONTROL);
        // String newIndentPart = "2805-0005-85-01-1-1";
        orderSavePage = orderControlPage.toModifyOrder(indentPart);
        String IndentPart = orderSavePage.editOrderWithPartNumUnchangedButBomTypeChanged();
        // 订单物料查询,颜色的变更
        BomPage bompage = (BomPage) homePage.switchToSubMenuPage(HomePage.SUBMENU_BOM);
        int searchResultCount = bompage.searchForResult(IndentPart, "");
        assertTrue(searchResultCount == 2);
        // 页面等待时间
        bompage.sleepSeconds(10);
    }

}
