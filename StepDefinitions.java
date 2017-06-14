package com.Creditor.IndividualPricing.StepDefinitions;

import org.junit.Assert;

import com.ApplicationModel.UniversignModel;
import com.Creditor.IndividualPricing.Pages.ApplicationPage;
import com.Creditor.IndividualPricing.Pages.ApplicationSummaryPage;
import com.Creditor.IndividualPricing.Pages.ConfirmationPage;
import com.Creditor.IndividualPricing.Pages.ContractualDocumentsPage;
import com.Creditor.IndividualPricing.Pages.LoanPage;
import com.Creditor.IndividualPricing.Pages.MainPage;
import com.Creditor.IndividualPricing.Pages.QuotationPage;
import com.Creditor.IndividualPricing.Pages.SearchPage;
import com.Creditor.StepDefinitions.AbstractStepDefinitions;
import com.hp.lft.sdk.GeneralLeanFtException;
import com.hp.lft.sdk.web.BrowserDescription;
import com.hp.lft.sdk.web.BrowserFactory;
import com.hp.lft.sdk.web.BrowserType;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class StepDefinitions extends AbstractStepDefinitions {

	protected com.Creditor.Pages.LoginPage loginPage = new com.Creditor.Pages.LoginPage();

	protected MainPage mainPage = new MainPage(browser);
	protected QuotationPage quotationPage = new QuotationPage(browser);
	protected LoanPage loanPage = new LoanPage(browser);
	protected ContractualDocumentsPage contractualPage = new ContractualDocumentsPage(browser);
	protected ApplicationPage applicationPage = new ApplicationPage(browser);
	protected ConfirmationPage confirmationPage = new ConfirmationPage(browser);
	protected ApplicationSummaryPage summaryPage = new ApplicationSummaryPage(browser);
	protected SearchPage searchPage = new SearchPage(browser);

	protected com.Universign.Pages.SignaturePage signaturePage = new com.Universign.Pages.SignaturePage();
	protected com.DMS.Pages.WACLoginPage wacLoginPage = new com.DMS.Pages.WACLoginPage();
	protected com.DMS.Pages.LoginPage dmsLoginPage = new com.DMS.Pages.LoginPage();
	protected com.DMS.Pages.CreateAccessCodePage dmsPasscodePage = new com.DMS.Pages.CreateAccessCodePage();
	protected com.DMS.Pages.DetailsPage dmsDetailsPage = new com.DMS.Pages.DetailsPage();
	protected com.DMS.Pages.QuestionnairePage dmsQuestionnairePage = new com.DMS.Pages.QuestionnairePage();
	protected com.DMS.Pages.SignaturePage dmsSignaturePage = new com.DMS.Pages.SignaturePage();

	
	/*
	 * GIVEN Step Definitions
	 */
	@Given("^I am connected as POS$")
	public void connectAsPOS() throws Throwable {
		indpModel = getAppModel(browser, indpModel);
		environment = "staging";
		navigateToApp(environment);
		loginPage.waitForLoad(browser);
		switch (environment) {
		case "staging":
			loginPage.LogIn(com.Creditor.IndividualPricing.Environment.Staging.posUsername,
					com.Creditor.IndividualPricing.Environment.Staging.posPassword, credModel);
			break;
		case "integration":
			loginPage.LogIn(com.Creditor.IndividualPricing.Environment.Integration.posUsername,
					com.Creditor.IndividualPricing.Environment.Integration.posPassword, credModel);
			break;
		}
		
		Assert.assertTrue("Unable to login", mainPage.isDisplayed(indpModel));
	}
	
	@Given("^my insured loan amount is greater than (\\d+)$")
	public void setLoanAmount(int minimum) throws Throwable {
		loanAmount = generator.generateNumber(minimum + 1, 1000000);
	}

	@Given("^I create a quotation with (\\d+) borrower\\(s\\)$")
	public void createQuotation(int numberofborrowers) throws Throwable {

		// Main Page
		mainPage.waitForLoad(browser);
		mainPage.clickNewQuotation(indpModel);

		// Quotation Page
		quotationPage.fillEtatCivil("Monsieur", firstBorrowerLastName, firstBorrowerFirstName, dateOfBirth,
				"Française", indpModel);
		quotationPage.fillResidentialAddress(generator.generateAddress(), "75020", "Paris", "France", indpModel);
		quotationPage.fillAcquisitionAddress(generator.generateAddress(), "93100", "Montreuil", "France", indpModel);
		quotationPage.fillContact(indpModel);
		quotationPage.fillFirstBorrowerProfessionalSituation(applicantProfession,
				"Trav. manuel important, utilisation d'outillage et/ou manip de marchandises dangereuses", indpModel);
		quotationPage.fillBorrowerProfile(indpModel);
		if (numberofborrowers == 2) {
			quotationPage.selectNumberOfBorrowers(1, indpModel);
			Thread.sleep(2000);
			quotationPage.waitForLoad(browser);

			quotationPage.fillSecondBorrowerEtatCivil("Madame", secondBorrowerLastName, secondBorrowerFirstName,
					dateOfBirth, indpModel);
			quotationPage.fillSecondBorrowerResidentialAddress(generator.generateAddress(), "75020", "Paris",
					indpModel);
			quotationPage.fillSecondBorrowerAcquisitionAddress(generator.generateAddress(), "93100", "Montreuil",
					indpModel);
			quotationPage.fillSecondBorrowerProfessionalSituation(applicantProfession,
					"Trav. manuel important, utilisation d'outillage et/ou manip de marchandises dangereuses",
					indpModel);
		}

		quotationPage.goToLoanPage(indpModel);

	}

	@When("^I am on the loan page$")
	public void i_am_on_the_loan_page() throws Throwable {

		// Loan Page
		Thread.sleep(2000);
		loanPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Loan Page is displayed", loanPage.isDisplayed(indpModel));
	}
 
	@And("^this applicant already exists$")
	public void applicantExists() throws Throwable {

		// Loan Page
		// @TODO Improve logic
		Assert.assertTrue("Borrowers not created", (!firstBorrowerFirstName.isEmpty() && !firstBorrowerLastName.isEmpty()));
	}
	
	@And("^I create a new loan (\\d+)$")
	public void createLoan(int loanindex) throws Throwable {

		// Loan Page
		Thread.sleep(2000);
		loanPage.waitForLoad(browser);
		loanPage.addNewLoan(indpModel);
		Assert.assertTrue("Loan " + loanindex + " NOT created", loanPage.isLoanPresent(loanindex, indpModel));
	}

	@Then("^I can continue up to the confirmation page with (\\d+) borrower\\(s\\)$")
	public void continueToConfirmationPage(int numberofborrowers) throws Throwable {

		// Contractual Documents Page
		contractualPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Contractual Documents Page is displayed", contractualPage.isDisplayed(indpModel));
		contractualPage.goToApplicationPage(indpModel);

		// Application Page
		applicationPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Application Page is displayed", applicationPage.isDisplayed(indpModel));
		applicationPage.calculatePremiums(indpModel);
		Assert.assertTrue("First Borrower Results table NOT displayed",
				applicationPage.firstBorrowerResultsTableIsDisplayed(indpModel));
		Assert.assertTrue("Confirm: Cumulative Premiums table is displayed",
				applicationPage.cumulativePremiumsTableIsDisplayed(indpModel));
		/*applicationPage.fillFirstBorrowerComplementaryInformation(generator.generateNumber(1000, 100000), generator.generateTodaysDate(), indpModel);

		if (numberofborrowers == 2) {
			Assert.assertTrue("Second Borrower Results table NOT displayed",
					applicationPage.secondBorrowerResultsTableIsDisplayed(indpModel));
			applicationPage.fillSecondBorrowerComplementaryInformation(generator.generateNumber(1000, 100000), generator.generateTodaysDate(),
					indpModel);
		}*/

		applicationPage.goToConfirmationPage(indpModel);

		// Confirmation Page
		confirmationPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Confirmation Page is displayed", confirmationPage.isDisplayed(indpModel));
	}

	/*
	 * WHEN Step Definitions
	 */
	

	@When("^I try to go to the loan page$")
	public void gotoLoanPage() throws Throwable {
		// Quotation Page
		quotationPage.goToLoanPage(indpModel);
		Assert.assertFalse("Loan page displayed", loanPage.isDisplayed(indpModel));
	}
	
	@When("^I click on New application$")
	public void clickNewApplication() throws Throwable {
		// Main Page
		mainPage.waitForLoad(browser);
		mainPage.clickNewQuotation(indpModel);
	}
	
	@When("^I click on New applicant$")
	public void clickNewApplicant() throws Throwable {
		// Main Page
		mainPage.waitForLoad(browser);
		mainPage.clickNewApplicant(indpModel);
	}

	@When("^I go to the page one$")
	public void verifyQuotationPageDisplayed() throws Throwable {
		// Quotation Page
		quotationPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Quotation Page is displayed", quotationPage.isDisplayed(indpModel));
	}

	/*
	 * AND Step Definitions
	 */
	
	@And("^I logout$")
	public void i_logout() throws Throwable {
	    quotationPage.logout(indpModel);
	    Thread.sleep(3000);
	}
	
	@And("^I select No for the field French tax residence on the martial status section$")
	public void selectNotFrenchTaxResident() throws Throwable {
		// Quotation Page
		quotationPage.selectFirstBorrowerNOTFrenchResident(indpModel);
	}

	@And("^I select Yes for Are you politically exposed$")
	public void selectPoliticallyExposed() throws Throwable {
		// Quotation Page
		quotationPage.firstBorrowerPoliticallyExposed(indpModel);
	}

	@And("^I select Yes for Do you have a relative politicaly exposed$")
	public void firstBorrowerRelativePoliticallyExposed() throws Throwable {
		// Quotation Page
		quotationPage.firstBorrowerRelativePoliticallyExposed(indpModel);
	}

	@And("^the same list values in the main address country is displayed$")
	public void verifyCountryTaxResidentListAndAddressCountryListAreEqual() throws Throwable {
		// Quotation Page
		Assert.assertEquals("Confirm: First Borrower fiscal residence country options same as address options",
				quotationPage.getFirstBorrowerAddressCountryList(indpModel),quotationPage.getFirstBorrowerFiscalResidenceCountryList(indpModel));
	}

	@And("^I fill all mandatory fields to validate the page one$")
	public void fillAllQuotationPageRequirements() throws Throwable {
		// Quotation Page
		createSingleQuotation();
	}

	@And("^I continue until the Confirmation page$")
	public void continueUntilConfirmationPage() throws Throwable {

		// Quotation Page
		createSingleQuotation();

		// Loan Page
		loanPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Loan Page is displayed", loanPage.isDisplayed(indpModel));
		loanPage.fillLoanInformation(0, loanAmount, "16", "Accession - Résidence Principale", "Variable",
				"Amortissable", "2", "Non", generator.generateName(8), indpModel);
		loanPage.fillFirstBorrowerSpecificities(100, generator.generateNumber(0, 10), "Formule 1 - Amortissable",
				loanPeriodicity, indpModel);
		loanPage.fillFirstBorrowerComplementaryInformation(generator.generateTodaysDate(), Integer.toString(generator.generateNumber(1000, 100000)), indpModel);
		loanPage.calculatePremiums(indpModel);
		loanPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Results table is displayed",
				loanPage.secondBorrowerResultsTableIsDisplayed(indpModel));
		Assert.assertTrue("Confirm: Cumulative Premiums table is displayed",
				loanPage.cumulativePremiumsTableIsDisplayed(indpModel));
		loanPage.goToContractualDocumentsPage(indpModel);

		// Contractual Documents Page
		contractualPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Contractual Documents Page is displayed", contractualPage.isDisplayed(indpModel));
		contractualPage.goToApplicationPage(indpModel);
		
		// Application Page
		applicationPage.waitForLoad(browser);
		Assert.assertTrue("Application Page is NOT displayed", applicationPage.isDisplayed(indpModel));
		applicationPage.calculatePremiums(indpModel);
		Assert.assertTrue("Confirm: Results table is displayed",
				applicationPage.firstBorrowerResultsTableIsDisplayed(indpModel));
		Assert.assertTrue("Confirm: Cumulative Premiums table is displayed",
				applicationPage.cumulativePremiumsTableIsDisplayed(indpModel));
		//applicationPage.fillFirstBorrowerComplementaryInformation(generator.generateNumber(1000, 99999), generator.generateTodaysDate(), indpModel);
		applicationPage.goToConfirmationPage(indpModel);
		

		// Confirmation Page
		confirmationPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Confirmation Page is displayed", confirmationPage.isDisplayed(indpModel));
	}

	@And("^I have launched an application process until the medical selection block$")
	public void continueTillMedicalSelectionBlock() throws Throwable {
		// Main Page
		clickNewApplication();
		verifyQuotationPageDisplayed();

		// Quotation Page
		createSingleQuotation();

		// Loan Page
		loanPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Loan Page is displayed", loanPage.isDisplayed(indpModel));
		loanPage.fillLoanInformation(0, loanAmount, "16", "Accession - Résidence Principale", "Variable",
				"Amortissable", "2", "Non", generator.generateName(8), indpModel);
		loanPage.fillFirstBorrowerSpecificities(generator.generateNumber(0, 100), generator.generateNumber(0, 10),
				"Formule 1 - Amortissable", loanPeriodicity, indpModel);
		loanPage.fillFirstBorrowerComplementaryInformation(generator.generateTodaysDate(), Integer.toString(generator.generateNumber(1000, 100000)), indpModel);
		loanPage.calculatePremiums(indpModel);
		loanPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Results table is displayed",
				loanPage.secondBorrowerResultsTableIsDisplayed(indpModel));
		Assert.assertTrue("Confirm: Cumulative Premiums table is displayed",
				loanPage.cumulativePremiumsTableIsDisplayed(indpModel));
		loanPage.goToContractualDocumentsPage(indpModel);
		// Contractual Documents Page
		contractualPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Contractual Documents Page is displayed", contractualPage.isDisplayed(indpModel));
		contractualPage.goToApplicationPage(indpModel);
		// Application Page
		applicationPage.waitForLoad(browser);
		Assert.assertTrue("Application Page is NOT displayed", applicationPage.isDisplayed(indpModel));
		applicationPage.calculatePremiums(indpModel);
		Assert.assertTrue("Confirm: Results table is displayed",
				applicationPage.firstBorrowerResultsTableIsDisplayed(indpModel));
		Assert.assertTrue("Confirm: Cumulative Premiums table is displayed",
				applicationPage.cumulativePremiumsTableIsDisplayed(indpModel));
		//applicationPage.fillFirstBorrowerComplementaryInformation(generator.generateNumber(1000, 99999), generator.generateTodaysDate(), indpModel);
		applicationPage.goToConfirmationPage(indpModel);
		// Confirmation Page
		confirmationPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Confirmation Page is displayed", confirmationPage.isDisplayed(indpModel));
		confirmationPage.selectSignatureOption("Papier", indpModel);
		confirmationPage.goToApplicationSummaryPage(indpModel);
	}

	@And("^I have just signed my application form online$")
	public void continueSignApplicationOnline() throws Throwable {

		// Main Page
		mainPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Quotation Page is displayed", mainPage.isDisplayed(indpModel));
		mainPage.clickNewQuotation(indpModel);

		// Quotation Page
		createSingleQuotation();

		// Loan Page
		loanPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Loan Page is displayed", loanPage.isDisplayed(indpModel));
		loanPage.fillLoanInformation(0, loanAmount, "16", "Accession - Résidence Principale", "Variable",
				"Amortissable", "2", "Non", generator.generateName(8), indpModel);
		loanPage.fillFirstBorrowerSpecificities(100, generator.generateNumber(0, 10), "Formule 1 - Amortissable",
				loanPeriodicity, indpModel);
		loanPage.fillFirstBorrowerComplementaryInformation(generator.generateTodaysDate(),Integer.toString(generator.generateNumber(1000, 100000)), indpModel);
		loanPage.calculatePremiums(indpModel);
		loanPage.waitForLoad(browser);
		Thread.sleep(10000);
		Assert.assertTrue("Confirm: Results table is displayed",
				loanPage.secondBorrowerResultsTableIsDisplayed(indpModel));
		Assert.assertTrue("Confirm: Cumulative Premiums table is displayed",
				loanPage.cumulativePremiumsTableIsDisplayed(indpModel));
		/* Ajout AFR
		loanPage.fillFirstBorrowerComplementaryInformation(generator.generateTodaysDate(),Integer.toString(generator.generateNumber(1000, 100000)), indpModel);
		loanPage.calculatePremiums(indpModel);
		loanPage.waitForLoad(browser);
		Thread.sleep(10000);*/
		loanPage.goToContractualDocumentsPage(indpModel);

		// Contractual Documents Page
		contractualPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Contractual Documents Page is displayed", contractualPage.isDisplayed(indpModel));
		contractualPage.goToApplicationPage(indpModel);

		// Application Page
		applicationPage.waitForLoad(browser);
		Assert.assertTrue("Application Page is NOT displayed", applicationPage.isDisplayed(indpModel));
		applicationPage.calculatePremiums(indpModel);
		Assert.assertTrue("Results table is NOT displayed",
				applicationPage.firstBorrowerResultsTableIsDisplayed(indpModel));
		Assert.assertTrue("Cumulative Premiums table is NOT displayed",
				applicationPage.cumulativePremiumsTableIsDisplayed(indpModel));
		//applicationPage.fillFirstBorrowerComplementaryInformation(generator.generateTodaysDate(), indpModel);
		applicationPage.goToConfirmationPage(indpModel);

		// Confirmation Page
		confirmationPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Confirmation Page is displayed", confirmationPage.isDisplayed(indpModel));
		confirmationPage.selectSignatureOption("Electronique", indpModel);
		confirmationPage.selectAgencySignature(indpModel);

		// Universign Signature Page
		signaturePage.waitForLoad(browser);
		Thread.sleep(60000);
		Assert.assertTrue("Universign tab not opened", utility.isNewTabCreated(browser, 1, 2));
		browser = BrowserFactory.attach(
				new BrowserDescription.Builder().openTitle("Universign - Service de Signature en Ligne").build());
		unisModel = getAppModel(browser, unisModel);
		Assert.assertTrue("Universign Signature Page was NOT displayed", signaturePage.isDisplayed(unisModel));
		signaturePage.waitForLoad(browser);
		signaturePage.goToLastPage(unisModel);
		signaturePage.confirmDeclarations(unisModel);
		signaturePage.clickToSign(unisModel);
		Thread.sleep(20000);
		Assert.assertTrue("SMS Popup was NOT displayed", signaturePage.isSMSPopupDisplayed(unisModel));

		// Telerivet
		String code = com.Helpers.Telerivet.getCode("Universign", 4);

		// Universign
		signaturePage.enterCode(code, unisModel);
		signaturePage.submitSignature(unisModel);
		browser.sync();
		
		// Application Summary Page
		summaryPage.waitForLoad(browser);
		Thread.sleep(30000);
		Assert.assertTrue("DMS tab not opened automatically", utility.isNewTabCreated(browser, 2, 3));
		confirmationPage.goToApplicationSummaryPage(indpModel);
		Assert.assertTrue("Verify: Application Summary Page (Page 6) is displayed", summaryPage.isDisplayed(indpModel));

	}

	@And("^I have launched my medical selection process online$")
	public void launchDMS() throws Throwable {

		continueSignApplicationOnline();
		browser = BrowserFactory.attach(new BrowserDescription.Builder().openTitle("Authentification").build());
		browser.close();

		// Application Summary Page
		summaryPage.launchDMS(indpModel);

		// WAC
		Thread.sleep(10000);
		browser = BrowserFactory.attach(new BrowserDescription.Builder().openTitle("Authentification").build());
		wacLoginPage.waitForLoad(browser);
		dmsModel = getAppModel(browser, dmsModel);
		if (wacLoginPage.isDisplayed(dmsModel)) {
			Assert.assertTrue("WAC login page not displayed", wacLoginPage.isDisplayed(dmsModel));
			wacLoginPage.enterUsername(com.DMS.Environment.WAC.username, dmsModel);
			wacLoginPage.enterPasscode(com.DMS.Environment.WAC.password, browser);
			wacLoginPage.clickValidate(dmsModel);
		} else {
			System.out.println("WAC login page by-passed");
		}

		// DMS
		dmsLoginPage.waitForLoad(browser);
		Assert.assertTrue("DMS login page not displayed", dmsLoginPage.isDisplayed(dmsModel, browser));
		dmsLoginPage.requestAccessCode(dmsModel);
		Assert.assertTrue("DMS code request popup not displayed", dmsLoginPage.codeRequestPopupIsDisplayed(dmsModel));
		dmsLoginPage.enterEmail(userEmail, dmsModel);
		dmsLoginPage.selectEmailReception(dmsModel);
		dmsLoginPage.validateCodeRequest(dmsModel);

		// Outlook
		String dmscode = com.Helpers.Outlook.getCode("Confirmation du code d'accès au questionnaire médical en ligne",
				6);

		// DMS Login
		dmsLoginPage.enterPasscode(dmscode, browser);
		dmsLoginPage.validateLogin(dmsModel);
		browser.sync();
		Thread.sleep(10000);
		dmsLoginPage.acceptLanguage(dmsModel);

		// DMS Passcode
		dmsPasscodePage.isDisplayed(dmsModel);
		dmsPasscodePage.enterNewPasscode(dmsModel);
		dmsPasscodePage.selectQuestion(dmsModel);
		dmsPasscodePage.enterQuestionRespnse(dmsModel);
		dmsPasscodePage.validatePasscodeChange(dmsModel);
		dmsPasscodePage.waitForLoad(browser);
		dmsPasscodePage.beginQuestionnaire(dmsModel);

		// DMS Details
		dmsDetailsPage.waitForLoad(browser);
		dmsDetailsPage.confirmInfo(dmsModel);
		dmsDetailsPage.gotoQuestionnaire(dmsModel);
		Thread.sleep(10000);
	}

	@And("^I have launched my medical selection process offline$")
	public void offlineProcess() throws Throwable {
		// Main Page
		clickNewApplication();
		verifyQuotationPageDisplayed();
		createSingleQuotation();
		// Loan Page
		loanPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Loan Page is displayed", loanPage.isDisplayed(indpModel));
		loanPage.fillLoanInformation(0, loanAmount, "16", "Accession - Résidence Principale", "Variable",
				"Amortissable", "2", "Non", generator.generateName(8), indpModel);
		loanPage.fillFirstBorrowerSpecificities(100, generator.generateNumber(0, 10), "Formule 1 - Amortissable",
				loanPeriodicity, indpModel);
		loanPage.fillFirstBorrowerComplementaryInformation(generator.generateTodaysDate(),Integer.toString(generator.generateNumber(1000, 100000)), indpModel);
		loanPage.calculatePremiums(indpModel);
		loanPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Results table is displayed",
				loanPage.secondBorrowerResultsTableIsDisplayed(indpModel));
		Assert.assertTrue("Confirm: Cumulative Premiums table is displayed",
				loanPage.cumulativePremiumsTableIsDisplayed(indpModel));
		loanPage.goToContractualDocumentsPage(indpModel);
		// Contractual Documents Page
		contractualPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Contractual Documents Page is displayed", contractualPage.isDisplayed(indpModel));
		contractualPage.goToApplicationPage(indpModel);

		// Application Page
		applicationPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Application Page is displayed", applicationPage.isDisplayed(indpModel));
		applicationPage.calculatePremiums(indpModel);
		Assert.assertTrue("Confirm: Results table is displayed",
				applicationPage.firstBorrowerResultsTableIsDisplayed(indpModel));
		Assert.assertTrue("Confirm: Cumulative Premiums table is displayed",
				applicationPage.cumulativePremiumsTableIsDisplayed(indpModel));
		//applicationPage.fillFirstBorrowerComplementaryInformation(generator.generateNumber(1000, 99999), generator.generateTodaysDate(), indpModel);
		applicationPage.goToConfirmationPage(indpModel);

		// Confirmation Page
		confirmationPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Confirmation Page is displayed", confirmationPage.isDisplayed(indpModel));
		confirmationPage.selectSignatureOption("Papier", indpModel);
		confirmationPage.waitForLoad(browser);
		confirmationPage.openFirstApplicationAdmissionForm(indpModel);
		confirmationPage.verifyPDFIsOpen(indpModel);
		//confirmationPage.fillFirstSignatureDate(generator.generateTodaysDate(), indpModel);
		//confirmationPage.confirmFirstSignatureDate(generator.generateTodaysDate(), indpModel);
		confirmationPage.goToApplicationSummaryPage(indpModel);

		// Application Summary Page
		summaryPage.waitForLoad(browser);

	}

	@And("^I create a quotation with one borrower$")
	public void createSingleQuotation() throws Throwable {

		// Quotation Page
		quotationPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Quotation Page is displayed", quotationPage.isDisplayed(indpModel));
		quotationPage.fillEtatCivil("Monsieur", generator.generateName(10), generator.generateName(5), "12/12/1978",
				"Française", indpModel);
		quotationPage.fillResidentialAddress(generator.generateAddress(), "75020", "Paris", "France", indpModel);
		quotationPage.fillAcquisitionAddress(generator.generateAddress(), "93100", "Montreuil", "France", indpModel);
		quotationPage.fillContact(userEmail, "0637364636", indpModel);
		quotationPage.fillFirstBorrowerProfessionalSituation(applicantProfession,
				"Trav. manuel important, utilisation d'outillage et/ou manip de marchandises dangereuses", indpModel);
		quotationPage.fillBorrowerProfile(indpModel);
		quotationPage.goToLoanPage(indpModel);

	}

	@And("^I have not yet began my medical selection process$")
	public void verifyMedicalSelectionNotStarted() throws Throwable {
		browser.sync();
		// Application Summary Page
		// verifyOnPage6();

	}

	@And("^my application status is \"([^\"]*)\"$")
	public void my_application_status_is(String status) throws Throwable {
		browser.sync();
		Thread.sleep(5000);
		//summaryPage.refreshSection(indpModel);
		summaryPage.waitForLoad(browser);
		Thread.sleep(5000);
		Assert.assertEquals("Incorrect application status", status, summaryPage.getApplicationStatus(indpModel));
	}

	@And("^I modify the status to \"([^\"]*)\"$")
	public void modifyStatus(String status) throws Throwable {
		summaryPage.selectMedicalSelection(indpModel);
		summaryPage.modifyApplicationStatus(status, indpModel);
		Thread.sleep(10000);
		summaryPage.selectMedicalSelection(indpModel);
	}

	@And("^DMS questionnaire status is displayed as \"([^\"]*)\"$")
	public void verifyDMSQuestionnaireStatus(String status) throws Throwable {
		summaryPage.selectMedicalSelection(indpModel);
		Assert.assertEquals("DMS status incorrect", status, summaryPage.getDMSQuestionnaireStatus(indpModel));
	}

	@Then("^DMS questionnaire status is displayed as \"([^\"]*)\" \\+ signature date$")
	public void verifyDMSQuestionnaireStatusDate(String status) throws Throwable {
		summaryPage.selectMedicalSelection(indpModel);
		verifyDMSQuestionnaireStatus(status + generator.generateTodaysDate());
	}

	@And("^DMS decision status is displayed as \"([^\"]*)\"$")
	public void verifyDMSdecisionStatus(String status) throws Throwable {
		// Summary Page
		summaryPage.waitForLoad(browser);
		summaryPage.selectMedicalSelection(indpModel);
		Thread.sleep(1000);
		//browser.close();
		browser = BrowserFactory.attach(new BrowserDescription.Builder().openTitle("Universign - Service de Signature en Ligne").build());
		Assert.assertEquals("DMS status incorrect", status, summaryPage.getDMSDecisionStatus(indpModel));
			
	}
	

	@And("^I am obese$")
	public void makeOverweight() throws Throwable {
		weight = generator.generateNumber(400, 500);
	}

	@And("^I fill all required information for the (\\d+) loan\\(s\\) with (\\d+) borrower\\(s\\)$")
	public void fillLoanInfo(int numberofloans, int numberofborrowers) throws Throwable {

		// Loan Page
		for (int i = 0; i < numberofloans; i++) {
			loanPage.fillLoanInformation(i, loanAmount, "16", "Accession - Résidence Principale", "Variable",
					"Amortissable", "2", "Non", generator.generateName(8), indpModel);
		}

		for (int i = 0; i < numberofloans; i++) {
			for (int j = 0; j < numberofborrowers; j++) {
				loanPage.fillBorrowerSpecificities(i, j, generator.generateNumber(0, 100),
						generator.generateNumber(0, 10), "Formule 1 - Amortissable", loanPeriodicity, indpModel);
			}
		}

		if (numberofborrowers == 2) {
			loanPage.fillFirstBorrowerComplementaryInformation(generator.generateTodaysDate(),Integer.toString(generator.generateNumber(1000, 100000)), indpModel);
			loanPage.fillSecondBorrowerComplementaryInformation(generator.generateTodaysDate(),Integer.toString(generator.generateNumber(1000, 100000)), indpModel);
		} else {
			loanPage.fillFirstBorrowerComplementaryInformation(generator.generateTodaysDate(),Integer.toString(generator.generateNumber(1000, 100000)), indpModel);
		}

		loanPage.calculatePremiums(indpModel);
		loanPage.waitForLoad(browser);

		if (numberofborrowers == 2) {
			Assert.assertTrue("Confirm: Results table is displayed",
					loanPage.firstBorrowerResultsTableIsDisplayed(indpModel));
			Assert.assertTrue("Confirm: Results table is displayed",
					loanPage.secondBorrowerResultsTableIsDisplayed(indpModel));
		} else {
			Assert.assertTrue("Confirm: Results table is displayed",
					loanPage.firstBorrowerResultsTableIsDisplayed(indpModel));
		}

		Assert.assertTrue("Confirm: Cumulative Premiums table is displayed",
				loanPage.cumulativePremiumsTableIsDisplayed(indpModel));
		loanPage.goToContractualDocumentsPage(indpModel);

	}

	@And("^I select \"([^\"]*)\" as a disease$")
	public void i_select_as_a_disease(String disease) throws Throwable {

		/* DMS Questionnaire */
		dmsQuestionnairePage.waitForLoad(browser);
		Assert.assertTrue("DMS questionnaire page NOT displayed", dmsQuestionnairePage.isDisplayed(dmsModel));
		dmsQuestionnairePage.enterHeightWeight("180", Integer.toString(weight), dmsModel);
		dmsQuestionnairePage.enterPathologiesDiseasesAffections("Dermatologique", disease, dmsModel);
		dmsQuestionnairePage.enterNoTreatments(dmsModel);
		dmsQuestionnairePage.enterNoHospitalizationsInterventions(dmsModel);
		dmsQuestionnairePage.enterNoDiseasesWorkStopages(dmsModel);
		dmsQuestionnairePage.submitQuestionnaire(dmsModel);
		dmsQuestionnairePage.confirmQuestionnaire(dmsModel);

		/* DMS Signature */
		dmsSignaturePage.waitForLoad(browser);
		Assert.assertTrue("DMS signature page NOT displayed", dmsSignaturePage.isDisplayed(dmsModel));
		dmsSignaturePage.signElectronically(dmsModel);
		dmsSignaturePage.waitForLoad(browser);
		Thread.sleep(10000);
		Assert.assertTrue("Electronic signature popup failed", dmsSignaturePage.verifyCodePopupIsDisplayed(dmsModel));
		String sigcode = com.Helpers.Telerivet.getCode("Universign", 4);
		dmsSignaturePage.enterCode(sigcode, dmsModel);
		Assert.assertTrue("Electronic signature popup failed",
				dmsSignaturePage.verifyConfirmationIsDisplayed(dmsModel));
		dmsSignaturePage.logout(dmsModel);

		/* Application Summary Page */
		summaryPage.refreshSection(indpModel);
		Thread.sleep(10000);
		summaryPage.refreshSection(indpModel);
		Thread.sleep(10000);
	}

	@And("^I select No to all questionnaire questions$")
	public void selectNoToAllDMSQuestions() throws Throwable {

		/* DMS Questionnaire */
		dmsQuestionnairePage.waitForLoad(browser);
		dmsQuestionnairePage.enterHeightWeight("180", Integer.toString(weight), dmsModel);
		dmsQuestionnairePage.enterNoPathologiesDiseasesAffections(dmsModel);
		dmsQuestionnairePage.enterNoTreatments(dmsModel);
		dmsQuestionnairePage.enterNoHospitalizationsInterventions(dmsModel);
		dmsQuestionnairePage.enterNoDiseasesWorkStopages(dmsModel);
		dmsQuestionnairePage.submitQuestionnaire(dmsModel);
		dmsQuestionnairePage.confirmQuestionnaire(dmsModel);

		/* DMS Signature */
		dmsSignaturePage.signElectronically(dmsModel);
		dmsSignaturePage.waitForLoad(browser);
		Thread.sleep(10000);
		Assert.assertTrue("Electronic signature popup failed", dmsSignaturePage.verifyCodePopupIsDisplayed(dmsModel));
		// Mock DMS Universign, code figé à 1234
		//String sigcode = com.Helpers.Telerivet.getCode("Universign", 4);
		String sigcode = "1234";
		dmsSignaturePage.enterCode(sigcode, dmsModel);
		Assert.assertTrue("Electronic signature popup failed",
				dmsSignaturePage.verifyConfirmationIsDisplayed(dmsModel));
		dmsSignaturePage.logout(dmsModel);

		/* Application Summary Page */
		summaryPage.refreshSection(indpModel);
		Thread.sleep(10000);
	}

	@And("^DMS questionnaire is pending for signature$")
	public void verifyDMSSignaturePending() throws Throwable {

		/* DMS Signature */
		dmsSignaturePage.signElectronically(dmsModel);
		dmsSignaturePage.waitForLoad(browser);
		Thread.sleep(10000);
		Assert.assertTrue("Electronic signature popup failed", dmsSignaturePage.verifyCodePopupIsDisplayed(dmsModel));

		/* Application Summary Page */
		summaryPage.refreshSection(indpModel);
		Thread.sleep(10000);

	}
	
	@And("^I simulate a DMS decision status of \"([^\"]*)\" with \"([^\"]*)\" and \"([^\"]*)\"  with DC \"([^\"]*)\" as and PTIA as \"([^\"]*)\"$")
	public void i_simulate_a_DMS_decision_status_of_with_and(String decision, String gis, String questionaire, String dc, String ptia) throws Throwable {
		dmsSim.simulateDMSResponse(gis, questionaire, decision, dc, ptia, browser);
		summaryPage.waitForLoad(browser);
	}
	
	@And("^I add a RIB document$")
	public void i_add_a_RIB_document() throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
		/*indpModel = getAppModel(browser, indpModel);
		navigateToApp("https://stagingaxa-creditor.cs84.force.com/creditor/ap100_applicationconfirmation_specific1?id=a0A5E000000jLqCUAU");
		Thread.sleep(5000);*/
		summaryPage.addRIB(indpModel);
	}
	
	@And("^I add a \"([^\"]*)\" Id Card$")
	public void i_add_an_Id_Card(String stateIdCard) throws Throwable {
		/*indpModel = getAppModel(browser, indpModel);
		navigateToApp("https://stagingaxa-creditor.cs84.force.com/creditor/ap100_applicationconfirmation_specific1?id=a0A5E000000jblVUAQ");
		Thread.sleep(5000);*/
		summaryPage.addIdCard(indpModel,stateIdCard);
		Thread.sleep(5000);
	}
	
	
	@And("^I can add an Id Card$")
	public void i_can_add_an_Id_Card() throws Throwable {
		summaryPage.getAddIdCard(indpModel);
	}
	
	@And("^I enter my IBAN information$")
	public void i_enter_my_IBAN_Information() throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
		summaryPage.addIBANInformation(indpModel);
	}
	
	@And("^I enter my BIC information$")
	public void i_enter_my_BIC_Information() throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
		summaryPage.addBICInformation(indpModel);
	}
	
	@And("^I sign the SEPA$")
	public void i_sign_the_SEPA() throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
		summaryPage.signSEPA(indpModel);
		//summaryPage.goToSignaturePage();
		// Universign Signature Page
		//signaturePage.waitForLoad(browser);
		Thread.sleep(15000);
		//Assert.assertTrue("Universign tab not opened", utility.isNewTabCreated(browser, 1, 2));
		/*browser = BrowserFactory.attach(
				new BrowserDescription.Builder().openTitle("Authentification").build());
		browser = BrowserFactory.attach(
				new BrowserDescription.Builder().openTitle("Universign - Service de Signature en Ligne").build());*/
		//Assert.assertTrue("Universign Signature Page was NOT displayed", signaturePage.isDisplayed(unisModel));
		//signaturePage.waitForLoad(browser);

		//Boolean existPage = unisModel.OnlineSignatureServicePage().exists(10);
		signaturePage.goToLastPage(unisModel);
		signaturePage.confirmDeclarations(unisModel);
		signaturePage.clickToSign(unisModel);
		Thread.sleep(20000);
		Assert.assertTrue("SMS Popup was NOT displayed", signaturePage.isSMSPopupDisplayed(unisModel));

		// Telerivet
		String code = com.Helpers.Telerivet.getCode("Universign", 4);

		// Universign
		signaturePage.enterCode(code, unisModel);
		signaturePage.submitSignature(unisModel);
		browser.sync();
		
		// Application Summary Page
		summaryPage.waitForLoad(browser);
		Thread.sleep(30000);
		
	}	
	
	
	@And("^I sign electronically$")
	public void i_sign_electronically() throws Throwable {
		summaryPage.electronicSignature(indpModel);
		browser = BrowserFactory.attach(
				new BrowserDescription.Builder().openTitle("Universign - Service de Signature en Ligne").build());
		unisModel = getAppModel(browser, unisModel);
		Assert.assertTrue("Universign Signature Page was NOT displayed", signaturePage.isDisplayed(unisModel));
		signaturePage.waitForLoad(browser);
		signaturePage.goToLastPage(unisModel);
		signaturePage.confirmDeclarations(unisModel);
		signaturePage.clickToSign(unisModel);
		Thread.sleep(20000);
		Assert.assertTrue("SMS Popup was NOT displayed", signaturePage.isSMSPopupDisplayed(unisModel));

		// Telerivet
		String code = com.Helpers.Telerivet.getCode("Universign", 4);

		// Universign
		signaturePage.enterCode(code, unisModel);
		signaturePage.submitSignature(unisModel);
		browser.sync();
		
		// Application Summary Page
		summaryPage.waitForLoad(browser);
		Thread.sleep(30000);
	}
	
	@And("^I validate subscription$")
	public void validateSubscription() throws Throwable {
		summaryPage.validateSubscription(indpModel);
	}
	
	@And("^I select borrower (\\d+) the as concerned borrower for loan (\\d+)$")
	public void selectConcernedBorrower(int borrowerindex, int loanindex) throws Throwable {
	    loanPage.selectConcernedBorrower(loanindex, borrowerindex, indpModel);
	}
	
	@And("^I close the browser$")
	public void closeBrowser() throws Throwable {
	    browser.closeAllTabs();
	}

	/*
	 * WHEN Step Definitions
	 */

	@When("^I have answered to all my questions and confirm them$")
	public void selectNoToAllDMSQuestionsAndConfirm() throws Throwable {

		/* DMS Questionnaire */
		dmsQuestionnairePage.waitForLoad(browser);
		dmsQuestionnairePage.enterHeightWeight("180", Integer.toString(weight), dmsModel);
		dmsQuestionnairePage.enterNoPathologiesDiseasesAffections(dmsModel);
		dmsQuestionnairePage.enterNoTreatments(dmsModel);
		dmsQuestionnairePage.enterNoHospitalizationsInterventions(dmsModel);
		dmsQuestionnairePage.enterNoDiseasesWorkStopages(dmsModel);
		dmsQuestionnairePage.submitQuestionnaire(dmsModel);
		dmsQuestionnairePage.confirmQuestionnaire(dmsModel);
		Assert.assertTrue("DMS Signaturepage NOT displayed", dmsSignaturePage.isDisplayed(dmsModel));

	}

	@When("^the medical questionnaire is completed and signed$")
	public void selectNoToAllDMSQuestionsConfirmAndSigned() throws Throwable {

		/* DMS Questionnaire */
		selectNoToAllDMSQuestionsAndConfirm();

		/* DMS Signature */
		dmsSignaturePage.signElectronically(dmsModel);
		dmsSignaturePage.waitForLoad(browser);
		Thread.sleep(10000);
		Assert.assertTrue("Electronic signature popup failed", dmsSignaturePage.verifyCodePopupIsDisplayed(dmsModel));
		String sigcode = com.Helpers.Telerivet.getCode("Universign", 4);
		dmsSignaturePage.enterCode(sigcode, dmsModel);
		Assert.assertTrue("Electronic signature popup failed",
				dmsSignaturePage.verifyConfirmationIsDisplayed(dmsModel));
		dmsSignaturePage.logout(dmsModel);

		/* Application Summary Page */
		summaryPage.refreshSection(indpModel);
		Thread.sleep(10000);

	}

	@When("^I am on page six of the application process$")
	public void verifyOnPage6() throws Throwable {

		// Application Summary Page
		summaryPage.waitForLoad(browser);
		Thread.sleep(1000);
		Assert.assertTrue("Application Summary Page not displayed", summaryPage.isDisplayed(indpModel));
	}
	
	@When("^I am on the Finalisation section of page six$")
	public void i_am_on_the_Finalisation_section_of_page_six() throws Throwable {
	    summaryPage.selectFinalisationSelection(indpModel);
	    Assert.assertTrue("Finalisation section is NOT displayed", summaryPage.verifyFinalisationSelectionBlockIsEnabled(indpModel));
	}


	/*
	 * THEN Step Definitions
	 */
	
	
	@Then("^Validation date is todays date$")
	public void getValidationDate() throws Throwable {
		Assert.assertTrue("Validation date is not today's date",summaryPage.getValidationDate(indpModel));
	}
	@Then("^Country tax residence picklist is displayed on the martial status section$")
	public void verifyCountryTaxresidentIsDisplayed() throws Throwable {

		// Quotation Page
		Assert.assertTrue("Confirm: First Borrower fiscal residence country options displayed",
				quotationPage.isFirstBorrowerFiscalResidenceCountryDisplayed(indpModel));
	}

	@Then("^I can choose both signature process online or offline$")
	public void verifySignatureOptions() throws Throwable {

		// Quotation Page
		Assert.assertTrue("Both online and offline options not available",
				confirmationPage.getSignatureOptionsAvailable(indpModel).contains("Electronique Papier"));

	}

	@Then("^the application status is displayed next to the application number at the top of the page$")
	public void verifyApplicationStatusDisplayed() throws Throwable {
		Assert.assertTrue("Verify: Application status is displayed",
				summaryPage.verifyApplicationStatusIsDisplayed(indpModel));
	}

	@Then("^the medical selection block is enabled$")
	public void verifyMedicalSelectionBlockDisplayed() throws Throwable {
		summaryPage.waitForLoad(browser);
		Thread.sleep(1000);
		Assert.assertTrue("Medical Selection Block not displayed",
				summaryPage.verifyMedicalSelectionBlockIsEnabled(indpModel));
	}

	@Then("^the creation date of the medical questionnaire is displayed in the section next to the label Creation date$")
	public void verifyCreationDateDisplayed() throws Throwable {
		Assert.assertEquals("Medical Selection Block not displayed", generator.generateTodaysDate(),
				summaryPage.getCreationDate(indpModel));
	}

	@Then("^the decision summary table is displayed including the following : \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\" and \"([^\"]*)\" column per loan\\.$")
	public void verifyDecisionSummaryTable(String loan, String garanty, String decision, String prime, String gis,
			String globaldecision) throws Throwable {
		Assert.assertEquals("Incorrect decision summary table headers",
				"[" + loan + ", " + garanty + ", " + decision + ", " + prime + ", " + gis + ", " + globaldecision + "]",
				summaryPage.getDecisionSummaryTableHeaders(indpModel));
	}
	
	@Then("^I relaunch the browser$")
	public void relaunchBrowser() throws Throwable {
		browser = getBrowser("Chrome");
	}
	
	@Then("^A confirmation message is displayed for borrower (\\d+) stating the applicant already exists$")
	public void a_confirmation_message_is_displayed_stating_the_applicant_already_exists(int borrowerId) throws Throwable {
	    if (borrowerId == 1){
	    	Assert.assertEquals("Incorrect confirmation message", "× Confirmation L'adhérent " + firstBorrowerLastName + " " + firstBorrowerFirstName + " existe déjà",loanPage.getConfirmationMessage(0, indpModel) );
	    }
	    	else{
	    		Assert.assertEquals("Incorrect confirmation message", "× Confirmation L'adhérent " + secondBorrowerLastName + " " + secondBorrowerFirstName + " existe déjà",loanPage.getConfirmationMessage(1, indpModel) );
	    	}
	}
	
	@Then("^A confirmation message is displayed$")
	public void a_confirmation_message_is_displayed() throws Throwable {
	    	Assert.assertTrue("Undisplayed confirmation message", indpModel.ConfirmationMessageWebElement().exists(30));
	}
	
	@Then("^An error message is displayed$")
	public void an_error_message_is_displayed() throws Throwable {
	    	Assert.assertTrue("Undisplayed error message", indpModel.ErrorMessageWebElement().exists(30));
	}
	
	@And("^I am on the homepage of the Partner Portal$")
	public void verifyOnMainPage() throws GeneralLeanFtException {
		mainPage.waitForLoad(browser);
		Assert.assertTrue("Main page NOT displayed", mainPage.isDisplayed(indpModel));
	}
	
	@When("^I search for a non-existant quotation ID$")
	public void i_search_for_a_non_existant_quotation_ID() throws Throwable {
	    
		quotationID = "QU" + generator.generateNumber(1000000, 9000000);
		
		// Main Page
		mainPage.fillSearchField(quotationID, indpModel);
		mainPage.submitSearchField(indpModel);
	}

	@Then("^the search results page does not display any results$")
	public void the_search_results_page_does_not_display_any_results() throws Throwable {

		// Search Page
		Assert.assertEquals("Results were returned", 1, searchPage.getNumberOfApplicantsInSearchResult(indpModel));
	}
	
	@And("^I create a non-converted quotation$")
	public void createNonConvertedQuotation() throws Throwable {
		
		// Quotation Page
		createQuotation(1);
		
		// Loan Page
		loanPage.waitForLoad(browser);
		Assert.assertTrue("Loan page is NOT displayed", loanPage.isDisplayed(indpModel));
		loanPage.fillLoanInformation(0, loanAmount, "16", "Accession - Résidence Principale", "Variable",
				"Amortissable", "2", "Non", generator.generateName(8), indpModel);
		loanPage.fillFirstBorrowerSpecificities(100, generator.generateNumber(0, 10), "Formule 1 - Amortissable",
				loanPeriodicity, indpModel);
		loanPage.fillFirstBorrowerComplementaryInformation(generator.generateTodaysDate(), Integer.toString(generator.generateNumber(1000, 100000)), indpModel);
		loanPage.calculatePremiums(indpModel);
		loanPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Results table is displayed",
				loanPage.secondBorrowerResultsTableIsDisplayed(indpModel));
		Assert.assertTrue("Confirm: Cumulative Premiums table is displayed",
				loanPage.cumulativePremiumsTableIsDisplayed(indpModel));
		loanPage.goToContractualDocumentsPage(indpModel);
		
		// Contractual Documents Page
		contractualPage.waitForLoad(browser);
		Assert.assertTrue("Contractual Documents page is NOT displayed", contractualPage.isDisplayed(indpModel));
		quotationID = contractualPage.getQuotationID(indpModel);
	}

	@When("^I go to the homepage$")
	public void i_go_to_the_homepage() throws Throwable {
		contractualPage.goToMainPage(indpModel);
	}
	
	@When("^I look for the documents section")
	public void i_look_for_the_documents_section() throws Throwable {
		Integer docIndex = summaryPage.getDocumentSectionIndex(indpModel);
	}
	
	@When("^this section is displayed beneath the underwriting workflow")
	public void this_section_is_displayed_beneath_the_underwriting_workflow() throws Throwable {
		Integer documentsLocation = summaryPage.getDocumentSectionIndex(indpModel);
		Integer underwritingWorkflowLocation = summaryPage.getUnderwritingWorkflowSectionIndex(indpModel);
		Assert.assertTrue("Document section is not beneath the underwriting workflow", documentsLocation > underwritingWorkflowLocation);
	}

	@And("^I search for the non-converted quotation$")
	public void i_search_for_the_non_converted_quotation() throws Throwable {
		
		// Main Page
		mainPage.waitForLoad(browser);
		Assert.assertTrue("Main page is NOT displayed", mainPage.isDisplayed(indpModel));
		mainPage.fillSearchField(quotationID, indpModel);
		mainPage.submitSearchField(indpModel);
	}

	@Then("^the loan page of the corresponding quotation is displayed$")
	public void verifyLoanPageDisplayedForQuotation() throws Throwable {
		
		// Loan Page
		i_am_on_the_loan_page();
		loanPage.calculatePremiums(indpModel);
		loanPage.waitForLoad(browser);
		loanPage.goToContractualDocumentsPage(indpModel);
		
		// Contractual Documents Page
		contractualPage.waitForLoad(browser);
		Assert.assertEquals("Incorrect quotation returned", quotationID, contractualPage.getQuotationID(indpModel));
	}
	
	@And("^I create a converted quotation$")
	public void createConvertedQuotation() throws Throwable {
		
		createNonConvertedQuotation();
		
		// Contractual Documents Page
		contractualPage.goToApplicationPage(indpModel);

		// Application Page
		applicationPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Application Page is displayed", applicationPage.isDisplayed(indpModel));
		applicationID = applicationPage.getApplicationID(indpModel);
	}

	@And("^I search for the converted quotation$")
	public void searchConvertedQuotation() throws Throwable {
		
		// Main Page
		mainPage.waitForLoad(browser);
		Assert.assertTrue("Main page is NOT displayed", mainPage.isDisplayed(indpModel));
		mainPage.fillSearchField(applicationID, indpModel);
		mainPage.submitSearchField(indpModel);
	}

	@Then("^the application page of the corresponding quotation is displayed$")
	public void verifyApplicationPageDisplayedForQuotation() throws Throwable {
		
		// Application Page
		applicationPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Application Page is displayed", applicationPage.isDisplayed(indpModel));
		Assert.assertEquals("Incorrect quotation returned", applicationID, applicationPage.getApplicationID(indpModel));
	}
	
	@And("^I create a converted quotation beyond page four$")
	public void createConvertedQuotationBeyond() throws Throwable {
		
		createConvertedQuotation();
		
		// Application Page
		applicationPage.calculatePremiums(indpModel);
		Assert.assertTrue("Confirm: Results table is displayed",
				applicationPage.firstBorrowerResultsTableIsDisplayed(indpModel));
		Assert.assertTrue("Confirm: Cumulative Premiums table is displayed",
				applicationPage.cumulativePremiumsTableIsDisplayed(indpModel));
		applicationPage.goToConfirmationPage(indpModel);

		// Confirmation Page
		confirmationPage.waitForLoad(browser);
		Assert.assertTrue("Confirm: Confirmation Page is displayed", confirmationPage.isDisplayed(indpModel));
		confirmationPage.selectSignatureOption("Papier", indpModel);
		confirmationPage.waitForLoad(browser);
		confirmationPage.openFirstApplicationAdmissionForm(indpModel);
		confirmationPage.verifyPDFIsOpen(indpModel);
		confirmationPage.fillFirstSignatureDate(generator.generateTodaysDate(), indpModel);
		confirmationPage.confirmFirstSignatureDate(generator.generateTodaysDate(), indpModel);
		confirmationPage.goToApplicationSummaryPage(indpModel);

		// Application Summary Page
		summaryPage.waitForLoad(browser);
		Assert.assertTrue("Application Summary page is displayed", summaryPage.isDisplayed(indpModel));

	}
	
	@Then("^the application summary page of the corresponding quotation is displayed$")
	public void verifyApplicationSummaryPageDisplayedForQuotation() throws Throwable {
		
		// Application Summary Page
		verifyOnPage6();
	}
	
	@When("^I have signed my insurance certificate$")
	public void i_have_signed_my_insurance_certificate() throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    throw new PendingException();
	}

	@Then("^I can set the applicant status as Inforce$")
	public void i_can_set_the_applicant_status_as_Inforce() throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    throw new PendingException();
	}
	
	
	@And("^I return to Quotation Page$")
	public void returnToQuotationPage() throws Throwable {
		// Loan page 
		loanPage.returnToQuotationPage(indpModel);
		Assert.assertTrue("Confirm: Quotation Page is displayed", quotationPage.isDisplayed(indpModel));
	}
	
	@And("^I return to Main Page$")
	public void returnToMainPage() throws Throwable {
		loanPage.waitForLoad(browser);
		Thread.sleep(3000);
		loanPage.returnToMainPage(indpModel);
		Assert.assertTrue("Confirm: Main Page is displayed", mainPage.isDisplayed(indpModel));
	}
	
	@And("^I enter \"([^\"]*)\" in the loan duration textbox$")
	public void setValueInLoanDuration(String loanDuration) throws Throwable{
		loanPage.setLoanDuration(loanDuration, indpModel);
	}
	
	@And("^I enter \"([^\"]*)\" in the deferred amortization textbox$")
	public void setValueInDeferredAmortization(String deferredAmortization) throws Throwable{
		loanPage.fillLoan(0, loanAmount, "16", "Accession - Résidence Principale", "Variable",
				"Amortissable", deferredAmortization, "Non", generator.generateName(8), indpModel);
	}
	
	@And("^The picklist next to the loan duration textbox displayed \"([^\"]*)\"$")
	public void getLoanDurationPicklist(String loanDurationPicklistExpected) throws Throwable{
		Assert.assertEquals("Value displayed in the picklist is different as expected", loanDurationPicklistExpected,loanPage.getLoanDurationPicklistValue());
	}
	
	@And("^The picklist next to the deferred amortization textbox displayed \"([^\"]*)\"$")
	public void getDeferredAmortizationPicklist(String deferredAmortizationExpected) throws Throwable{
		Assert.assertEquals("Value displayed in the picklist is different as expected", deferredAmortizationExpected,loanPage.getDeferredAmortizationPicklistValue());
	}
	
	@And("^I modify last name, first name and birthdate for borrower (\\d+)$")
	public void modifyApplicantInformations(int borrowerIndex) throws Throwable {
		// Loan page 
		if (borrowerIndex ==1){
			firstBorrowerLastName = generator.generateName(10);
			firstBorrowerFirstName = generator.generateName(8);
			dateOfBirth = "01/02/1980";
			quotationPage.fillEtatCivil("Monsieur", firstBorrowerLastName, firstBorrowerFirstName, dateOfBirth,
					"Française", indpModel);
		}
		else
		{
			secondBorrowerLastName = generator.generateName(10);
			secondBorrowerFirstName= generator.generateName(9);
			dateOfBirth = "02/02/1980";
			quotationPage.fillSecondBorrowerEtatCivil("Madame", secondBorrowerLastName, secondBorrowerFirstName,
					dateOfBirth, indpModel);
		}
		quotationPage.goToLoanPage(indpModel);
	}
	
	@And("^Loan page is displayed without confirmation message for borrower (\\d+)$")
	public void loanPageIsDisplayedWithoutConfirmationMessage (int borrowerId) throws Throwable {
		Assert.assertTrue("Loan Page not displayed", loanPage.isDisplayed(indpModel));
		if (borrowerId == 1){
	    	Assert.assertNotEquals("Confirmation message displayed ", "× Confirmation L'adhérent " + firstBorrowerLastName + " " + firstBorrowerFirstName + " existe déjà",loanPage.getConfirmationMessage(0, indpModel) );
	    }
	    	else{
	    		Assert.assertNotEquals("Confirmation message displayed ", "× Confirmation L'adhérent " + secondBorrowerLastName + " " + secondBorrowerFirstName + " existe déjà",loanPage.getConfirmationMessage(1, indpModel) );
	    	}
	}
	
	@And("^I click on the link of an existing applicant$")
	public void I_click_on_the_link_of_an_existing_applicant() throws Throwable{
		mainPage.setExistingApplicantLink(indpModel,firstBorrowerFirstName + " " + firstBorrowerLastName);
	}
	
	@And("^I am on the civil status section of my borrower profile$")
	public void I_am_on_the_civil_status() throws Throwable{
		quotationPage.getPoliticallyExposedSection(indpModel);
	}
	
	@And("^The two political exposure questions are displayed at the end of the civil status section for \"([^\"]*)\"$")
	public void checkPoliticalExposureDisplay(String callerPage) throws Throwable{
		quotationPage.checkPoliticallyExposedDisplay(indpModel,callerPage);	
	}
}
