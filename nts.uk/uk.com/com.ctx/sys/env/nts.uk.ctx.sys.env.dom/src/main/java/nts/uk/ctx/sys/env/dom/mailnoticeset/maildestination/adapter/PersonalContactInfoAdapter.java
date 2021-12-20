package nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter;

import java.util.List;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.環境.メール通知設定.Import.社員ID（List）から個人連絡先を取得
 */
public interface PersonalContactInfoAdapter {

	// 取得する(<<List>> 社員IDList: 社員ID): List<個人のメールアドレス>
	List<PersonalMailAddressImport> find(List<String> sids);
}
