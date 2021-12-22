package nts.uk.ctx.at.request.app.command.application.holidayshipment.refactor5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.app.command.application.kdl035.HolidayWorkAssociationStart;
import nts.uk.ctx.at.request.app.command.application.kdl035.Kdl035InputData;
import nts.uk.ctx.at.request.app.command.application.kdl035.Kdl035OutputData;
import nts.uk.ctx.at.request.app.command.application.kdl036.HolidayAssociationStart;
import nts.uk.ctx.at.request.app.command.application.kdl036.Kdl036InputData;
import nts.uk.ctx.at.request.app.command.application.kdl036.Kdl036OutputData;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.refactor5.dto.DisplayInforWhenStarting;
import nts.uk.ctx.at.request.dom.application.EmploymentRootAtr;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.DetailBeforeUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentApp;
import nts.uk.ctx.at.shared.app.find.remainingnumber.paymana.PayoutSubofHDManagementDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.subhdmana.dto.LeaveComDayOffManaDto;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManagement;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeUnit;

/**
 * @author thanhpv
 * @URL UKDesign.UniversalK.就業.KAF_申請.KAF011_振休振出申請.B：振休振出申請（詳細）.アルゴリズム.登録系処理.振休振出申請（詳細）登録前のチェック.振休振出申請（詳細）登録前のチェック
 * 
 */
@Stateless
public class PreUpdateErrorCheck {
	
	@Inject
	private PreRegistrationErrorCheck preRegistrationErrorCheck;
	
	@Inject
	private ErrorCheckProcessingBeforeRegistrationKAF011 errorCheckBeforeRegistrationKAF011;
	
	@Inject
	private CommonAlgorithm commonAlgorithm;
	
	@Inject
	private DetailBeforeUpdate detailBeforeUpdate;
	
	@Inject
    private HolidayAssociationStart holidayAssociationStart;
	
	@Inject
    private HolidayWorkAssociationStart holidayWorkAssociationStart;
	
	/**
	 * 振休振出申請（詳細）登録前のチェック
	 * @param companyId 申請者会社ID
	 * @param abs 振休申請
	 * @param rec 振出申請
	 * @param displayInforWhenStarting 振休振出申請起動時の表示情報
	 */
	public void errorCheck(String companyId, Optional<AbsenceLeaveApp> abs, Optional<RecruitmentApp> rec, DisplayInforWhenStarting displayInforWhenStarting, 
	        List<PayoutSubofHDManagement> payoutSubofHDManagements, List<LeaveComDayOffManagement> leaveComDayOffManagements, 
	        boolean checkFlag, List<WorkType> listWorkTypes) {
		//アルゴリズム「登録前エラーチェック（更新）」を実行する
		this.preRegistrationErrorCheck.preconditionCheck(abs, rec, 
		        Optional.ofNullable(displayInforWhenStarting.getApplicationForHoliday() == null ? null : displayInforWhenStarting.getApplicationForHoliday().getWorkInformationForApplication()), 
		        Optional.ofNullable(displayInforWhenStarting.getApplicationForWorkingDay() == null ? null : displayInforWhenStarting.getApplicationForWorkingDay().getWorkInformationForApplication()));
		
		//終日半日矛盾チェック
		this.preRegistrationErrorCheck.allDayAndHalfDayContradictionCheck(companyId, abs, rec);
		
		List<GeneralDate> dateLst = new ArrayList<>();
		List<String> workTypeLst = new ArrayList<>();
		if(rec.isPresent()) {
			dateLst.add(rec.get().getAppDate().getApplicationDate());
			workTypeLst.add(rec.get().getWorkInformation().getWorkTypeCode().v());
		}
		if(abs.isPresent()) {
			dateLst.add(abs.get().getAppDate().getApplicationDate());
			workTypeLst.add(abs.get().getWorkInformation().getWorkTypeCode().v());
		}
		//申請の矛盾チェック
		this.commonAlgorithm.appConflictCheck(companyId,
				displayInforWhenStarting.appDispInfoStartup.getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0),
				dateLst, workTypeLst,
				displayInforWhenStarting.appDispInfoStartup.toDomain().getAppDispInfoWithDateOutput()
						.getOpActualContentDisplayLst().orElse(new ArrayList<ActualContentDisplay>()));

		boolean existFlag = false;
		if (rec.isPresent() && abs.isPresent()) {
		    existFlag = true;
		}
		//振休残数不足チェック
//		 this.errorCheckBeforeRegistrationKAF011.checkForInsufficientNumberOfHolidays(companyId, rec.isPresent()?rec.get().getEmployeeID():abs.get().getEmployeeID(), abs, rec);
		// INPUT．振出申請がNULL　AND　INPUT．振休申請がNULLじゃない　AND　INPUT.振休申請.振出振休紐付け管理＝設定なし
        if (!rec.isPresent() && abs.isPresent() && checkFlag && payoutSubofHDManagements.isEmpty()) {
            throw new BusinessException("Msg_2223");
        }
	 
		 if(rec.isPresent()) {

			 //アルゴリズム「登録前共通処理（更新）」を実行する
			 this.detailBeforeUpdate.processBeforeDetailScreenRegistration(companyId, 
					 rec.get().getEmployeeID(), 
					 rec.get().getAppDate().getApplicationDate(), 
					 EmploymentRootAtr.APPLICATION.value, 
					 rec.get().getAppID(), 
					 rec.get().getPrePostAtr(), 
					 displayInforWhenStarting.appDispInfoStartup.getAppDetailScreenInfo().getApplication().getVersion(), 
					 null,
					 null,
					 displayInforWhenStarting.appDispInfoStartup.toDomain(), 
					 Arrays.asList(rec.get().getWorkInformation().getWorkTypeCode().v()), 
					 Optional.empty(), 
					 existFlag, 
					 Optional.of(rec.get().getWorkInformation().getWorkTypeCode().v()), 
					 rec.get().getWorkInformation().getWorkTimeCodeNotNull().map(WorkTimeCode::v));
		 }
		 if(abs.isPresent()) {
			 //アルゴリズム「登録前共通処理（更新）」を実行する
			 this.detailBeforeUpdate.processBeforeDetailScreenRegistration(companyId, 
					 abs.get().getEmployeeID(), 
					 abs.get().getAppDate().getApplicationDate(), 
					 EmploymentRootAtr.APPLICATION.value, 
					 abs.get().getAppID(), 
					 abs.get().getPrePostAtr(), 
					 displayInforWhenStarting.appDispInfoStartup.getAppDetailScreenInfo().getApplication().getVersion(), 
					 null,
					 null,
					 displayInforWhenStarting.appDispInfoStartup.toDomain(), 
					 Arrays.asList(abs.get().getWorkInformation().getWorkTypeCode().v()), 
					 Optional.empty(), 
					 existFlag, 
					 Optional.of(abs.get().getWorkInformation().getWorkTypeCode().v()), 
					 abs.get().getWorkInformation().getWorkTimeCodeNotNull().map(WorkTimeCode::v));
			 
			 Optional<WorkType> workType = listWorkTypes.stream().filter(x -> x.getWorkTypeCode().v().equals(abs.get().getWorkInformation().getWorkTypeCode().v())).findFirst();
			 if (workType.isPresent() && isHolidayWorkType(workType.get()) && !rec.isPresent()) {
                 // 休出代休関連付けダイアログ起動
                 Kdl036OutputData output = holidayAssociationStart.init(new Kdl036InputData(
                         abs.get().getEmployeeID(), 
                         abs.get().getAppDate().getApplicationDate(), 
                         abs.get().getAppDate().getApplicationDate(), 
                         workType.get().getDailyWork().getWorkTypeUnit().equals(WorkTypeUnit.OneDay) ? 1 : 0, 
                         1,
                         displayInforWhenStarting.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst(), 
                         new ArrayList<LeaveComDayOffManaDto>()));
                 
                 // データがある　AND　INPUT.振休申請.休出代休紐付け管理がEmpty 
                 if (!output.getHolidayWorkInfoList().isEmpty() && leaveComDayOffManagements.isEmpty()) {
                     throw new BusinessException("Msg_3255");
                 }
             }
			 
			 if (workType.isPresent() && isPauseWorkType(workType.get()) && !rec.isPresent()) {
			     // 振休振休関連付けダイアログ起動
			     Kdl035OutputData kdl035output = holidayWorkAssociationStart.init(new Kdl035InputData(
			             abs.get().getEmployeeID(), 
			             abs.get().getAppDate().getApplicationDate(), 
			             abs.get().getAppDate().getApplicationDate(), 
			             workType.get().getDailyWork().getWorkTypeUnit().equals(WorkTypeUnit.OneDay) ? 1 : 0, 
			                     1,
			                     displayInforWhenStarting.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst(), 
			                     new ArrayList<PayoutSubofHDManagementDto>()));
			     
			     if (!kdl035output.getSubstituteWorkInfoList().isEmpty() && payoutSubofHDManagements.isEmpty()) {
			         throw new BusinessException("Msg_2223");
			     }
			 }
		 }
		
	}
	
	public boolean isHolidayWorkType(WorkType workType) {
        WorkTypeUnit workTypeUnit = workType.getDailyWork().getWorkTypeUnit();
        if (workTypeUnit.equals(WorkTypeUnit.MonringAndAfternoon)) {
            return workType.getDailyWork().getMorning().equals(WorkTypeClassification.SubstituteHoliday) || workType.getDailyWork().getAfternoon().equals(WorkTypeClassification.SubstituteHoliday);
        }
        return false;
    }
	
	public boolean isPauseWorkType(WorkType workType) {
        WorkTypeUnit workTypeUnit = workType.getDailyWork().getWorkTypeUnit();
        if (workTypeUnit.equals(WorkTypeUnit.OneDay)) {
            return workType.getDailyWork().getOneDay().equals(WorkTypeClassification.Pause);
        } else {
            return workType.getDailyWork().getMorning().equals(WorkTypeClassification.Pause) || workType.getDailyWork().getAfternoon().equals(WorkTypeClassification.Pause);
        }
    }
	
}




