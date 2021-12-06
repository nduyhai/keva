package dev.keva.server.command.impl.string;

import dev.keva.ioc.annotation.Autowired;
import dev.keva.ioc.annotation.Component;
import dev.keva.protocol.resp.reply.BulkReply;
import dev.keva.server.command.annotation.CommandImpl;
import dev.keva.server.command.annotation.Execute;
import dev.keva.server.command.annotation.ParamLength;
import dev.keva.server.command.impl.key.manager.ExpirationManager;
import dev.keva.store.KevaDatabase;
import lombok.val;

import static dev.keva.server.command.annotation.ParamLength.Type.EXACT;

@Component
@CommandImpl("getset")
@ParamLength(type = EXACT, value = 2)
public class GetSet {
    private final KevaDatabase database;
    private final ExpirationManager expirationManager;

    @Autowired
    public GetSet(KevaDatabase database, ExpirationManager expirationManager) {
        this.database = database;
        this.expirationManager = expirationManager;
    }

    @Execute
    public BulkReply execute(byte[] key, byte[] val) {
        boolean isExpired = expirationManager.isExpirable(key);
        if (isExpired) {
            database.put(key, val);
            expirationManager.clearExpiration(key);
            return BulkReply.NIL_REPLY;
        } else {
            val got = database.get(key);
            database.put(key, val);
            expirationManager.clearExpiration(key);
            return got == null ? BulkReply.NIL_REPLY : new BulkReply(got);
        }
    }
}
