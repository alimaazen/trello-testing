package utils;

import org.testng.ITestListener;
import org.testng.ITestResult;

public class DemoDelayListener implements ITestListener {

    private static final int DEMO_DELAY_SECONDS = 5;

    @Override
    public void onTestSuccess(ITestResult result) {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("✅ TEST COMPLETED: " + result.getMethod().getMethodName());
        System.out.println("⏳ WAITING 2 SECONDS FOR DEMO...");
        System.out.println("==============================================");

        waitForDemo();
    }

    @Override
    public void onTestFailure(ITestResult result) {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("❌ TEST FAILED: " + result.getMethod().getMethodName());
        System.out.println("⏳ WAITING 2 SECONDS FOR DEMO...");
        System.out.println("==============================================");

        waitForDemo();
    }

    private void waitForDemo() {

        try {
            for (int remaining = DEMO_DELAY_SECONDS; remaining > 0; remaining--) {

                System.out.println(
                        "Next test starts in " + remaining + " seconds..."
                );

                Thread.sleep(1000);
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println(
                    "Demo wait interrupted. Continuing to next test."
            );
        }
    }
}