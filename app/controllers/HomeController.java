package controllers;

import models.Spend;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.concurrent.ClassLoaderExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import repository.SpendRepository;

import javax.inject.Inject;
import jakarta.persistence.PersistenceException;

import static java.util.concurrent.CompletableFuture.completedFuture;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class HomeController extends Controller {

    private final SpendRepository spendRepository;
    private final FormFactory formFactory;
    private final ClassLoaderExecutionContext classLoaderExecutionContext;
    private final MessagesApi messagesApi;

    @Inject
    public HomeController(FormFactory formFactory,
                        SpendRepository spendRepository,
                        ClassLoaderExecutionContext classLoaderExecutionContext,
                        MessagesApi messagesApi) {
        this.spendRepository = spendRepository;
        this.formFactory = formFactory;
        this.classLoaderExecutionContext = classLoaderExecutionContext;
        this.messagesApi = messagesApi;
    }

    private Result GO_HOME = Results.redirect(
        routes.HomeController.list(0, "name", "asc", "")
    );

    public Result index() {
        return GO_HOME;
    }

    public CompletionStage<Result> list(Http.Request request, int page, String sortBy, String order, String filter) {
        return spendRepository.page(page, 10, sortBy, order, filter).thenApplyAsync(list -> {
            return ok(views.html.list.render(list, sortBy, order, filter, request, messagesApi.preferred(request)));
        }, classLoaderExecutionContext.current());
    }

    public Result create(Http.Request request) {
        Form<Spend> spendForm = formFactory.form(Spend.class);
        return ok(views.html.createForm.render(spendForm, request, messagesApi.preferred(request)));
    }

    public CompletionStage<Result> save(Http.Request request) {
        Form<Spend> spendForm = formFactory.form(Spend.class).bindFromRequest(request);
        if (spendForm.hasErrors()) {
            return CompletableFuture.supplyAsync(() -> badRequest(views.html.createForm.render(spendForm, request, messagesApi.preferred(request))));
        }

        Spend spend = spendForm.get();
        return spendRepository.insert(spend).thenApplyAsync(data -> {
            return GO_HOME
                .flashing("success", "Spending " + spend.getName() + " has been created");
        }, classLoaderExecutionContext.current());
    }

    public CompletionStage<Result> edit(Http.Request request,Long id) {
        return spendRepository.lookup(id).thenApplyAsync(spend -> {
            Spend c = spend.get();
            Form<Spend> spendForm = formFactory.form(Spend.class).fill(c);
            return ok(views.html.editForm.render(id, spendForm, request, messagesApi.preferred(request)));
        }, classLoaderExecutionContext.current());
    }

    public CompletionStage<Result> update(Http.Request request, Long id) throws PersistenceException {
        Form<Spend> spendForm = formFactory.form(Spend.class).bindFromRequest(request);
        if (spendForm.hasErrors()) {
            return CompletableFuture.supplyAsync(() -> badRequest(views.html.editForm.render(id, spendForm, request, messagesApi.preferred(request))));            
        } else {
            Spend newSpendData = spendForm.get();
            return spendRepository.update(id, newSpendData).thenApplyAsync(data -> {
                return GO_HOME
                    .flashing("success", "Spend " + newSpendData.getName() + " has been updated");
            }, classLoaderExecutionContext.current());
        }
    }

    public CompletionStage<Result> delete(Long id) {
        return spendRepository.delete(id).thenApplyAsync(v -> {
            return GO_HOME
                .flashing("success", "Spending has been deleted.");
        }, classLoaderExecutionContext.current());
    }
}
