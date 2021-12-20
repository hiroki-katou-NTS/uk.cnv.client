package nts.uk.ctx.at.shared.dom.specialholiday.grantinformation;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * テーブル以降の付与周期
 * @author masaaki_jinno
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class GrantCycleAfterTbl {

	/**
	 * 付与周期
	 */
	private ElapseYearMonth elapseYearMonth;
	
	/**
	 * コンストラクタ
	 */
	public GrantCycleAfterTbl(){
		elapseYearMonth = new ElapseYearMonth();
	}
	
	public static GrantCycleAfterTbl createFromJavaType(int year, int month){
		return new GrantCycleAfterTbl(ElapseYearMonth.createFromJavaType(year, month) );
	}
	
	/**
	 * 指定した付与回数までの経過年数テーブルを取得する
	 * @param beforeElapseYearMonthTbl
	 * @param grantcnt
	 * @return
	 */
	public List<ElapseYearMonthTbl> getElapseYearMonthTbltheGrantCnt(
			ElapseYearMonthTbl beforeElapseYearMonthTbl,
			int grantcnt){
		
		List<ElapseYearMonthTbl> elapseYearMonthTbl = new ArrayList<>();
		
		ElapseYearMonth elapseYearMonth = beforeElapseYearMonthTbl.getElapseYearMonth();
		
		for(int i = beforeElapseYearMonthTbl.getGrantCnt()+1; i <= grantcnt; i++){
			elapseYearMonth = elapseYearMonth.addElapseYearMonth(this.getElapseYearMonth());
			elapseYearMonthTbl.add(new ElapseYearMonthTbl(i, elapseYearMonth));
		}
		return elapseYearMonthTbl;
	}
	
}
