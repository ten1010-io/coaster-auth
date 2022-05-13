package io.ten1010.coaster.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles({"test-db", "in-memory-rsa-key-source", "local-oauth2", "allow-any-redirect-uris", "dummy-client-secret"})
public class GeneralPurposeLiveTest {

    @Test
    void run() throws InterruptedException {
        Object lock = new Object();
        synchronized (lock) {
            lock.wait();
        }
    }

}
