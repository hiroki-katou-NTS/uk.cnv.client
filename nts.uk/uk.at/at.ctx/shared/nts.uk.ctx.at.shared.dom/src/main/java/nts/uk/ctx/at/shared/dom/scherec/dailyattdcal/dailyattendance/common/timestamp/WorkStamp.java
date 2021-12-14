package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp;

import java.util.Optional;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.calculationsetting.ActualStampOfPriorityClass;
import nts.uk.ctx.at.shared.dom.calculationsetting.AutoStampReflectionClass;
import nts.uk.ctx.at.shared.dom.calculationsetting.StampReflectionManagement;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author nampt
 * 勤怠打刻
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).日の勤怠計算.日別勤怠.Common.勤怠打刻
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class WorkStamp extends DomainObject{

	/*
	 * 時刻
	 */
	private WorkTimeInformation timeDay;
	
	/*
	 * 場所コード
	 */
	private Optional<WorkLocationCD> locationCode;
	
	/**
	 * 自動セットで作る
	 * @param time 	時刻
	 * @return
	 */
	public static WorkStamp createByAutomaticSet(TimeWithDayAttr time) {
		
		return new WorkStamp(
				WorkTimeInformation.createByAutomaticSet(time),
				Optional.empty());
	}
	

	public WorkStamp(TimeWithDayAttr timeWithDay, WorkLocationCD locationCode,
			TimeChangeMeans timeChangeMeans,EngravingMethod engravingMethod) {
		super();
		this.timeDay = new WorkTimeInformation(new ReasonTimeChange(timeChangeMeans, Optional.ofNullable(engravingMethod)), timeWithDay);
		this.locationCode = Optional.ofNullable(locationCode);
	}
	
	public WorkStamp(TimeWithDayAttr timeWithDay, WorkLocationCD locationCode,
			TimeChangeMeans timeChangeMeans) {
		super();
		this.timeDay = new WorkTimeInformation(new ReasonTimeChange(timeChangeMeans, Optional.empty()), timeWithDay);
		this.locationCode = Optional.ofNullable(locationCode);
	}
	
	
	public void setPropertyWorkStamp(TimeWithDayAttr timeWithDay, WorkLocationCD locationCode,
			TimeChangeMeans timeChangeMeans){
		this.timeDay = new WorkTimeInformation(new ReasonTimeChange(timeChangeMeans, Optional.empty()), timeWithDay);
		this.locationCode = Optional.ofNullable(locationCode);
		
	}
	
	public boolean isFromSPR() {
		return this.timeDay.getReasonTimeChange().getTimeChangeMeans() == TimeChangeMeans.SPR_COOPERATION;
	}
	
	public void setStampFromPcLogOn(TimeWithDayAttr PcLogOnStamp) {
		this.timeDay.setTimeWithDay(Optional.ofNullable(PcLogOnStamp));
	}

	public WorkStamp(WorkTimeInformation timeDay, Optional<WorkLocationCD> locationCode) {
		super();
		this.timeDay = timeDay;
		this.locationCode = locationCode;
	}
	
	/**
	 * ＜
	 * @param compareValue 比較値
	 * @return
	 */
	public boolean lessThan(WorkStamp compareValue) {
		if(this.getTimeDay().getTimeWithDay().isPresent() && compareValue.getTimeDay().getTimeWithDay().isPresent()) {
			return this.getTimeDay().getTimeWithDay().get().lessThan(compareValue.getTimeDay().getTimeWithDay().get());
		}

		return false;
	}
	
	public static WorkStamp createDefault() {
		return new WorkStamp(
				new WorkTimeInformation(
						new ReasonTimeChange(TimeChangeMeans.APPLICATION, Optional.empty()), null),
				Optional.empty());
	}
	
	/**
	 * 時刻を変更してもいいか判断する (new_2020)
	 */
	public boolean isCanChangeTime(Require require, String cid, TimeChangeMeans reasonTimeChangeNew) {
		//ドメインモデル「時刻の優先順位」を取得する
		Optional<StampReflectionManagement> optTimePriority =  require.findByCid(cid);
		//時刻変更手段と反映時刻優先もとに優先順位をチェックする
		TimeChangeMeans timeChangeMeansNew = reasonTimeChangeNew;
		TimeChangeMeans timeChangeMeansOld = this.getTimeDay().getReasonTimeChange().getTimeChangeMeans();
		//true 1	
		if (timeChangeMeansNew == TimeChangeMeans.HAND_CORRECTION_OTHERS
				|| timeChangeMeansNew == TimeChangeMeans.HAND_CORRECTION_PERSON
				|| timeChangeMeansNew == TimeChangeMeans.APPLICATION) {
			return true;
		}
		//true 2
		if((timeChangeMeansNew == TimeChangeMeans.REAL_STAMP || timeChangeMeansNew == TimeChangeMeans.SPR_COOPERATION )
			&& 	(timeChangeMeansOld == TimeChangeMeans.REAL_STAMP || timeChangeMeansOld == TimeChangeMeans.SPR_COOPERATION)) {
			return true;
		}
		//true 3
		if((timeChangeMeansNew == TimeChangeMeans.REAL_STAMP || timeChangeMeansNew == TimeChangeMeans.SPR_COOPERATION )
				&& 	timeChangeMeansOld == TimeChangeMeans.DIRECT_BOUNCE_APPLICATION
				&& 	(optTimePriority.isPresent() && optTimePriority.get().getActualStampOfPriorityClass() == ActualStampOfPriorityClass.ACTUAL_STAMP_PRIORITY)) {
			return true;
		}
		// true4
		if((timeChangeMeansNew == TimeChangeMeans.REAL_STAMP || timeChangeMeansNew == TimeChangeMeans.SPR_COOPERATION )
			&& 	(timeChangeMeansOld == TimeChangeMeans.DIRECT_BOUNCE || timeChangeMeansOld == TimeChangeMeans.AUTOMATIC_SET) 
			&& optTimePriority.get().getAutoStampReflectionClass() ==  AutoStampReflectionClass.STAMP_REFLECT) {
			return true;
		}
		//true 6
		if(timeChangeMeansNew == TimeChangeMeans.DIRECT_BOUNCE_APPLICATION
				&& (timeChangeMeansOld == TimeChangeMeans.DIRECT_BOUNCE_APPLICATION || timeChangeMeansOld == TimeChangeMeans.DIRECT_BOUNCE
					||timeChangeMeansOld == TimeChangeMeans.AUTOMATIC_SET)) {
			return true;
		}
		//true 5
		if(timeChangeMeansNew == TimeChangeMeans.DIRECT_BOUNCE_APPLICATION
				&& (timeChangeMeansOld == TimeChangeMeans.REAL_STAMP || timeChangeMeansOld == TimeChangeMeans.SPR_COOPERATION)
				&& (optTimePriority.isPresent() && optTimePriority.get().getActualStampOfPriorityClass() == ActualStampOfPriorityClass.GOSTRAIGHT_OR_BUSINESS_STAMP_PRIORITY)) {
			return true;
		}
		//true 7
		if((timeChangeMeansNew == TimeChangeMeans.DIRECT_BOUNCE || timeChangeMeansNew == TimeChangeMeans.AUTOMATIC_SET )
				&& 	(timeChangeMeansOld == TimeChangeMeans.DIRECT_BOUNCE || timeChangeMeansOld == TimeChangeMeans.AUTOMATIC_SET )) {
			return true;
		}
		return false;
	}
	
	public static interface Require{
		//StampReflectionManagementRepository
		Optional<StampReflectionManagement> findByCid(String companyId);
	}
	
	/**
	 * @author thanh_nx
	 *
	 *         勤怠打刻を変更する
	 */
	public void change(Require require, String companyId, Optional<TimeChangeMeans> timeChangeMeans,
			Optional<EngravingMethod> engravingMethod, Optional<TimeWithDayAttr> timeWithDay,
			Optional<WorkLocationCD> locationCode) {

		// アルゴリズム「時刻を変更してもいいか判断する」を実行する
		if(!timeChangeMeans.isPresent()) return;
		boolean change = this.isCanChangeTime(require, companyId,
				timeChangeMeans.get());
		if (!change)
			return;

		// 返自身の「勤怠打刻」を更新する
		this.getTimeDay().setTimeWithDay(timeWithDay);
		this.getTimeDay().getReasonTimeChange().setTimeChangeMeans(timeChangeMeans.orElse(null));
		this.getTimeDay().getReasonTimeChange().setEngravingMethod(engravingMethod);
		this.setLocationCode(locationCode);

		return;
	}
	
	public Optional<TimeWithDayAttr> getWithTimeDay(){
		return this.getTimeDay().getTimeWithDay();
	}
}
