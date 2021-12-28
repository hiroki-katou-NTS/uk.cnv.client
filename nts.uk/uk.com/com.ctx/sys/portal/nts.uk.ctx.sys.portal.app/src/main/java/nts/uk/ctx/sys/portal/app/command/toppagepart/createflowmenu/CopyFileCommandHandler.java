package nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.sys.portal.app.screenquery.topppagepart.createflowmenu.CreateFlowMenuDto;
import nts.uk.ctx.sys.portal.app.screenquery.topppagepart.createflowmenu.ExtractionResponseDto;
import nts.uk.ctx.sys.portal.dom.toppagepart.TopPagePartCode;
import nts.uk.ctx.sys.portal.dom.toppagepart.TopPagePartName;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.CreateFlowMenu;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.CreateFlowMenuFileService;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.CreateFlowMenuRepository;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.FileAttachmentSetting;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.FixedClassification;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.FlowMenuLayout;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.ImageInformation;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.ImageSetting;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.MenuSetting;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CopyFileCommandHandler extends CommandHandlerWithResult<CopyFileCommand, CopyFileResultDto> {

	@Inject
	private CreateFlowMenuRepository createFlowMenuRepository;

	@Inject
	private CreateFlowMenuFileService createFlowMenuFileService;

	@Inject
	private FileExportService fileExportService;

	@Override
	protected CopyFileResultDto handle(CommandHandlerContext<CopyFileCommand> context) {
		CopyFileCommand command = context.getCommand();
		String flowMenuCode = command.getFlowMenuCode();
		// Get フローメニュー作成
		Optional<CreateFlowMenu> optCreateFlowMenu = this.createFlowMenuRepository
				.findByPk(AppContexts.user().companyId(), flowMenuCode);
		if (!optCreateFlowMenu.isPresent()) {
			return null;
		}

		CreateFlowMenu createFlowMenu = optCreateFlowMenu.get();
		if (!createFlowMenu.getFlowMenuLayout().isPresent()) {
			return null;
		}
		// If フローメニュー作成 != null
		createFlowMenu.setFlowMenuCode(new TopPagePartCode(command.getNewFlowMenuCode()));
		createFlowMenu.setFlowMenuName(new TopPagePartName(command.getFlowMenuName()));

		FlowMenuLayout layout = createFlowMenu.getFlowMenuLayout().get();
		Map<String, String> fileMap = new HashMap<>();
		// Loop all 添付ファイル設定 to copy uploaded files
		for (FileAttachmentSetting fileSetting : layout.getFileAttachmentSettings()) {
			String fileId = fileSetting.getFileId();
			fileSetting.setFileId(this.createFlowMenuFileService.copyFile(fileId));
			fileMap.put(fileId, fileSetting.getFileId());
		}
		// Loop all 画像設定 to copy uploaded images
		for (ImageSetting imageSetting : layout.getImageSettings()) {
			if (imageSetting.getImageInformation().getIsFixed().equals(FixedClassification.RANDOM)
					&& imageSetting.getImageInformation().getFileId().isPresent()) {
				String fileId = imageSetting.getImageInformation().getFileId().get();
				imageSetting.getImageInformation()
						.setFileId(Optional.ofNullable(this.createFlowMenuFileService.copyFile(fileId)));
				fileMap.put(fileId, imageSetting.getImageInformation().getFileId().orElse(""));
			}
		}
		// Loop all メニュー設定 to copy uploaded images
		for (MenuSetting menuSetting : layout.getMenuSettings()) {
			if (!menuSetting.getImageInformation().isPresent()) continue;
			ImageInformation imageInfo = menuSetting.getImageInformation().get();
			if (imageInfo.getIsFixed().equals(FixedClassification.RANDOM) && imageInfo.getFileId().isPresent()) {
				String fileId = imageInfo.getFileId().get();
				imageInfo.setFileId(Optional.ofNullable(this.createFlowMenuFileService.copyFile(fileId)));
				fileMap.put(fileId, imageInfo.getFileId().orElse(""));
			}
		}
		// Copy フローメニューレイアウト fileId
		// Replace all old fileIds with new fileIds
		try {
			Optional<ExtractionResponseDto> optHtmlContent = this.fileExportService.extract(layout.getFileId());
			if (!optHtmlContent.isPresent()) {
				return new CopyFileResultDto(CreateFlowMenuDto.fromDomain(createFlowMenu), "");
			}
			String htmlContent = optHtmlContent.get().getHtmlContent();
			for (Entry<String, String> entry : fileMap.entrySet()) {
				htmlContent = htmlContent.replace(entry.getKey(), entry.getValue());
			}
			return new CopyFileResultDto(CreateFlowMenuDto.fromDomain(createFlowMenu), htmlContent);
		} catch (IOException e) {
			return new CopyFileResultDto(CreateFlowMenuDto.fromDomain(createFlowMenu), "");
		}
	}
}
