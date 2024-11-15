package weshare.controller;

import io.javalin.http.Handler;
import org.javamoney.moneta.Money;
import weshare.model.Expense;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.Routes;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class PaymentController {
    public static final Handler submitForm = context -> {

        String expenseId = context.queryParamAsClass("expenseId", String.class)
                .check(Objects::nonNull, "Expense UUID")
                .get();
        String description = context.formParamAsClass("description", String.class)
                .check(Objects::nonNull, "Enter a description")
                .get();
        String dateString = context.formParamAsClass("date", String.class)
                .check(Objects::nonNull, "Enter a date")
                .get();
        String amount = context.formParamAsClass("amount", String.class)
                .check(Objects::nonNull, "Enter amount")
                .get();

        String dateFormatPattern = "yyyy-MM-dd";
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(dateFormatPattern));

        String[] parts = amount.split(" ");
        String numericalValue = parts[1];
        int amount1 = (int) (Double.parseDouble(numericalValue));
        MonetaryAmount monetaryAmount = Money.of((Number) Integer.parseInt(String.valueOf(amount1)),"ZAR");

        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);
        Expense expense = expensesDAO.get(UUID.fromString(context.formParam("expenseId"))).get();
        Expense newExpense = new Expense(personLoggedIn, description, monetaryAmount, date);
        expensesDAO.save(newExpense);
        context.redirect(Routes.PAYMENTREQUEST_RECEIVED);
    };

}
