package nts.uk.shr.infra.file.storage.stream;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.error.RawErrorMessage;
import nts.arc.layer.app.file.storage.StoredFileInfo;
import nts.arc.layer.infra.file.storage.StoredFileInfoRepository;
import nts.arc.layer.infra.file.storage.StoredFileStreamService;
import nts.gul.error.FatalLog;
import nts.gul.file.FileUtil;
import nts.gul.security.crypt.commonkey.CommonKeyCrypt;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.file.storage.info.StoredPackInfoRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

@ApplicationScoped
public class DefaultStoredFileStreamService implements StoredFileStreamService {
	
	@Inject
	private StoredFileInfoRepository fileInfoRepository;
	
	@Inject
	private StoredPackInfoRepository packInfoRepository;

	private final FileStoragePath storagePath = new FileStoragePath();

	@Override
	public void store(StoredFileInfo fileInfo, InputStream streamToStore) {

		String tenantCode = AppContexts.user().contractCode();
		try {
			Files.copy(
					CommonKeyCrypt.encrypt(streamToStore, fileInfo.getOriginalSize()),
					pathToTargetStoredFile(fileInfo.getId(), tenantCode));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void storeZipEntry(StoredFileInfo fileInfo, InputStream streamToStore) {

		String tenantCode = AppContexts.user().contractCode();
		val pathToEntry = pathToStoredZipEntry(fileInfo, tenantCode);

		try (val is = CommonKeyCrypt.encrypt(streamToStore, fileInfo.getOriginalSize())){
			Files.createDirectories(pathToEntry.getParent());
			Files.copy(is, pathToEntry);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public InputStream takeOutFromFileId(String fileId) {

		Optional<StoredFileInfo> fileInfo = fileInfoRepository.find(fileId);
		if(!fileInfo.isPresent()){
			throw new BusinessException(new RawErrorMessage("file not found"));
		}
		
		return CommonKeyCrypt.decrypt(
				FileUtil.NoCheck.newInputStream(getExistingPathToTargetStoredFile(fileInfo.get().getId())),
				fileInfo.get().getOriginalSize());
	}
	
	@Override
	public InputStream takeOut(StoredFileInfo fileInfo) {
		Path filePath = null;
		if (fileInfo.isZipEntryFile()) {
			filePath = getExistingPathToStoredZipEntry(fileInfo);
		} else {
			filePath = getExistingPathToTargetStoredFile(fileInfo.getId());
		}
		
		return CommonKeyCrypt.decrypt(
				FileUtil.NoCheck.newInputStream(filePath), fileInfo.getOriginalSize());
	}

	@Override
	public InputStream takeOutDeleteOnClosed(StoredFileInfo fileInfo) {
		Path filePath = null;
		if (fileInfo.isZipEntryFile()) {
			filePath = getExistingPathToStoredZipEntry(fileInfo);
		} else {
			filePath = getExistingPathToTargetStoredFile(fileInfo.getId());
		}
		
		return CommonKeyCrypt.decrypt(
				FileUtil.NoCheck.newInputStream(filePath, StandardOpenOption.DELETE_ON_CLOSE), 
				fileInfo.getOriginalSize());
	}

	@Override
	public void delete(String fileId) {
		try {
			Files.deleteIfExists(getExistingPathToTargetStoredFile(fileId));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * すでに存在するはずのファイルへのパスを返す
	 * @param fileId
	 * @return
	 */
	private Path getExistingPathToTargetStoredFile(String fileId) {

		// テナントフォルダにあればそれを返す
		String tenantCode = AppContexts.user().contractCode();
		val pathByTenant = pathToTargetStoredFile(fileId, tenantCode);
		if (Files.exists(pathByTenant)) {
			return pathByTenant;
		}

		// なければテナントフォルダ対応以前に作られたファイルとみなして、そちらを返す
		return pathToStorage(null).resolve(fileId);
	}

	private Path pathToTargetStoredFile(String fileId, String tenantCode) {
		return pathToStorage(tenantCode).resolve(fileId);
	}

	private Path getExistingPathToStoredZipEntry(StoredFileInfo entryInfo) {

		// テナントフォルダにあればそれを返す
		String tenantCode = AppContexts.user().contractCode();
		val pathByTenant = pathToStoredZipEntry(entryInfo, tenantCode);
		if (Files.exists(pathByTenant)) {
			return pathByTenant;
		}

		// なければテナントフォルダ対応以前に作られたファイルとみなして、そちらを返す
		return pathToStoredZipEntry(entryInfo, null);
	}

	private Path pathToStoredZipEntry(StoredFileInfo entryInfo, String tenantCode) {
		String packId = this.packInfoRepository.getPackId(entryInfo.getId())
				.orElseThrow(() -> new RuntimeException("pack not found"));
		
		String packsDirectory = "packs";
		
		return pathToStorage(tenantCode)
				.resolve(packsDirectory)
				.resolve(packId)
				.resolve(entryInfo.getOriginalName());
	}

	private Path pathToStorage(String tenantCode) {

		// テナントコード別にフォルダを分ける対応をする以前に作った環境でも動作するように・・・
		if (tenantCode == null) {
			return storagePath.getSingleStoragePath();
		}

		val pathByTenant = storagePath.getPath(tenantCode).resolve(tenantCode);

		if (!Files.exists(pathByTenant)) {
			try {
				Files.createDirectory(pathByTenant);
			} catch (IOException e) {
				throw FatalLog.writeThenException(this, "フォルダ作成に失敗：" + pathByTenant);
			}
		}

		return pathByTenant;
	}
}
