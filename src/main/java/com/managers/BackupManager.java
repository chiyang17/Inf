package com.managers;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupManager {

    private final List<Path> worldPaths;
    private final Path backupDir;

    public BackupManager(List<Path> worldPaths, Path backupDir) {
        this.worldPaths = worldPaths;
        this.backupDir = backupDir;
    }

    public void backup() throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path zipPath = backupDir.resolve("backup_" + timestamp + ".zip");

        Files.createDirectories(backupDir);

        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(zipPath)))) {
            for (Path worldPath : worldPaths) {
                if (Files.exists(worldPath)) {
                    Files.walk(worldPath).forEach(path -> {
                        try {
                            if (Files.isRegularFile(path)) {
                                String zipEntryName = worldPath.getFileName() + "/" + worldPath.relativize(path).toString().replace("\\", "/");
                                zos.putNextEntry(new ZipEntry(zipEntryName));
                                Files.copy(path, zos);
                                zos.closeEntry();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }

        System.out.println("✅ 所有世界已备份至压缩包: " + zipPath.toAbsolutePath());
    }
}
