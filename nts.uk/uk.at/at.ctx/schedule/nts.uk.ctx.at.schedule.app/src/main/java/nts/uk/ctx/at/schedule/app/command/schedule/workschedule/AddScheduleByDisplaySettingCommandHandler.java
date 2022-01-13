package nts.uk.ctx.at.schedule.app.command.schedule.workschedule;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.schedule.dom.displaysetting.DisplayRangeType;
import nts.uk.ctx.at.schedule.dom.displaysetting.DisplaySettingByDate;
import nts.uk.ctx.at.schedule.dom.displaysetting.DisplaySettingByDateForOrgRepository;
import nts.uk.ctx.at.schedule.dom.displaysetting.DisplaySettingByDateForOrganization;
import nts.uk.ctx.at.schedule.dom.displaysetting.DisplayStartTime;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.shr.com.context.AppContexts;

/**
 * «Command» 組織別スケジュール修正日付別の表示設定を登録する
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務予定.表示設定.App.組織別スケジュール修正日付別の表示設定を登録する
 * 
 * @author HieuLt
 *
 */
@Stateless
public class AddScheduleByDisplaySettingCommandHandler extends CommandHandler<AddScheduleByDisplaySettingCommand> {

	@Inject
	private DisplaySettingByDateForOrgRepository repo;
	
	@Override
	protected void handle(CommandHandlerContext<AddScheduleByDisplaySettingCommand> context) {
		AddScheduleByDisplaySettingCommand command = context.getCommand();
		String companyId = AppContexts.user().companyId();
		TargetOrgIdenInfor targetOrgIdenInfor = null;
		// 1: <call>()
		if (command.targetUnit == TargetOrganizationUnit.WORKPLACE_GROUP.value) {
			targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplaceGroup(command.organizationID);
		} else {
			targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplace(command.organizationID);
		}
		// 2:作る(表示の範囲, 表示の開始時刻, 初期表示の開始時刻):スケジュール修正日付別の表示設定
		DisplaySettingByDate displaySettingByDate = DisplaySettingByDate.create(
				EnumAdaptor.valueOf(command.dispRange, DisplayRangeType.class), new DisplayStartTime(command.startTime),
				new DisplayStartTime(command.initDispStart));
		// 3:get(対象組織):Optional<組織別スケジュール修正日付別の表示設定> 
		Optional<DisplaySettingByDateForOrganization> displaySettingForOrg = repo.get(companyId, targetOrgIdenInfor);
		
		DisplaySettingByDateForOrganization dispSetOrg = new DisplaySettingByDateForOrganization(targetOrgIdenInfor, displaySettingByDate);
		//4: Optional<組織別スケジュール修正日付別の表示設定>.isPresent
		 if(displaySettingForOrg.isPresent()){
			 //set
			 repo.update(companyId, dispSetOrg); 
		 }else{
			 //create
			 repo.insert(companyId, dispSetOrg); 
		 }
	}

}
