package module.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.Test;

import com.google.inject.Inject;

import module.config.base.BaseTest;
import module.pages.AboutUsPage;

@Epic("Interface do Usuário")
@Feature("Modal Sobre Nós e Contato")
public class AboutUsTest extends BaseTest {

    @Inject
    private AboutUsPage aboutUsPage;

    @Test // Informa ao JUnit 5 que este método é um caso de teste executável
    @Severity(SeverityLevel.NORMAL)
    @Description("Test the About Us page functionality - Valida a abertura do modal e a exibição correta de todos os seus elementos e botões de contato.")
    public void testAboutUsPage() {
        // Fluxo linear chamando as validações que já possuem @Step dentro da AboutUsPage
        this.aboutUsPage.clickContactLink();
        this.aboutUsPage.validateModalTitle();
        this.aboutUsPage.validateContactEmailLink();
        this.aboutUsPage.validateContactNameLink();
        this.aboutUsPage.validateMessageLink();
        this.aboutUsPage.validateCloseButton();
        this.aboutUsPage.validateSendMessageButton();
    }
}