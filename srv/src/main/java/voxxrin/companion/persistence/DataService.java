package voxxrin.companion.persistence;

import com.google.common.base.Optional;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import restx.jongo.JongoCollection;
import voxxrin.companion.domain.technical.Referenceable;

public abstract class DataService<T extends Referenceable> {

    private final JongoCollection collection;
    private final Class<T> clazz;

    public DataService(JongoCollection collection, Class<T> clazz) {
        this.collection = collection;
        this.clazz = clazz;
    }

    public Iterable<T> findAll() {
        return collection.get().find().as(clazz);
    }

    public Iterable<T> findAll(String query, Object... params) {
        return collection.get().find(query, params).as(clazz);
    }

    public Iterable<T> findAllAndSort(String sorting) {
        return collection.get().find().sort(sorting).as(clazz);
    }

    public Iterable<T> findAllAndSort(String query, String sorting, Object... params) {
        return collection.get().find(query, params).sort(sorting).as(clazz);
    }

    public T find(String query, Object... params) {
        return collection.get().findOne(query, params).as(clazz);
    }

    public Optional<T> findById(String id) {
        return Optional.fromNullable(collection.get().findOne(new ObjectId(id)).as(clazz));
    }

    public T save(T entity) {
        beforeEntitySave(entity);
        collection.get().save(entity);
        return entity;
    }

    protected void beforeEntitySave(T entity) {
        DateTime now = DateTime.now();
        if (entity.getKey() == null) {
            entity.setCreationDate(now);
            entity.setKey(new ObjectId().toString());
        }
        entity.setUpdateDate(now);
    }

    public void removeCrawledEntities(String eventId) {
        collection.get().remove("{ eventId: # }", eventId);
    }

    protected final JongoCollection getCollection() {
        return this.collection;
    }
}
