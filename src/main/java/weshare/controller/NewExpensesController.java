package weshare.controller;

import io.javalin.http.Handler;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.jetbrains.annotations.NotNull;
import weshare.model.Expense;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.Routes;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static weshare.model.MoneyHelper.ZERO_RANDS;

public class NewExpensesController {
    public static final Handler view = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);
        Map<String, Object> viewModel = Map.of("expenses", expenses);
        context.render("newexpense.html", viewModel);
    };

    public static final Handler submitForm = context -> {

        String description = context.formParamAsClass("description", String.class)
                .check(Objects::nonNull, "Enter a description")
                .get();
        String dateString = context.formParamAsClass("date", String.class)
                .check(Objects::nonNull, "Enter a date")
                .get();
        String amount = context.formParamAsClass("amount", String.class)
                .check(Objects::nonNull, "Enter amount")
                .get();

        String dateFormatPattern = "dd/MM/yyyy";
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(dateFormatPattern));

        MonetaryAmount monetaryAmount = Money.of((Number) Integer.parseInt(amount),"ZAR");

        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Expense newExpense = new Expense(personLoggedIn, description, monetaryAmount, date);
        expensesDAO.save(newExpense);
        context.redirect(Routes.EXPENSES);
    };
}
