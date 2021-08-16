package com.jinyframework.keva.server.command;

import com.jinyframework.keva.server.ServiceInstance;
import com.jinyframework.keva.server.replication.master.ReplicationService;
import com.jinyframework.keva.server.storage.StorageService;
import com.jinyframework.keva.server.util.ZipUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

@Slf4j
public class FSync implements CommandHandler {
    private final StorageService storageService = ServiceInstance.getStorageService();
    private final ReplicationService replicationService = ServiceInstance.getReplicationService();

    @Override
    public Object handle(List<String> args) {
        // send snapshot to replica
        try {
            final Path snapFolder = storageService.getSnapshotPath();
            final Path zipPath = snapFolder.resolve("data.zip");
            ZipUtil.pack(snapFolder.toString(), zipPath.toString());
            // register replica and start buffering commands to forward
            log.info(String.valueOf(args));
            replicationService.addReplica(args.get(0) + ':' + args.get(1));
            log.info(zipPath.toString());
            return Base64.getEncoder().encodeToString(Files.readAllBytes(zipPath));
        } catch (IOException e) {
            log.error("FSYNC failed: ", e);
            return "null";
        }
    }
}