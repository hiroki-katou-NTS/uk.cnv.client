package nts.uk.ctx.exio.app.find.exo.authset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.exio.dom.exo.authset.ExOutCtgAuthSet;

@AllArgsConstructor
@Getter
public class ExOutCtgAuthSetDTO {
    private final String companyId;

    /** ロールID */
    private final String roleId;

    /** 機能NO */
    private final int functionNo;

    /** 利用できる */
    private boolean isAvailable;


    public static ExOutCtgAuthSetDTO fromDomain(ExOutCtgAuthSet domain){
        return new ExOutCtgAuthSetDTO(
                domain.getCompanyId(),
                domain.getRoleId(),
                domain.getFunctionNo(),
                domain.isAvailable()
        );
    }
}
