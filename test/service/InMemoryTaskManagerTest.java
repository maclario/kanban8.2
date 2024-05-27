package service;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
        super.beforeEach();
    }

}
