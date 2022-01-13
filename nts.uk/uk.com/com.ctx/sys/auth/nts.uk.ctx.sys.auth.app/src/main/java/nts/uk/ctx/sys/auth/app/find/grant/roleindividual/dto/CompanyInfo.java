package nts.uk.ctx.sys.auth.app.find.grant.roleindividual.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyInfo {

    /** 会社コード*/
    private String companyCode;

    /** 会社名*/
    private String companyName;

    /** 会社ID*/
    private String companyId;

    /** 廃止区分*/
    private int isAbolition;
}
