package ua.knu.backend.validator;

import org.springframework.stereotype.Component;

@Component
public class BlockLoader implements Runnable {
    private Validator validator;

    public BlockLoader(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void run() {
        while (true) {
            validator.loadBlock();
        }
    }
}
