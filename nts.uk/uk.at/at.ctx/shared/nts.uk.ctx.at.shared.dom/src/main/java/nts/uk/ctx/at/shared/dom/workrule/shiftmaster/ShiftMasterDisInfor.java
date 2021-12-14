package nts.uk.ctx.at.shared.dom.workrule.shiftmaster;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.DomainObject;

/**
 * シフトマスタの表示情報
 * @author tutk
 */
@Getter
@AllArgsConstructor
public class ShiftMasterDisInfor extends DomainObject {

	/** 名称 **/
	private ShiftMasterName name;
	/** 色 **/
	private ColorCodeChar6 color;
	/** スマホ表示用の色 **/
	private ColorCodeChar6 colorSmartPhone;
	/** 備考 **/
	private Optional<Remarks> remarks;

}
