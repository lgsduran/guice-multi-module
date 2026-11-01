package module.tests;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import module.config.providers.ThreadLocalWebDriverProvider;

/**
 * Test class for ThreadLocalWebDriverProvider.
 * This class contains tests to ensure that each thread gets its own WebDriver instance
 * and that the provider correctly manages the lifecycle of WebDriver instances.
 */
public class ThreadLocalWebDriverProviderTest {
    @Test
    public void givesEachThreadItsOwnDriver() throws InterruptedException {
        AtomicInteger createdDrivers = new AtomicInteger();
        ThreadLocalWebDriverProvider provider = new ThreadLocalWebDriverProvider(() -> {
            createdDrivers.incrementAndGet();
            return (WebDriver) Proxy.newProxyInstance(
                    WebDriver.class.getClassLoader(),
                    new Class<?>[] { WebDriver.class },
                    (proxy, method, args) -> null);
        });
        WebDriver[] drivers = new WebDriver[2];
        boolean[] reusedOnSameThread = new boolean[2];

        Thread firstThread = createThread(provider, drivers, reusedOnSameThread, 0);
        Thread secondThread = createThread(provider, drivers, reusedOnSameThread, 1);
        firstThread.start();
        secondThread.start();
        firstThread.join();
        secondThread.join();

        Assert.assertNotSame(drivers[0], drivers[1], "Each thread should get a different driver");
        Assert.assertTrue(reusedOnSameThread[0] && reusedOnSameThread[1],
                "Each thread should reuse its own driver");
        Assert.assertEquals(createdDrivers.get(), 2, "A driver should be created for each thread");
    }

    private static Thread createThread(
            ThreadLocalWebDriverProvider provider,
            WebDriver[] drivers,
            boolean[] reusedOnSameThread,
            int index) {
        return new Thread(() -> {
            drivers[index] = provider.get();
            reusedOnSameThread[index] = provider.get() == drivers[index];
            ThreadLocalWebDriverProvider.quitDriver();
        });
    }
}
