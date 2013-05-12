package com.playtech.proov.services.gamelaunch.impl;


import com.playtech.proov.RemoteServicesFactory;
import com.playtech.proov.ServerApplication;
import com.playtech.proov.annotations.Application;
import com.playtech.proov.http.HttpRequest;
import com.playtech.proov.http.HttpResponse;
import com.playtech.proov.server.ServerRequest;
import com.playtech.proov.server.ServerResponse;
import com.playtech.proov.services.ServicesPool;
import com.playtech.proov.services.wallet.WalletService;
import com.playtech.proov.services.wallet.model.IncrementBalanceResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Entry point to game launch application.
 * <p/>
 * Currently simply knows how to send remote balance update requests
 * based on HTTP request.
 * <p/>
 * Can be entry point a game launch web framework.
 */

@Application
public class GameApplication implements ServerApplication {

    private static final Logger LOG = LoggerFactory.getLogger(GameApplication.class);
    private ServicesPool servicesPool;

    public GameApplication(ServicesPool servicesPool) {
        this.servicesPool = servicesPool;
    }

    @Override
    public void handleRequestResponse(ServerRequest request, ServerResponse response) {
        HttpRequest httpRequest = (HttpRequest) request;
        HttpResponse httpResponse = (HttpResponse) response;

        String userName = request.getServerSession().getAuthenticationContext().getUserName();
        String balanceParam = httpRequest.getParam("balance");

        LOG.debug("Username is {} and balance is {}", userName, balanceParam);
        if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(balanceParam)) {

            BigDecimal balance = BigDecimal.valueOf(Double.parseDouble(balanceParam));
            WalletService walletservice = RemoteServicesFactory.getFactory().getRemoteService(WalletService.class);
            IncrementBalanceResponse balanceResponse = walletservice.incrementBalance(userName, balance);
            httpResponse.write("New balance is " + balanceResponse.getBalance());
        } else {
            httpResponse.write("Invalid request ");
        }

    }


    public static void main(String[] args) {
        WalletService walletservice = RemoteServicesFactory.getFactory().getRemoteService(WalletService.class);
        IncrementBalanceResponse balanceResponse = walletservice.incrementBalance("andrus", BigDecimal.valueOf(10));
        System.out.println(balanceResponse.getBalance());
    }

}