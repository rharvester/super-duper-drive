package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {

	protected WebDriver driver;
	protected String baseURL;

	@LocalServerPort
	protected int port;

	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	public void beforeEach() {
		this.driver = new ChromeDriver();
		baseURL = "http://localhost:" + port;

		driver.get(baseURL + "/signup");
		SignupPage signupPage = new SignupPage(driver);
		signupPage.signup("John", "Doe", "doej", "abc123");


		driver.get(baseURL + "/login");
		LoginPage loginPage = new LoginPage(driver);
		loginPage.login("doej", "abc123");
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.quit();
		}
	}


	@Test
	public void testUnauthorizedUserAccess(){
		HomePage homePage = new HomePage(driver);
		homePage.logout();

		driver.get(baseURL + "/home");

		assertEquals(baseURL + "/login", driver.getCurrentUrl());

		driver.get(baseURL + "/result");

		assertEquals(baseURL + "/login", driver.getCurrentUrl());
	}

	@Test
	public void testUserSignupAndLoginAndLogout() {
		driver.get(baseURL + "/signup");
		Assertions.assertEquals("Sign Up", driver.getTitle());

		SignupPage signupPage = new SignupPage(driver);
		signupPage.signup("John", "Doe", "doej", "abc123");

		driver.get(baseURL + "/login");
		Assertions.assertEquals("Login", driver.getTitle());

		LoginPage loginPage = new LoginPage(driver);
		loginPage.login("doej", "abc123");

		HomePage homePage = new HomePage(driver);
		homePage.logout();

		driver.get(baseURL + "/home");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		Assertions.assertEquals("Login", driver.getTitle());

	}

	@Test
	public void testNoteCreationAndDisplay() {
		HomePage homePage = new HomePage(driver);

		String noteTitle = "My Note";
		String noteDescription = "This is my note.";

		createNote(noteTitle, noteDescription, homePage);
		homePage.navToNotesTab();

		Note note = homePage.getFirstNote();
		Assertions.assertEquals(noteTitle, note.getNoteTitle());
		Assertions.assertEquals(noteDescription, note.getNoteDescription());

		deleteNote(homePage);
		homePage.logout();
	}

	@Test
	public void testNoteModificationAndDisplay() {
		HomePage homePage = new HomePage(driver);

		String noteTitle = "Test";
		String noteDescription = "Testing 1..2..3...";

		createNote(noteTitle, noteDescription, homePage);
		homePage.navToNotesTab();
		homePage.editNote();

		String modifiedNoteTitle = "Note Changed";
		homePage.modifyNoteTitle(modifiedNoteTitle);

		String modifiedNoteDescription = "This note had been modified";
		homePage.modifyNoteDescription(modifiedNoteDescription);

		homePage.saveNoteChanges();

		ResultPage resultPage = new ResultPage(driver);
		resultPage.clickOk();
		homePage.navToNotesTab();

		Note note = homePage.getFirstNote();
		Assertions.assertEquals(modifiedNoteTitle, note.getNoteTitle());
		Assertions.assertEquals(modifiedNoteDescription, note.getNoteDescription());

		deleteNote(homePage);
		homePage.logout();
	}

	@Test
	public void testNoteDeletion() {
		HomePage homePage = new HomePage(driver);

		String noteTitle = "Test";
		String noteDescription = "Testing 1..2..3...";

		createNote(noteTitle, noteDescription, homePage);
		homePage.navToNotesTab();

		Assertions.assertFalse(homePage.noNotes(driver));

		deleteNote(homePage);

		Assertions.assertTrue(homePage.noNotes(driver));
	}



	@Test
	public void testCredentialCreationAndDisplay() {
		HomePage homePage = new HomePage(driver);
		createAndVerifyCredential("www.abc.com", "abc", "123", homePage);
		homePage.deleteCredential();
		ResultPage resultPage = new ResultPage(driver);
		resultPage.clickOk();
		homePage.logout();
	}


	@Test
	public void testCredentialModificationAndDisplay() {
		HomePage homePage = new HomePage(driver);
		createAndVerifyCredential("www.abc.com", "abc", "123", homePage);
		Credential originalCredential = homePage.getFirstCredential();
		String firstEncryptedPassword = originalCredential.getPassword();
		homePage.editCredential();
		setCredentialFields("www.change.com", "change", "me", homePage);
		homePage.saveCredentialChanges();
		ResultPage resultPage = new ResultPage(driver);
		resultPage.clickOk();
		homePage.navToCredentialsTab();
		Credential modifiedCredential = homePage.getFirstCredential();
		Assertions.assertEquals("www.change.com", modifiedCredential.getUrl());
		Assertions.assertEquals("change", modifiedCredential.getUserName());
		String modifiedCredentialPassword = modifiedCredential.getPassword();
		Assertions.assertNotEquals("me", modifiedCredentialPassword);
		Assertions.assertNotEquals(firstEncryptedPassword, modifiedCredentialPassword);
		homePage.deleteCredential();
		resultPage.clickOk();
		homePage.logout();
	}


	@Test
	public void testCredentialDeletion() {
		HomePage homePage = new HomePage(driver);
		createCredential("www.abc.com", "abc", "123", homePage);
		createCredential("www.def.org", "user1", "test", homePage);
		Assertions.assertFalse(homePage.noCredentials(driver));
		homePage.deleteCredential();
		ResultPage resultPage = new ResultPage(driver);
		resultPage.clickOk();
		homePage.navToCredentialsTab();
		homePage.deleteCredential();
		resultPage.clickOk();
		homePage.navToCredentialsTab();
		Assertions.assertTrue(homePage.noCredentials(driver));
		homePage.logout();
	}

	private void deleteNote(HomePage homePage) {
		homePage.deleteNote();
		ResultPage resultPage = new ResultPage(driver);
		resultPage.clickOk();
	}


	private void createNote(String noteTitle, String noteDescription, HomePage homePage) {
		homePage.navToNotesTab();
		homePage.addNewNote();
		homePage.setNoteTitle(noteTitle);
		homePage.setNoteDescription(noteDescription);
		homePage.saveNoteChanges();
		ResultPage resultPage = new ResultPage(driver);
		resultPage.clickOk();
		homePage.navToNotesTab();
	}

	private void createCredential(String url, String username, String password, HomePage homePage) {
		homePage.navToCredentialsTab();
		homePage.addNewCredential();
		setCredentialFields(url, username, password, homePage);
		homePage.saveCredentialChanges();
		ResultPage resultPage = new ResultPage(driver);
		resultPage.clickOk();
		homePage.navToCredentialsTab();
	}

	private void setCredentialFields(String url, String username, String password, HomePage homePage) {
		homePage.setCredentialUrl(url);
		homePage.setCredentialUsername(username);
		homePage.setCredentialPassword(password);
	}

	private void createAndVerifyCredential(String url, String username, String password, HomePage homePage) {
		createCredential(url, username, password, homePage);
		homePage.navToCredentialsTab();
		Credential credential = homePage.getFirstCredential();
		Assertions.assertEquals(url, credential.getUrl());
		Assertions.assertEquals(username, credential.getUserName());
		Assertions.assertNotEquals(password, credential.getPassword());
	}
}
