package com.power.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebDriverBuilder {

    @Value("${webdriver.chrome.driver}")
    private String chromeDriverPath;
    @Value("${webdriver.gecko.driver}")
    private String firefoxDriverPath;

    public WebDriver getChromeDriver() {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        return new ChromeDriver();
    }

    // see https://sites.google.com/a/chromium.org/chromedriver/mobile-emulation
    public WebDriver getChromeMobileDriver() {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        Map<String, String> mobileEmulation = new HashMap<>();

        mobileEmulation.put("deviceName", "iPhone X");

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);

        return new ChromeDriver(chromeOptions);
    }

    public WebDriver getFirefoxDriver() {
        System.setProperty("webdriver.gecko.driver", firefoxDriverPath);

        return new FirefoxDriver();
    }
}
