package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * ボタン詳細設定
 * @author phongtq
 *
 */
@AllArgsConstructor
public class ButtonSettings {

	/** ボタン位置NO */
	@Getter
	private ButtonPositionNo buttonPositionNo;
	
	/** 使用区分 */
	@Getter
	private NotUseAtr usrArt;
	
	/** ボタンの表示設定 */
	@Getter
	private ButtonDisSet buttonDisSet;
	
	/** 打刻種類 */
	@Getter
	private StampType type;
	
	/** 音声使用方法 */
	@Getter
	private AudioType audioType;
	
	/** 応援職場設定方法 */
	@Getter
	private Optional<SupportWplSet> supportWplSet;
	
	/** 作業指定方法 */
	@Getter
	private Optional<AssignmentMethod> taskChoiceArt;
	
}
