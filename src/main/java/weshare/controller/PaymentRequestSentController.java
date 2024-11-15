package weshare.controller;

import io.javalin.http.Handler;
import weshare.model.Expense;
import weshare.model.MoneyHelper;
import weshare.model.PaymentRequest;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class PaymentRequestSentController {
    public static final Handler view = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);
        Collection<PaymentRequest> paymentRequests = new ArrayList<>();
        MonetaryAmount total_paymentrequests_sent = MoneyHelper.amountOf(0);

        for (Expense expense : expenses){
            for (PaymentRequest paymentRequest : expense.listOfPaymentRequests()){
                paymentRequests.add(paymentRequest);
                total_paymentrequests_sent = total_paymentrequests_sent.add(paymentRequest.getAmountToPay());
            }
        }

        Map<String, Object> viewModel = Map.of("paymentrequests_sent", paymentRequests, "total_paymentrequests_sent",total_paymentrequests_sent);
        context.render("paymentrequests_sent.html", viewModel);
    };
}
