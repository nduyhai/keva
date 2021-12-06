package dev.keva.server.command.impl.hash;

import dev.keva.ioc.annotation.Autowired;
import dev.keva.ioc.annotation.Component;
import dev.keva.protocol.resp.reply.IntegerReply;
import dev.keva.server.command.annotation.CommandImpl;
import dev.keva.server.command.annotation.Execute;
import dev.keva.server.command.annotation.ParamLength;
import dev.keva.store.KevaDatabase;
import lombok.val;

@Component
@CommandImpl("hexists")
@ParamLength(2)
public class HExists {
    private final KevaDatabase database;

    @Autowired
    public HExists(KevaDatabase database) {
        this.database = database;
    }

    @Execute
    public IntegerReply execute(byte[] key, byte[] field) {
        val got = database.hget(key, field);
        return got == null ? new IntegerReply(0) : new IntegerReply(1);
    }
}