package nts.uk.shr.infra.file.storage.stream;

import lombok.Getter;
import nts.arc.system.ServerSystemProperties;
import nts.gul.error.FatalLog;
import nts.tenantlocator.client.TenantLocatorClient;
import nts.uk.shr.com.context.AppContexts;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * ファイルストレージのパスを扱う
 * 必要に応じてTenantLocatorに問い合わせることも隠蔽
 */
public class FileStoragePath {

    /** true: テナントごとにストレージが異なるモード */
    private final boolean isTenantStoragesMode;

    /** 単一ストレージであればそのパスを持つ、テナントごとならばnull */
    @Getter
    private final Path singleStoragePath;

    public FileStoragePath() {
        String param = ServerSystemProperties.fileStoragePath();
        this.isTenantStoragesMode = "tenant".equals(param);
        this.singleStoragePath = this.isTenantStoragesMode ? null : new File(param).toPath();
    }

    /**
     * ログインしているテナントのファイルストレージパスを取得する
     * @return
     */
    public Path getPathOfCurrentTenant(){
        return getPath(AppContexts.user().contractCode());
    }

    /**
     * ファイルストレージのパスを取得する
     * @param tenantCode
     * @return
     */
    public Path getPath(String tenantCode) {

        if (!isTenantStoragesMode) {
            return singleStoragePath;
        }

        Path path = getPathByTenant(tenantCode);
        if (path == null) {
            FatalLog.write(FileStoragePath.class, "テナント " + tenantCode + " のFileStorageが設定されていません。");
        }

        File dir = new File(path.toString());
        if (!dir.exists()) {
            FatalLog.write(FileStoragePath.class, "テナント " + tenantCode + " のFileStorageに対応するフォルダが存在しません。");
        }

        return path;
    }

    private final Map<String, Path> pathsByTenant = new HashMap<>();

    private Path getPathByTenant(String tenantCode) {

        return pathsByTenant.computeIfAbsent(
                tenantCode,
                tc -> TenantLocatorClient.getFileStorage(tc)
                        .map(f -> f.getStoragePath())
                        .map(p -> new File(p).toPath())
                        .orElse(null));
    }
}
