package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.CompanyHolidayMngSetting;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.service.RemainCreateInforByApplicationData;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.service.RemainCreateInforByRecordData;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.service.RemainCreateInforByScheData;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

@Stateless
public class InterimRemainDataMngRegisterDateChangeImpl implements InterimRemainDataMngRegisterDateChange{
	@Inject
	private RemainCreateInforByScheData remainScheData;
	@Inject
	private RemainCreateInforByRecordData remainRecordData;
	@Inject
	private RemainCreateInforByApplicationData remainAppData;
	@Inject
	private InterimRemainDataMngRegister mngRegister;
	@Inject
	private ComSubstVacationRepository subRepos;
	@Inject
	private CompensLeaveComSetRepository leaveSetRepos;
	@Override
	public void registerDateChange(String cid, String sid, List<GeneralDate> lstDate) {
		//「残数作成元情報(実績)」を取得する
		List<RecordRemainCreateInfor> lstRecordData = remainRecordData.lstRecordRemainData(cid, sid, lstDate);
		//「残数作成元の勤務予定を取得する」
		List<ScheRemainCreateInfor> lstScheData = remainScheData.createRemainInfor(cid, sid, lstDate);
		//「残数作成元の申請を取得する」
		List<AppRemainCreateInfor> lstAppData = remainAppData.lstRemainDataFromApp(cid, sid, lstDate);
		//雇用履歴と休暇管理設定を取得する
		Optional<ComSubstVacation> comSetting = subRepos.findById(cid);
		CompensatoryLeaveComSetting leaveComSetting = leaveSetRepos.find(cid);
		CompanyHolidayMngSetting comHolidaySetting = new CompanyHolidayMngSetting(cid, comSetting, leaveComSetting);
		
		for (GeneralDate loopDate : lstDate) {
			DatePeriod datePeriod = new DatePeriod(loopDate, loopDate);
			//指定期間の暫定残数管理データを作成する
			InterimRemainCreateDataInputPara inputData = new InterimRemainCreateDataInputPara(cid, 
					sid, 
					datePeriod, 
					lstRecordData, 
					lstScheData,
					lstAppData,
					false);
			mngRegister.registryInterimDataMng(inputData, comHolidaySetting);
		}
	}

}
