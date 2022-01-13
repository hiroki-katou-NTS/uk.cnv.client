package nts.uk.screen.at.app.ktgwidget.find.dto;

import lombok.Builder;
import lombok.Data;

/**
 * KTG001表示年月
 *
 */
@Data
@Builder
public class WidgetInitialDisplayMonthDto {
	//当月翌月区分
	private int currentOrNextMonth;
	
	// 締めID
	private int closureId;
	
	// 当月処理月
	private int currentMonth;
	
	// 当月開始日
	private String currentMonthStart;
	
	// 当月終了日
	private String currentMonthEnd;
	
	// 翌月処理月
	private int nextMonth;
	
	// 翌月開始日
	private String nextMonthStart;
	
	// 翌月終了日
	private String nextMonthEnd;
}
