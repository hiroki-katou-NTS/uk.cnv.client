/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.app.command.optitem;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.app.find.optitem.OptionalItemService;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.scherec.event.OptionalItemAtrExport;
import nts.uk.ctx.at.shared.dom.scherec.optitem.*;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.CalcResultRangeRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.Formula;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.FormulaRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.disporder.FormulaDispOrder;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.disporder.FormulaDispOrderRepository;
import nts.uk.ctx.at.shared.dom.scherec.service.AttendanceAtrService;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.LanguageConsts;

/**
 * The Class OptionalItemSaveCommandHandler.
 */
@Stateless
@Transactional
public class OptionalItemSaveCommandHandler extends CommandHandler<OptionalItemSaveCommand> {

	@Inject
	private OptionalItemPolicy optItemSv;

	@Inject
	private OptionalItemRepository optItemRepo;

	/** The repository. */
	@Inject
	private FormulaRepository formulaRepo;

	/** The order repo. */
	@Inject
	private FormulaDispOrderRepository orderRepo;
	
	@Inject
	private OptionalItemNameOtherRepository itemNameOtherRepo;

    @Inject
    private OptionalItemService optItemService;
    
    @Inject
    private CalcResultRangeRepository calcRepo;

	@Inject
	private AttendanceAtrService attendanceAtrService;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.arc.layer.app.command.CommandHandler#handle(nts.arc.layer.app.command
	 * .CommandHandlerContext)
	 */
	@Override
	protected void handle(CommandHandlerContext<OptionalItemSaveCommand> context) {
		// get company id
		String companyId = AppContexts.user().companyId();

		// Get command.
		OptionalItemSaveCommand command = context.getCommand();

		// Map to optionaItem domain
		OptionalItem dom = new OptionalItem(command);

		// Get optional item no
		int optionalItemNo = command.getOptionalItemNo().v();
		
		// Get calc result range
		CalcResultRange calResult = command.getInputControlSetting().getCalcResultRange();

		// Map to list domain Formula
		List<Formula> formulas = command.getFormulas().stream().map(item -> {
			return new Formula(item);
		}).collect(Collectors.toList());

		// Map to list domain FormulaDispOrder
		List<FormulaDispOrder> dispOrders = command.getFormulas().stream().map(item -> {
			return new FormulaDispOrder(item);
		}).collect(Collectors.toList());

		//insert or update en,vi..
		if(!command.getLangId().equals(LanguageConsts.DEFAULT_LANGUAGE_ID)) {
			if(itemNameOtherRepo.findByKey(companyId, optionalItemNo, command.getLangId()).isPresent()) {
				itemNameOtherRepo.update(new OptionalItemNameOther(new CompanyId(companyId),
						command.getOptionalItemNo(), command.getLangId(), command.getOptionalItemName()));
			}else {
				itemNameOtherRepo.add(new OptionalItemNameOther(new CompanyId(companyId),
						command.getOptionalItemNo(), command.getLangId(), command.getOptionalItemName()));
			}
			return;
		}
		
		// process data jp
		if (this.optItemSv.canRegister(dom, formulas)) {

		    if (this.optItemRepo.find(companyId, optionalItemNo).getOptionalItemAtr() != dom.getOptionalItemAtr()) {
		        this.optItemService.updateItemControl(dom.getPerformanceAtr().value, dom.getOptionalItemNo().v(), BigDecimal.ONE);
		    }
		    
			// update optional item.
			this.optItemRepo.update(dom);
			
			// update result range
//			this.calcRepo.update(companyId, optionalItemNo, calResult);

			// Remove all existing formulas
			this.formulaRepo.remove(companyId, optionalItemNo);
			this.orderRepo.remove(companyId, optionalItemNo);

			// Insert new formulas
			this.formulaRepo.create(formulas);
			this.orderRepo.create(dispOrders);

			// EA修正履歴NO.4071
			// EA修正履歴NO.4079
			this.attendanceAtrService.updateAttendanceAtr(new OptionalItemAtrExport(
					dom.getPerformanceAtr().value,
					dom.getOptionalItemAtr().value,
					dom.getOptionalItemNo().v(),
					dom.getUsageAtr() == OptionalItemUsageAtr.USE,
					dom.getCalcAtr() == CalculationClassification.CALC,
					dom.getInputControlSetting().isInputWithCheckbox()
			));
		}
	}

}
