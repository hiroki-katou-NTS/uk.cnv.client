package nts.uk.ctx.sys.portal.pub.toppagealarm;

import java.util.List;
import java.util.Optional;
import lombok.Data;
import nts.arc.time.GeneralDateTime;

@Data
public class ToppageAlarmExport {
	/**
	 * アラーム分類
	 */
	private AlarmClassification alarmClassification;
	
	/**
	 * 発生日時
	 */
	private GeneralDateTime occurrenceDateTime;
	
	/**
	 * 表示社員ID
	 */
	private String displaySId;
	
	/**
	 * 表示社員区分
	 */
	private DisplayEmpClassfication displayEmpClassfication;
	
	//部下の社員ID
	private List<String> subSids; //#116503
	
	/*
	 * パターンコード
	 */
	private Optional<String> patternCode;
	
	/*
	 * パターン名
	 */
	private Optional<String> patternName;
	
	/**
	 * リンクURL
	 */
	private Optional<String> linkUrl;
	
	/**
	 * 表示メッセージ
	 */
	private Optional<String> displayMessage;
	
	/**
	 * 部下のエラーがない社員ID
	 */
	private List<String> noErrEmployeeIds;
}
