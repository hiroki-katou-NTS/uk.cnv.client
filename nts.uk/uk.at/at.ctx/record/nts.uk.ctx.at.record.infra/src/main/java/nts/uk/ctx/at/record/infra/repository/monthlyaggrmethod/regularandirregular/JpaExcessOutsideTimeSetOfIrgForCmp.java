package nts.uk.ctx.at.record.infra.repository.monthlyaggrmethod.regularandirregular;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.monthlyaggrmethod.regularandirregular.ExcessOutsideTimeSet;
import nts.uk.ctx.at.record.dom.monthlyaggrmethod.regularandirregular.ExcessOutsideTimeSetOfIrgForCmpRepository;
import nts.uk.ctx.at.record.infra.entity.monthlyaggrmethod.KrcstMonsetCmpRegAggrPK;
import nts.uk.ctx.at.record.infra.entity.monthlyaggrmethod.company.KrcstMonsetCmpIrgExot;

/**
 * リポジトリ実装：会社の変形労働時間勤務の時間外超過設定
 * @author shuichu_ishida
 */
@Stateless
public class JpaExcessOutsideTimeSetOfIrgForCmp extends JpaRepository implements ExcessOutsideTimeSetOfIrgForCmpRepository {

	/** 追加 */
	@Override
	public void insert(String companyId, ExcessOutsideTimeSet excessOutsideTimeSetting) {
		this.commandProxy().insert(toEntity(companyId, excessOutsideTimeSetting, false));
	}
	
	/** 更新 */
	@Override
	public void update(String companyId, ExcessOutsideTimeSet excessOutsideTimeSetting) {
		this.toEntity(companyId, excessOutsideTimeSetting, true);
	}
	
	/**
	 * ドメイン→エンティティ
	 * @param companyId キー値：会社ID
	 * @param domain ドメイン：時間外超過設定
	 * @param execUpdate 更新を実行する
	 * @return エンティティ：会社の変形労働時間勤務の時間外超過設定
	 */
	private KrcstMonsetCmpIrgExot toEntity(String companyId,
			ExcessOutsideTimeSet domain, boolean execUpdate){

		// キー
		val key = new KrcstMonsetCmpRegAggrPK(companyId);
		
		// 1日の基準時間未満の残業時間の扱い
		val treatOverTimeOfLessThanCriteriaPerDay = domain.getTreatOverTimeOfLessThanCriteriaPerDay();
		// 1週間の基準時間未満の休日出勤時間の扱い
		val treatHolidayWorkTimeOfLessThanCriteriaPerWeek = domain.getTreatHolidayWorkTimeOfLessThanCriteriaPerWeek();
		
		KrcstMonsetCmpIrgExot entity;
		if (execUpdate){
			entity = this.queryProxy().find(key, KrcstMonsetCmpIrgExot.class).get();
		}
		else {
			entity = new KrcstMonsetCmpIrgExot();
			entity.PK = key;
		}
		entity.setValue.exceptLegalHolidayWorkTime =
				(domain.isAutoExcludeHolidayWorkTimeFromExcessOutsideWorkTime() ? 1 : 0);
				
		int atrAutoExcludeOverTime = 1;
		for (val autoExcludeOverTimeFrame : treatOverTimeOfLessThanCriteriaPerDay.getAutoExcludeOverTimeFrames()){
			switch(autoExcludeOverTimeFrame.v().intValue()){
			case 1:
				entity.setValue.treatOverTime01 = atrAutoExcludeOverTime;
				break;
			case 2:
				entity.setValue.treatOverTime02 = atrAutoExcludeOverTime;
				break;
			case 3:
				entity.setValue.treatOverTime03 = atrAutoExcludeOverTime;
				break;
			case 4:
				entity.setValue.treatOverTime04 = atrAutoExcludeOverTime;
				break;
			case 5:
				entity.setValue.treatOverTime05 = atrAutoExcludeOverTime;
				break;
			case 6:
				entity.setValue.treatOverTime06 = atrAutoExcludeOverTime;
				break;
			case 7:
				entity.setValue.treatOverTime07 = atrAutoExcludeOverTime;
				break;
			case 8:
				entity.setValue.treatOverTime08 = atrAutoExcludeOverTime;
				break;
			case 9:
				entity.setValue.treatOverTime09 = atrAutoExcludeOverTime;
				break;
			case 10:
				entity.setValue.treatOverTime10 = atrAutoExcludeOverTime;
				break;
			}
		}
		
		int atrLegalOverTime = 2;
		for (val legalOverTimeFrame : treatOverTimeOfLessThanCriteriaPerDay.getLegalOverTimeFrames()){
			switch(legalOverTimeFrame.v().intValue()){
			case 1:
				entity.setValue.treatOverTime01 = atrLegalOverTime;
				break;
			case 2:
				entity.setValue.treatOverTime02 = atrLegalOverTime;
				break;
			case 3:
				entity.setValue.treatOverTime03 = atrLegalOverTime;
				break;
			case 4:
				entity.setValue.treatOverTime04 = atrLegalOverTime;
				break;
			case 5:
				entity.setValue.treatOverTime05 = atrLegalOverTime;
				break;
			case 6:
				entity.setValue.treatOverTime06 = atrLegalOverTime;
				break;
			case 7:
				entity.setValue.treatOverTime07 = atrLegalOverTime;
				break;
			case 8:
				entity.setValue.treatOverTime08 = atrLegalOverTime;
				break;
			case 9:
				entity.setValue.treatOverTime09 = atrLegalOverTime;
				break;
			case 10:
				entity.setValue.treatOverTime10 = atrLegalOverTime;
				break;
			}
		}
		
		int atrAutoExcludeHolidayWork = 1;
		for (val autoExcludeHolidayWorkFrame : treatHolidayWorkTimeOfLessThanCriteriaPerWeek.getAutoExcludeHolidayWorkFrames()){
			switch(autoExcludeHolidayWorkFrame.v().intValue()){
			case 1:
				entity.setValue.treatHolidayWorkTime01 = atrAutoExcludeHolidayWork;
				break;
			case 2:
				entity.setValue.treatHolidayWorkTime02 = atrAutoExcludeHolidayWork;
				break;
			case 3:
				entity.setValue.treatHolidayWorkTime03 = atrAutoExcludeHolidayWork;
				break;
			case 4:
				entity.setValue.treatHolidayWorkTime04 = atrAutoExcludeHolidayWork;
				break;
			case 5:
				entity.setValue.treatHolidayWorkTime05 = atrAutoExcludeHolidayWork;
				break;
			case 6:
				entity.setValue.treatHolidayWorkTime06 = atrAutoExcludeHolidayWork;
				break;
			case 7:
				entity.setValue.treatHolidayWorkTime07 = atrAutoExcludeHolidayWork;
				break;
			case 8:
				entity.setValue.treatHolidayWorkTime08 = atrAutoExcludeHolidayWork;
				break;
			case 9:
				entity.setValue.treatHolidayWorkTime09 = atrAutoExcludeHolidayWork;
				break;
			case 10:
				entity.setValue.treatHolidayWorkTime10 = atrAutoExcludeHolidayWork;
				break;
			}
		}
				
		if (execUpdate) this.commandProxy().update(entity);
		return entity;
	}
}
