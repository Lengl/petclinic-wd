package ru.pflb.wd;

import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Пусть;
import cucumber.api.java.ru.Тогда;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.openqa.selenium.firefox.FirefoxDriver.PROFILE;

/**
 * @author <a href="mailto:8445322@gmail.com">Ivan Bonkin</a>.
 */
public class WdSteps {

    private WebDriver driver;

    private Map<String, String> aliasMap = new HashMap<>();

    @Пусть("^запущен браузер Firefox$")
    public void runBrowser() throws Throwable {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        FirefoxProfile profile = new FirefoxProfile();
        File firebug = new File(FirefoxDriver.class.getResource("/firebug-1.12.7-fx.xpi").toURI());
        File firepath = new File(FirefoxDriver.class.getResource("/firepath-0.9.7-fx.xpi").toURI());
        profile.addExtension(firebug);
        profile.addExtension(firepath);
        profile.setPreference("extensions.firebug.showFirstRunPage", false);
        capabilities.setCapability(PROFILE, profile);
        driver = new FirefoxDriver(capabilities);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, SECONDS);
    }

    /**
     * Заход на главную страницу.
     */
    @И("^введен адрес \"([^\"]*)\"$")
    public void driverGet(String url) {
        driver.get(url);
    }

    @И("^произведен клик по меню \"([^\"]*)\"$")
    public void clickMenu(String name) throws Throwable {
        // клик по меню Find Owners
        driver.findElement(By.xpath("//a[@title='" + name + "']")).click();
    }

    @И("^в поле \"([^\"]*)\" введено (запомненное )?\"([^\"]*)\"$")
    public void insertedInField(String label, String remembered, String text) throws Throwable {

        // если присутствует слово "запомненное", то берем само значение из алиасов
        String textToEnter = remembered == null ? text : aliasMap.get(text);

        // ввод фамилии клиента (Black) в поле поиска
        WebElement element = driver.findElement(By.xpath("//label[starts-with(.,'" + label + "')]/following-sibling::div/input"));
        element.clear();
        element.sendKeys(textToEnter);
    }

    @Тогда("^закрываем браузер$")
    public void quite() throws Throwable {
        driver.quit();
    }

    @И("^нажать кнопку, начинающуюся с \"([^\"]*)\"$")
    public void clickButton(String caption) throws Throwable {
        // клик кнопки
        driver.findElement(By.xpath("//*[self::a or self::button][starts-with(text(),'" + caption + "')]")).click();
    }

    @И("^запомнить произвольное слово из (\\d+) букв как \"([^\"]*)\"$")
    public void rememberKeyword(int length, String alias) throws Throwable {
        aliasMap.put(alias, capitalize(randomAlphabetic(length).toLowerCase()));
    }

    @И("^запомнить текст поля ввода \"([^\"]*)\" как \"([^\"]*)\"$")
    public void readField(String fieldName, String alias) throws Throwable {
        WebElement element = driver.findElement(By.xpath(
                "//label[starts-with(.,'" + fieldName + "')]/following-sibling::div/input"));
        aliasMap.put(alias, element.getAttribute("value"));
    }

    @Тогда("^в таблице в строке \"([^\"]*)\" значится (?:запомненное|запомненный) \"([^\"]*)\"$")
    public void validateTable(String rowName, String alias) throws Throwable {
        validateTable(rowName, alias, "");
    }

    @Тогда("^в таблице в строке \"([^\"]*)\" значится запомненное \"([^\"]*)\" дополненное словом \"([^\"]*)\"$")
    public void validateTable(String rowName, String alias, String append) throws Throwable {
        WebElement element = driver.findElement(By.xpath(String.format(
                "(//th[.='%1$s']/following-sibling::td/b)|(//th[.='%1$s']/following-sibling::td[not(b)])", rowName)));
        assertThat(element.getText())
                .describedAs(alias + " пользователя не совпадает со сгенерированным [" + aliasMap.get(alias) + "]")
                .isEqualTo(aliasMap.get(alias) + append);

    }
}
