package ua.knu.backend.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MemPoolManager implements Runnable {
    private final Validator validator;

    public MemPoolManager(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void run() {
        while (true) {
            try {
                validator.addTransaction();
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
                break;
            }
        }
    }
}
