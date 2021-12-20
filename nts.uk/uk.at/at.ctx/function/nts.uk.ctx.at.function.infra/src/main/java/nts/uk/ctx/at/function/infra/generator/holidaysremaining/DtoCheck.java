package nts.uk.ctx.at.function.infra.generator.holidaysremaining;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DtoCheck {
    private Integer firstRow;
    private Integer count;
    private Boolean pageBreak;
    private Integer countEmployeeBefore;
}
