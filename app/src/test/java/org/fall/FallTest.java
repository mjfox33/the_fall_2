
/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.fall;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    @Test
    void appHasAGreeting() {
        App classUnderTest = new App();
        assertNotNull(classUnderTest.getGreeting(), "app should have a greeting");
    }

    @Test
    void brokenWell() {
        Player fallTest = new Player();
        Player.main();
    }
}
