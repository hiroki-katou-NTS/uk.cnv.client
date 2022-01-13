package nts.uk.screen.at.app.find.ktg.ktg005.a;

import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author sonnlb
 *         UKDesign.UniversalK.就業.KTG_ウィジェット.KTG005_申請件数.アルゴリズム.申請件数起動.申請件数の実行結果
 */
@Data
@NoArgsConstructor
public class ExecutionResultNumberOfApplicationDto {

	// 申請状況の詳細設定
	List<ApplicationStatusDetailedSettingDto> appSettings = Collections.emptyList();
	
	// 申請締切利用設定
	boolean deadlineSetting = false;
	
	// 締め切り日
	GeneralDate dueDate = GeneralDate.today();
	
	// 承認件数
	int numberApprovals = 0;

	// 未承認件数
	int numberNotApprovals = 0;

	// 否認件数
	int numberDenials = 0;

	// 差し戻し件数
	int numberRemand = 0;
	
	// 名称
	String topPagePartName;
	
	// 勤怠担当者である
	boolean employeeCharge = false;
	
	DatePeriodDto period = new DatePeriodDto();

	public void setNumberOfApp(NumberOfAppDto number) {
		this.numberApprovals = number.getNumberApprovals();
		this.numberNotApprovals = number.getNumberNotApprovals();
		this.numberDenials = number.getNumberDenials();
		this.numberRemand = number.getNumberRemand();
	}

}
