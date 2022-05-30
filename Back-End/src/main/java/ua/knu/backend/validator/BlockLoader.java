package ua.knu.backend.validator;

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
