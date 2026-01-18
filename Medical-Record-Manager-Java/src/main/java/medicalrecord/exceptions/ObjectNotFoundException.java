package medicalrecord.exceptions;

public class ObjectNotFoundException extends RepositoryException {
    public ObjectNotFoundException(int id) {
        super("Nu exista obiect cu ID=" + id);
    }
}

