package nts.uk.ctx.sys.gateway.dom.loginold.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.環境.Export.RQ397：社員のメールアドレスを取得する.アルゴリズム.送信メール一覧
 */
@Data
@AllArgsConstructor
public class SentMailListImport {

	/**
	 * メールアドレス
	 */
	private List<String> mailAddresses;
	
	/**
	 * 社員ID
	 */
	private String sid;
}
