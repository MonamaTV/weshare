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

import static weshare.model.DateHelper.TODAY;

public class PaymentRequestReceivedController {
    public static final Handler view = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<PaymentRequest> paymentRequestsReceived = expensesDAO.findPaymentRequestsReceived(personLoggedIn);
        Collection<PaymentRequest> paymentRequests = new ArrayList<>();
        MonetaryAmount total_paymentrequests_received = MoneyHelper.amountOf(0);

        for (PaymentRequest paymentRequest : paymentRequestsReceived){
            paymentRequests.add(paymentRequest);
            total_paymentrequests_received = total_paymentrequests_received.add(paymentRequest.getAmountToPay());
        }

        Map<String, Object> viewModel = Map.of("paymentrequests_received", paymentRequests, "total_paymentrequests_received", total_paymentrequests_received);
        context.render("paymentrequests_received.html", viewModel);
    };
    public static final Handler submitForm = context -> {

        String description = context.formParamAsClass("description", String.class)
                .check(Objects::nonNull, "Enter a description")
                .get();
        String dateString = context.formParam("date");
        String amount = context.formParam("amount");

        LocalDate date = LocalDate.parse(dateString);
        String[] parts = amount.split(" ");
        String numericalValue = parts[1];
        int amount1 = (int) (Double.parseDouble(numericalValue));
        MonetaryAmount monetaryAmount = Money.of((Number) Integer.parseInt(String.valueOf(amount1)),"ZAR");

        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);


        Expense expense = expensesDAO.get(UUID.fromString(context.formParam("paymentId"))).get();
        Collection<PaymentRequest> request = expense.listOfPaymentRequests();

        for (PaymentRequest paymentRequest : request){
            if (paymentRequest.getId().equals(UUID.fromString(context.formParam("id")))){
                paymentRequest.pay(personLoggedIn,TODAY);
            }

        }
//        expense.payPaymentRequest(UUID.fromString(context.formParam("paymentId")),personLoggedIn, date);

        Expense newExpense = new Expense(personLoggedIn, description, monetaryAmount, date);
        expensesDAO.save(newExpense);
        context.redirect(Routes.PAYMENTREQUEST_RECEIVED);
    };
}
