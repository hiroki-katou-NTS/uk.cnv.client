package nts.uk.ctx.at.function.app.find.alarm.mailsettings;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * アラームリスト実行メール設定（職場別）を登録する
 *
 * @author rafiqul.islam
 */
@Data
@AllArgsConstructor
public class RegisterAlarmExecutionMailSettingsDTO {
        /**
         * 個人職場区分
         */
        private int individualWkpClassify;

        /**
         * 通常自動区分
         */
        private int normalAutoClassify;

        /**
         * 本人管理区分
         */
        private int personalManagerClassify;

        /**
         * 内容メール設定
         */
        private MailSettingsContentDto mailContents;

        /**
         * 送信元アドレス
         */
        private String senderAddress;

        /**
         * マスタチェック結果を就業担当へ送信
         */
        private boolean sendResult;


}
