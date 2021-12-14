package nts.uk.ctx.office.dom.equipment.data;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.office.dom.equipment.achievement.ErrorItem;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備利用実績データ.登録する結果
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisterResult {
	
	/**
	 * エラーがあるか
	 */
	private boolean hasError;
	
	/**
	 * エラーList						
	 */
	private List<ErrorItem> errorItems;
	
	/**
	 * 設備利用実績データの永続化処理
	 */
	private Optional<AtomTask> persistTask;
	
	/**
	 * [C-1] エラーありで作成する
	 * @param errorItems	List<エラー項目>									
	 * @return				登録する結果
	 */
	public static RegisterResult withErrors(List<ErrorItem> errorItems) {
		return new RegisterResult(true, errorItems, Optional.empty());
	}
	
	/**
	 * [C-2] エラーなしで作成する	
	 * @param persistTask	設備利用実績データの永続化処理
	 * @return				登録する結果
	 */
	public static RegisterResult withoutErrors(AtomTask persistTask) {
		return new RegisterResult(false, Collections.emptyList(), Optional.of(persistTask));
	}
}
