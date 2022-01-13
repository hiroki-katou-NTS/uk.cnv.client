package nts.uk.shr.infra.file.storage.stream;

import lombok.Getter;
import nts.arc.system.ServerSystemProperties;
import nts.gul.error.FatalLog;
import nts.tenantlocator.client.TenantLocatorClient;
import nts.uk.shr.com.context.AppContexts;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

        File dir = new File(path.toString());
        if (!dir.exists()) {
            throw FatalLog.writeThenException(
                    this, "テナント " + tenantCode + " のFileStorageに対応するフォルダ [" + path + "] が存在しません。");
        }

        return path;
    }

    private Path getPathByTenant(String tenantCode) {
        return TenantLocatorClient.getFileStorage(tenantCode)
                .map(f -> f.getStoragePath())
                .map(p -> new File(p).toPath())
                .orElseThrow(() -> FatalLog.writeThenException(
                        this, "テナント " + tenantCode + " のFileStorageが設定されていません。"));
    }
}
