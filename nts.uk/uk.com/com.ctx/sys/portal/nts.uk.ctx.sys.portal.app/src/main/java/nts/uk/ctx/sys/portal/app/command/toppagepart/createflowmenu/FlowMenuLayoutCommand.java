package nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Value;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.sys.portal.app.screenquery.topppagepart.createflowmenu.ArrowSettingDto;
import nts.uk.ctx.sys.portal.app.screenquery.topppagepart.createflowmenu.FileAttachmentSettingDto;
import nts.uk.ctx.sys.portal.app.screenquery.topppagepart.createflowmenu.ImageSettingDto;
import nts.uk.ctx.sys.portal.app.screenquery.topppagepart.createflowmenu.LabelSettingDto;
import nts.uk.ctx.sys.portal.app.screenquery.topppagepart.createflowmenu.LinkSettingDto;
import nts.uk.ctx.sys.portal.app.screenquery.topppagepart.createflowmenu.MenuSettingDto;
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

@Value
public class FlowMenuLayoutCommand implements CreateFlowMenu.MementoGetter {
	
	/**
	 * ファイルID
	 */
	private String fileId;

	/**
	 * メニュー設定
	 */
	private List<MenuSettingDto> menuSettings;

	/**
	 * ラベル設定
	 */
	private List<LabelSettingDto> labelSettings;

	/**
	 * リンク設定
	 */
	private List<LinkSettingDto> linkSettings;

	/**
	 * 添付ファイル設定
	 */
	private List<FileAttachmentSettingDto> fileAttachmentSettings;

	/**
	 * 画像設定
	 */
	private List<ImageSettingDto> imageSettings;

	/**
	 * 矢印設定
	 */
	private List<ArrowSettingDto> arrowSettings;

	@Override
	public List<MenuSetting> getMenuSettings() {
		return this.menuSettings.stream()
				.map(dto -> MenuSetting.builder()
						.fontSetting(new FontSetting(
								new SizeAndColor(dto.getBold() == SizeAndColor.BOLD, Optional.empty(),
										Optional.ofNullable(dto.getTextColor()).map(ColorCode::new), 
										new FontSize(dto.getFontSize())),
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
		return this.arrowSettings.stream().map(dto -> ArrowSetting.builder().fileName(new FileName(dto.getFileName()))
				.sizeAndPosition(new SizeAndPosition(new HorizontalAndVerticalSize(dto.getColumn()),
						new HorizontalAndVerticalSize(dto.getRow()), new HorizontalAndVerticalSize(dto.getHeight()),
						new HorizontalAndVerticalSize(dto.getWidth())))
				.build()).collect(Collectors.toList());
	}

	@Override
	public List<FileAttachmentSetting> getFileAttachmentSettings() {
		return this.fileAttachmentSettings.stream()
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
		return this.imageSettings.stream().map(dto -> ImageSetting.builder()
				.sizeAndPosition(new SizeAndPosition(new HorizontalAndVerticalSize(dto.getColumn()),
						new HorizontalAndVerticalSize(dto.getRow()), new HorizontalAndVerticalSize(dto.getHeight()),
						new HorizontalAndVerticalSize(dto.getWidth())))
				.imageInformation(ImageInformation.createFromJavatype(dto.getIsFixed(), dto.getRatio(),
						dto.getFileId(), dto.getFileName()))
				.build()).collect(Collectors.toList());
	}

	@Override
	public List<LabelSetting> getLabelSettings() {
		return this.labelSettings
				.stream().map(
						dto -> LabelSetting.builder().fontSetting(new FontSetting(
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
		return this.linkSettings.stream()
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

	@Override
	public String getFlowMenuCode() {
		// NOT USED
		return null;
	}

	@Override
	public String getFlowMenuName() {
		// NOT USED
		return null;
	}

	@Override
	public String getCid() {
		// NOT USED
		return null;
	}
}
