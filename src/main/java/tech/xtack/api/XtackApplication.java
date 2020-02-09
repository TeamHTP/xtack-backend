package tech.xtack.api;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import tech.xtack.api.auth.XtackAuthenticator;
import tech.xtack.api.auth.XtackAuthorizer;
import tech.xtack.api.model.Account;
import tech.xtack.api.resource.*;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.EnumSet;

public class XtackApplication extends Application<XtackConfiguration> {
    public static void main(String[] args) throws Exception {
        System.setProperty("dw.server.applicationConnectors[0].port", System.getenv("PORT"));
        new XtackApplication().run(args);
    }

    @Override
    public String getName() {
        return "xtack";
    }

    @Override
    public void initialize(Bootstrap<XtackConfiguration> bootstrap) {
        // TODO

    }

    public void run(XtackConfiguration xtackConfiguration, Environment environment) throws URISyntaxException, SQLException {
        Database database = new Database();

        // CORS
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
        cors.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, "false");

        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        // Auth chain
        environment.jersey().register(new AuthDynamicFeature(
                new OAuthCredentialAuthFilter.Builder<Account>()
                        .setAuthenticator(new XtackAuthenticator(database))
                        .setAuthorizer(new XtackAuthorizer())
                        .setPrefix("Bearer")
                        .buildAuthFilter()));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Account.class));

        // Resources
        final AccountResource accountResource = new AccountResource(database);
        environment.jersey().register(accountResource);
        final CreateAccountResource createAccountResource = new CreateAccountResource(database);
        environment.jersey().register(createAccountResource);
        final QuestionResource questionResource = new QuestionResource(database);
        environment.jersey().register(questionResource);
        final AuthResource authResource = new AuthResource(database);
        environment.jersey().register(authResource);
        final WalletResource walletResource = new WalletResource();
        environment.jersey().register(walletResource);
        final AnswerResource answerResource = new AnswerResource(database);
        environment.jersey().register(answerResource);
        final CreateAnswerResource createAnswerResource = new CreateAnswerResource(database);
        environment.jersey().register(createAnswerResource);
        final CreateQuestionResource createQuestionResource = new CreateQuestionResource(database);
        environment.jersey().register(createQuestionResource);
        final AcceptAnswerResource acceptAnswerResource = new AcceptAnswerResource(database);
        environment.jersey().register(acceptAnswerResource);
    }
}
