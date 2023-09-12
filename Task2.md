# KeepCode Test Task

## Задание 2: Рефакторинг

### Требования:
Необходимо оптимизировать метод, сделать код более читабельным, не нарушив изначальную логику:

### До:

```java
void process(ChannelHandlerContext channelHandlerContext) {
    InetSocketAddress inetSocketAddress = new InetSocketAddress(getIpAddress(), getUdpPort());

    for (Command command : getAllCommands()) {
        if (command.getCommandType() == CommandType.REBOOT_CHANNEL) {
            if (!command.isAttemptsNumberExhausted()) {
                if (command.isTimeToSend()) {
                    sendCommandToContext(channelHandlerContext, inetSocketAddress, command.getCommandText());
                    try {
                        AdminController.getInstance()
                            .processUssdMessage(new DblIncomeUssdMessage(
                                    inetSocketAddress,
                                    EnumGoip.getByModel(getGoipModel()),
                                    command.getCommandText()
                            ), false);
                     } catch (Exception ignored) {
                     }

                    command.setSendDate(new Date());

                    logger.write("send message : {}", command.getCommandText());

                    command.incSendCounter();
                }
            } else {
                deleteCommand(command.getCommandType());
            }
        } else {
            if (!currentCommand.isAttemptsNumberExhausted()) {
                sendCommandToContext(channelHandlerContext, inetSocketAddress, command.getCommandText());
                try {
                    AdminController.getInstance()
                        .processUssdMessage(new DblIncomeUssdMessage(
                                inetSocketAddress,
                                EnumGoip.getByModel(getGoipModel()),
                                command.getCommandText()
                        ), false);
                } catch (Exception ignored) {
                }
                
                logger.write("send message : {}", command.getCommandText());
                
                command.setSendDate(new Date());
                command.incSendCounter();
            } else {
                CommandType typeToRemove = command.getCommandType();
                deleteCommand(typeToRemove);
            }
        }
    }
    sendKeepAliveOkAndFlush(channelHandlerContext);
}
```


### После:

```java
void sendAllCommandsToContext(ChannelHandlerContext context) {
    var socketAddress = new InetSocketAddress(getIpAddress(), getUdpPort());
    
    for (Command command : getAllCommands()) {
        if (command.isAttemptsNumberExhausted()) {
            deleteCommand(command.getCommandType());
            logger.write("Command #{} was deleted", command.getCommandType());
            continue;
        }
        if (isCommandHasSendRestriction(command)) {
            logger.write("Not able to send #{} due to restrictions", command.getCommandType());
            continue;
        }
        
        sendCommandToContext(context, socketAddress, command.getCommandText());
        
        trySendUssdMessage(socketAddress, command);
             
        command.setSendDate(new Date());
        command.incSendCounter();
    }
    sendKeepAliveOkAndFlush(context);
}

boolean isCommandHasSendRestriction(Command command) {
    return command.getCommandType() == CommandType.REBOOT_CHANNEL && !command.isTimeToSend();
}

void trySendUssdMessage(InetSocketAddress socketAddress, Command command) {
    try {
        AdminController.getInstance()
            .processUssdMessage(new DblIncomeUssdMessage(
                socketAddress,
                EnumGoip.getByModel(getGoipModel()),
                command.getCommandText()
            ), false);
        logger.write("Sent message: {}", command.getCommandText());
    } catch (Exception ignored) {
        logger.warn("Exception while sending ussd message: {}", ignored.getMessage());
    }
}
```