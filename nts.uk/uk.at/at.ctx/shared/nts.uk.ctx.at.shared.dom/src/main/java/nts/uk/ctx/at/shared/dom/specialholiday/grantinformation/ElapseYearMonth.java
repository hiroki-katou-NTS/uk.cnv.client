package nts.uk.ctx.at.shared.dom.specialholiday.grantinformation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 経過年数
 * @author masaaki_jinno
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ElapseYearMonth {

	/** 年 */
	private int year;
	/** 月 */
	private int month;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ElapseYearMonth))
            return false;

        ElapseYearMonth mdc = (ElapseYearMonth) obj;
        return mdc.year == this.year && mdc.month == this.month;
    }

    public static ElapseYearMonth createFromJavaType(
			int year, int month) {
		return new ElapseYearMonth(year, month);
	}
    
    /**
     * 付与周期を加算する
     * @param addElapseYearMonth
     * @return
     */
    public ElapseYearMonth addElapseYearMonth(ElapseYearMonth addElapseYearMonth){
    	return new ElapseYearMonth(this.getYear() + addElapseYearMonth.getYear(), this.getMonth() + addElapseYearMonth.getMonth());
    }
}
