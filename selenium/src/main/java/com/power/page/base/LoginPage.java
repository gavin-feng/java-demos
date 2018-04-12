package com.power.page.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.power.page.BasePage;

public class LoginPage extends BasePage {
    private static Logger log = LoggerFactory.getLogger(LoginPage.class);
    
    public LoginPage(WebDriver driver) {
        super(driver);
    }
    
    public HomePage login(String url, String username, String passwd) {
        driver.get(url);
        driver.findElement(By.id("userName")).clear();
        driver.findElement(By.id("userName")).sendKeys(username);
        driver.findElement(By.id("passwd")).clear();
        driver.findElement(By.id("passwd")).sendKeys(passwd);
        driver.findElement(By.id("submit33")).click();
        driver.manage().window().maximize();
        super.waitForAjaxPresent(5);
        log.info("登录成功");
        return new HomePage(driver);
    }
}
