package nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu;

import lombok.val;
import nts.arc.layer.app.file.storage.FileStorage;
import nts.arc.layer.app.file.storage.StoredFileInfo;
import nts.gul.text.StringUtil;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.CreateFlowMenuFileService;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.FixedClassification;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.FlowMenuLayout;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.ImageInformation;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.InputStream;

@Stateless
public class CreateFlowMenuFileServiceImpl implements CreateFlowMenuFileService {

	@Inject
	private FileStorage fileStorage;

	@Override
	public String copyFile(String fileId) {

		val sourceInfoOpt = this.fileStorage.getInfo(fileId);
		val sourceStreamOpt = this.fileStorage.getStream(fileId);
		if (!sourceInfoOpt.isPresent() || !sourceStreamOpt.isPresent()) {
			return "";
		}

		StoredFileInfo sourceInfo = sourceInfoOpt.get();
		InputStream sourceStream = sourceStreamOpt.get();

		StoredFileInfo copiedInfo = this.fileStorage.store(
				sourceStream, sourceInfo.getOriginalName(), sourceInfo.getFileType());

		return copiedInfo.getId();
    }

	@Override
	public void deleteUploadedFiles(FlowMenuLayout layout) {
		layout.getFileAttachmentSettings().forEach(file -> this.deleteFile(file.getFileId()));
		layout.getImageSettings().forEach(image -> {
			ImageInformation imageInfo = image.getImageInformation();
			if (imageInfo.getIsFixed().equals(FixedClassification.RANDOM) && imageInfo.getFileId().isPresent()) {
				this.deleteFile(imageInfo.getFileId().get());
			}
		});
		layout.getMenuSettings().forEach(menu -> {
			if (menu.getImageInformation().isPresent()) {
				ImageInformation imageInfo = menu.getImageInformation().get();
				if (imageInfo.getIsFixed().equals(FixedClassification.RANDOM) && imageInfo.getFileId().isPresent()) {
					this.deleteFile(imageInfo.getFileId().get());
				}
			}
		});
		this.deleteLayout(layout);
	}

	@Override
	public void deleteLayout(FlowMenuLayout layout) {
		this.deleteFile(layout.getFileId());
	}
	
	private void deleteFile(String fileId) {
		if (!StringUtil.isNullOrEmpty(fileId, true)) {
			this.fileStorage.delete(fileId);
		}
	}
}
