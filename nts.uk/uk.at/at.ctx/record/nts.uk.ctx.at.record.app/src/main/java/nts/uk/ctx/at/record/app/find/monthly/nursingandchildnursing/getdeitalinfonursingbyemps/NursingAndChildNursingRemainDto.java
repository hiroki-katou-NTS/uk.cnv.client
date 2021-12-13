package nts.uk.ctx.at.record.app.find.monthly.nursingandchildnursing.getdeitalinfonursingbyemps;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 看護・介護残数DTO
 * 
 * @author tutk
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class NursingAndChildNursingRemainDto {

	/**
	 * 上限制限開始日
	 */
	private String upperLimitStartDate;

	/**
	 * 上限制限終了日
	 */
	private String upperLimitEndDate;

	/**
	 * 上限日数
	 */
	private String maxNumberOfDays;

	/**
	 * 使用数
	 */
	private String numberOfUse;

	/**
	 * 年間上限数
	 */
	private String maxNumberOfYear;

	/**
	 * 消化詳細一覧
	 */
	private List<DigestionDetails> listDigestionDetails = new ArrayList<>();

	/**
	 * 管理区分
	 */
	private boolean managementSection;

	public NursingAndChildNursingRemainDto() {
		this.upperLimitStartDate = "";
		this.upperLimitEndDate = "";
		this.maxNumberOfDays = "";
		this.numberOfUse = "";
		this.maxNumberOfYear = "";
		this.listDigestionDetails = new ArrayList<>();
		this.managementSection = false;
	}

}
