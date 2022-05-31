package ua.knu.backend.validator;

import org.springframework.stereotype.Component;

@Component
public class Miner implements Runnable {
    private final Validator validator;

    public Miner(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void run() {
        while (true) {
            validator.mainRoutine();
        }
    }
}
