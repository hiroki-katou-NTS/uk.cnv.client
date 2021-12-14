package nts.uk.ctx.at.request.dom.application.applist.service.detail;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * get info detail application
 * 
 * @author hoatt
 *
 */
@Stateless
public class AppDetailInfoImpl implements AppDetailInfoRepository {
	
	@Inject
	private WorkTypeRepository repoWorkType;
	@Inject
	private WorkTimeSettingRepository repoworkTime;

	/**
	 * convert time from integer to Time_Short_HM
	 * 
	 * @param time
	 * @return
	 */
	@Override
	public String convertTime(Integer time) {
		if (time == null) {
			return "";
		}
		TimeWithDayAttr timeConvert = new TimeWithDayAttr(time);
		return timeConvert.getDayDivision().description + timeConvert.getInDayTimeWithFormat();
	}

	/**
	 * 勤務就業名称を作成 - WorkType
	 * 
	 * @param lstWkType
	 * @param wkTypeCd
	 * @return
	 */
	@Override
	public String findWorkTypeName(List<WorkType> lstWkType, String wkTypeCd) {
		if(wkTypeCd==null) {
			return Strings.EMPTY;
		}
		if (lstWkType.isEmpty()) {
			Optional<WorkType> wt = repoWorkType.findByPK(AppContexts.user().companyId(), wkTypeCd);
			return wt.isPresent() ? wt.get().getName().v() : "マスタ未登録";
		}
		for (WorkType workType : lstWkType) {
			if (workType.getWorkTypeCode().v().equals(wkTypeCd)) {
				return workType.getName().v();
			}
		}
		return "マスタ未登録";
	}

	/**
	 * 勤務就業名称を作成 - WorkTime
	 * 
	 * @param lstWkTime
	 * @param wkTimeCd
	 * @return
	 */
	@Override
	public String findWorkTimeName(List<WorkTimeSetting> lstWkTime, String wkTimeCd) {
		if(wkTimeCd==null) {
			return Strings.EMPTY;
		}
		if (lstWkTime.isEmpty()) {
			Optional<WorkTimeSetting> workTime = repoworkTime.findByCode(AppContexts.user().companyId(), wkTimeCd);
			return workTime.isPresent() ? workTime.get().getWorkTimeDisplayName().getWorkTimeName().v() : "マスタ未登録";
		}
		for (WorkTimeSetting workTime : lstWkTime) {
			if (workTime.getWorktimeCode().v().equals(wkTimeCd)) {
				return workTime.getWorkTimeDisplayName().getWorkTimeName().v();
			}
		}
		return "マスタ未登録";
	}

}
