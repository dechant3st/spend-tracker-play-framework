package repository;

import io.ebean.DB;
import io.ebean.Model;
import io.ebean.PagedList;
import io.ebean.Transaction;
import models.Spend;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class SpendRepository {
    
    private final DatabaseExecutionContext executionContext;

    @Inject
    public SpendRepository(DatabaseExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public CompletionStage<Optional<Spend>> lookup(Long id) {
        return supplyAsync(() -> DB.find(Spend.class).setId(id).findOneOrEmpty(), executionContext);
    }

    public CompletionStage<PagedList<Spend>> page(int page, int pageSize, String sortBy, String order, String filter) {
        return supplyAsync(() ->
                DB.find(Spend.class)
                    .where()
                    .ilike("name", "%" + filter + "%")
                    .orderBy(sortBy + " " + order)
                    .setFirstRow(page * pageSize)
                    .setMaxRows(pageSize)
                    .findPagedList(), executionContext);
    }

    public CompletionStage<Long> insert(Spend spend) {
        return supplyAsync(() -> {
            spend.setId(System.currentTimeMillis());
            DB.insert(spend);
            return spend.getId();
        }, executionContext);
    }

    public CompletionStage<Optional<Long>> update(Long id, Spend newSpendData) {
        return supplyAsync(() -> {
            Transaction txn = DB.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                Spend savedSpend = DB.find(Spend.class).setId(id).findOne();
                if (savedSpend != null) {
                    savedSpend.update(newSpendData);
                    txn.commit();
                    value = Optional.of(id);
                }
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }

    public CompletionStage<Optional<Long>> delete(Long id) {
        return supplyAsync(() -> {
            try {
                Optional<Spend> spendOptional = DB.find(Spend.class).setId(id).findOneOrEmpty();
                spendOptional.ifPresent(Model::delete);
                return spendOptional.map(c -> c.getId());
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }
}
