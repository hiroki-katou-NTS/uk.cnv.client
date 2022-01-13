package nts.uk.ctx.at.record.dom.jobmanagement.manhourinput;

import lombok.AllArgsConstructor;
import nts.arc.enums.EnumAdaptor;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数入力.時間帯別勤怠の削除状態
 * 
 * @author tutt
 *
 */
@AllArgsConstructor
public enum AttendanceDeletionStatusEnum {

	OVERWRITE(0, "上書き削除"),

	COMPLETE(1, "完全削除");

	public final int value;
	public final String nameId;

	public static AttendanceDeletionStatusEnum of(int value) {
		return EnumAdaptor.valueOf(value, AttendanceDeletionStatusEnum.class);
	}
}
