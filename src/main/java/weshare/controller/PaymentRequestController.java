package weshare.controller;

import io.javalin.http.Handler;
import org.javamoney.moneta.Money;
import weshare.model.Expense;
import weshare.model.MoneyHelper;
import weshare.model.PaymentRequest;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.Routes;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PaymentRequestController {
    public static final Handler view = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Expense expense = expensesDAO.get(UUID.fromString(context.queryParam("expenseId"))).get();

        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);
        Collection<PaymentRequest> paymentRequests = new ArrayList<>();
        MonetaryAmount total_paymentrequests = MoneyHelper.amountOf(0);

        for (Expense expense1 : expenses){
            for (PaymentRequest paymentRequest : expense1.listOfPaymentRequests()){
                paymentRequests.add(paymentRequest);
                if (!paymentRequest.isPaid()) {
                    total_paymentrequests = total_paymentrequests.add(paymentRequest.getAmountToPay());
                }
            }
        }

        Map<String, Object> viewModel = Map.of("expenses", expense, "total_paymentrequests", total_paymentrequests);
        context.render("requestpayment.html", viewModel);
    };
    public static final Handler submitForm = context -> {

        String email = context.formParamAsClass("email", String.class)
                .check(Objects::nonNull, "Enter an email")
                .get();
        String amount = context.formParamAsClass("amount", String.class)
                .check(Objects::nonNull, "Enter amount")
                .get();
        String dateString = context.formParamAsClass("due_date", String.class)
                .check(Objects::nonNull, "Enter a date")
                .get();
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);

        MonetaryAmount monetaryAmount = Money.of((Number) Integer.parseInt(amount),"ZAR");

        String dateFormatPattern = "dd/MM/yyyy";
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(dateFormatPattern));

        Expense expense = expensesDAO.get(UUID.fromString(context.formParam("expenseId"))).get();
        expense.requestPayment(new Person(email), monetaryAmount,date);

        context.redirect("/requestpayment?expenseId="+ expense.getId());
    };
}
