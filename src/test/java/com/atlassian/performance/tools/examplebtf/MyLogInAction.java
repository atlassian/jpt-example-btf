package com.atlassian.performance.tools.examplebtf;

import com.atlassian.performance.tools.jiraactions.api.ActionTypes;
import com.atlassian.performance.tools.jiraactions.api.WebJira;
import com.atlassian.performance.tools.jiraactions.api.action.Action;
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter;
import com.atlassian.performance.tools.jiraactions.api.memories.User;
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory;
import com.atlassian.performance.tools.jiraactions.api.page.DashboardPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class MyLogInAction implements Action {

    private final WebJira jira;
    private final ActionMeter meter;
    private final UserMemory users;
    private final By loginFormLocator = By.id("login-form");
    private final WebDriver driver;

    MyLogInAction(WebJira jira, ActionMeter meter, UserMemory users) {
        this.jira = jira;
        this.meter = meter;
        this.users = users;
        driver = jira.getDriver();
    }

    @Override
    public void run() {
        meter.measure(ActionTypes.LOG_IN, () -> {
            navigateToLoginPage();
            waitForLoginForm(Duration.ofSeconds(15));
            submitUserCredentials();
            waitToBeLoggedIn();
            return null;
        });
    }

    private void navigateToLoginPage() {
        jira.navigateTo("login.jsp");
    }

    private void waitForLoginForm(
        Duration patience
    ) {
        WebDriverWait wait = new WebDriverWait(
            driver,
            patience.getSeconds()
        );
        wait.until(presenceOfElementLocated(loginFormLocator));
    }

    private void submitUserCredentials() {
        User user = Objects.requireNonNull(users.recall());
        WebElement loginForm = driver.findElement(loginFormLocator);
        loginForm.findElement(By.name("os_username")).sendKeys(user.getName());
        loginForm.findElement(By.name("os_password")).sendKeys(user.getPassword());
        loginForm.findElement(By.id("login-form-submit")).click();
    }

    private void waitToBeLoggedIn() {
        new DashboardPage(driver).waitForDashboard();
    }
}
