package ru.pflb.wd;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.openqa.selenium.firefox.FirefoxDriver.PROFILE;

/**
 * @author <a href="mailto:8445322@gmail.com">Ivan Bonkin</a>.
 */
public class PetclinicTest {

    private WebDriver driver;

    @Before
    public void initDriver() throws URISyntaxException, IOException {
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

    @After
    public void closeDriver() {
        driver.quit();
    }

    /**
     * Поиск клиента по фамилии и изменение его имени.
     */
    @Test
    public void shouldFindOwnerAndChangeHisName() {
        final String surname = "Black";

        // заход на главную страницу
        driver.get("http://localhost:8080/");

        // клик по меню Find Owners
        driver.findElement(By.xpath("//a[@title='find owners']")).click();

        // ввод фамилии клиента (Black) в поле поиска
        driver.findElement(By.xpath("//input[@id='lastName']")).sendKeys(surname);

        // клик кнопки Find Owner
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        // клик кнопки Edit Owner
        driver.findElement(By.xpath("//h2[.='Owner Information']/following-sibling::a[starts-with(., 'Edit')]")).click();

        // генерация произвольного имени из 6 букв
        String newClientName = RandomStringUtils.randomAlphabetic(6);

        // ввод сгенерированного имени в поле First Name
        WebElement textField = driver.findElement(By.xpath("//input[@id='firstName']"));
        textField.clear();
        textField.sendKeys(newClientName);

        // запоминание содержимого полей Address, City, Telephone
        String address = driver.findElement(By.xpath("//input[@id='address']")).getAttribute("value");
        String city = driver.findElement(By.xpath("//input[@id='city']")).getAttribute("value");
        String telephone = driver.findElement(By.xpath("//input[@id='telephone']")).getAttribute("value");

        // нажатие кнопки Update Owner
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        // проверки содержимого таблицы Owner Information
        assertThat(driver.findElement(By.xpath("//th[.='Name']/following-sibling::td/b")).getText())
                .describedAs("Измененное имя пользователя не совпадает со сгенерированным")
                .isEqualTo(newClientName + " " + surname);
        assertThat(driver.findElement(By.xpath("//th[.='Address']/following-sibling::td")).getText())
                .describedAs("Адрес пользователя не совпадает с первоначальным")
                .isEqualTo(address);
        assertThat(driver.findElement(By.xpath("//th[.='City']/following-sibling::td")).getText())
                .describedAs("Город пользователя не совпадает с первоначальным")
                .isEqualTo(city);
        assertThat(driver.findElement(By.xpath("//th[.='Telephone']/following-sibling::td")).getText())
                .describedAs("Телефон пользователя не совпадает с первоначальным")
                .isEqualTo(telephone);

    }

    /**
     * Найти пользователя по фамилии Black и добавить ему домашнее животное.
     */
    @Test
    public void shouldAddNewPet() {

    }

    /**
     * Домашнее задание.
     * <p>
     * Сценарий:<ol>
     * <li>Открыть http://localhost:8080/</li>
     * <li>Перейти в меню Find Owners -> Add Owner</li>
     * <li>Ввести валидные случайные данные (новые для каждого запуска теста)</li>
     * <li>Нажать Add Owner, открылась страница Owner Information</li>
     * <li>Проверить, что добавилась новая запись, и все ее поля соответствуют введенным значениям, использую поиск в Find Owners (заново)</li>
     * </ul>
     *
     * Условие - не использовать индексы в XPath без крайней на то необходимости
     */
    public void shouldValidateAddedUser() {
        // TODO
    }
}
