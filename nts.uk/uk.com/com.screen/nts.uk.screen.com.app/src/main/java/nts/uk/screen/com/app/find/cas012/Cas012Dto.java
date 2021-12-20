package nts.uk.screen.com.app.find.cas012;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.enums.EnumConstant;
import nts.uk.ctx.sys.auth.dom.adapter.company.CompanyImport;

import java.util.List;


@AllArgsConstructor
@Data
public class Cas012Dto {
    private List<EnumConstant> enumRoleType;

    private List<CompanyImport> listCompany;
}
