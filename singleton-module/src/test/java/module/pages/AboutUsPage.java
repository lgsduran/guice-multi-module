package module.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.inject.Inject;
import module.config.qualifiers.Driver;
import module.config.qualifiers.DriverWait;
public class AboutUsPage {

    private final WebDriverWait wait;

    @Inject
    public AboutUsPage(@Driver WebDriver driver, @DriverWait WebDriverWait wait) {
        this.wait = wait;
        // OBRIGATÓRIO: Inicializa os elementos mapeados com @FindBy
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "[data-target='#exampleModal']")
    private WebElement contactLink;

    @FindBy(css = "#exampleModalLabel")
    private WebElement modalTitle;

    @FindBy(xpath = "//label[starts-with(text(), 'Contact Email')]")
    private WebElement contactEmailLink;

    @FindBy(xpath = "//label[starts-with(text(), 'Contact Name')]")
    private WebElement contactNameLink;

    @FindBy(xpath = "//label[starts-with(text(), 'Message')]")
    private WebElement messageLink;

    @FindBy(xpath = "//button[text()='Send message']/preceding::button[text()='Close']")
    private WebElement closeButton;

    @FindBy(xpath = "//button[text()='Send message']")
    private WebElement sendMessageButton;

    public void clickContactLink() {
        this.wait.until(ExpectedConditions.elementToBeClickable(contactLink)).click();
    }

    public void validateModalTitle() {
        String expectedTitle = "New message";
        String actualTitle = this.wait.until(ExpectedConditions.visibilityOf(modalTitle)).getText();
        if (!actualTitle.equals(expectedTitle)) {
            throw new AssertionError("Expected modal title: " + expectedTitle + ", but got: " + actualTitle);
        }
    }

    public void validateContactEmailLink() {
        String expectedText = "Contact Email:";
        String actualText = contactEmailLink.getText();
        if (!actualText.equals(expectedText)) {
            throw new AssertionError("Expected contact email link text: " + expectedText + ", but got: " + actualText);
        }
    }

    public void validateContactNameLink() {
        String expectedText = "Contact Name:";
        String actualText = contactNameLink.getText();
        if (!actualText.equals(expectedText)) {
            throw new AssertionError("Expected contact name link text: " + expectedText + ", but got: " + actualText);
        }
    }

    public void validateMessageLink() {
        String expectedText = "Message:";
        String actualText = messageLink.getText();
        if (!actualText.equals(expectedText)) {
            throw new AssertionError("Expected message link text: " + expectedText + ", but got: " + actualText);
        }
    }

    public void validateCloseButton() {
        String expectedText = "Close";
        String actualText = closeButton.getText();
        if (!actualText.equals(expectedText)) {
            throw new AssertionError("Expected close button text: " + expectedText + ", but got: " + actualText);
        }
    }

    public void validateSendMessageButton() {
        String expectedText = "Send message";
        String actualText = sendMessageButton.getText();
        if (!actualText.equals(expectedText)) {
            throw new AssertionError("Expected send message button text: " + expectedText + ", but got: " + actualText);
        }
    }

}