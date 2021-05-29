package com.udacity.jwdnd.course1.cloudstorage;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SignupPage {
    @FindBy(id = "inputFirstName")
    private WebElement inputFirstName;

    @FindBy(id = "inputLastName")
    private WebElement inputLastName;

    @FindBy(id = "inputUsername")
    private WebElement inputUsername;

    @FindBy(id = "inputPassword")
    private WebElement inputPassword;

    @FindBy(id = "submit-button")
    private WebElement submitButton;

    private final JavascriptExecutor js;

    public SignupPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        js = (JavascriptExecutor) driver;
    }

    public void signup(String firstName, String lastName, String username, String password) {
        js.executeScript("arguments[0].value='"+ firstName +"';", inputFirstName);
        js.executeScript("arguments[0].value='"+ lastName +"';", inputLastName);
        js.executeScript("arguments[0].value='"+ username +"';", inputUsername);
        js.executeScript("arguments[0].value='"+ password +"';", inputPassword);
        js.executeScript("arguments[0].click();", submitButton);
    }
}
