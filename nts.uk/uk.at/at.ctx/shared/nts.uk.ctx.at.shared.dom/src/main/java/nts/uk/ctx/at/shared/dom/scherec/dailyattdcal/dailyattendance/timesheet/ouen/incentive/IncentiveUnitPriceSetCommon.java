package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.incentive;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.WorkingHoursUnitPrice;

@Getter
public abstract class IncentiveUnitPriceSetCommon extends AggregateRoot {
	
	/** 会社ID: 会社ID */
	protected String companyId;
	
	/** インセンティブ単価: 作業別インセンティブ単価の設定 */
	protected List<IncentiveUnitPriceSetEachWork> unitPriceSet;
	
	protected IncentiveUnitPriceSetCommon(String companyId) {
		super();
		this.companyId = companyId;
		this.unitPriceSet = new ArrayList<>();
	}
	
	public void addUnitPriceSet(IncentiveUnitPriceSetEachWork useHis) {
	
		this.unitPriceSet.add(useHis);
	}
	
	/**
	 * インセンティブ単価を取得する
	 * @param workGroup
	 * @return
	 */
	public WorkingHoursUnitPrice getIncentiveUnitPrice(WorkGroup workGroup, GeneralDate baseDate) {
		Optional<IncentiveUnitPriceSetEachWork> unitPriceSet = getUnitPriceSet(workGroup);
		if(!unitPriceSet.isPresent())
			return new WorkingHoursUnitPrice(0);
			
		Optional<IncentiveUnitPriceSetHis> unitPriceSetHis = unitPriceSet.get().getPriceSetHis(baseDate);
		if(!unitPriceSetHis.isPresent())
			return new WorkingHoursUnitPrice(0);
		
		return unitPriceSetHis.get().getPriceUnit();
	}
	
	/**
	 * 作業別インセンティブ単価の設定を取得する
	 * @param workGroup
	 * @return
	 */
	public Optional<IncentiveUnitPriceSetEachWork> getUnitPriceSet(WorkGroup workGroup) {
		//2020.08.24 ichioka 作業枠が確定するまで仮実装。階層で設定しているかどうかの判断処理を作成する必要がある。
		boolean isHierarchical = false;
		if(isHierarchical) {
			//部分一致
			return getPartialMatch(workGroup);
		}
		//完全一致
		return getAllMatch(workGroup);
	}
	
	/**
	 * 部分一致で取得する
	 * @param workGroup
	 * @return
	 */
	public Optional<IncentiveUnitPriceSetEachWork> getPartialMatch(WorkGroup workGroup) {
		//作業枠を後ろから一つずつ削除して、一致する設定を取得する
		for(int i = workGroup.getWorkCount(); i < 1; i--) {
			Optional<IncentiveUnitPriceSetEachWork> unitPriceSet = getAllMatch(workGroup.reCreateUpToWorkFrame(i));
			if(unitPriceSet.isPresent())
				return unitPriceSet;
		}
		return Optional.empty();
	}
	
	/**
	 * 指定した作業グループと完全一致する設定を取得する
	 * @param workGroup
	 * @return
	 */
	public Optional<IncentiveUnitPriceSetEachWork> getAllMatch(WorkGroup workGroup) {
		return this.unitPriceSet.stream()
			.filter(t -> t.getWork().equals(workGroup))
			.findFirst();
	}
}
