package test.com.automation.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzerForTests implements IRetryAnalyzer {

    int retryCount = 0;

    @Override
    public boolean retry(ITestResult iTestResult) {

        if(retryCount > 0) {
            retryCount --;
            return true;
        }

        return false;
    }
}
