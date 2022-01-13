package nts.uk.ctx.at.function.app.command.dailyperformanceformat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.AuthorityDailyPerformanceFormat;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.AuthorityFomatDaily;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.AuthorityFomatMonthly;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.AuthorityFormatSheet;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.primitivevalue.DailyPerformanceFormatCode;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.repository.AuthorityDailyPerformanceFormatRepository;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.repository.AuthorityFormatDailyRepository;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.repository.AuthorityFormatMonthlyRepository;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.repository.AuthorityFormatSheetRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class DuplicateAuthorityDailyFormatCommandHandler extends CommandHandler<DuplicateAuthorityDailyFormatCommand> {

	@Inject
	private AuthorityDailyPerformanceFormatRepository authorityDailyPerformanceFormatRepository;
	
	@Inject
	private AuthorityFormatSheetRepository authorityFormatSheetRepository;
	
	@Inject
	private AuthorityFormatDailyRepository authorityFormatDailyItemRepository;
	
	@Inject
	private AuthorityFormatMonthlyRepository authorityFormatMonthlyRepository;
	
	@Override
	protected void handle(CommandHandlerContext<DuplicateAuthorityDailyFormatCommand> context) {
		
		DuplicateAuthorityDailyFormatCommand command = context.getCommand();
		String companyId = AppContexts.user().companyId();
		
		AuthorityDailyPerformanceFormat authorityDailyPerformanceFormat = AuthorityDailyPerformanceFormat.createFromJavaType(companyId, command.getDailyPerformanceFormatCode(), command.getDailyPerformanceFormatName());
		
		if (this.authorityDailyPerformanceFormatRepository.checkExistCode(companyId,
				new DailyPerformanceFormatCode(command.getDailyPerformanceFormatCode()))) {
			throw new BusinessException("Msg_3");
		}
		
		authorityDailyPerformanceFormatRepository.add(authorityDailyPerformanceFormat);
		
		List<AuthorityFormatSheet> listAuthorityFormatSheet = command.getListDailyFormSheetCommand().stream()
				.map(e -> AuthorityFormatSheet.createJavaTye(companyId, command.getDailyPerformanceFormatCode(), e.getSheetNo(), e.getSheetName()))
				.collect(Collectors.toList());
		authorityFormatSheetRepository.add(listAuthorityFormatSheet);
		
		List<AuthorityFomatDaily> listAuthorityFomatDaily = sheetToItem(companyId, command.getDailyPerformanceFormatCode(), command.getListDailyFormSheetCommand());
		authorityFormatDailyItemRepository.add(listAuthorityFomatDaily);
		
		// duplicate format monthly
		List<AuthorityFomatMonthly> authorityFomatMonthlies = command.getAuthorityMonthlyCommand().getDailyAttendanceAuthorityDetailDtos().stream()
				.map(f -> {
					return new AuthorityFomatMonthly(companyId,
							new DailyPerformanceFormatCode(command.getAuthorityMonthlyCommand().getDailyPerformanceFormatCode()),
							f.getAttendanceItemId(),
							f.getOrder(),
							f.getColumnWidth());
				}).collect(Collectors.toList());

		if (this.authorityFormatMonthlyRepository.checkExistCode(companyId,
				new DailyPerformanceFormatCode(command.getDailyPerformanceFormatCode()))) {
			throw new BusinessException("Msg_3");
		}
		this.authorityFormatMonthlyRepository.add(authorityFomatMonthlies);
	}
	
	private List<AuthorityFomatDaily> sheetToItem(String companyId, String dailyPerformanceFormatCode, List<DailyFormSheetCommand> listSheet) {
		
		List<AuthorityFomatDaily> result = new ArrayList<AuthorityFomatDaily>();
		
		for (DailyFormSheetCommand sheet:listSheet) {
			sheet.getListDailyFormItemCommand().stream()
				.forEach(e -> {
					AuthorityFomatDaily authorityFomatDaily = AuthorityFomatDaily.createFromJavaType(companyId, dailyPerformanceFormatCode, e.getAttendanceItemId(), sheet.getSheetNo(), e.getDisplayOrder(), e.getColumnWidth());	
					result.add(authorityFomatDaily);
				});
		}
		
		return result;
	}

}
