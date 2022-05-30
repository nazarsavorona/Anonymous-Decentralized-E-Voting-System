package ua.knu.backend.validator;

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
