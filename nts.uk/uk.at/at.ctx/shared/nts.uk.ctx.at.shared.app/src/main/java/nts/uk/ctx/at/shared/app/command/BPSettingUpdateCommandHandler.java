package nts.uk.ctx.at.shared.app.command;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.bonuspay.services.BonusPaySettingService;
import nts.uk.ctx.at.shared.dom.bonuspay.setting.BonusPaySetting;
import nts.uk.ctx.at.shared.dom.bonuspay.setting.BonusPayTimesheet;
import nts.uk.ctx.at.shared.dom.bonuspay.setting.SpecBonusPayTimesheet;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class BPSettingUpdateCommandHandler extends CommandHandler<BPSettingUpdateCommand> {
	@Inject
	private BonusPaySettingService bonusPaySettingService;

	@Override
	protected void handle(CommandHandlerContext<BPSettingUpdateCommand> context) {
		String companyId = AppContexts.user().companyId();
		BPSettingUpdateCommand bpSettingUpdateCommand = context.getCommand();
		bonusPaySettingService.updateBonusPaySetting(this.toBonusPaySettingDomain(bpSettingUpdateCommand,companyId));
	}

	private BonusPaySetting toBonusPaySettingDomain(BPSettingUpdateCommand bpSettingUpdateCommand,String companyId) {
		List<BPTimesheetUpdateCommand> lstBPTimesheetUpdateCommand = bpSettingUpdateCommand.getLstBonusPayTimesheet();
		List<BonusPayTimesheet> lstBonusPayTimesheet = lstBPTimesheetUpdateCommand.stream()
				.map(c -> toBonusPayTimesheetDomain(c)).collect(Collectors.toList());
		List<SpecBPTimesheetUpdateCommand> lstSpecBPTimesheetUpdateCommand = bpSettingUpdateCommand
				.getLstSpecBonusPayTimesheet();
		List<SpecBonusPayTimesheet> lstSpecBonusPayTimesheet = lstSpecBPTimesheetUpdateCommand.stream()
				.map(c -> toSpecBonusPayTimesheetDomain(c)).collect(Collectors.toList());
		
		return BonusPaySetting.createFromJavaType(companyId,
				bpSettingUpdateCommand.code, bpSettingUpdateCommand.name,lstBonusPayTimesheet,lstSpecBonusPayTimesheet);
//		bonusPaySetting.setListTimesheet(lstBonusPayTimesheet);
//		bonusPaySetting.setListSpecialTimesheet(lstSpecBonusPayTimesheet);
	}

	private BonusPayTimesheet toBonusPayTimesheetDomain(BPTimesheetUpdateCommand bpTimesheetUpdateCommand) {
		return BonusPayTimesheet.createFromJavaType(bpTimesheetUpdateCommand.timeSheetNO,
				bpTimesheetUpdateCommand.useAtr, bpTimesheetUpdateCommand.timeItemId,
				Long.valueOf(bpTimesheetUpdateCommand.startTime), Long.valueOf(bpTimesheetUpdateCommand.endTime),
				bpTimesheetUpdateCommand.roundingTimeAtr, bpTimesheetUpdateCommand.roundingAtr);
	}

	private SpecBonusPayTimesheet toSpecBonusPayTimesheetDomain(
			SpecBPTimesheetUpdateCommand specBPTimesheetUpdateCommand) {
		return SpecBonusPayTimesheet.createFromJavaType(specBPTimesheetUpdateCommand.timeSheetNO,
				specBPTimesheetUpdateCommand.useAtr, specBPTimesheetUpdateCommand.timeItemId,
				Long.valueOf(specBPTimesheetUpdateCommand.startTime),
				Long.valueOf(specBPTimesheetUpdateCommand.endTime), specBPTimesheetUpdateCommand.roundingTimeAtr,
				specBPTimesheetUpdateCommand.roundingAtr, specBPTimesheetUpdateCommand.specialDateItemNO);

	}

}
