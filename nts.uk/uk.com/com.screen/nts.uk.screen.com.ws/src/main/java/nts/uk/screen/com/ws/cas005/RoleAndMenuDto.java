package nts.uk.screen.com.ws.cas005;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.screen.com.app.find.webmenu.webmenu.DtoRole;
import nts.uk.screen.com.app.find.webmenu.webmenu.RoleByRoleTiesDto;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoleAndMenuDto {
    private RoleByRoleTiesDto roleByRoleTies;
    private DtoRole role;
}
