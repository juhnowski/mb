package ru.simplgroupp.rest;

import ru.simplgroupp.rest.api.AccountController;
import ru.simplgroupp.rest.api.AiController;
import ru.simplgroupp.rest.api.AntifraudController;
import ru.simplgroupp.rest.api.AnyPaymentController;
import ru.simplgroupp.rest.api.CallbackController;
import ru.simplgroupp.rest.api.ChangePasswordController;
import ru.simplgroupp.rest.api.DocumentsController;
import ru.simplgroupp.rest.api.FiasController;
import ru.simplgroupp.rest.api.FirstCreditRequestController;
import ru.simplgroupp.rest.api.ListPaymentController;
import ru.simplgroupp.rest.api.ListSumsController;
import ru.simplgroupp.rest.api.NewCreditController;
import ru.simplgroupp.rest.api.OverviewController;
import ru.simplgroupp.rest.api.PaymentController;
import ru.simplgroupp.rest.api.ProlongCreditController;
import ru.simplgroupp.rest.api.ReCaptchaController;
import ru.simplgroupp.rest.api.RefinanceCreditController;
import ru.simplgroupp.rest.api.SocialController;
import ru.simplgroupp.rest.api.ViewBonusController;
import ru.simplgroupp.rest.api.auth.AuthorizationController;
import ru.simplgroupp.rest.api.user.FriendsController;
import ru.simplgroupp.rest.api.user.UserSettingsController;
import ru.simplgroupp.rest.providers.AppExceptionsMapper;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * REST приложение
 */
@ApplicationPath("/rest")
public class MainApplication extends Application {

    private HashSet<Class<?>> classes = new HashSet<Class<?>>();

    public MainApplication() {
        classes.add(AppExceptionsMapper.class);
        classes.add(JacksonConfigurator.class);

        classes.add(CreditRequestWizardPostController.class);
        classes.add(FiasController.class);
        classes.add(ReferenceController.class);
        classes.add(CreditRequestWizardGetController.class);
        classes.add(SocialController.class);
        classes.add(PayonlineVerificationController.class);
        classes.add(OverviewController.class);
        classes.add(UserSettingsController.class);
        classes.add(AuthorizationController.class);
        classes.add(ViewBonusController.class);
        classes.add(NewCreditController.class);
        classes.add(ProlongCreditController.class);
        classes.add(RefinanceCreditController.class);
        classes.add(ListPaymentController.class);
        classes.add(ListSumsController.class);
        classes.add(AnyPaymentController.class);
        classes.add(AiController.class);
        classes.add(ChangePasswordController.class);
        classes.add(FirstCreditRequestController.class);
        classes.add(CallbackController.class);
        classes.add(FriendsController.class);
        classes.add(ReCaptchaController.class);
        classes.add(AntifraudController.class);
        classes.add(DocumentsController.class);
        classes.add(PaymentController.class);
        classes.add(AccountController.class);
    }

    public Set<Class<?>> getClasses() {
        return classes;
    }
}
