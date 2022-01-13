package nts.uk.ctx.sys.portal.app.screenquery.topppagepart.createflowmenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Data;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.ArrowSetting;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.CreateFlowMenu;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.DisplayName;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.FileAttachmentSetting;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.FileName;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.FontSetting;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.FontSize;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.HorizontalAndVerticalPosition;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.HorizontalAndVerticalSize;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.HorizontalPosition;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.ImageInformation;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.ImageSetting;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.LabelContent;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.LabelSetting;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.LinkSetting;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.MenuClassification;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.MenuSetting;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.SizeAndColor;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.SizeAndPosition;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.System;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.URL;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.VerticalPosition;
import nts.uk.ctx.sys.portal.dom.webmenu.ColorCode;
import nts.uk.ctx.sys.portal.dom.webmenu.MenuCode;
import nts.uk.shr.com.context.AppContexts;

/**
 * フローメニュー作成 DTO
 */
@Data
public class CreateFlowMenuDto implements CreateFlowMenu.MementoSetter, CreateFlowMenu.MementoGetter {

	/**
	 * 会社ID
	 */
	private String cid;

	/**
	 * フローメニューコード
	 */
	private String flowMenuCode;

	/**
	 * フローメニュー名称
	 */
	private String flowMenuName;

	/**
	 * ファイルID
	 */
	private String fileId;

	/**
	 * フローメニューレイアウトの矢印設定
	 */
	private List<ArrowSettingDto> arrowData = new ArrayList<>();

	/**
	 * フローメニューレイアウトの添付ファイル設定
	 */
	private List<FileAttachmentSettingDto> fileAttachmentData = new ArrayList<>();

	/**
	 * フローメニューレイアウトの画像設定
	 */
	private List<ImageSettingDto> imageData = new ArrayList<>();

	/**
	 * フローメニューレイアウトのラベル設定
	 */
	private List<LabelSettingDto> labelData = new ArrayList<>();

	/**
	 * フローメニューレイアウトのリンク設定
	 */
	private List<LinkSettingDto> linkData = new ArrayList<>();

	/**
	 * フローメニューレイアウトのメニュー設定
	 */
	private List<MenuSettingDto> menuData = new ArrayList<>();

	@Override
	public void setContractCode(String contractCode) {
		// NOT USED
	}

	@Override
	public void setMenuSettings(List<MenuSetting> menuSettings, String contractCode) {
		this.menuData = menuSettings.stream()
				.map(domain -> MenuSettingDto.builder().bold(domain.getFontSetting().getSizeAndColor().isBold() ? 1 : 0)
						.column(domain.getSizeAndPosition().getColumn().v())
						.fontSize(domain.getFontSetting().getSizeAndColor().getFontSize().v())
						.height(domain.getSizeAndPosition().getHeight().v())
						.horizontalPosition(domain.getFontSetting().getPosition().getHorizontalPosition().value)
						.menuClassification(domain.getMenuClassification().value).menuCode(domain.getMenuCode().v())
						.menuName(domain.getMenuName().v()).row(domain.getSizeAndPosition().getRow().v())
						.systemType(domain.getSystemType().value)
						.verticalPosition(domain.getFontSetting().getPosition().getVerticalPosition().value)
						.width(domain.getSizeAndPosition().getWidth().v())
						.textColor(domain.getFontSetting().getSizeAndColor().getFontColor().map(ColorCode::v).orElse(""))
						.isFixed(domain.getImageInformation().map(data -> data.getIsFixed().value).orElse(null))
						.ratio(domain.getImageInformation().map(ImageInformation::getRatio).orElse(null))
						.fileId(domain.getImageInformation().map(data -> data.getFileId().orElse("")).orElse(""))
						.fileName(domain.getImageInformation().map(data -> data.getFileName().map(FileName::v).orElse("")).orElse(""))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public void setArrowSettings(List<ArrowSetting> arrowSettings, String contractCode) {
		this.arrowData = arrowSettings.stream()
				.map(domain -> ArrowSettingDto.builder().column(domain.getSizeAndPosition().getColumn().v())
						.fileName(domain.getFileName().v()).height(domain.getSizeAndPosition().getHeight().v())
						.row(domain.getSizeAndPosition().getRow().v()).width(domain.getSizeAndPosition().getWidth().v())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public void setFileAttachmentSettings(List<FileAttachmentSetting> fileAttachmentSettings, String contractCode) {
		this.fileAttachmentData = fileAttachmentSettings.stream()
				.map(domain -> FileAttachmentSettingDto.builder()
						.bold(domain.getFontSetting().getSizeAndColor().isBold() ? 1 : 0)
						.column(domain.getSizeAndPosition().getColumn().v()).fileId(domain.getFileId())
						.fontSize(domain.getFontSetting().getSizeAndColor().getFontSize().v())
						.height(domain.getSizeAndPosition().getHeight().v())
						.horizontalPosition(domain.getFontSetting().getPosition().getHorizontalPosition().value)
						.linkContent(domain.getLinkContent().map(DisplayName::v).orElse(null)).row(domain.getSizeAndPosition().getRow().v())
						.verticalPosition(domain.getFontSetting().getPosition().getVerticalPosition().value)
						.width(domain.getSizeAndPosition().getWidth().v()).build())
				.collect(Collectors.toList());
	}

	@Override
	public void setImageSettings(List<ImageSetting> imageSettings, String contractCode) {
		this.imageData = imageSettings.stream()
				.map(domain -> ImageSettingDto.builder().column(domain.getSizeAndPosition().getColumn().v())
						.column(domain.getSizeAndPosition().getColumn().v()).height(domain.getSizeAndPosition().getHeight().v())
						.row(domain.getSizeAndPosition().getRow().v()).width(domain.getSizeAndPosition().getWidth().v())
						.isFixed(domain.getImageInformation().getIsFixed().value).ratio(domain.getImageInformation().getRatio())
						.fileName(domain.getImageInformation().getFileName().map(FileName::v).orElse(""))
						.fileId(domain.getImageInformation().getFileId().orElse("")).build()).collect(Collectors.toList());
	}

	@Override
	public void setLabelSettings(List<LabelSetting> labelSettings, String contractCode) {
		this.labelData = labelSettings.stream()
				.map(domain -> LabelSettingDto.builder()
						.backgroundColor(
								domain.getFontSetting().getSizeAndColor().getBackgroundColor().map(ColorCode::v).orElse(null))
						.bold(domain.getFontSetting().getSizeAndColor().isBold() ? 1 : 0)
						.column(domain.getSizeAndPosition().getColumn().v())
						.fontSize(domain.getFontSetting().getSizeAndColor().getFontSize().v())
						.height(domain.getSizeAndPosition().getHeight().v())
						.horizontalPosition(domain.getFontSetting().getPosition().getHorizontalPosition().value)
						.labelContent(domain.getLabelContent().map(LabelContent::v).orElse(null))
						.row(domain.getSizeAndPosition().getRow().v())
						.textColor(domain.getFontSetting().getSizeAndColor().getFontColor().map(ColorCode::v).orElse(null))
						.verticalPosition(domain.getFontSetting().getPosition().getVerticalPosition().value)
						.width(domain.getSizeAndPosition().getWidth().v()).build())
				.collect(Collectors.toList());
	}

	@Override
	public void setLinkSettings(List<LinkSetting> linkSettings, String contractCode) {
		this.linkData = linkSettings.stream()
				.map(domain -> LinkSettingDto.builder().bold(domain.getFontSetting().getSizeAndColor().isBold() ? 1 : 0)
						.column(domain.getSizeAndPosition().getColumn().v())
						.fontSize(domain.getFontSetting().getSizeAndColor().getFontSize().v())
						.height(domain.getSizeAndPosition().getHeight().v())
						.horizontalPosition(domain.getFontSetting().getPosition().getHorizontalPosition().value)
						.linkContent(domain.getLinkContent().map(DisplayName::v).orElse(null)).row(domain.getSizeAndPosition().getRow().v())
						.url(domain.getUrl().v())
						.verticalPosition(domain.getFontSetting().getPosition().getVerticalPosition().value)
						.width(domain.getSizeAndPosition().getWidth().v()).build())
				.collect(Collectors.toList());
	}

	@Override
	public List<MenuSetting> getMenuSettings() {
		return this.menuData.stream()
				.map(dto -> MenuSetting.builder()
						.fontSetting(new FontSetting(
								new SizeAndColor(dto.getBold() == SizeAndColor.BOLD, Optional.empty(),
										Optional.ofNullable(dto.getTextColor()).map(ColorCode::new), new FontSize(dto.getFontSize())),
								new HorizontalAndVerticalPosition(
										EnumAdaptor.valueOf(dto.getHorizontalPosition(), HorizontalPosition.class),
										EnumAdaptor.valueOf(dto.getVerticalPosition(), VerticalPosition.class))))
						.menuClassification(EnumAdaptor.valueOf(dto.getMenuClassification(), MenuClassification.class))
						.menuCode(new MenuCode(dto.getMenuCode())).menuName(new DisplayName(dto.getMenuName()))
						.sizeAndPosition(new SizeAndPosition(new HorizontalAndVerticalSize(dto.getColumn()),
								new HorizontalAndVerticalSize(dto.getRow()),
								new HorizontalAndVerticalSize(dto.getHeight()),
								new HorizontalAndVerticalSize(dto.getWidth())))
						.systemType(EnumAdaptor.valueOf(dto.getSystemType(), System.class))
						.imageInformation(Optional.ofNullable(ImageInformation.createFromJavatype(dto.getIsFixed(),
								dto.getRatio(), dto.getFileId(), dto.getFileName())))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<ArrowSetting> getArrowSettings() {
		return this.arrowData.stream()
				.map(dto -> ArrowSetting.builder().fileName(new FileName(dto.getFileName()))
						.sizeAndPosition(new SizeAndPosition(new HorizontalAndVerticalSize(dto.getColumn()),
								new HorizontalAndVerticalSize(dto.getRow()), new HorizontalAndVerticalSize(dto.getHeight()),
								new HorizontalAndVerticalSize(dto.getWidth())))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<FileAttachmentSetting> getFileAttachmentSettings() {
		return this.fileAttachmentData.stream()
				.map(dto -> FileAttachmentSetting.builder().fileId(dto.getFileId())
						.fontSetting(new FontSetting(
								new SizeAndColor(dto.getBold() == SizeAndColor.BOLD, Optional.empty(), Optional.empty(),
										new FontSize(dto.getFontSize())),
								new HorizontalAndVerticalPosition(
										EnumAdaptor.valueOf(dto.getHorizontalPosition(), HorizontalPosition.class),
										EnumAdaptor.valueOf(dto.getVerticalPosition(), VerticalPosition.class))))
						.linkContent(Optional.ofNullable(dto.getLinkContent()).map(DisplayName::new))
						.sizeAndPosition(new SizeAndPosition(new HorizontalAndVerticalSize(dto.getColumn()),
								new HorizontalAndVerticalSize(dto.getRow()),
								new HorizontalAndVerticalSize(dto.getHeight()),
								new HorizontalAndVerticalSize(dto.getWidth())))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<ImageSetting> getImageSettings() {
		return this.imageData.stream()
				.map(dto -> ImageSetting.builder()
						.sizeAndPosition(new SizeAndPosition(new HorizontalAndVerticalSize(dto.getColumn()),
								new HorizontalAndVerticalSize(dto.getRow()), new HorizontalAndVerticalSize(dto.getHeight()),
								new HorizontalAndVerticalSize(dto.getWidth())))
						.imageInformation(ImageInformation.createFromJavatype(dto.getIsFixed(), dto.getRatio(),
								dto.getFileId(), dto.getFileName()))
						.build()).collect(Collectors.toList());
	}

	@Override
	public List<LabelSetting> getLabelSettings() {
		return this.labelData.stream()
				.map(dto -> LabelSetting.builder()
						.fontSetting(new FontSetting(
								new SizeAndColor(dto.getBold() == SizeAndColor.BOLD,
										Optional.of(new ColorCode(dto.getBackgroundColor())), Optional
												.of(new ColorCode(dto.getTextColor())),
										new FontSize(dto.getFontSize())),
								new HorizontalAndVerticalPosition(
										EnumAdaptor.valueOf(dto.getHorizontalPosition(), HorizontalPosition.class),
										EnumAdaptor.valueOf(dto.getVerticalPosition(), VerticalPosition.class))))
						.labelContent(dto.getLabelContent() != null
										? Optional.of(new LabelContent(dto.getLabelContent()))
										: Optional.empty())
						.sizeAndPosition(new SizeAndPosition(new HorizontalAndVerticalSize(dto.getColumn()),
										new HorizontalAndVerticalSize(dto.getRow()),
										new HorizontalAndVerticalSize(dto.getHeight()),
										new HorizontalAndVerticalSize(dto.getWidth())))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<LinkSetting> getLinkSettings() {
		return this.linkData.stream()
				.map(dto -> LinkSetting.builder()
						.fontSetting(new FontSetting(
								new SizeAndColor(dto.getBold() == SizeAndColor.BOLD, Optional.empty(), Optional.empty(),
										new FontSize(dto.getFontSize())),
								new HorizontalAndVerticalPosition(
										EnumAdaptor.valueOf(dto.getHorizontalPosition(), HorizontalPosition.class),
										EnumAdaptor.valueOf(dto.getVerticalPosition(), VerticalPosition.class))))
						.linkContent(Optional.ofNullable(dto.getLinkContent()).map(DisplayName::new))
						.sizeAndPosition(new SizeAndPosition(new HorizontalAndVerticalSize(dto.getColumn()),
								new HorizontalAndVerticalSize(dto.getRow()),
								new HorizontalAndVerticalSize(dto.getHeight()),
								new HorizontalAndVerticalSize(dto.getWidth())))
						.url(new URL(dto.getUrl())).build())
				.collect(Collectors.toList());
	}
	
	public static CreateFlowMenuDto fromDomain(CreateFlowMenu domain) {
		CreateFlowMenuDto dto = new CreateFlowMenuDto();
		domain.setMemento(dto, AppContexts.user().contractCode());
		return dto;
	}
}
