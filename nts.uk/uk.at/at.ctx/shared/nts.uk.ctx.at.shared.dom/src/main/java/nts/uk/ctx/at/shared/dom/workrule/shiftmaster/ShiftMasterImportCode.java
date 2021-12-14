package nts.uk.ctx.at.shared.dom.workrule.shiftmaster;

import nts.arc.primitive.constraint.StringMaxLength;
import nts.uk.shr.com.primitive.CodePrimitiveValue;
/**
 * シフトマスタ取り込みコード
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.シフトマスタ.シフトマスタ取り込みコード
 * @author lan_lt
 *
 */
@StringMaxLength(10)
public class ShiftMasterImportCode extends CodePrimitiveValue<ShiftMasterImportCode> {
	private static final long serialVersionUID = 1L;
	
	public ShiftMasterImportCode(String rawValue) {
		super(rawValue);
	}
	
}
