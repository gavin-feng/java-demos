package com.power.page.base;

import com.power.page.BasePage;
import com.power.page.base.bean.MenuInfoBean;
import com.power.page.data.OrderControlPage;
import com.power.page.data.OrderForRepeatListPage;
import com.power.page.data.OrderSavePage;
import com.power.page.manufacture.BomPage;
import com.power.page.manufacture.ManufactureSignForPage;
import com.power.page.plan.PlanSchedulerPage;
import com.power.page.purchase.PurchaseApplyPage;
import com.power.page.warehouse.MaterialsApplyPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HomePage extends BasePage {
    private static Logger log = LoggerFactory.getLogger(HomePage.class);
    // 菜单的xpath使用的条件：菜单名不能重名
    public static final String MENU_PLAN = "计划";
    public static final String MENU_DATA_PROC = "工程";
    public static final String MENU_PURCHASE = "采购管理";
    public static final String MENU_SYSTEM = "系统管理";
    public static final String MENU_MANUFACTURE = "生产";
    public static final String MENU_WAREHOUSE = "仓储管理";

    // 子菜单的xpath使用的条件：菜单名不能重名
    // 计划菜单下的
    public static final String SUBMENU_DISPATCH = "计划调度";
    // 工程菜单下的
    public static final String SUBMENU_NEW_ORDER = "订单登记";
    public static final String SUBMENU_ORDER_CONTROL = "订单控制";
    public static final String SUBMENU_ORDER_REPEAT = "重复下单";
    // 采购相关
    public static final String SUBMENU_PURCHASE_APPLY = "请购申请（新）";
    public static final String SUBMENU_PURCHASE_APPLY_AUDIT = "请购审批（新）";
    // 生产菜单下的
    public static final String SUBMENU_BOM = "订单物料查询";
    public static final String SUBMENU_SIGN_FOR = "签收订单";
    // 仓储管理菜单下的
    public static final String SUBMENU_MATERIALS_APPLY = "物料领用申请（新）";
    
    private String curMenuXPath;
    private boolean bFirstLoad = true;
    private Map<String, MenuInfoBean> menuMap;
    
    public HomePage(WebDriver driver) {
        super(driver);
        menuMap = new HashMap<String, MenuInfoBean>();
        // 订单登记
        BasePage page = new OrderSavePage(driver);
        MenuInfoBean bean = buildMenuInfoBean(page, MENU_DATA_PROC, SUBMENU_NEW_ORDER, "010003");
        menuMap.put(SUBMENU_NEW_ORDER, bean);
        // 订单控制
        page = new OrderControlPage(driver);
        bean = buildMenuInfoBean(page, MENU_DATA_PROC, SUBMENU_ORDER_CONTROL, "013004");
        menuMap.put(SUBMENU_ORDER_CONTROL, bean);
        // 重复下单
        page = new OrderForRepeatListPage(driver);
        bean = buildMenuInfoBean(page, MENU_DATA_PROC, SUBMENU_ORDER_REPEAT, "010005");
        menuMap.put(SUBMENU_ORDER_REPEAT, bean);
        // 计划调度
        page = new PlanSchedulerPage(driver);
        bean = buildMenuInfoBean(page, MENU_PLAN, SUBMENU_DISPATCH, "015001");
        menuMap.put(SUBMENU_DISPATCH, bean);
        // 请购申请（新）
        page = new PurchaseApplyPage(driver);
        bean = buildMenuInfoBean(page, MENU_PURCHASE, SUBMENU_PURCHASE_APPLY, "050001");
        menuMap.put(SUBMENU_PURCHASE_APPLY, bean);
        // 请购审批（新）
        page = new PurchaseApplyPage(driver);
        bean = buildMenuInfoBean(page, MENU_PURCHASE, SUBMENU_PURCHASE_APPLY_AUDIT, "050002");
        menuMap.put(SUBMENU_PURCHASE_APPLY_AUDIT, bean);
        /*
         * 生产
         */
        // 订单物料查询
        page = new BomPage(driver);
        bean = buildMenuInfoBean(page, MENU_MANUFACTURE, SUBMENU_BOM, "032001");
        menuMap.put(SUBMENU_BOM, bean);
        // 签收订单
        page = new ManufactureSignForPage(driver);
        bean = buildMenuInfoBean(page, MENU_MANUFACTURE, SUBMENU_SIGN_FOR, "016001");
        menuMap.put(SUBMENU_SIGN_FOR, bean);
        // 物料领用申请（新）
        page = new MaterialsApplyPage(driver);
        bean = buildMenuInfoBean(page, MENU_WAREHOUSE, SUBMENU_MATERIALS_APPLY, "048008");
        menuMap.put(SUBMENU_MATERIALS_APPLY, bean);
    }
    
    private MenuInfoBean buildMenuInfoBean(BasePage page, String menuName, String subMenuName, 
            String id) {
        String menuXPath = buildMenuXPath( menuName );
        String subMenuXPath = "//span[contains(.,'" + subMenuName + "')]";
        return new MenuInfoBean(page, menuXPath, subMenuXPath, id);
    }
    
    private String buildMenuXPath(String menuName) {
        return "//div[@class='panel-title'][contains(.,'" + menuName + "')]";
    }
    
    public BasePage switchToSubMenuPage(String subMenuName) {
        log.info("switch to submenu: " + subMenuName);
        // 调回default的frame（可以定位菜单的frame）
        driver.switchTo().defaultContent();
        // 获取菜单信息
        MenuInfoBean menuInfo = menuMap.get(subMenuName);
        // 菜单点击
        clickAndWaitSubMenuShow(menuInfo.getMenuXPath(), menuInfo.getSubMenuXPath());
        // 必须切换到子菜单对应的iframe中
        WebElement iframeEle = driver.findElement(By.id(menuInfo.getIframeId()));
        driver.switchTo().frame(iframeEle);
        menuInfo.getPage().getPageLoadTime(subMenuName);
        // 点击子菜单载入新页面，有些页面一载入就自动查询数据，这里设置较长的timeout时间
        super.waitForAjaxPresent(10);
        menuInfo.getPage().initPage();
        return menuInfo.getPage();
    }
    
    private void clickAndWaitSubMenuShow(String menuXPath, String subMenuXPath) {
        // 为防止要点击的主菜单menuXPath已经展开，再次点击会收缩
        if (bFirstLoad) {   // 首次登录，点击“系统管理”，正确记录curMenuXPath值
            driver.findElement(By.xpath(buildMenuXPath(MENU_SYSTEM))).click();
            super.sleepSeconds(1);
            curMenuXPath = MENU_SYSTEM;
            bFirstLoad = false;
        }
        
        // 点击菜单与子菜单
        if (!menuXPath.equals(curMenuXPath)) {
            log.debug("menuXPath is: " + menuXPath);
            // 点击主菜单
            driver.findElement(By.xpath(menuXPath)).click();
            // 等待Ajax装载结束；如果使用 
            super.sleepSeconds(1);
        }
        driver.findElement(By.xpath(subMenuXPath)).click();
        curMenuXPath = menuXPath;
    }
    
    public void logout() {
        driver.switchTo().defaultContent();
        driver.findElement(By.linkText("退出")).click();
    }
}

