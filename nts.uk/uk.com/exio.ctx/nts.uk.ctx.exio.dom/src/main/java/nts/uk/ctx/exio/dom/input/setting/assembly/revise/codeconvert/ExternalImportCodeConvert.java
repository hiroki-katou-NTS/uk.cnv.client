package nts.uk.ctx.exio.dom.input.setting.assembly.revise.codeconvert;

import java.util.List;

import lombok.Getter;
import lombok.val;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.exio.dom.input.errors.ErrorMessage;
import nts.gul.util.Either;

/**
 * 受入コード変換
 */
@Getter
public class ExternalImportCodeConvert extends DomainObject {
	
	/** 変換対象外を受け入れる */
	private boolean importWithoutSetting;
	
	/** コード変換 */
	private List<CodeConvertDetail> convertDetails;
	
	public ExternalImportCodeConvert(boolean importWithoutSetting, List<CodeConvertDetail> listConvertDetails) {
		super();
		this.importWithoutSetting = importWithoutSetting;
		this.convertDetails = listConvertDetails;
	}
	
	/**
	 * 変換する
	 * @param target
	 * @return
	 */
	public Either<ErrorMessage, CodeConvertValue> convert(String target) {
		for (val detail : convertDetails) {
			if (detail.getBefore().v().equals(target)) {
				return Either.right(detail.getAfter());
			}
		}
		
		if(importWithoutSetting) {
			// 変換対象外を受け入れる設定のためINPUTを変換値として返す
			return Either.right(new CodeConvertValue(target));
		}
		
		return Either.left(new ErrorMessage("受入データがコード変換の設定に一致しませんでした。"));
	}
}
