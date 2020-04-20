package orm;

public interface EntityManager<T> {

    static <T> EntityManager<T> of(Class<T> clss) {
        return new EntityMangerImpl<>();
    }

    void persist(T t);
}
