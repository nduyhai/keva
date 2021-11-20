package dev.keva.server.command.impl.key;

import dev.keva.ioc.annotation.Autowired;
import dev.keva.ioc.annotation.Component;
import dev.keva.protocol.resp.reply.IntegerReply;
import dev.keva.server.command.annotation.CommandImpl;
import dev.keva.server.command.annotation.Execute;
import dev.keva.server.command.annotation.ParamLength;
import dev.keva.server.command.impl.key.manager.ExpirationManager;
import dev.keva.store.KevaDatabase;

import static dev.keva.server.command.annotation.ParamLength.Type.AT_LEAST;

@Component
@CommandImpl("del")
@ParamLength(type = AT_LEAST, value = 1)
public class Del {
    private final KevaDatabase database;
    private final ExpirationManager expirationManager;

    @Autowired
    public Del(KevaDatabase database, ExpirationManager expirationManager) {
        this.database = database;
        this.expirationManager = expirationManager;
    }

    @Execute
    public IntegerReply execute(byte[]... keys) {
        var deleted = 0;
        for (byte[] key : keys) {
            if (database.remove(key)) {
                deleted++;
                expirationManager.clearExpiration(key);
            }
        }
        return new IntegerReply(deleted);
    }
}
