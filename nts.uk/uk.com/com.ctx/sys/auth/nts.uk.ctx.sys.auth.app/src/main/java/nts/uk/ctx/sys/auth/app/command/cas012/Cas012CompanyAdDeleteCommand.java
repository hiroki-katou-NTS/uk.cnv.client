package nts.uk.ctx.sys.auth.app.command.cas012;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cas012CompanyAdDeleteCommand {
    private String cId;
    private String uId;
    private Integer roleType;

}
