package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.incentive;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;

@Getter
/** 作業別インセンティブ単価の設定 */
public class IncentiveUnitPriceSetEachWork extends AggregateRoot {

	/** 作業グループ: 作業グループ */
	private WorkGroup work;
	
	/** 単価設定単位: 単価設定単位 */
	private List<IncentiveUnitPriceSetHis> useHis;
	
	private IncentiveUnitPriceSetEachWork(WorkGroup work) {
		super();
		this.work = work;
		this.useHis = new ArrayList<>();
	}

	public static IncentiveUnitPriceSetEachWork create(WorkGroup work) {
		
		return new IncentiveUnitPriceSetEachWork(work);
	}
	
	public void addHis(IncentiveUnitPriceSetHis useHis) {
	
		this.useHis.add(useHis);
	}
	
	/**
	 * 基準日から設定を取得する
	 * @param baseDate
	 * @return
	 */
	public Optional<IncentiveUnitPriceSetHis> getPriceSetHis(GeneralDate baseDate) {
		return this.useHis.stream()
				.filter(t -> t.getStartUseDate().beforeOrEquals(baseDate))
				.sorted((f,s) -> s.getStartUseDate().compareTo(f.getStartUseDate()))
				.findFirst();
	}
}
