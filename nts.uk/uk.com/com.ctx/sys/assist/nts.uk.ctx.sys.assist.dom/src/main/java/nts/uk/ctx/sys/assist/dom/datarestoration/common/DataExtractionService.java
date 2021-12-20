package nts.uk.ctx.sys.assist.dom.datarestoration.common;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.infra.file.storage.StoredFileStreamService;
import nts.arc.system.ServerSystemProperties;
import nts.gul.file.archive.ArchiveFormat;
import nts.gul.file.archive.FileArchiver;
import nts.uk.ctx.sys.assist.dom.datarestoration.ServerPrepareMng;
import nts.uk.ctx.sys.assist.dom.datarestoration.ServerPrepareMngRepository;
import nts.uk.ctx.sys.assist.dom.datarestoration.ServerPrepareOperatingCondition;
import nts.uk.shr.infra.file.storage.stream.FileStoragePath;

@Stateless
public class DataExtractionService {
	@Inject
	private StoredFileStreamService fileStreamService;
	@Inject
	private ServerPrepareMngRepository serverPrepareMngRepository;

	// アルゴリズム「ファイル解凍処理」を実行する
	public ServerPrepareMng extractData(ServerPrepareMng serverPrepareMng) {
		if(! serverPrepareMng.getFileId().isPresent()){ 
			serverPrepareMng.setOperatingCondition(ServerPrepareOperatingCondition.EXTRACTION_FAILED);
			return serverPrepareMng;
		}
		String fileId = serverPrepareMng.getFileId().get();
		InputStream inputStream = this.fileStreamService.takeOutFromFileId(fileId);
		Path destinationDirectory = Paths.get(new FileStoragePath().getPathOfCurrentTenant().toString() + "//packs" + "//" + fileId);
		String password = serverPrepareMng.getPassword().isPresent() ? serverPrepareMng.getPassword().get().v() : null;
		// Update validate status by extract status
		serverPrepareMng = serverPrepareMng.setOperatingConditionBy(FileArchiver.create(ArchiveFormat.ZIP).extract(inputStream, password,destinationDirectory));
		serverPrepareMngRepository.update(serverPrepareMng);
		return serverPrepareMng;
	}
}
