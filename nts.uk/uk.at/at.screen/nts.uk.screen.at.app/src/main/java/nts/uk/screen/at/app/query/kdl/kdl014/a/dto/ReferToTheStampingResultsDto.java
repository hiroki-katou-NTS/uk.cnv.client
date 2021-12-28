package nts.uk.screen.at.app.query.kdl.kdl014.a.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
【output】

・Map表示（利用する/利用しない）
・マップ表示アドレス
・List
　社員コード　　社員情報．社員コード
　社員名　　　　社員情報．ビジネスネーム
　日時　　　　　List＜社員の打刻情報＞．打刻日時
　打刻手段　　　List＜社員の打刻情報＞．打刻する方法．打刻手段
　打刻区分　　　List＜社員の打刻情報＞．打刻情報．打刻区分
　打刻場所　　　勤務場所．勤務場所名称
　打刻位置情報　List＜社員の打刻情報＞．勤務場所情報．打刻位置情報
*/

@Data
@AllArgsConstructor
public class ReferToTheStampingResultsDto {

	public ReferToTheStampingResultsDto() {
	}

	private boolean display;
	private String address;
	
	private boolean checkMobile;
	
	private List<EmpInfomationDto> listEmps;
	
}
