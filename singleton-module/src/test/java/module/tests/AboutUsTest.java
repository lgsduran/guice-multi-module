package module.tests;

import org.junit.jupiter.api.Test;

import com.google.inject.Inject;

import module.pages.AboutUsPage;

//@ExtendWith(GuiceExtension.class) // This handles the bridge
//@IncludeModule(WebDriverModule.class) // Tells the extension which module to load
public class AboutUsTest extends BaseTest {

    @Inject
    private AboutUsPage aboutUsPage;

    @Test
    public void testAboutUsPage() {
        this.aboutUsPage.clickContactLink();
        this.aboutUsPage.validateModalTitle();
        this.aboutUsPage.validateContactEmailLink();
        this.aboutUsPage.validateContactNameLink();
        this.aboutUsPage.validateMessageLink();
        this.aboutUsPage.validateCloseButton();
        this.aboutUsPage.validateSendMessageButton();
    }
    
}
