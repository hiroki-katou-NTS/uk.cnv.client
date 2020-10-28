package nts.uk.ctx.sys.portal.app.screenquery.flowmenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Data;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.sys.portal.dom.flowmenu.ArrowSetting;
import nts.uk.ctx.sys.portal.dom.flowmenu.CreateFlowMenu;
import nts.uk.ctx.sys.portal.dom.flowmenu.DisplayName;
import nts.uk.ctx.sys.portal.dom.flowmenu.FileAttachmentSetting;
import nts.uk.ctx.sys.portal.dom.flowmenu.FileName;
import nts.uk.ctx.sys.portal.dom.flowmenu.FixedClassification;
import nts.uk.ctx.sys.portal.dom.flowmenu.FontSetting;
import nts.uk.ctx.sys.portal.dom.flowmenu.FontSize;
import nts.uk.ctx.sys.portal.dom.flowmenu.HorizontalAndVerticalPosition;
import nts.uk.ctx.sys.portal.dom.flowmenu.HorizontalAndVerticalSize;
import nts.uk.ctx.sys.portal.dom.flowmenu.HorizontalPosition;
import nts.uk.ctx.sys.portal.dom.flowmenu.ImageSetting;
import nts.uk.ctx.sys.portal.dom.flowmenu.LabelContent;
import nts.uk.ctx.sys.portal.dom.flowmenu.LabelSetting;
import nts.uk.ctx.sys.portal.dom.flowmenu.LinkSetting;
import nts.uk.ctx.sys.portal.dom.flowmenu.MenuClassification;
import nts.uk.ctx.sys.portal.dom.flowmenu.MenuSetting;
import nts.uk.ctx.sys.portal.dom.flowmenu.SizeAndColor;
import nts.uk.ctx.sys.portal.dom.flowmenu.SizeAndPosition;
import nts.uk.ctx.sys.portal.dom.flowmenu.System;
import nts.uk.ctx.sys.portal.dom.flowmenu.URL;
import nts.uk.ctx.sys.portal.dom.flowmenu.VerticalPosition;
import nts.uk.ctx.sys.portal.dom.webmenu.ColorCode;
import nts.uk.ctx.sys.portal.dom.webmenu.MenuCode;

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
	private List<ArrowSettingDto> arrowSettings = new ArrayList<>();
	
	/**
	 * フローメニューレイアウトの添付ファイル設定
	 */
	private List<FileAttachmentSettingDto> fileAttachmentSettings = new ArrayList<>();
	
	/**
	 * フローメニューレイアウトの画像設定
	 */
	private List<ImageSettingDto> imageSettings = new ArrayList<>();
	
	/**
	 * フローメニューレイアウトのラベル設定
	 */
	private List<LabelSettingDto> labelSettings = new ArrayList<>();
	
	/**
	 * フローメニューレイアウトのリンク設定
	 */
	private List<LinkSettingDto> linkSettings = new ArrayList<>();
	
	/**
	 * フローメニューレイアウトのメニュー設定
	 */
	private List<MenuSettingDto> menuSettings = new ArrayList<>();

	@Override
	public void setContractCode(String contractCode) {
		//NOT USED
	}

	@Override
	public void setMenuSettings(List<MenuSetting> menuSettings, String contractCode) {
		this.menuSettings = menuSettings.stream()
										.map(domain -> MenuSettingDto.builder()
												.bold(domain.getFontSetting().getSizeAndColor().isBold()? 1 : 0)
												.column(domain.getSizeAndPosition().getColumn().v())
												.fontSize(domain.getFontSetting().getSizeAndColor().getFontSize().v())
												.height(domain.getSizeAndPosition().getHeight().v())
												.horizontalPosition(domain.getFontSetting().getPosition().getHorizontalPosition().value)
												.menuClassification(domain.getMenuClassification().value)
												.menuCode(domain.getMenuCode().v())
												.menuName(domain.getMenuName().v())
												.row(domain.getSizeAndPosition().getRow().v())
												.systemType(domain.getSystemType().value)
												.verticalPosition(domain.getFontSetting().getPosition().getVerticalPosition().value)
												.width(domain.getSizeAndPosition().getWidth().v())
												.build())
										.collect(Collectors.toList());
	}

	@Override
	public void setArrowSettings(List<ArrowSetting> arrowSettings, String contractCode) {
		this.arrowSettings = arrowSettings.stream()
				.map(domain -> ArrowSettingDto.builder()
						.column(domain.getSizeAndPosition().getColumn().v())
						.fileName(domain.getFileName().v())
						.height(domain.getSizeAndPosition().getHeight().v())
						.row(domain.getSizeAndPosition().getRow().v())
						.width(domain.getSizeAndPosition().getWidth().v())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public void setFileAttachmentSettings(List<FileAttachmentSetting> fileAttachmentSettings, String contractCode) {
		this.fileAttachmentSettings = fileAttachmentSettings.stream()
				.map(domain -> FileAttachmentSettingDto.builder()
						.bold(domain.getFontSetting().getSizeAndColor().isBold()? 1 : 0)
						.column(domain.getSizeAndPosition().getColumn().v())
						.fileId(domain.getFileId())
						.fontSize(domain.getFontSetting().getSizeAndColor().getFontSize().v())
						.height(domain.getSizeAndPosition().getHeight().v())
						.horizontalPosition(domain.getFontSetting().getPosition().getHorizontalPosition().value)
						.linkContent(domain.getLinkContent().orElse(null))
						.row(domain.getSizeAndPosition().getRow().v())
						.verticalPosition(domain.getFontSetting().getPosition().getVerticalPosition().value)
						.width(domain.getSizeAndPosition().getWidth().v())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public void setImageSettings(List<ImageSetting> imageSettings, String contractCode) {
		this.imageSettings = imageSettings.stream()
				.map(domain -> ImageSettingDto.builder()
						.column(domain.getSizeAndPosition().getColumn().v())
						.fileId(domain.getFileId().orElse(null))
						.fileName(domain.getFileName().map(FileName::v).orElse(null))
						.height(domain.getSizeAndPosition().getHeight().v())
						.isFixed(domain.getIsFixed().value)
						.row(domain.getSizeAndPosition().getRow().v())
						.width(domain.getSizeAndPosition().getWidth().v())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public void setLabelSettings(List<LabelSetting> labelSettings, String contractCode) {
		this.labelSettings = labelSettings.stream()
				.map(domain -> LabelSettingDto.builder()
						.backgroundColor(domain.getFontSetting().getSizeAndColor().getBackgroundColor().map(ColorCode::v).orElse(null))
						.bold(domain.getFontSetting().getSizeAndColor().isBold()? 1 : 0)
						.column(domain.getSizeAndPosition().getColumn().v())
						.fontSize(domain.getFontSetting().getSizeAndColor().getFontSize().v())
						.height(domain.getSizeAndPosition().getHeight().v())
						.horizontalPosition(domain.getFontSetting().getPosition().getHorizontalPosition().value)
						.labelContent(domain.getLabelContent().map(LabelContent::v).orElse(null))
						.row(domain.getSizeAndPosition().getRow().v())
						.textColor(domain.getFontSetting().getSizeAndColor().getFontColor().map(ColorCode::v).orElse(null))
						.verticalPosition(domain.getFontSetting().getPosition().getVerticalPosition().value)
						.width(domain.getSizeAndPosition().getWidth().v())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public void setLinkSettings(List<LinkSetting> linkSettings, String contractCode) {
		this.linkSettings = linkSettings.stream()
				.map(domain -> LinkSettingDto.builder()
						.column(domain.getSizeAndPosition().getColumn().v())
						.height(domain.getSizeAndPosition().getHeight().v())
						.linkContent(domain.getLinkContent().orElse(null))
						.row(domain.getSizeAndPosition().getRow().v())
						.url(domain.getUrl().v())
						.width(domain.getSizeAndPosition().getWidth().v())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<MenuSetting> getMenuSettings() {
		return this.menuSettings.stream()
				.map(dto -> MenuSetting.builder()
						.fontSetting(new FontSetting(
								new SizeAndColor(dto.getBold() == SizeAndColor.BOLD, Optional.empty(), Optional.empty(), new FontSize(dto.getFontSize())), 
								new HorizontalAndVerticalPosition(
										EnumAdaptor.valueOf(dto.getHorizontalPosition(), HorizontalPosition.class), 
										EnumAdaptor.valueOf(dto.getVerticalPosition(), VerticalPosition.class))))
						.menuClassification(EnumAdaptor.valueOf(dto.getMenuClassification(), MenuClassification.class))
						.menuCode(new MenuCode(dto.getMenuCode()))
						.menuName(new DisplayName(dto.getMenuName()))
						.sizeAndPosition(new SizeAndPosition(
								new HorizontalAndVerticalSize(dto.getColumn()),
								new HorizontalAndVerticalSize(dto.getRow()),
								new HorizontalAndVerticalSize(dto.getHeight()),
								new HorizontalAndVerticalSize(dto.getWidth())))
						.systemType(EnumAdaptor.valueOf(dto.getSystemType(), System.class))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<ArrowSetting> getArrowSettings() {
		return this.arrowSettings.stream()
				.map(dto -> ArrowSetting.builder()
						.fileName(new FileName(dto.getFileName()))
						.sizeAndPosition(new SizeAndPosition(
								new HorizontalAndVerticalSize(dto.getColumn()),
								new HorizontalAndVerticalSize(dto.getRow()),
								new HorizontalAndVerticalSize(dto.getHeight()),
								new HorizontalAndVerticalSize(dto.getWidth())))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<FileAttachmentSetting> getFileAttachmentSettings() {
		return this.fileAttachmentSettings.stream()
				.map(dto -> FileAttachmentSetting.builder()
						.fileId(dto.getFileId())
						.fontSetting(new FontSetting(
								new SizeAndColor(dto.getBold() == SizeAndColor.BOLD, Optional.empty(), Optional.empty(), new FontSize(dto.getFontSize())), 
								new HorizontalAndVerticalPosition(
										EnumAdaptor.valueOf(dto.getHorizontalPosition(), HorizontalPosition.class), 
										EnumAdaptor.valueOf(dto.getVerticalPosition(), VerticalPosition.class))))
						.linkContent(Optional.ofNullable(dto.getLinkContent()))
						.sizeAndPosition(new SizeAndPosition(
								new HorizontalAndVerticalSize(dto.getColumn()),
								new HorizontalAndVerticalSize(dto.getRow()),
								new HorizontalAndVerticalSize(dto.getHeight()),
								new HorizontalAndVerticalSize(dto.getWidth())))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<ImageSetting> getImageSettings() {
		return this.imageSettings.stream()
				.map(dto -> ImageSetting.builder()
						.fileId(Optional.ofNullable(dto.getFileId()))
						.fileName(dto.getFileName() != null ? Optional.of(new FileName(dto.getFileName())) : Optional.empty())
						.isFixed(EnumAdaptor.valueOf(dto.getIsFixed(), FixedClassification.class))
						.sizeAndPosition(new SizeAndPosition(
								new HorizontalAndVerticalSize(dto.getColumn()),
								new HorizontalAndVerticalSize(dto.getRow()),
								new HorizontalAndVerticalSize(dto.getHeight()),
								new HorizontalAndVerticalSize(dto.getWidth())))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<LabelSetting> getLabelSettings() {
		return this.labelSettings.stream()
				.map(dto -> LabelSetting.builder()
						.fontSetting(new FontSetting(
								new SizeAndColor(
										dto.getBold() == SizeAndColor.BOLD,
										Optional.of(new ColorCode(dto.getBackgroundColor())),
										Optional.of(new ColorCode(dto.getTextColor())),
										new FontSize(dto.getFontSize())), 
								new HorizontalAndVerticalPosition(
										EnumAdaptor.valueOf(dto.getHorizontalPosition(), HorizontalPosition.class), 
										EnumAdaptor.valueOf(dto.getVerticalPosition(), VerticalPosition.class))))
						.labelContent(dto.getLabelContent() != null 
								? Optional.of(new LabelContent(dto.getLabelContent())) 
								: Optional.empty())
						.sizeAndPosition(new SizeAndPosition(
								new HorizontalAndVerticalSize(dto.getColumn()),
								new HorizontalAndVerticalSize(dto.getRow()),
								new HorizontalAndVerticalSize(dto.getHeight()),
								new HorizontalAndVerticalSize(dto.getWidth())))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<LinkSetting> getLinkSettings() {
		return this.linkSettings.stream()
				.map(dto -> LinkSetting.builder()
						.fontSetting(new FontSetting(
								new SizeAndColor(
										dto.getBold() == SizeAndColor.BOLD,
										Optional.empty(),
										Optional.empty(),
										new FontSize(dto.getFontSize())), 
								new HorizontalAndVerticalPosition(
										EnumAdaptor.valueOf(dto.getHorizontalPosition(), HorizontalPosition.class), 
										EnumAdaptor.valueOf(dto.getVerticalPosition(), VerticalPosition.class))))
						.linkContent(Optional.ofNullable(dto.getLinkContent()))
						.sizeAndPosition(new SizeAndPosition(
								new HorizontalAndVerticalSize(dto.getColumn()),
								new HorizontalAndVerticalSize(dto.getRow()),
								new HorizontalAndVerticalSize(dto.getHeight()),
								new HorizontalAndVerticalSize(dto.getWidth())))
						.url(new URL(dto.getUrl()))
						.build())
				.collect(Collectors.toList());
	}
}
