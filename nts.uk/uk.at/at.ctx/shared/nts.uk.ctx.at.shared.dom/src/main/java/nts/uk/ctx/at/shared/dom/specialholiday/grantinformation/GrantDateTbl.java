package nts.uk.ctx.at.shared.dom.specialholiday.grantinformation;

import java.util.ArrayList;
//import java.util.HashSet;
import java.util.List;
//import java.util.Set;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayCode;

/**
 * 特別休暇付与日数テーブル
 * @author masaaki_jinno
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GrantDateTbl extends AggregateRoot {

	/** 会社ID */
	private String companyId;

	/** 特別休暇コード */
	private SpecialHolidayCode specialHolidayCode;

	/** コード */
	private GrantDateCode grantDateCode;

	/** 名称 */
	@Setter
	private GrantDateName grantDateName;

	/** 付与日数  */
	@Setter
	private List<GrantElapseYearMonth> elapseYear;

	/** 規定のテーブルとする */
	@Setter
	private boolean isSpecified;

	/** テーブル以降の付与日数 */
	@Setter
	private Optional<GrantedDays> grantedDays;

	@Override
	public void validate() {
		super.validate();
	}
	
	// 経過年数テーブルより多いテーブルを削除する
	public void deleteMoreTableThanElapsedYearsTable(int numOfElapsedYears) {
		if (this.elapseYear.size() > numOfElapsedYears) {
			this.elapseYear.removeIf(e -> e.getElapseNo() > numOfElapsedYears);
		}
	}
	
	// 経過年数テーブルより少ない分のテーブルを追加する
	public void addLessTableThanElapsedYearsTable(int numOfElapsedYears) {
		for (int numOfGrants = this.elapseYear.size() + 1; numOfGrants <= numOfElapsedYears; numOfGrants++) {
			GrantElapseYearMonth grantElapseYearMonth = new GrantElapseYearMonth(numOfGrants, new GrantedDays(0));
			this.elapseYear.add(grantElapseYearMonth);
		}
	}
	
	

	/**
	 * Create from Java Type
	 * @param companyId 会社ID
	 * @param specialHolidayCode 特別休暇コード
	 * @param grantDateCode 付与テーブルコード
	 * @param grantDateName 付与テーブル名称
	 * @param isSpecified 規定のテーブルとする
	 * @param numberOfDays テーブル以降の付与日数
	 * @return
	 */
	public static GrantDateTbl createFromJavaType(
			String companyId,
			int specialHolidayCode,
			String grantDateCode,
			String grantDateName,
			boolean isSpecified,
			Integer numberOfDays) {
		return new GrantDateTbl(companyId,
				new SpecialHolidayCode(specialHolidayCode),
				new GrantDateCode(grantDateCode),
				new GrantDateName(grantDateName),
				new ArrayList<GrantElapseYearMonth>(),
				isSpecified,
				Optional.ofNullable(numberOfDays == null ? 
						null : new GrantedDays(numberOfDays))
				);
	}
	
	/**
	 * 付与日数を取得する
	 * @param elapseNo
	 * @return
	 */
	public Optional<GrantedDays> getGrantDays(int elapseNo){
		
		Optional<GrantedDays> grantDays = elapseYear.stream().filter(c -> c.getElapseNo() == elapseNo)
				.map(x -> x.getGrantedDays()).findFirst();

		if(grantDays.isPresent()){
			return grantDays;
		}
		
		if(!this.getGrantedDays().isPresent()){
			return Optional.empty();
		}
		
		return this.getGrantedDays();
	}

}
