package nts.uk.ctx.sys.portal.infra.entity.flowmenu;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
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

/**
 * フローメニュー作成																			
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "SPTMT_FLOWMENU_CREATE")
public class SptmtCreateFlowMenu implements Serializable,
											CreateFlowMenu.MementoGetter,
											CreateFlowMenu.MementoSetter {

	private static final long serialVersionUID = 1L;

	// column 排他バージョン
	@Version
	@Column(name = "EXCLUS_VER")
	private long version;
	
	@EmbeddedId
	private SptmtCreateFlowMenuPk pk;
	
	/**
	 * 契約コード									
	 */
	@Basic(optional = false)
	@Column(name = "CONTRACT_CD")
	private String contractCode;
	
	/**
	 * フローメニュー名称									
	 */
	@Basic(optional = false)
	@Column(name = "FLOW_MENU_NAME")
	private String flowMenuName;
	
	/**
	 * ファイルID									
	 */
	@Basic(optional = true)
	@Column(name = "FILE_ID")
	private String fileId;
	
	/**
	 * フローメニューレイアウトの矢印設定
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "flowMenu", orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "SPTMT_FLOW_LAYOUT_ARROW")
	private List<SptmtFlowLayoutArrow> arrowSettings;
	
	/**
	 * フローメニューレイアウトの添付ファイル設定
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "flowMenu", orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "SPTMT_FLOW_LAYOUT_ATT_LINK")
	private List<SptmtFlowLayoutFileAttachment> fileAttachmentSettings;
	
	/**
	 * フローメニューレイアウトの画像設定
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "flowMenu", orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "SPTMT_FLOW_LAYOUT_IMG")
	private List<SptmtFlowLayoutImage> imageSettings;
	
	/**
	 * フローメニューレイアウトのラベル設定
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "flowMenu", orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "SPTMT_FLOW_LAYOUT_LABEL")
	private List<SptmtFlowLayoutLabel> labelSettings;
	
	/**
	 * フローメニューレイアウトのリンク設定
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "flowMenu", orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "SPTMT_FLOW_LAYOUT_LINK")
	private List<SptmtFlowLayoutLink> linkSettings;
	
	/**
	 * フローメニューレイアウトのメニュー設定
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "flowMenu", orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "SPTMT_FLOW_LAYOUT_MENU")
	private List<SptmtFlowLayoutMenu> menuSettings;

	@Override
	public void setFlowMenuCode(String flowMenuCode) {
		if (pk == null)
			pk = new SptmtCreateFlowMenuPk();
		pk.flowMenuCode = flowMenuCode;
	}

	@Override
	public void setCid(String cid) {
		if (pk == null)
			pk = new SptmtCreateFlowMenuPk();
		pk.cid = cid;
	}

	@Override
	public void setMenuSettings(List<MenuSetting> menuSettings, String contractCode) {
		this.menuSettings = menuSettings.stream()
										.map(domain -> SptmtFlowLayoutMenu.builder()
												.bold(domain.getFontSetting().getSizeAndColor().isBold() ? 1 : 0)
												.contractCode(contractCode)
												.fontSize(domain.getFontSetting().getSizeAndColor().getFontSize().v())
												.height(domain.getSizeAndPosition().getHeight().v())
												.horizontalPosition(domain.getFontSetting().getPosition().getHorizontalPosition().value)
												.menuClassification(domain.getMenuClassification().value)
												.menuCode(domain.getMenuCode().v())
												.menuName(domain.getMenuName().v())
												.pk(new SptmtFlowLayoutMenuPk(
														this.getCid(), 
														this.getFlowMenuCode(), 
														domain.getSizeAndPosition().getColumn().v(),
														domain.getSizeAndPosition().getRow().v()))
												.systemType(domain.getSystemType().value)
												.verticalPosition(domain.getFontSetting().getPosition().getVerticalPosition().value)
												.width(domain.getSizeAndPosition().getWidth().v())
												.build())
										.collect(Collectors.toList());
	}

	@Override
	public void setArrowSettings(List<ArrowSetting> arrowSettings, String contractCode) {
		this.arrowSettings = arrowSettings.stream()
				.map(domain -> SptmtFlowLayoutArrow.builder()
						.fileName(domain.getFileName().v())
						.height(domain.getSizeAndPosition().getHeight().v())
						.pk(new SptmtFlowLayoutArrowPk(
								this.getCid(), 
								this.getFlowMenuCode(), 
								domain.getSizeAndPosition().getColumn().v(),
								domain.getSizeAndPosition().getRow().v()))
						.width(domain.getSizeAndPosition().getWidth().v())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public void setFileAttachmentSettings(List<FileAttachmentSetting> fileAttachmentSettings, String contractCode) {
		this.fileAttachmentSettings = fileAttachmentSettings.stream()
				.map(domain -> SptmtFlowLayoutFileAttachment.builder()
						.bold(domain.getFontSetting().getSizeAndColor().isBold()? 1 : 0)
						.fileId(domain.getFileId())
						.fontSize(domain.getFontSetting().getSizeAndColor().getFontSize().v())
						.height(domain.getSizeAndPosition().getHeight().v())
						.horizontalPosition(domain.getFontSetting().getPosition().getHorizontalPosition().value)
						.linkContent(domain.getLinkContent().orElse(null))
						.pk(new SptmtFlowLayoutFileAttachmentPk(
								this.getCid(), 
								this.getFlowMenuCode(), 
								domain.getSizeAndPosition().getColumn().v(),
								domain.getSizeAndPosition().getRow().v()))
						.verticalPosition(domain.getFontSetting().getPosition().getVerticalPosition().value)
						.width(domain.getSizeAndPosition().getWidth().v())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public void setImageSettings(List<ImageSetting> imageSettings, String contractCode) {
		this.imageSettings = imageSettings.stream()
				.map(domain -> SptmtFlowLayoutImage.builder()
						.fileId(domain.getFileId().orElse(null))
						.fileName(domain.getFileName().map(FileName::v).orElse(null))
						.height(domain.getSizeAndPosition().getHeight().v())
						.isFixed(domain.getIsFixed().value)
						.pk(new SptmtFlowLayoutImagePk(
								this.getCid(), 
								this.getFlowMenuCode(), 
								domain.getSizeAndPosition().getColumn().v(),
								domain.getSizeAndPosition().getRow().v()))
						.width(domain.getSizeAndPosition().getWidth().v())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public void setLabelSettings(List<LabelSetting> labelSettings, String contractCode) {
		this.labelSettings = labelSettings.stream()
				.map(domain -> SptmtFlowLayoutLabel.builder()
						.backgroundColor(domain.getFontSetting().getSizeAndColor().getBackgroundColor().map(ColorCode::v).orElse(null))
						.bold(domain.getFontSetting().getSizeAndColor().isBold()? 1 : 0)
						.fontSize(domain.getFontSetting().getSizeAndColor().getFontSize().v())
						.height(domain.getSizeAndPosition().getHeight().v())
						.horizontalPosition(domain.getFontSetting().getPosition().getHorizontalPosition().value)
						.labelContent(domain.getLabelContent().map(LabelContent::v).orElse(null))
						.pk(new SptmtFlowLayoutLabelPk(
								this.getCid(), 
								this.getFlowMenuCode(), 
								domain.getSizeAndPosition().getColumn().v(),
								domain.getSizeAndPosition().getRow().v()))
						.textColor(domain.getFontSetting().getSizeAndColor().getFontColor().map(ColorCode::v).orElse(null))
						.verticalPosition(domain.getFontSetting().getPosition().getVerticalPosition().value)
						.width(domain.getSizeAndPosition().getWidth().v())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public void setLinkSettings(List<LinkSetting> linkSettings, String contractCode) {
		this.linkSettings = linkSettings.stream()
				.map(domain -> SptmtFlowLayoutLink.builder()
						.height(domain.getSizeAndPosition().getHeight().v())
						.linkContent(domain.getLinkContent().orElse(null))
						.pk(new SptmtFlowLayoutLinkPk(
								this.getCid(), 
								this.getFlowMenuCode(), 
								domain.getSizeAndPosition().getColumn().v(),
								domain.getSizeAndPosition().getRow().v()))
						.url(domain.getUrl().v())
						.width(domain.getSizeAndPosition().getWidth().v())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<MenuSetting> getMenuSettings() {
		return this.menuSettings.stream()
				.map(entity -> MenuSetting.builder()
						.fontSetting(new FontSetting(
								new SizeAndColor(entity.getBold() == SizeAndColor.BOLD, Optional.empty(), Optional.empty(), new FontSize(entity.getFontSize())), 
								new HorizontalAndVerticalPosition(
										EnumAdaptor.valueOf(entity.getHorizontalPosition(), HorizontalPosition.class), 
										EnumAdaptor.valueOf(entity.getVerticalPosition(), VerticalPosition.class))))
						.menuClassification(EnumAdaptor.valueOf(entity.getMenuClassification(), MenuClassification.class))
						.menuCode(new MenuCode(entity.getMenuCode()))
						.menuName(new DisplayName(entity.getMenuName()))
						.sizeAndPosition(new SizeAndPosition(
								new HorizontalAndVerticalSize(entity.getPk().column),
								new HorizontalAndVerticalSize(entity.getPk().row),
								new HorizontalAndVerticalSize(entity.getHeight()),
								new HorizontalAndVerticalSize(entity.getWidth())))
						.systemType(EnumAdaptor.valueOf(entity.getSystemType(), System.class))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<ArrowSetting> getArrowSettings() {
		return this.arrowSettings.stream()
				.map(entity -> ArrowSetting.builder()
						.fileName(new FileName(entity.getFileName()))
						.sizeAndPosition(new SizeAndPosition(
								new HorizontalAndVerticalSize(entity.getPk().column),
								new HorizontalAndVerticalSize(entity.getPk().row),
								new HorizontalAndVerticalSize(entity.getHeight()),
								new HorizontalAndVerticalSize(entity.getWidth())))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<FileAttachmentSetting> getFileAttachmentSettings() {
		return this.fileAttachmentSettings.stream()
				.map(entity -> FileAttachmentSetting.builder()
						.fileId(entity.getFileId())
						.fontSetting(new FontSetting(
								new SizeAndColor(entity.getBold() == SizeAndColor.BOLD, Optional.empty(), Optional.empty(), new FontSize(entity.getFontSize())), 
								new HorizontalAndVerticalPosition(
										EnumAdaptor.valueOf(entity.getHorizontalPosition(), HorizontalPosition.class), 
										EnumAdaptor.valueOf(entity.getVerticalPosition(), VerticalPosition.class))))
						.linkContent(Optional.ofNullable(entity.getLinkContent()))
						.sizeAndPosition(new SizeAndPosition(
								new HorizontalAndVerticalSize(entity.getPk().column),
								new HorizontalAndVerticalSize(entity.getPk().row),
								new HorizontalAndVerticalSize(entity.getHeight()),
								new HorizontalAndVerticalSize(entity.getWidth())))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<ImageSetting> getImageSettings() {
		return this.imageSettings.stream()
				.map(entity -> ImageSetting.builder()
						.fileId(Optional.ofNullable(entity.getFileId()))
						.fileName(entity.getFileName() != null ? Optional.of(new FileName(entity.getFileName())) : Optional.empty())
						.isFixed(EnumAdaptor.valueOf(entity.getIsFixed(), FixedClassification.class))
						.sizeAndPosition(new SizeAndPosition(
								new HorizontalAndVerticalSize(entity.getPk().column),
								new HorizontalAndVerticalSize(entity.getPk().row),
								new HorizontalAndVerticalSize(entity.getHeight()),
								new HorizontalAndVerticalSize(entity.getWidth())))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<LabelSetting> getLabelSettings() {
		return this.labelSettings.stream()
				.map(entity -> LabelSetting.builder()
						.fontSetting(new FontSetting(
								new SizeAndColor(
										entity.getBold() == SizeAndColor.BOLD,
										Optional.of(new ColorCode(entity.getBackgroundColor())),
										Optional.of(new ColorCode(entity.getTextColor())),
										new FontSize(entity.getFontSize())), 
								new HorizontalAndVerticalPosition(
										EnumAdaptor.valueOf(entity.getHorizontalPosition(), HorizontalPosition.class), 
										EnumAdaptor.valueOf(entity.getVerticalPosition(), VerticalPosition.class))))
						.labelContent(entity.getLabelContent() != null 
								? Optional.of(new LabelContent(entity.getLabelContent())) 
								: Optional.empty())
						.sizeAndPosition(new SizeAndPosition(
								new HorizontalAndVerticalSize(entity.getPk().column),
								new HorizontalAndVerticalSize(entity.getPk().row),
								new HorizontalAndVerticalSize(entity.getHeight()),
								new HorizontalAndVerticalSize(entity.getWidth())))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<LinkSetting> getLinkSettings() {
		return this.linkSettings.stream()
				.map(entity -> LinkSetting.builder()
						.fontSetting(new FontSetting(
								new SizeAndColor(
										entity.getBold() == SizeAndColor.BOLD,
										Optional.empty(),
										Optional.empty(),
										new FontSize(entity.getFontSize())), 
								new HorizontalAndVerticalPosition(
										EnumAdaptor.valueOf(entity.getHorizontalPosition(), HorizontalPosition.class), 
										EnumAdaptor.valueOf(entity.getVerticalPosition(), VerticalPosition.class))))
						.linkContent(Optional.ofNullable(entity.getLinkContent()))
						.sizeAndPosition(new SizeAndPosition(
								new HorizontalAndVerticalSize(entity.getPk().column),
								new HorizontalAndVerticalSize(entity.getPk().row),
								new HorizontalAndVerticalSize(entity.getHeight()),
								new HorizontalAndVerticalSize(entity.getWidth())))
						.url(new URL(entity.getUrl()))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public String getFlowMenuCode() {
		return this.pk.flowMenuCode;
	}

	@Override
	public String getCid() {
		// TODO Auto-generated method stub
		return this.pk.cid;
	}
}
