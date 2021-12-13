package nts.uk.ctx.exio.dom.input.setting.assembly.revise;

import nts.uk.ctx.exio.dom.input.errors.ErrorMessage;
import nts.gul.util.Either;

/**
 * 値の編集インターフェース
 */
public interface ReviseValue {
	/**
	 * 編集を実行した結果を返す。失敗した場合はLeftでエラーメッセージを返す。
	 * @param target
	 * @return
	 */
	public Either<ErrorMessage, ?> revise(String target) ;

}
