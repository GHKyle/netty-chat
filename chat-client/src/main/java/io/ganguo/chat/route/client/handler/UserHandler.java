package io.ganguo.chat.route.client.handler;

import io.ganguo.chat.route.biz.entity.Message;
import io.ganguo.chat.route.core.connetion.IMConnection;
import io.ganguo.chat.route.core.handler.IMHandler;
import io.ganguo.chat.route.core.protocol.Commands;
import io.ganguo.chat.route.core.protocol.Handlers;
import io.ganguo.chat.route.core.transport.Header;
import io.ganguo.chat.route.core.transport.IMRequest;

import io.ganguo.chat.route.core.transport.IMResponse;
import io.ganguo.chat.route.server.dto.MessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * @author Tony
 * @createAt Feb 17, 2015
 */
public class UserHandler extends IMHandler {

    private Logger logger = LoggerFactory.getLogger(UserHandler.class);

    @Override
    public short getId() {
        return Handlers.USER;
    }

    @Override
    public void dispatch(IMConnection connection, IMRequest request) {
        Header header = request.getHeader();
        switch (header.getCommandId()) {
            case Commands.LOGIN_SUCCESS:
                onLoginSuccess(connection, request);
                break;

            case Commands.LOGIN_CHANNEL_SUCCESS:
                onLoginChannelSuccess(connection, request);
                break;

            case Commands.LOGIN_FAIL:
                onLoginFail(connection, request);
                break;

            case Commands.LOGIN_CHANNEL_FAIL:
                onLoginChannelFail(connection, request);
                break;

            case Commands.LOGIN_CHANNEL_KICKED:
                onKicked(connection, request);
                break;
            default:
                break;
        }
    }

    private void onLoginChannelSuccess(IMConnection connection, IMRequest request) {
        logger.info("onLoginChannelSuccess");
    }

    private void onLoginChannelFail(IMConnection connection, IMRequest request) {
        logger.info("onLoginChannelFail");

        connection.close();
    }

    /**
     * @param connection
     * @param request
     */
    private void onLoginSuccess(final IMConnection connection, IMRequest request) {
        logger.info("onLoginSuccess");

        new Thread() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);

                while (true) {
                    String input[] = scanner.next().split("#");
                    String cmd = input[0];
                    if (cmd.equals("send")) {
                        String to = input[1];
                        String content = input[2];
                        Message message = new Message();
                        message.setTo(Long.parseLong(to));
                        message.setMessage(content);

                        IMResponse resp = new IMResponse();
                        Header header = new Header();
                        header.setHandlerId(Handlers.MESSAGE);
                        header.setCommandId(Commands.USER_MESSAGE_REQUEST);
                        resp.setHeader(header);
                        resp.writeEntity(new MessageDTO(message));
                        connection.sendResponse(resp);
                    }
                }
            }
        }.start();

    }

    /**
     * @param connection
     * @param request
     */
    private void onLoginFail(IMConnection connection, IMRequest request) {
        logger.info("onLoginFail");

        connection.close();
    }

    private void onKicked(IMConnection connection, IMRequest request) {
        logger.info("onKicked");

        connection.close();
    }

}