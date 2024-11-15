package weshare.server;

import weshare.controller.*;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public class Routes {
    public static final String LOGIN_PAGE = "/";
    public static final String LOGOUT = "/logout";
    public static final String EXPENSES = "/expenses";
    public static final String NEWEXPENSES = "/newexpense";
    public static final String PAYMENT_ACTION = "/payment.action";
    public static final String LOGIN_ACTION = "/login.action";
    public static final String PAYMENTREQUEST = "/requestpayment";
    public static final String NEWEXPENSE_ACTION = "/expenses.action";
    public static final String PAYMENTREQUEST_SENT = "/paymentrequests_sent";
    public static final String PAYMENTREQUEST_ACTION = "/requestpayment.action";
    public static final String PAYMENTREQUEST_RECEIVED = "/paymentrequests_received";
    public static final String PAYMENTREQUEST_RECEIVED_form = "/paymentrequests_received.form";

    public static void configure(WeShareServer server) {
        server.routes(() -> {
            post(LOGIN_ACTION,  PersonController.login);
            get(LOGOUT,         PersonController.logout);
            get(NEWEXPENSES,    NewExpensesController.view);
            get(EXPENSES,       ExpensesController.view);
            post(PAYMENT_ACTION,PaymentController.submitForm);
            get(PAYMENTREQUEST, PaymentRequestController.view);
            post(NEWEXPENSE_ACTION, NewExpensesController.submitForm);
            get(PAYMENTREQUEST_SENT,PaymentRequestSentController.view);
            post(PAYMENTREQUEST_ACTION, PaymentRequestController.submitForm);
            get(PAYMENTREQUEST_RECEIVED, PaymentRequestReceivedController.view);
            post(PAYMENTREQUEST_RECEIVED_form, PaymentRequestReceivedController.submitForm);
        });
    }
}
