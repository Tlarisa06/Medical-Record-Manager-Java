package medicalrecord.exceptions;

public class MyException extends Exception {
    public MyException() {
        super("A apărut o eroare personalizată!");
    }

    public MyException(String message) {
        super(message);
    }

    public MyException(String message, Throwable cause) {
        super(message, cause);
    }
}

