package nts.uk.ctx.at.request.app.command.application.appabsence;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.at.request.dom.application.AppReason;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository_New;
import nts.uk.ctx.at.request.dom.application.appabsence.AllDayHalfDayLeaveAtr;
import nts.uk.ctx.at.request.dom.application.appabsence.AppAbsence;
import nts.uk.ctx.at.request.dom.application.appabsence.AppAbsenceRepository;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.after.DetailAfterUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.DetailBeforeUpdate;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;

public class UpdateAppAbsenceCommandHandler extends CommandHandlerWithResult<UpdateAppAbsenceCommand, List<String>>{
	@Inject
	private AppAbsenceRepository appAbsenceRepository;
	@Inject
	private DetailBeforeUpdate detailBeforeUpdate;
	@Inject
	private ApplicationRepository_New applicationRepository;
	@Inject
	private DetailAfterUpdate detailAfterUpdate;
	@Override
	protected List<String> handle(CommandHandlerContext<UpdateAppAbsenceCommand> context) {
		String companyID = AppContexts.user().companyId();
		UpdateAppAbsenceCommand command = context.getCommand();
		Optional<AppAbsence> opAppAbsence = this.appAbsenceRepository.getAbsenceByAppId(companyID, command.getAppID());
		if(!opAppAbsence.isPresent()){
			throw new BusinessException("Msg_198");
		}
		AppAbsence appAbsence = opAppAbsence.get();
		appAbsence.setAllDayHalfDayLeaveAtr(EnumAdaptor.valueOf(command.getAllDayHalfDayLeaveAtr(), AllDayHalfDayLeaveAtr.class));
		appAbsence.setChangeWorkHour(command.isChangeWorkHour());
		appAbsence.setStartTime1(command.getStartTime1() == null ? null : new TimeWithDayAttr(command.getStartTime1()));
		appAbsence.setEndTime1(command.getEndTime1() == null ? null : new TimeWithDayAttr(command.getEndTime1()));
		appAbsence.setStartTime2(command.getStartTime2() == null ? null : new TimeWithDayAttr(command.getStartTime2()));
		appAbsence.setEndTime2(command.getEndTime2() == null ? null : new TimeWithDayAttr(command.getEndTime2()));
		appAbsence.setWorkTypeCode(command.getWorkTypeCode() == null ? null : new WorkTypeCode(command.getWorkTypeCode()));
		appAbsence.setWorkTimeCode(command.getWorkTimeCode() == null ? null : new WorkTimeCode(command.getWorkTimeCode()));
		String applicationReason = command.getApplicationReason().replaceFirst(":", System.lineSeparator());
		appAbsence.getApplication().setAppReason(new AppReason(applicationReason));
		appAbsence.setVersion(appAbsence.getVersion());
		appAbsence.getApplication().setVersion(command.getVersion());
		
		
		//6.休暇申請（詳細）登録
		// 4-1.詳細画面登録前の処理
		detailBeforeUpdate.processBeforeDetailScreenRegistration(
				companyID, 
				appAbsence.getApplication().getEmployeeID(), 
				appAbsence.getApplication().getAppDate(), 
				1, 
				appAbsence.getAppID(), 
				appAbsence.getApplication().getPrePostAtr(), command.getVersion());
		//update appAbsence
		appAbsenceRepository.updateAbsence(appAbsence);
		//update application
		applicationRepository.updateWithVersion(appAbsence.getApplication());
		// 4-2.詳細画面登録後の処理
		return detailAfterUpdate.processAfterDetailScreenRegistration(appAbsence.getApplication());
	}

}
