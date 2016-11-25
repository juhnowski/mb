package ru.simplgroupp.rest.api;

import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.spi.validation.ValidateRequest;
import ru.simplgroupp.client.common.facade.AccountAlreadyExistException;
import ru.simplgroupp.client.common.facade.AccountFacade;
import ru.simplgroupp.client.common.facade.impl.YandexWalletNotIdentifiedException;
import ru.simplgroupp.client.common.facade.payment.InvalidCardException;
import ru.simplgroupp.exception.ActionException;
import ru.simplgroupp.rest.api.data.BankAccountRequest;
import ru.simplgroupp.rest.api.data.SimpleAccountRequest;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.util.ErrorKeys;

import javax.ejb.ObjectNotFoundException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.naming.AuthenticationException;
import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Account controller
 */
@RequestScoped
@Path("/account")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class AccountController {


    private AccountFacade accountFacade;

    public AccountController() {
    }

    @Inject
    public AccountController(AccountFacade accountFacade) {
        this.accountFacade = accountFacade;
    }

    @POST
    @Path("/card")
    public Response addCard(SimpleAccountRequest request) {
        try {
            accountFacade.createCardAccount(Convertor.fromMask(request.getNumber()));
        } catch (AuthenticationException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (ObjectNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (AccountAlreadyExistException e) {
            return Response.status(Response.Status.CONFLICT).build();
        } catch (InvalidCardException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().build();
    }

    @POST
    @Path("/bank")
    @ValidateRequest
    public Response addBankAccount(@Valid BankAccountRequest request) {
        try {
            accountFacade.createBankAccount(request.getBankBik(), request.getBankName(), request.getBankKPP(),
                    request.getBankCorAccount(), request.getAccount());
        } catch (AuthenticationException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (ObjectNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (AccountAlreadyExistException e) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.ok().build();
    }

    @POST
    @Path("/yandex")
    public Response addYandexWallet(SimpleAccountRequest request) {
        try {
            YandexAccount yandexAccount = new YandexAccount(request.getNumber());
            accountFacade.createYandexAccount(yandexAccount.getAccount());
        } catch (YandexWalletNotIdentifiedException | IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (ActionException e) {
            if (e.getInfo().getCode() == ErrorKeys.INVALID_ACCOUNT) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (AuthenticationException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (ObjectNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (AccountAlreadyExistException e) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.ok().build();
    }
}
