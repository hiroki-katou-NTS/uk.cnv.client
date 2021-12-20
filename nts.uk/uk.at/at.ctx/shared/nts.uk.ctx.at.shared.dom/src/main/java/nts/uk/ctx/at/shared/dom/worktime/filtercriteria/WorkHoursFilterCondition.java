package nts.uk.ctx.at.shared.dom.worktime.filtercriteria;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.AggregateRoot;
import nts.gul.text.StringUtil;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.就業時間帯.絞り込み条件.就業時間帯の絞り込み条件
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class WorkHoursFilterCondition extends AggregateRoot {

	/**
	 * 会社ID
	 */
	private final String cid;

	/**
	 * NO
	 */
	private final WorkHoursFilterConditionNo no;

	/**
	 * 使用区分
	 */
	private NotUseAtr notUseAtr;

	/**
	 * <<Optional>>名称
	 */
	private Optional<WorkHoursFilterConditionName> name;

	@Override
	public void validate() {
		if (this.notUseAtr.isUse() && (!name.isPresent() || StringUtil.isNullOrEmpty(name.get().v(), true))) {
			throw new BusinessException("Msg_2304");
		}
	}
}
