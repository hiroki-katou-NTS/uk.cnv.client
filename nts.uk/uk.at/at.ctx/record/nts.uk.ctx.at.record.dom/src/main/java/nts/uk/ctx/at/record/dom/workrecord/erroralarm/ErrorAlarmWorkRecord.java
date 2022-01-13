/**
 * 9:37:57 AM Jul 21, 2017
 */
package nts.uk.ctx.at.record.dom.workrecord.erroralarm;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.ErrorAlarmCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.ErrorAlarmClassification;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.ErrorAlarmWorkRecordName;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 日別実績のエラーアラーム
 * @author hungnm 勤務実績のエラーアラーム
 */
@Getter
public class ErrorAlarmWorkRecord extends AggregateRoot {

	/* 会社ID */
	private String companyId;

	/* コード */
	private ErrorAlarmWorkRecordCode code;

	/* 名称 */
	private ErrorAlarmWorkRecordName name;

	/* システム固定とする */
	private Boolean fixedAtr;

	/* 使用する */
	@Setter
	private Boolean useAtr;
	
	/* 備考入力でエラーを解除する */
	private NotUseAtr remarkCancelErrorInput;
	
	/* 備考欄NO */
	private int remarkColumnNo;

	/* 区分 */
	private ErrorAlarmClassification typeAtr;

	/* メッセージ */
	@Setter
	private ErrorAlarmMessage message;

	/* エラーアラームを解除できる */
	private Boolean cancelableAtr;

	/* エラー表示項目 */
	private Integer errorDisplayItem;

	/* エラー解除ロールID */
	private String cancelRoleId;

	private List<Integer> lstApplication;

	private String errorAlarmCheckID;
	
	@Setter
	private ErrorAlarmCondition errorAlarmCondition;

	/* Constructor */
	public ErrorAlarmWorkRecord() {
		super();
	}

	/* Constructor */
	public ErrorAlarmWorkRecord(String companyId, ErrorAlarmWorkRecordCode code, ErrorAlarmWorkRecordName name,
			boolean fixedAtr, boolean useAtr, NotUseAtr remarkCancelErrorInput, int remarkColumnNo,
			ErrorAlarmClassification typeAtr, ErrorAlarmMessage message, boolean cancelableAtr,
			Integer errorDisplayItem, List<Integer> lstApplication, String errorAlarmCheckID) {
		super();
		this.companyId = companyId;
		this.code = code;
		this.name = name;
		this.fixedAtr = fixedAtr;
		this.useAtr = useAtr;
		this.remarkCancelErrorInput = remarkCancelErrorInput;
		this.remarkColumnNo = remarkColumnNo;
		this.typeAtr = typeAtr;
		this.message = message;
		this.cancelableAtr = cancelableAtr;
		this.errorDisplayItem = errorDisplayItem;
		this.lstApplication = lstApplication;
		this.errorAlarmCheckID = errorAlarmCheckID;
	}

	/**
	 * Create ErrorAlarmWorkRecord from Java type
	 * 
	 * @param
	 * @return ErrorAlarmWorkRecord
	 **/
	public static ErrorAlarmWorkRecord createFromJavaType(String companyId, String code, String name, boolean fixedAtr,
			boolean useAtr, boolean remarkCancelErrorInput, int remarkColumnNo, int typeAtr, boolean boldAtr,
			String messageColor, boolean cancelableAtr, Integer errorDisplayItem, List<Integer> lstApplication,
			String errorAlarmCheckID) {
		ErrorAlarmWorkRecord errorAlarmWorkRecord = new ErrorAlarmWorkRecord(companyId,
				new ErrorAlarmWorkRecordCode(code), new ErrorAlarmWorkRecordName(name), fixedAtr, useAtr,
				NotUseAtr.valueOf(remarkCancelErrorInput), remarkColumnNo, EnumAdaptor.valueOf(typeAtr, ErrorAlarmClassification.class),
				ErrorAlarmMessage.createFromJavaType(boldAtr, messageColor), cancelableAtr, errorDisplayItem,
				lstApplication, errorAlarmCheckID);
		return errorAlarmWorkRecord;
	}

	/**
	 * Init
	 * 
	 * @param
	 * @return ErrorAlarmWorkRecord
	 **/
	public static ErrorAlarmWorkRecord init(String companyId, String code, String name, boolean fixedAtr,
			boolean useAtr, int remarkCancelErrorInput, int remarkColumnNo, int typeAtr, boolean boldAtr,
			String messageColor, boolean cancelableAtr, Integer errorDisplayItem, List<Integer> lstApplication) {
		ErrorAlarmWorkRecord errorAlarmWorkRecord = new ErrorAlarmWorkRecord(companyId,
				code.length() < 4 ? new ErrorAlarmWorkRecordCode("U" + code) : new ErrorAlarmWorkRecordCode(code),
				new ErrorAlarmWorkRecordName(name), fixedAtr, useAtr, NotUseAtr.valueOf(remarkCancelErrorInput), remarkColumnNo,
				EnumAdaptor.valueOf(typeAtr, ErrorAlarmClassification.class),
				ErrorAlarmMessage.createFromJavaType(boldAtr, messageColor), cancelableAtr, errorDisplayItem,
				lstApplication, IdentifierUtil.randomUniqueId());
		return errorAlarmWorkRecord;
	}

	public void setCheckId(String errorAlarmCheckID) {
		this.errorAlarmCheckID = errorAlarmCheckID;
	}
	
	public ErrorAlarmWorkRecord  cleanData() {
		if(this.lstApplication != null){
			this.lstApplication = this.lstApplication.stream().distinct().collect(Collectors.toList());
		}
		
		if(this.errorAlarmCondition != null && !this.fixedAtr){
			if(this.errorAlarmCondition.getAtdItemCondition().getGroup1() != null){
				this.errorAlarmCondition.getAtdItemCondition().getGroup1().getLstErAlAtdItemCon().stream().forEach(ea -> {
					ea.updateCode(this.getCode());
				});
			}
			if(this.errorAlarmCondition.getAtdItemCondition().getGroup2() != null){
				this.errorAlarmCondition.getAtdItemCondition().getGroup2().getLstErAlAtdItemCon().stream().forEach(ea -> {
					ea.updateCode(this.getCode());
				});
			}
			this.errorAlarmCondition.clearDuplicate();
		}
		
		return this;
	}
	
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	

}

