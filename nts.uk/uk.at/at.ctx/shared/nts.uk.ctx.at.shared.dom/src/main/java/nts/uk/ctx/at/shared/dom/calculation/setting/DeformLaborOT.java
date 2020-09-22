package nts.uk.ctx.at.shared.dom.calculation.setting;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.DomainObject;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * @author yennh
 *
 */
@AllArgsConstructor
@Getter
public class DeformLaborOT extends DomainObject {
	/*会社ID*/
	private String cid;

	/*変形法定内残業を計算する*/
	private NotUseAtr legalOtCalc;

	public static DeformLaborOT createFromJavaType(String cid, NotUseAtr legalOtCalc) {
		return new DeformLaborOT(cid, legalOtCalc);
	}
	
	/**
	 * 変形法定内残業を計算する
	 * @return true：計算する false：計算しない
	 */
	public boolean isLegalOtCalc() {
		return this.legalOtCalc.equals(NotUseAtr.USE);
	}
}
