package nts.uk.ctx.at.function.dom.processexecution;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.ot.frame.NotUseAtr;

/**
 * データの削除
 */
@Getter
@Setter
@AllArgsConstructor
public class DeleteData extends DomainObject {
	/** 
	 * データの削除区分 
	 **/
	private NotUseAtr dataDeletionClassification;
	
	/** 
	 * パターンコード
	 * 補助パターンコード 
	 **/
	private Optional<AuxiliaryPatternCode> patternCode;
}
