/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.ws.wagetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.uk.ctx.pr.core.app.wagetable.command.WtInitCommand;
import nts.uk.ctx.pr.core.app.wagetable.command.WtInitCommandHandler;
import nts.uk.ctx.pr.core.app.wagetable.command.WtUpdateCommand;
import nts.uk.ctx.pr.core.app.wagetable.command.WtUpdateCommandHandler;
import nts.uk.ctx.pr.core.dom.base.simplehistory.SimpleHistoryBaseService;
import nts.uk.ctx.pr.core.dom.wagetable.DemensionNo;
import nts.uk.ctx.pr.core.dom.wagetable.ElementType;
import nts.uk.ctx.pr.core.dom.wagetable.WtCode;
import nts.uk.ctx.pr.core.dom.wagetable.WtHead;
import nts.uk.ctx.pr.core.dom.wagetable.WtHeadRepository;
import nts.uk.ctx.pr.core.dom.wagetable.history.WtHistory;
import nts.uk.ctx.pr.core.dom.wagetable.history.WtHistoryRepository;
import nts.uk.ctx.pr.core.dom.wagetable.history.element.ElementSetting;
import nts.uk.ctx.pr.core.dom.wagetable.history.element.RangeLimit;
import nts.uk.ctx.pr.core.dom.wagetable.history.element.StepElementSetting;
import nts.uk.ctx.pr.core.dom.wagetable.history.element.item.CodeItem;
import nts.uk.ctx.pr.core.dom.wagetable.history.element.item.RangeItem;
import nts.uk.ctx.pr.core.dom.wagetable.history.element.item.generator.ElementItemFactory;
import nts.uk.ctx.pr.core.dom.wagetable.history.service.WtHistoryService;
import nts.uk.ctx.pr.core.ws.base.simplehistory.SimpleHistoryWs;
import nts.uk.ctx.pr.core.ws.base.simplehistory.dto.HistoryModel;
import nts.uk.ctx.pr.core.ws.base.simplehistory.dto.MasterModel;
import nts.uk.ctx.pr.core.ws.wagetable.dto.DemensionItemDto;
import nts.uk.ctx.pr.core.ws.wagetable.dto.ElementItemDto;
import nts.uk.ctx.pr.core.ws.wagetable.dto.ElementSettingDto;
import nts.uk.ctx.pr.core.ws.wagetable.dto.ElementTypeDto;
import nts.uk.ctx.pr.core.ws.wagetable.dto.SettingInfoInModel;
import nts.uk.ctx.pr.core.ws.wagetable.dto.SettingInfoOutModel;
import nts.uk.ctx.pr.core.ws.wagetable.dto.WageTableModel;
import nts.uk.ctx.pr.core.ws.wagetable.dto.WtHeadDto;
import nts.uk.ctx.pr.core.ws.wagetable.dto.WtHistoryDto;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class WageTableWs.
 */
@Path("pr/proto/wagetable")
@Produces(MediaType.APPLICATION_JSON)
public class WageTableWs extends SimpleHistoryWs<WtHead, WtHistory> {

	/** The wt init command handler. */
	@Inject
	private WtInitCommandHandler wtInitCommandHandler;

	/** The wt update command handler. */
	@Inject
	private WtUpdateCommandHandler wtUpdateCommandHandler;

	/** The wt head repo. */
	@Inject
	private WtHeadRepository wtHeadRepo;

	/** The wt history repo. */
	@Inject
	private WtHistoryRepository wtHistoryRepo;

	/** The wt history service. */
	@Inject
	private WtHistoryService wtHistoryService;

	/** The element item factory. */
	@Inject
	private ElementItemFactory elementItemFactory;

	/**
	 * Find.
	 *
	 * @param id
	 *            the id
	 * @return the wage table history dto
	 */
	@POST
	@Path("find/{id}")
	public WageTableModel find(@PathParam("id") String id) {
		WageTableModel model = new WageTableModel();

		// Get the detail history.
		Optional<WtHistory> optWageTableHistory = this.wtHistoryRepo.findHistoryByUuid(id);

		// Check exsit.
		if (optWageTableHistory.isPresent()) {
			WtHistory wageTableHistory = optWageTableHistory.get();
			WtHistoryDto historyDto = new WtHistoryDto();
			wageTableHistory.saveToMemento(historyDto);
			model.setHistory(historyDto);

			Optional<WtHead> optWageTable = this.wtHeadRepo.findByCode(
					wageTableHistory.getCompanyCode().v(), wageTableHistory.getWageTableCode().v());

			// Check exsit.
			if (optWageTable.isPresent()) {
				WtHead wageTableHead = optWageTable.get();
				WtHeadDto headDto = new WtHeadDto();
				wageTableHead.saveToMemento(headDto);
				model.setHead(headDto);
			}
		}

		// Return
		return model;
	}

	/**
	 * Inits the table.
	 *
	 * @param command
	 *            the command
	 */
	@POST
	@Path("init")
	public HistoryModel initTable(WtInitCommand command) {
		WtHistory history = this.wtInitCommandHandler.handle(command);
		// Ret.
		return HistoryModel.builder().uuid(history.getUuid()).start(history.getStart().v())
				.end(history.getEnd().v()).build();
	}

	/**
	 * Update.
	 *
	 * @param command
	 *            the command
	 */
	@POST
	@Path("update")
	public void update(WtUpdateCommand command) {
		this.wtUpdateCommandHandler.handle(command);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.pr.core.ws.base.simplehistory.SimpleHistoryWs#
	 * loadMasterModelList()
	 */
	@Override
	@Path("masterhistory")
	@POST
	public List<MasterModel> loadMasterModelList() {
		// Get list of wage table.
		List<WtHead> wageTables = this.wtHeadRepo.findAll(AppContexts.user().companyCode());

		// Get list of wage table history.
		List<WtHistory> wageTableHistories = this.wtHistoryRepo
				.findAll(AppContexts.user().companyCode());

		// Group histories by unit code.
		Map<WtCode, List<HistoryModel>> historyMap = wageTableHistories.stream().collect(
				Collectors.groupingBy(WtHistory::getWageTableCode, Collectors.mapping((res) -> {
					return HistoryModel.builder().uuid(res.getUuid()).start(res.getStart().v())
							.end(res.getEnd().v()).build();
				}, Collectors.toList())));

		// Return
		return wageTables.stream().map(item -> {
			return MasterModel.builder().code(item.getCode().v()).name(item.getName().v())
					.historyList(historyMap.get(item.getCode())).build();
		}).collect(Collectors.toList());
	}

	/**
	 * Generate setting item.
	 *
	 * @param input
	 *            the input
	 * @return the setting info out model
	 */
	@POST
	@Path("reference/genitem")
	public SettingInfoOutModel generateSettingItem(SettingInfoInModel input) {
		SettingInfoOutModel model = new SettingInfoOutModel();

		List<ElementSetting> elementSettings = input.getElementSettings().stream().map(item -> {
			if (ElementType.valueOf(item.getType()).isCodeMode) {
				return new ElementSetting(DemensionNo.valueOf(item.getDemensionNo()),
						ElementType.valueOf(item.getType()), Collections.emptyList());
			} else {
				StepElementSetting el = new StepElementSetting(
						DemensionNo.valueOf(item.getDemensionNo()),
						ElementType.valueOf(item.getType()), Collections.emptyList());
				el.setSetting(new RangeLimit(item.getUpperLimit()),
						new RangeLimit(item.getLowerLimit()), new RangeLimit(item.getInterval()));
				return el;
			}
		}).collect(Collectors.toList());

		elementSettings = elementItemFactory.generate(input.getCompanyCode(), input.getHistoryId(),
				elementSettings);

		List<ElementSettingDto> elementSettingDtos = elementSettings.stream().map(item -> {

			if (item.getType().isCodeMode) {

				List<ElementItemDto> itemList = item.getItemList().stream().map(subItem -> {
					CodeItem codeItem = (CodeItem) subItem;
					return ElementItemDto.builder().uuid(codeItem.getUuid().v())
							.referenceCode(codeItem.getReferenceCode()).build();
				}).collect(Collectors.toList());

				return ElementSettingDto.builder().demensionNo(item.getDemensionNo().value)
						.type(item.getType().value)
						// .demensionName(demensionName)
						.itemList(itemList).build();

			} else {

				StepElementSetting stepElementSetting = (StepElementSetting) item;

				List<ElementItemDto> itemList = item.getItemList().stream().map(subItem -> {
					RangeItem rangeItem = (RangeItem) subItem;
					return ElementItemDto.builder().uuid(rangeItem.getUuid().v())
							.orderNumber(rangeItem.getOrderNumber())
							.startVal(rangeItem.getStartVal()).endVal(rangeItem.getEndVal())
							.build();
				}).collect(Collectors.toList());

				return ElementSettingDto.builder().demensionNo(item.getDemensionNo().value)
						.type(item.getType().value)
						.lowerLimit(stepElementSetting.getLowerLimit().v())
						.upperLimit(stepElementSetting.getUpperLimit().v())
						.interval(stepElementSetting.getInterval().v())
						// .demensionName(demensionName)
						.itemList(itemList).build();
			}

		}).collect(Collectors.toList());

		model.setElementSettings(elementSettingDtos);

		return model;
	}

	/**
	 * Load demension selection list.
	 *
	 * @return the list
	 */
	@POST
	@Path("demensions")
	public List<DemensionItemDto> loadDemensionSelectionList() {
		List<DemensionItemDto> items = new ArrayList<>();

		/** The age fix. */
		// AGE_FIX(4, false, true),
		items.add(new DemensionItemDto(ElementType.AGE_FIX));

		/** The experience fix. */
		// EXPERIENCE_FIX(3, false, true),
		items.add(new DemensionItemDto(ElementType.EXPERIENCE_FIX));

		/** The family mem fix. */
		// FAMILY_MEM_FIX(5, false, true),
		items.add(new DemensionItemDto(ElementType.FAMILY_MEM_FIX));

		/** The master ref. */
		// MASTER_REF(0, true, false),
		// Find all in MASTER_REF table.

		/** The code ref. */
		// CODE_REF(1, true, false),
		// Find all in CODE_REF table.

		/** The item data ref. */
		// ITEM_DATA_REF(2, false, true),
		items.add(new DemensionItemDto(ElementType.ITEM_DATA_REF));

		return items;
	}

	private static final List<ElementTypeDto> ELEMENT_TYPE_DTO;
	static {
		ELEMENT_TYPE_DTO = new ArrayList<>();
		for (ElementType type : ElementType.values()) {
			ElementTypeDto dto = ElementTypeDto.builder().value(type.value)
					.isCodeMode(type.isCodeMode).isRangeMode(type.isRangeMode)
					.displayName(type.displayName).build();
			ELEMENT_TYPE_DTO.add(dto);
		}
	}

	/**
	 * Load element type dto.
	 *
	 * @return the list
	 */
	@POST
	@Path("elements")
	public List<ElementTypeDto> loadElementTypeDto() {
		return ELEMENT_TYPE_DTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.pr.core.ws.base.simplehistory.SimpleHistoryWs#getServices()
	 */
	@Override
	protected SimpleHistoryBaseService<WtHead, WtHistory> getServices() {
		return this.wtHistoryService;
	}
}
