package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {

    @Test
    public void methodGetDefaultShouldReturnNotNull() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    public void methodGetDefaultHistoryShouldReturnNotNull() {
        assertNotNull(Managers.getDefaultHistory());
    }

}
