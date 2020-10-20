package nts.uk.ctx.sys.portal.dom.flowmenu;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CreateFlowMenuDto implements CreateFlowMenu.MementoSetter, CreateFlowMenu.MementoGetter {
	
	/**
	 * 会社ID
	 */
	public String cid;

	/**
	 * フローメニューコード
	 */
	public String flowMenuCode;
	
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
	private List<ArrowSettingDto> arrowSettings;
	
	/**
	 * フローメニューレイアウトの添付ファイル設定
	 */
	private List<FileAttachmentSettingDto> fileAttachmentSettings;
	
	/**
	 * フローメニューレイアウトの画像設定
	 */
	private List<ImageSettingDto> imageSettings;
	
	/**
	 * フローメニューレイアウトのラベル設定
	 */
	private List<LabelSettingDto> labelSettings;
	
	/**
	 * フローメニューレイアウトのリンク設定
	 */
	private List<LinkSettingDto> linkSettings;
	
	/**
	 * フローメニューレイアウトのメニュー設定
	 */
	private List<MenuSettingDto> menuSettings;

	@Override
	public void setContractCode(String contractCode) {
		//NOT USED
	}

	@Override
	public void setMenuSettings(List<MenuSetting> menuSettings, String contractCode) {
		this.menuSettings = menuSettings.stream()
										.map(domain -> {
											MenuSettingDto dto = new MenuSettingDto();
											domain.setMemento(dto);
											return dto;
										}).collect(Collectors.toList());
	}

	@Override
	public void setArrowSettings(List<ArrowSetting> arrowSettings, String contractCode) {
		this.arrowSettings = arrowSettings.stream()
				.map(domain -> {
					ArrowSettingDto dto = new ArrowSettingDto();
					domain.setMemento(dto);
					return dto;
				}).collect(Collectors.toList());
	}

	@Override
	public void setFileAttachmentSettings(List<FileAttachmentSetting> fileAttachmentSettings, String contractCode) {
		this.fileAttachmentSettings = fileAttachmentSettings.stream()
				.map(domain -> {
					FileAttachmentSettingDto dto = new FileAttachmentSettingDto();
					domain.setMemento(dto);
					return dto;
				}).collect(Collectors.toList());
	}

	@Override
	public void setImageSettings(List<ImageSetting> imageSettings, String contractCode) {
		this.imageSettings = imageSettings.stream()
				.map(domain -> {
					ImageSettingDto dto = new ImageSettingDto();
					domain.setMemento(dto);
					return dto;
				}).collect(Collectors.toList());
	}

	@Override
	public void setLabelSettings(List<LabelSetting> labelSettings, String contractCode) {
		this.labelSettings = labelSettings.stream()
				.map(domain -> {
					LabelSettingDto dto = new LabelSettingDto();
					domain.setMemento(dto);
					return dto;
				}).collect(Collectors.toList());
	}

	@Override
	public void setLinkSettings(List<LinkSetting> linkSettings, String contractCode) {
		this.linkSettings = linkSettings.stream()
				.map(domain -> {
					LinkSettingDto dto = new LinkSettingDto();
					domain.setMemento(dto);
					return dto;
				}).collect(Collectors.toList());
	}

	@Override
	public List<MenuSetting> getMenuSettings() {
		return this.menuSettings.stream()
				.map(MenuSetting::createFromMemento)
				.collect(Collectors.toList());
	}

	@Override
	public List<ArrowSetting> getArrowSettings() {
		return this.arrowSettings.stream()
				.map(ArrowSetting::createFromMemento)
				.collect(Collectors.toList());
	}

	@Override
	public List<FileAttachmentSetting> getFileAttachmentSettings() {
		return this.fileAttachmentSettings.stream()
				.map(FileAttachmentSetting::createFromMemento)
				.collect(Collectors.toList());
	}

	@Override
	public List<ImageSetting> getImageSettings() {
		return this.imageSettings.stream()
				.map(ImageSetting::createFromMemento)
				.collect(Collectors.toList());
	}

	@Override
	public List<LabelSetting> getLabelSettings() {
		return this.labelSettings.stream()
				.map(LabelSetting::createFromMemento)
				.collect(Collectors.toList());
	}

	@Override
	public List<LinkSetting> getLinkSettings() {
		return this.linkSettings.stream()
				.map(LinkSetting::createFromMemento)
				.collect(Collectors.toList());
	}
	
	
}
