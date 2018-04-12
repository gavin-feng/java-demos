package com.power.page;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Page Object Design Pattern 中的Page类的父类，将通用方法集中起来
 * 参照官网的wiki： https://github.com/SeleniumHQ/selenium/wiki/PageFactory
 *
 * 说明：
 * 每个页面都有对应的Page对象，使用 @FindBy 注解与 PageFactory.initElements配合，可以以lazy
 * 的方式加载WebElement，也就是【 That is, if you never use a WebElement field in a PageObject,
 * there will never be a call to "findElement" for it. 】
 *
 * 页面跳转：
 * 登录后跳转到 Home 页面： HomePage homePage = loginPage.login(xx, xx);
 * 点击左侧栏的链接的跳转： 看HomePage.switchToSubMenuPage方法，在return对应page对象之前进行 initPage
 *
 * @author gavin
 *
 */
@Slf4j
public class BasePage {
    public final WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    // 对于
    public void initPage() {
        PageFactory.initElements(driver, this);
    }

    /**
     * sleep the current step for a few seconds
     *
     * @param seconds
     *            -- the seconds we need to sleep
     */
    public void sleepSeconds(int seconds) {
        log.info("Begin to sleep current step for " + seconds + " seconds........");
        // You need to be in a synchronized block in order for Object.wait() to
        // work.

        // Also, I recommend looking at the concurrency packages instead of the
        // old school threading packages. They are safer and way easier to work
        // with.
        // synchronized (driver)
        // {
        // driver.wait(seconds * 1000);
        // }
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            log.error("Sleep current step met an error:" + e.getMessage());
        }
    }

    /**
     * this is the first method we must use in every page ,so that we can get
     * the page loading time in seconds
     *
     * @param pagename
     *
     * http://www.softwareishard.com/blog/firebug/support-for-performance-
     *      timing-in-firebug/
     * http://selenium.polteq.com/en/implement-web-timings/
     * http://www.html5rocks.com/en/tutorials/webperformance/basics/
     * http://www.theautomatedtester.co.uk/blog/2010/selenium-webtimings-
     *      api.html
     */
    public long getPageLoadTime(String pagename) {
        // this is a line seperator so that we can see the debug log clearly
        log.info("\n" + Strings.repeat("*", 20) + pagename + Strings.repeat("*", 20));
        // get the page loading time in every page if we use this method
        long pageloadtime = 0;
        long pagestarttime = 0;
        long pageendtime = 0;
        // try{
        // different with browser ,ie will return is double value but firefox
        // and chrome will return is long
        Object startobject = executeJSReturn("return window.performance.timing.navigationStart;");
        Object endobject = executeJSReturn("return window.performance.timing.loadEventEnd;");
        // @SuppressWarnings("unchecked")
        // pagetimer=executeJSReturn("var performance = window.performance ||
        // window.webkitPerformance || window.mozPerformance ||
        // window.msPerformance || {};"+
        // " var timings = performance.timing || {};"+
        // " return timings;");
        // long pageloadend=(pagetimer.get("loadEventEnd"))/1000;
        // long pageloadstart=(pagetimer.get("navigationStart"))/1000;
        // pageloadtime=(pageloadend-pageloadstart);
        // think it's the firefox or chrome browser
        if (startobject instanceof Long) {
            pagestarttime = (Long) startobject;
            log.debug("the page navigate start time is:" + pagestarttime);
        }
        if (startobject instanceof Double) {
            Double tempvalue = (Double) startobject;
            pagestarttime = new Double(tempvalue).longValue();
            log.debug("the page navigate start time is:" + pagestarttime);
        }
        if (endobject instanceof Long) {
            pageendtime = ((Long) endobject);
            log.debug("the page end time is:" + pageendtime);
        }
        if (endobject instanceof Double) {
            double tempvalue = (Double) endobject;
            pageendtime = new Double(tempvalue).longValue();
            log.debug("the page end time is:" + pageendtime);
        }

        pageloadtime = (pageendtime - pagestarttime) / 1000;
        log.info("Get current page loading time is:" + pageloadtime);

        return pageloadtime;
    }

    /** Is the text present in page. */
    public boolean isTextPresent(String text) {
        boolean textpresent = driver.getPageSource().contains(text);
        log.info("Verify the element text present in the page,the text seacrh is :" + text
                + ",and found the element status is:" + textpresent);
        return textpresent;
    }

    /** Is the Element in page. */
    public boolean isWebElementPresent(By by) {
        try {
            driver.findElement(by);// if it does not find the element throw
            // NoSuchElementException, thus returns
            // false.
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Checks if the elment is in the DOM and displayed.
     *
     * @param e
     *            - selector to find the element
     * @return true or false
     */
    public boolean isElementPresentAndDisplay(WebElement e) {
        boolean isdisplay = false;
        try {
            isdisplay = e.isDisplayed();
            log.info("Verify current element is displayed in the page :" + isdisplay);
        } catch (NoSuchElementException error) {
            log.info("Sorry,this element not displayed in the page,throw:" + error.getMessage());
        }
        return isdisplay;
    }

    /**
     * Returns the first WebElement using the given method. It shortens
     * "driver.findElement(By)".
     *
     * @param by
     *            element locater.
     * @return the first WebElement
     */
    public WebElement getWebElement(By by) {
        return driver.findElement(by);
    }

    /**
     * clear the text in the elment and then type the new string in this element
     *
     * @param e
     *            -- the webelment you need to type
     * @param text
     *            -- the text you want to input
     */
    public void clearAndTypeString(WebElement e, String text) {
        log.info("Type a text into the element is:" + e.getTagName() + ", the inputted text:"
                + text);
        highLight(e);
        e.clear();
        e.sendKeys(text);
    }

    /**
     * highLight:(highlight the web element in the page).
     *
     *
     * @author huchan
     * @param driver
     *            -- the web driver instance
     * @param e
     *            -- the web element object
     */
    public void highLightExt(WebDriver driver, WebElement e) {
        log.info("Highlight the element ,the object is:" + e.getTagName()
                + ",the text in this object is:" + e.getText());
        Actions action = new Actions(driver);
        action.contextClick(e).perform();
        log.info("Had right click the object ,then press the escape key");
        e.sendKeys(Keys.ESCAPE);
    }

    public void highLight(WebElement e) {
        if (driver instanceof JavascriptExecutor) {
            executeJS("arguments[0].style.border='3px solid red'", e);
        }
    }

    /**
     * executeJS:(execute the java script in this page).
     *
     * @param script
     *            --the java script we need to execute
     */
    public Object executeJS(String script) {
        log.info("Run the javascript from page ,the java script is:" + script);
        JavascriptExecutor je = (JavascriptExecutor) driver;
        return je.executeScript(script);

    }

    public void executeJS(String script, WebElement e) {
        log.info("Run the javascript from page ,the java script is:" + script);
        JavascriptExecutor je = (JavascriptExecutor) driver;
        je.executeScript(script, e);

    }

    public Object executeJSReturn(String script, WebElement e) {
        log.info("Run the javascript from page ,the java script is:" + script);
        JavascriptExecutor je = (JavascriptExecutor) driver;
        Object object = je.executeScript(script, e);
        return object;

    }

    public Object executeJSReturn(String script) {
        log.info("Run the javascript from page ,the java script is:" + script);
        JavascriptExecutor je = (JavascriptExecutor) driver;
        Object object = je.executeScript(script);
        return object;
    }

    /**
     * click an element in the page
     *
     * @param e --the WebElment we need to click
     */
    public void clickElement(WebElement e) {
        highLight(e);
        e.click();
    }

    /**
     * right click an element in the page
     *
     * @param e
     *            --the WebElment we need to click
     */
    public void rightClickElement(WebElement e) {
        log.info("Right Click elements in page-clicked this element:" + e.getTagName()
                + ",the text is:" + e.getText());
        // In chrome browser this function didn't work ,so give a solution to
        // load the page correctly
        // ((JavascriptExecutor)
        // driver).executeScript("window.scrollTo(0,"+e.getLocation().y+")");
        highLight(e);
        new Actions(driver).contextClick(e).perform();

    }

    /**
     * send key to an element
     *
     * @param e
     *            --the webelement you want to send the key
     * @param text
     *            -- the keys need to send
     */
    public void sendKeys(WebElement e, String text) {
        log.info("Send keys in this element:" + e.getTagName() + ",the key we send is:" + text);
        // e.clear();
        highLight(e);
        e.sendKeys(text);
    }

    /**
     * @param url
     */
    public void open(String url) {
        driver.get(url);
    }

    /**
     * select an option from the drop list, depends on the visible text
     *
     * @param e
     *            --the web element object
     * @param text
     *            -- the visible text from the dropdown list
     * @author huchan
     */
    public void selectElementByText(WebElement e, String text) {
        log.info("Select option text from the list ,list element is:" + e.getTagName()
                + ",the option text is:" + text);
        highLight(e);
        Select select = new Select(e);
        select.selectByVisibleText(text);
    }

    /**
     * select an option from the drop list, depends on the tag's attribute value
     *
     * @param e
     *            --the web element object
     * @param value
     *            -- the value attribute for this element
     * @author huchan
     */
    public void selectElementByValue(WebElement e, String value) {
        log.info("Select option value from the list ,list element is:" + e.getTagName()
                + ",the option value is:" + value);
        highLight(e);
        Select select = new Select(e);
        select.selectByValue(value);
    }

    /**
     * select an option from the drop list, depends on the index ,the index
     * begin with 0
     *
     * @param e
     *            --the web element object
     * @param index
     *            -- the index of this webelement ,begin with 0
     * @author huchan
     */
    public void selectElementByIndex(WebElement e, int index) {
        log.info("Select option index from the list ,list element is:" + e.getTagName()
                + ",the option index is:" + index);
        highLight(e);
        Select select = new Select(e);
        select.selectByIndex(index);
    }

    /**
     * wait for an object to be dislayed in the page
     *
     * @param e
     *            -- the web element object, like ProcessBar,etc
     * @return true: the object displayed , false:the object not displayed in
     *         the page ;
     */
    public boolean waitForWebElementDisappear(WebElement e) {
        int waitcount = 0;
        boolean isDisplayed = false;
        while (e.isDisplayed()) {
            waitcount = waitcount + 1;
            isDisplayed = e.isDisplayed();
            log.info("Waitting for the object displayed status-the load object displayed status is:"
                    + isDisplayed);
            sleepSeconds(3);
            if (waitcount == 5) {
                log.error(
                        "Waitting for the object displayed status-the object cannot show in the page:"
                                + e.getTagName() + ",exit the identify the object ");
                break;
            }

        }
        return isDisplayed;

    }

    /**
     * wait for the object displayed in the page ,the time out will be 120
     * seconds ,if it still not show ,it will failed
     *
     * @param by
     */
    public void waitForWebElementClickable(final By by) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement we = wait.until(ExpectedConditions.elementToBeClickable(by));
        log.debug("inner html: " + getInnerHtmlCode(we));
    }

    /**
     * execute the script to set the datepicker value
     *
     * @param elementid
     *            -- the web element's id
     * @param date
     *            --the date we need to set ,it's string
     * @author huchan
     */
    public void setDateTimePicker(String elementid, String date) {
        log.info(
                "Set DatePicker Date or Time --Execute the java script to modify the weblement's attribute:value,the element id is:"
                        + elementid);

        executeJS("window.document.getElementById('" + elementid + "').setAttribute('value', '"
                + date + "');");

    }

    /**
     * select the checkbox ,if it selectd ,we will not select it again
     *
     * @param e
     *            -- the web element object
     * @author huchan
     */
    public void checkboxed(WebElement e) {
        highLight(e);
        if (!(e.isSelected())) {
            log.info("Check the checkbox,the webelment :" + e.getTagName() + e.getText()
                    + ",had been selected now");
            e.click();
        } else {
            log.info("Check the checkbox,the webelment :" + e.getTagName() + e.getText()
                    + ",had been selected default");
        }
    }

    /**
     * get the text in the web element
     *
     * @param e
     *            -- the web element object
     * @return String -- the web element's text
     */
    public String getElementText(WebElement e) {
        log.info("Get the element text.The webelement is:" + e.getTagName()
                + ",the text in the webelement is:" + e.getText().trim());
        highLight(e);
        return e.getText().trim();
    }

    /**
     * verify the object is enabled in the page
     *
     * @param e
     *            -- the web element object
     * @return true :the object is enabled ,false:the object is disabled
     * @author huchan
     */
    public boolean isEnabled(WebElement e) {
        log.info("Verify webelement Enabled in the page-the current webelement is:" + e.getTagName()
                + ",the text in the webelement is:" + e.getText().trim());
        highLight(e);
        return e.isEnabled();
    }

    /**
     * verify the object is selected in the page
     *
     * @param e
     *            --the web element object
     * @return true:the object is selected,false:the object is not selected
     * @author huchan
     */
    public boolean isSelected(WebElement e) {
        log.info("Verify webelement Selected in the page-the current webelement is:"
                + e.getTagName() + ",the text in the webelement is:" + e.getText().trim());
        highLight(e);
        return e.isSelected();

    }

    /**
     * get the webelement's attribute value
     *
     * @param e
     *            --the web element object
     * @param attributename
     *            -- the web element's attribute
     * @return String-- the attribute value for this web element
     * @author huchan
     */
    public String getAttribute(WebElement e, String attributename) {
        log.info("Get the webelement Attribute-the webelement's attribute:" + e.getTagName()
                + ",the text in the webelement is:" + e.getText().trim());
        String attributevalue = e.getAttribute(attributename);
        log.info("Get the webelement Attribute-the webelement's attribute:" + attributename
                + " value is:" + attributevalue);
        return attributevalue;
    }

    /**
     * get the web element's tag name
     *
     * @param e
     *            -- the web element object
     * @return String --the web element's tag name
     */
    public String getTagName(WebElement e) {
        log.info("Get the webelement TagName-the webelement's tag name is:" + e.getTagName()
                + ",the text in the webelement is:" + e.getText().trim());
        highLight(e);
        String tagname = e.getTagName();
        log.info("Get the webelement TagName-the webelement's tag name is:" + e.getTagName());
        return tagname;
    }

    /**
     * get all the web elements behind the specified element
     *
     * @param e
     *            -- the web element object
     * @param tagname
     *            -- the web element's tag name
     * @return List<WebElement> a list of all the sub web element we found
     * @author huchan
     */
    public List<WebElement> findElementListByTagName(WebElement e, String tagname) {
        log.info("Find all subelements by Tag Name:" + e.getTagName()
                + ",the sub elment's tag name is:" + tagname);
        highLight(e);
        List<WebElement> elements = e.findElements(By.tagName(tagname));
        return elements;
    }

    /**
     * find the element by xpath in the page
     *
     * @param e
     *            --the web element object
     * @param xpath
     *            -- the web element's xpath
     * @return WebElement -- get the found web element
     */
    public WebElement findElementByXpath(WebElement e, String xpath) {
        log.info("Find subelement by Xpath-we will find an sub element with the xpath:" + xpath);
        highLight(e);
        WebElement element = e.findElement(By.xpath(xpath));
        // highLight(driver, element);
        return element;
    }

    /**
     * find the element by xpath in the page
     *
     * @param e
     *            --the web element object
     * @param css
     *            -- the web element's css
     * @return WebElement -- get the found web element
     */
    public WebElement findElementByCSS(WebElement e, String css) {
        log.info("Find subelement by css-we will find an sub element with the css selector:" + css);
        highLight(e);
        WebElement element = e.findElement(By.cssSelector(css));
        // highLight(driver, element);
        return element;
    }

    /**
     * click the ok button in the pop up dialog (alert dialog)
     *
     * @param seconds
     *            -- the seconds we need to wait to click it
     * @author huchan
     */
    public boolean alertClickOK(int seconds) {
        boolean isclicked = false;
        WebDriverWait wait = new WebDriverWait(driver, seconds);
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            log.info("Pop up Alert text is:" + alert.getText());
            alert.accept();
            isclicked = true;
        } catch (NoAlertPresentException e) {
            log.info("the Alert didn't pop up currently:" + e.getMessage());
        }

        return isclicked;
    }

    /**
     * this fuction is used for clicking the cancel button
     *
     * @category click the Alert dialog ,click the cancel button
     *
     * @param seconds
     *            -- the second we need to wait to click the cancel button
     * see alertClickOK
     * @author huchan
     */

    public boolean alertClickCancel(int seconds) {
        boolean isclicked = false;
        WebDriverWait wait = new WebDriverWait(driver, seconds);
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            log.info("Pop up Alert text is:" + alert.getText());
            alert.dismiss();
            isclicked = true;
        } catch (NoAlertPresentException e) {
            log.info("the alert didn't pop up currently:" + e.getMessage());
        }

        return isclicked;
    }

    /**
     * getCurrentURL:(get the current page URL address).
     *
     * @return String ---the url of current page
     */
    @Deprecated
    public String getCurrentPageURL() {
        String pageurl = "";
        JavascriptExecutor je = (JavascriptExecutor) driver;
        final String docstate = (String) je.executeScript("return document.readyState");
        log.info("Current loading page state is:" + docstate);
        WebDriverWait wait = new WebDriverWait(driver, 120);
        ExpectedCondition<Boolean> ec = d -> (docstate.equals("complete"));
        log.info("We just wait for the current page load correctly now...");
        wait.until(ec);
        pageurl = driver.getCurrentUrl();
        log.info("we found the current url is:" + pageurl);
        return pageurl;
    }

    /**
     * wait for the web page to full loading correctly ,it will wait for 3
     * minutes to load the page , if the page not loading in 3 minutes ;it will
     * throw error;
     *
     */
    public boolean waitForBrowserFullSync() {
        final String currentbowserstate = (String) executeJS("return document.readyState;");
        log.info("Current browser state is:" + currentbowserstate);
        WebDriverWait wdw = new WebDriverWait(driver, 180);
        ExpectedCondition<Boolean> ec = driver -> {
            String newpagestate = (String) executeJS("return document.readyState;");
            log.debug("the new page state is:" + newpagestate);
            return (newpagestate.equals("complete"));
        };

        Boolean loaded = wdw.<Boolean>until(ec);
        log.debug("finally the load is loading with completed status is:" + loaded);
        return loaded;
    }

    /**
     * switchtoWindow:(Here describle the usage of this function).
     * http://santoshsarmajv.blogspot.com/2012/04/how-to-switch-control-to-pop-
     * up-window.html
     * http://stackoverflow.com/questions/11614188/switch-between-two-browser-
     * windows-using-selenium-webdriver
     *
     * @param windowTitle
     * @throws AWTException
     */
    public void switchtoWindow(String windowTitle) throws AWTException {
        Robot robot = new Robot();
        Set<String> allwindows = driver.getWindowHandles();
        for (String window : allwindows) {
            driver.switchTo().window(window);
            if (driver.getTitle().contains(windowTitle)) {
                robot.delay(5000);
                // robot.keyPress(keycode);
            }
        }
    }

    /**
     * refresh the current page
     *
     */
    @Deprecated
    public void refreshPage() {
        // driver.navigate().refresh();
        log.info("Now refresh the page to keep the session valid");
        // or below
        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.F5).perform();
    }

    /**
     * get the page title
     *
     * @return String
     */
    public String getPageTitle() {
        String title = driver.getTitle();
        log.info("Get current page title is:" + title);
        return title;
    }

    /**
     * get the webelement's html code
     *
     * @param e
     * @return String
     */
    public String getInnerHtmlCode(WebElement e) {
        String contents = (String) executeJSReturn("return arguments[0].innerHTML;", e);
        log.info("Get the html code for this webelement:" + contents);
        highLight(e);
        return contents;
    }

    /**
     * scroll the view to we can see in the page
     *
     * @param e
     */
    public void scrollToView(WebElement e) {
        highLight(e);
        executeJS("window.scrollTo(0," + e.getLocation().y + ")");
        executeJS("arguments[0].scrollIntoView(true);", e);
        log.info("Now we scroll the view to the position we can see");

    }

    /**
     * click the upload button to upload the file ,this is for hte webFile
     * element ,the input type is file
     *
     * @param e
     * @param filepath
     *            http://sauceio.com/index.php/2012/03/selenium-tips-uploading-
     *            files-in-remote-webdriver/ upload the local file from remote
     *            webdriver
     */
    public void uploadFile(WebElement e, String filepath) {
        String uploadcode = getInnerHtmlCode(e);
        highLight(e);
        log.info("the upload webelement html code we get is:" + uploadcode);
        e.sendKeys(filepath);
    }

    /**
     * wait for the ajax to be completed inspired by the the below url:
     *
     * @link http://hedleyproctor.com/2012/07/effective-selenium-testing/
     * @link http://stackoverflow.com/questions/3272883/how-to-use-selenium-2-
     *       pagefactory-init-elements-with-wait-until
     * @param timeoutInSeconds
     */
    public void waitForAjaxPresent(int timeoutInSeconds) {
        if (driver instanceof JavascriptExecutor) {
            WebDriverWait wdw = new WebDriverWait(driver, timeoutInSeconds);
            ExpectedCondition<Boolean> ec = driver -> {
                long numberOfAjaxConnections = (Long) executeJS("return jQuery.active;");
                log.debug("Number of active jquery ajax calls:" + numberOfAjaxConnections);
                return (numberOfAjaxConnections == 0L);
            };

            wdw.until(ec);
        }
    }
}
