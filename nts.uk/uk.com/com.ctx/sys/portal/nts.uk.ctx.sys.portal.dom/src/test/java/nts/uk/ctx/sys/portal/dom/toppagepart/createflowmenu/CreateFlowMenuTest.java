package nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.sys.portal.dom.toppagepart.TopPagePartName;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.dto.ArrowSettingDto;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.dto.CreateFlowMenuDto;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.dto.FileAttachmentSettingDto;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.dto.ImageSettingDto;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.dto.LabelSettingDto;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.dto.LinkSettingDto;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.dto.MenuSettingDto;

public class CreateFlowMenuTest {

	final MenuSettingDto menuDto = MenuSettingDto.builder().bold(0).cid("cid").column(0).flowMenuCode("code")
			.fontSize(0).height(0).horizontalPosition(0).menuClassification(1).menuCode("menuCode").menuName("menuName")
			.row(0).systemType(3).verticalPosition(0).width(0).textColor("color").isFixed(null)
			.ratio(null).fileId(null).fileName(null).build();
	final ArrowSettingDto arrowDto = ArrowSettingDto.builder().cid("cid").column(0).fileName("fileName")
			.flowMenuCode("code").height(0).row(0).width(0).build();
	final FileAttachmentSettingDto fileDto = FileAttachmentSettingDto.builder().bold(0).cid("cid").column(0)
			.fileId("fileId").flowMenuCode("code").fontSize(0).height(0).horizontalPosition(0).linkContent("content")
			.row(0).verticalPosition(0).width(0).build();
	final ImageSettingDto imageDtoWithFileId = ImageSettingDto.builder().cid("cid").column(0).fileId("fileId")
			.fileName(null).flowMenuCode("code").height(0).isFixed(0).row(0).width(0).build();
	final ImageSettingDto imageDtoWithFileName = ImageSettingDto.builder().cid("cid").column(0).fileId(null)
			.fileName("fileName").flowMenuCode("code").height(0).isFixed(1).row(0).width(0).build();
	final LabelSettingDto labelDto = LabelSettingDto.builder().backgroundColor("bgColor").bold(0).cid("cid").column(0)
			.flowMenuCode("code").fontSize(0).height(0).horizontalPosition(0).labelContent("content").row(0)
			.textColor("textColor").verticalPosition(0).width(0).build();
	final LinkSettingDto linkDto = LinkSettingDto.builder().bold(0).cid("cid").column(0).flowMenuCode("code")
			.fontSize(0).height(0).horizontalPosition(0).linkContent("content").row(0).url("url").verticalPosition(0)
			.width(0).build();
	final CreateFlowMenuDto createFlowMenuDto = new CreateFlowMenuDto("cid", "0001", "code", "file-id-123141",
			Arrays.asList(arrowDto), Arrays.asList(fileDto), Arrays.asList(imageDtoWithFileId, imageDtoWithFileName),
			Arrays.asList(labelDto), Arrays.asList(linkDto), Arrays.asList(menuDto));

	final CreateFlowMenu domain0 = new CreateFlowMenu();
	final CreateFlowMenu domainAll = CreateFlowMenu.createFromMemento(createFlowMenuDto);

	@Test
	public void getters() {
		NtsAssert.invokeGetters(domainAll);
		NtsAssert.invokeGetters(domainAll.getFlowMenuLayout().get());
		NtsAssert.invokeGetters(createFlowMenuDto);
		NtsAssert.invokeGetters(createFlowMenuDto.getArrowSettings().get(0));
		NtsAssert.invokeGetters(createFlowMenuDto.getFileAttachmentSettings().get(0));
		NtsAssert.invokeGetters(createFlowMenuDto.getImageSettings().get(0));
		NtsAssert.invokeGetters(createFlowMenuDto.getLabelSettings().get(0));
		NtsAssert.invokeGetters(createFlowMenuDto.getLinkSettings().get(0));
		NtsAssert.invokeGetters(createFlowMenuDto.getMenuSettings().get(0));
	}

	@Test
	public void setters() {
		domain0.setFlowMenuLayout(Optional.empty());
		domain0.setFlowMenuName(new TopPagePartName("code"));

		assertThat(domain0.getFlowMenuLayout()).as("setter flowMenuLayout").isEqualTo(Optional.empty());
		assertThat(domain0.getFlowMenuName().v()).as("setter flowMenuName").isEqualTo("code");
	}

	@Test
	public void testFlowMenuFull() {
		CreateFlowMenuDto dto = new CreateFlowMenuDto();
		domainAll.setMemento(dto, "0001");
		CreateFlowMenu domain = CreateFlowMenu.createFromMemento(dto);

		// Assert that the newly created domain has the same attributes as the original
		// domain
		assertThat(domain.getCid()).as("testFull cid").isEqualTo(domainAll.getCid());
		assertThat(domain.getFlowMenuCode()).as("testFull flowMenuCode").isEqualTo(domainAll.getFlowMenuCode());
		assertThat(domain.getFlowMenuName()).as("testFull flowMenuName").isEqualTo(domainAll.getFlowMenuName());
		assertThat(domain.getFlowMenuLayout().get().getFileId()).as("testFull fileId")
				.isEqualTo(domainAll.getFlowMenuLayout().get().getFileId());
		assertThat(domain.getFlowMenuLayout().get().getArrowSettings())
				.extracting(x -> x.getFileName().v(),
						x -> x.getSizeAndPosition().getColumn().v(),
						x -> x.getSizeAndPosition().getHeight().v(), 
						x -> x.getSizeAndPosition().getRow().v(),
						x -> x.getSizeAndPosition().getWidth().v())
				.as("testFull arrow")
				.containsExactly(tuple("fileName", 0, 0, 0, 0));
		assertThat(domain.getFlowMenuLayout().get().getFileAttachmentSettings())
				.extracting(x -> x.getFileId(),
						x -> x.getLinkContent().get().v(),
						x -> x.getFontSetting().getPosition().getHorizontalPosition().value,
						x -> x.getFontSetting().getPosition().getVerticalPosition().value,
						x -> x.getFontSetting().getSizeAndColor().getBackgroundColor(),
						x -> x.getFontSetting().getSizeAndColor().getFontColor(),
						x -> x.getFontSetting().getSizeAndColor().getFontSize().v(),
						x -> x.getFontSetting().getSizeAndColor().isBold(),
						x -> x.getSizeAndPosition().getColumn().v(),
						x -> x.getSizeAndPosition().getHeight().v(),
						x -> x.getSizeAndPosition().getRow().v(),
						x -> x.getSizeAndPosition().getWidth().v())
				.as("testFull fileAttachment")
				.containsExactly(tuple("fileId", "content", 0, 0, Optional.empty(), Optional.empty(), 0, false, 0, 0, 0, 0));
		assertThat(domain.getFlowMenuLayout().get().getImageSettings())
				.extracting(x -> x.getImageInformation().getFileId().orElse(""),
						x -> x.getImageInformation().getFileName().map(FileName::v).orElse(""),
						x -> x.getImageInformation().getIsFixed().value,
						x -> x.getSizeAndPosition().getColumn().v(),
						x -> x.getSizeAndPosition().getHeight().v(),
						x -> x.getSizeAndPosition().getRow().v(),
						x -> x.getSizeAndPosition().getWidth().v())
				.as("testFull image")
				.containsExactly(tuple("fileId", "", 0, 0, 0, 0, 0),
								 tuple("", "fileName", 1, 0, 0, 0, 0));
		assertThat(domain.getFlowMenuLayout().get().getLabelSettings())
				.extracting(x -> x.getLabelContent().get().v(),
						x -> x.getFontSetting().getPosition().getHorizontalPosition().value,
						x -> x.getFontSetting().getPosition().getVerticalPosition().value,
						x -> x.getFontSetting().getSizeAndColor().getBackgroundColor().get().v(),
						x -> x.getFontSetting().getSizeAndColor().getFontColor().get().v(),
						x -> x.getFontSetting().getSizeAndColor().getFontSize().v(),
						x -> x.getFontSetting().getSizeAndColor().isBold(),
						x -> x.getSizeAndPosition().getColumn().v(),
						x -> x.getSizeAndPosition().getHeight().v(),
						x -> x.getSizeAndPosition().getRow().v(),
						x -> x.getSizeAndPosition().getWidth().v())
				.as("testFull label")
				.containsExactly(tuple("content", 0, 0, "bgColor", "textColor", 0, false, 0, 0, 0, 0));
		assertThat(domain.getFlowMenuLayout().get().getLinkSettings())
				.extracting(x -> x.getLinkContent().get().v(),
						x -> x.getUrl().v(),
						x -> x.getFontSetting().getPosition().getHorizontalPosition().value,
						x -> x.getFontSetting().getPosition().getVerticalPosition().value,
						x -> x.getFontSetting().getSizeAndColor().getBackgroundColor(),
						x -> x.getFontSetting().getSizeAndColor().getFontColor(),
						x -> x.getFontSetting().getSizeAndColor().getFontSize().v(),
						x -> x.getFontSetting().getSizeAndColor().isBold(), 
						x -> x.getSizeAndPosition().getColumn().v(),
						x -> x.getSizeAndPosition().getHeight().v(), 
						x -> x.getSizeAndPosition().getRow().v(),
						x -> x.getSizeAndPosition().getWidth().v())
				.as("testFull link")
				.containsExactly(tuple("content", "url", 0, 0, Optional.empty(), Optional.empty(), 0, false, 0, 0, 0, 0));
		assertThat(domain.getFlowMenuLayout().get().getMenuSettings())
				.extracting(x -> x.getMenuClassification().value,
						x -> x.getMenuCode().v(),
						x -> x.getMenuName().v(),
						x -> x.getSystemType().value,
						x -> x.getFontSetting().getPosition().getHorizontalPosition().value,
						x -> x.getFontSetting().getPosition().getVerticalPosition().value,
						x -> x.getFontSetting().getSizeAndColor().getBackgroundColor(),
						x -> x.getFontSetting().getSizeAndColor().getFontColor(),
						x -> x.getFontSetting().getSizeAndColor().getFontSize().v(),
						x -> x.getFontSetting().getSizeAndColor().isBold(),
						x -> x.getSizeAndPosition().getColumn().v(),
						x -> x.getSizeAndPosition().getHeight().v(),
						x -> x.getSizeAndPosition().getRow().v(),
						x -> x.getSizeAndPosition().getWidth().v())
				.as("testFull menu")
				.containsExactly(tuple(1, "menuCode", "menuName", 3, 0, 0, Optional.empty(), Optional.empty(), 0, false, 0, 0, 0, 0));
	}

	@Test
	public void testFlowMenuEmpty() {
		CreateFlowMenuDto dto = new CreateFlowMenuDto();
		dto.setFlowMenuCode("");
		dto.setFlowMenuName("");
		dto.setCid("cid");
		dto.setContractCode("contracCd");
		CreateFlowMenu domain = CreateFlowMenu.createFromMemento(dto);

		assertThat(domain.getCid()).as("testEmpty cid").isEqualTo("cid");
		assertThat(domain.getFlowMenuCode().v()).as("testEmpty flowMenuCode").isEqualTo("");
		assertThat(domain.getFlowMenuName().v()).as("testEmpty flowMenuName").isEqualTo("");
		assertThat(domain.getFlowMenuLayout()).as("testEmpty flowMenuLayout").isEqualTo(Optional.empty());
	}

	@Test
	public void testFileFontEmptyWithBold() {
		CreateFlowMenuDto dto = new CreateFlowMenuDto();
		domainAll.setMemento(dto, "contractCd");
		FileAttachmentSettingDto newData = FileAttachmentSettingDto.builder().bold(1).cid(null).column(0)
				.fileId(null).flowMenuCode(null).fontSize(1).height(0).horizontalPosition(0).linkContent(null)
				.row(0).verticalPosition(0).width(0).build();
		dto.setFileAttachmentSettings(Arrays.asList(newData));
		CreateFlowMenu domain = CreateFlowMenu.createFromMemento(dto);
		domain.setMemento(dto, "contractCode");
		SizeAndColor data = dto.getFileAttachmentSettings().get(0).getFontSetting().getSizeAndColor();

		assertThat(data.getBackgroundColor()).as("testFileFont backgroundColor").isEqualTo(Optional.empty());
		assertThat(data.getFontColor()).as("testFileFont fontColor").isEqualTo(Optional.empty());
		assertThat(data.getFontSize().v()).as("testFileFont fontSize").isEqualTo(1);
		assertThat(data.isBold()).as("testFileFont bold").isEqualTo(true);
	}

	@Test
	public void testLabelFontEmptyWithBold() {
		CreateFlowMenuDto dto = new CreateFlowMenuDto();
		domainAll.setMemento(dto, "contractCd");
		LabelSettingDto newData = LabelSettingDto.builder().backgroundColor(null).bold(1).cid(null).column(0)
				.flowMenuCode(null).fontSize(1).height(0).horizontalPosition(0).labelContent(null).row(0)
				.textColor(null).verticalPosition(0).width(0).build();
		dto.setLabelSettings(Arrays.asList(newData));
		CreateFlowMenu domain = CreateFlowMenu.createFromMemento(dto);
		domain.setMemento(dto, "contractCode");
		SizeAndColor data = dto.getLabelSettings().get(0).getFontSetting().getSizeAndColor();

		assertThat(data.getBackgroundColor()).as("testLabelFont backgroundColor").isEqualTo(Optional.empty());
		assertThat(data.getFontColor()).as("testLabelFont fontColor").isEqualTo(Optional.empty());
		assertThat(data.getFontSize().v()).as("testLabelFont fontSize").isEqualTo(1);
		assertThat(data.isBold()).as("testLabelFont bold").isEqualTo(true);
	}

	@Test
	public void testLinkFontEmptyWithBold() {
		CreateFlowMenuDto dto = new CreateFlowMenuDto();
		domainAll.setMemento(dto, "contractCd");
		LinkSettingDto newData = LinkSettingDto.builder().bold(1).cid(null).column(0).flowMenuCode(null)
				.fontSize(1).height(0).horizontalPosition(0).linkContent(null).row(0).url(null).verticalPosition(0)
				.width(0).build();
		dto.setLinkSettings(Arrays.asList(newData));
		CreateFlowMenu domain = CreateFlowMenu.createFromMemento(dto);
		domain.setMemento(dto, "contractCode");
		SizeAndColor data = dto.getLinkSettings().get(0).getFontSetting().getSizeAndColor();

		assertThat(data.getBackgroundColor()).as("testLinkFont backgroundColor").isEqualTo(Optional.empty());
		assertThat(data.getFontColor()).as("testLinkFont fontColor").isEqualTo(Optional.empty());
		assertThat(data.getFontSize().v()).as("testLinkFont fontSize").isEqualTo(1);
		assertThat(data.isBold()).as("testLinkFont bold").isEqualTo(true);
	}

	@Test
	public void testMenuFontEmptyWithBold() {
		CreateFlowMenuDto dto = new CreateFlowMenuDto();
		domainAll.setMemento(dto, "contractCd");
		MenuSettingDto newData = MenuSettingDto.builder().bold(1).cid(null).column(0).flowMenuCode(null)
				.fontSize(1).height(0).horizontalPosition(0).menuClassification(1).menuCode(null).menuName(null)
				.row(0).systemType(3).verticalPosition(0).width(0).build();
		dto.setMenuSettings(Arrays.asList(newData));
		CreateFlowMenu domain = CreateFlowMenu.createFromMemento(dto);
		domain.setMemento(dto, "contractCode");
		SizeAndColor data = dto.getMenuSettings().get(0).getFontSetting().getSizeAndColor();

		assertThat(data.getBackgroundColor()).as("testMenuFont backgroundColor").isEqualTo(Optional.empty());
		assertThat(data.getFontColor()).as("testMenuFont fontColor").isEqualTo(Optional.empty());
		assertThat(data.getFontSize().v()).as("testMenuFont fontSize").isEqualTo(1);
		assertThat(data.isBold()).as("testMenuFont bold").isEqualTo(true);
	}

	@Test
	public void testBuilderToString() {
		String arrowBuilder = ArrowSettingDto.builder().toString();
		String fileAttachmentBuilder = FileAttachmentSettingDto.builder().toString();
		String imageBuilder = ImageSettingDto.builder().toString();
		String labelBuilder = LabelSettingDto.builder().toString();
		String linkBuilder = LinkSettingDto.builder().toString();
		String menuBuilder = MenuSettingDto.builder().toString();

		assertThat(arrowBuilder).as("testBuilder arrow").isNotNull();
		assertThat(fileAttachmentBuilder).as("testBuilder fileAttachment").isNotNull();
		assertThat(imageBuilder).as("testBuilder image").isNotNull();
		assertThat(labelBuilder).as("testBuilder label").isNotNull();
		assertThat(linkBuilder).as("testBuilder link").isNotNull();
		assertThat(menuBuilder).as("testBuilder menu").isNotNull();
	}
	
	@Test
	public void testMenuNoImg() {
		CreateFlowMenuDto dto = new CreateFlowMenuDto();
		domainAll.setMemento(dto, "contractCd");
		MenuSettingDto newData = menuDto;
		dto.setMenuSettings(Arrays.asList(newData));
		CreateFlowMenu domain = CreateFlowMenu.createFromMemento(dto);
		domain.setMemento(dto, "contractCode");
		Optional<ImageInformation> data = dto.getMenuSettings().get(0).getImageInformation();
		
		assertThat(data).isEmpty();
	}
	
	@Test
	public void testMenuDefaultImg() {
		CreateFlowMenuDto dto = new CreateFlowMenuDto();
		domainAll.setMemento(dto, "contractCd");
		MenuSettingDto newData = MenuSettingDto.builder().bold(0).cid("cid").column(0).flowMenuCode("code")
				.fontSize(0).height(0).horizontalPosition(0).menuClassification(1).menuCode("menuCode").menuName("menuName")
				.row(0).systemType(3).verticalPosition(0).width(0).textColor("color").isFixed(FixedClassification.FIXED.value)
				.ratio(1.0).fileId("fileId").fileName("fileName").build();
		dto.setMenuSettings(Arrays.asList(newData));
		CreateFlowMenu domain = CreateFlowMenu.createFromMemento(dto);
		domain.setMemento(dto, "contractCode");
		Optional<ImageInformation> data = dto.getMenuSettings().get(0).getImageInformation();
		
		assertThat(data).isPresent();
		assertThat(data.get().getIsFixed()).isEqualTo(FixedClassification.FIXED);
		assertThat(data.get().getFileName()).isPresent();
	}
	
	@Test
	public void testMenuUploadImg() {
		CreateFlowMenuDto dto = new CreateFlowMenuDto();
		domainAll.setMemento(dto, "contractCd");
		MenuSettingDto newData = MenuSettingDto.builder().bold(0).cid("cid").column(0).flowMenuCode("code")
				.fontSize(0).height(0).horizontalPosition(0).menuClassification(1).menuCode("menuCode").menuName("menuName")
				.row(0).systemType(3).verticalPosition(0).width(0).textColor("color").isFixed(FixedClassification.RANDOM.value)
				.ratio(1.0).fileId("fileId").fileName("fileName").build();
		dto.setMenuSettings(Arrays.asList(newData));
		CreateFlowMenu domain = CreateFlowMenu.createFromMemento(dto);
		domain.setMemento(dto, "contractCode");
		Optional<ImageInformation> data = dto.getMenuSettings().get(0).getImageInformation();
		
		assertThat(data).isPresent();
		assertThat(data.get().getIsFixed()).isEqualTo(FixedClassification.RANDOM);
		assertThat(data.get().getFileId()).isPresent();
	}
}
