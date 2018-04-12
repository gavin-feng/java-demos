package com.power.test.sample;

import com.power.page.WebDriverBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

/**
 * 没有采用Page Object的模式
 * Page Object模式使用，请看 BasePage类中的说明
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestLogin {
    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();
    @Autowired
    WebDriverBuilder webDriverBuilder;

    @Before
    public void setUp() throws Exception {
        driver = webDriverBuilder.getChromeDriver();
        baseUrl = "http://192.168.1.70:8080";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void testLogin() throws Exception {
          driver.get(baseUrl + "/PowerFramework/login.jsp");
          driver.findElement(By.id("userName")).clear();
          driver.findElement(By.id("userName")).sendKeys("situo");
          driver.findElement(By.id("passwd")).clear();
          driver.findElement(By.id("passwd")).sendKeys("123123");
          driver.findElement(By.id("submit33")).click();
          driver.findElement(By.xpath("//*[@id='menuDiv']/div[9]/div[1]/div[1]")).click();
          for (int second = 0;; second++) {
              if (second >= 3) fail("timeout");
              try { if (isElementPresent(By.xpath("//span[contains(.,'请购审批（新）')]"))) break; } catch (Exception e) {}
              Thread.sleep(1000);
          }

          driver.findElement(By.xpath("//span[contains(.,'请购审批（新）')]")).click();
    }

    @After
    public void tearDown() throws Exception {
      driver.quit();
      String verificationErrorString = verificationErrors.toString();
      if (!"".equals(verificationErrorString)) {
        fail(verificationErrorString);
      }
    }

    private boolean isElementPresent(By by) {
      try {
        driver.findElement(by);
        return true;
      } catch (NoSuchElementException e) {
        return false;
      }
    }

    private boolean isAlertPresent() {
      try {
        driver.switchTo().alert();
        return true;
      } catch (NoAlertPresentException e) {
        return false;
      }
    }

    private String closeAlertAndGetItsText() {
      try {
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        if (acceptNextAlert) {
          alert.accept();
        } else {
          alert.dismiss();
        }
        return alertText;
      } finally {
        acceptNextAlert = true;
      }
    }

}
