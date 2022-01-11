package nts.uk.ctx.at.shared.dom.specialholiday.grantinformation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayCode;

/**
 * 特別休暇付与経過年数テーブル
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ElapseYear extends AggregateRoot {
	
	/** 会社ID */
	private String companyId;

	/** 特別休暇コード */
	private SpecialHolidayCode specialHolidayCode;
	
	/** 経過年数テーブル */
	private List<ElapseYearMonthTbl> elapseYearMonthTblList;
	
	/** テーブル以降の固定付与をおこなう */
	private boolean fixedAssign;
	
	/** テーブル以降の付与周期 */
	private Optional<GrantCycleAfterTbl> grantCycleAfterTbl;
	
	
	@Override
	public void validate() {
		super.validate();
	}
	
	public List<String> validateInput() {
		
		List<String> errors = new ArrayList<>();
		List<YearMonth> yearMonth = new ArrayList<>();
		
		for (int i = 0; i < this.elapseYearMonthTblList.size(); i++) {
			ElapseYearMonthTbl elapseYearMonthTbl = this.elapseYearMonthTblList.get(i);
			YearMonth currentYearMonth = YearMonth.of(elapseYearMonthTbl.getElapseYearMonth().getYear(), elapseYearMonthTbl.getElapseYearMonth().getMonth());
			
			
			if (currentYearMonth.year() == 0 && currentYearMonth.month() == 0) {
				errors.add("Msg_95");
			}
	
			if (yearMonth.stream().anyMatch(x -> x.equals(currentYearMonth))) {
				errors.add("Msg_96");
			}
			
			yearMonth.add(currentYearMonth);
		}
		
		return errors;
	}
	
	// 	[1]付与日数テーブルの付与回数を合わせる
	public List<GrantDateTbl> matchNumberOfGrantsInGrantDaysTable(List<GrantDateTbl> listGrantDateTbl) {
		
		listGrantDateTbl.forEach(e -> {
			e.deleteMoreTableThanElapsedYearsTable(this.elapseYearMonthTblList.size());
			e.addLessTableThanElapsedYearsTable(this.elapseYearMonthTblList.size());
		});
		
		return listGrantDateTbl;
	}

	public static ElapseYear createFromJavaType(
			String companyId, 
			int specialHolidayCode, 
			boolean fixedAssign, 
			Integer years, Integer months ) {
		
		return new ElapseYear(
				companyId, 
				new SpecialHolidayCode(specialHolidayCode), 
				new ArrayList<ElapseYearMonthTbl>(),
				fixedAssign, 
				years == null && months == null ? Optional.empty() 
						: Optional.of(GrantCycleAfterTbl.createFromJavaType(years, months)));
	}
	
	/**
	 * 付与日を取得する
	 * @param baseDate
	 * @param elapseNo
	 * @return
	 */
	public Optional<GeneralDate> getGrantDate(GeneralDate baseDate, int elapseNo){
		
		List<ElapseYearMonthTbl> yearMonthTbl = this.getElapseYearMonthTbl(elapseNo);
		
		Optional<ElapseYearMonth> yearMonth = yearMonthTbl.stream()
				.filter(x->x.getGrantCnt() == elapseNo)
				.map(x -> x.getElapseYearMonth())
				.findFirst();
			
		if (yearMonth.isPresent()) {
			return Optional.of(baseDate.addYears(yearMonth.get().getYear())
					.addMonths(yearMonth.get().getMonth()));
		}
		return Optional.empty();
	}
	
	/**
	 * 指定した付与回数までの経過年数テーブルを取得する
	 * @param elapseNo
	 * @return
	 */
	private List<ElapseYearMonthTbl> getElapseYearMonthTbl(int elapseNo){
		if(this.getElapseYearMonthTblList().size() <= 0){
			return new ArrayList<>();
		}
		
		List<ElapseYearMonthTbl> tblList = this.getElapseYearMonthTblList().stream()
				.filter(x -> x.getGrantCnt() <= elapseNo).collect(Collectors.toList());
		
		if(tblList.isEmpty()){
			return new ArrayList<>();
		}
		
		ElapseYearMonthTbl lastTbl = tblList.stream()
				.sorted(Comparator.comparing(ElapseYearMonthTbl::getGrantCnt).reversed())
				.findFirst().get();		
		
		if(lastTbl.getGrantCnt() < elapseNo && this.getGrantCycleAfterTbl().isPresent()){
			tblList.addAll(this.getGrantCycleAfterTbl().get().getElapseYearMonthTbltheGrantCnt(lastTbl, elapseNo));
		}
		
		return tblList;
	}
	
}
