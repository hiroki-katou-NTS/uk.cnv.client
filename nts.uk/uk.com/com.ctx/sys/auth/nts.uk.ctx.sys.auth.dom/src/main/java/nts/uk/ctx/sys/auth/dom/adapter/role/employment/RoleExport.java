package nts.uk.ctx.sys.auth.dom.adapter.role.employment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;

@AllArgsConstructor
@Getter
@Setter
public class RoleExport {
    /** The p id. */
    // 個人ID
    private String personId;

    /** The p name.
     *   tên biến là personName nhưng value là của bussinessName.( sửa theo update EA : 「ビジネスネーム」：「個人.個人名グループ.ビジネスネーム」  李　2018.02.06)
     *  */
    private String personName;

    /** The birth day. */
    // 生年月日
    private GeneralDate birthDay;

    /** The p mail addr. */
    // 個人メールアドレス
    private String pMailAddr;

    /** The p mail addr. */
    // 個人メールアドレス
    private int gender;
}
