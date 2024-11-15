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

public class ExpensesController {

    public static final Handler view = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);
        MonetaryAmount total_expenses = MoneyHelper.amountOf(0);
        Collection<Expense> exense2 = new ArrayList<>();

        for (Expense expense : expenses){
            if (!expense.isFullyPaidByOthers()){
                exense2.add((expense));
                total_expenses = total_expenses.add(expense.amountLessPaymentsReceived());

            }
        }

        Map<String, Object> viewModel = Map.of("expenses", exense2, "total_expenses", total_expenses);
        context.render("expenses.html", viewModel);
    };
}
