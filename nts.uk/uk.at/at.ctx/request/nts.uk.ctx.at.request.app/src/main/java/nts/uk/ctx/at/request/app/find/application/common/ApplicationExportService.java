package nts.uk.ctx.at.request.app.find.application.common;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;
import nts.uk.ctx.at.request.app.command.application.applicationlist.ListOfAppTypesCmd;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.applist.service.ApplicationTypeDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.print.ApplicationGenerator;
import nts.uk.ctx.at.request.dom.application.common.service.print.CommonAppPrint;
import nts.uk.ctx.at.request.dom.application.common.service.print.PrintContentOfApp;
import nts.uk.ctx.at.request.dom.application.common.service.print.PrintContentOfEachApp;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.application.stamp.StampRequestMode;
import nts.uk.shr.com.context.AppContexts;

/**
 * refactor 4
 * @author Doan Duy Hung
 *
 */
@Stateless
public class ApplicationExportService extends ExportService<AppPrintQuery> {

	@Inject
	private CommonAppPrint commonAppPrint;

	@Inject
	private ApplicationGenerator applicationGenerator;

	@Override
	protected void handle(ExportServiceContext<AppPrintQuery> context) {
		AppPrintQuery appPrintQuery = context.getQuery();
		String companyID = AppContexts.user().companyId();
		AppDispInfoStartupOutput appDispInfoStartupOutput = appPrintQuery.appDispInfoStartupOutput.toDomain();
		Application application = appDispInfoStartupOutput.getAppDetailScreenInfo().get().getApplication();
		Optional<PrintContentOfEachApp> opPrintContentOfEachApp = Optional.empty();
		if(appPrintQuery.opPrintContentOfEachApp != null) {
			opPrintContentOfEachApp = Optional.of(appPrintQuery.opPrintContentOfEachApp.toDomain());
		}

		PrintContentOfApp printContentOfApp = commonAppPrint.print(
				companyID,
				application.getAppID(),
				appDispInfoStartupOutput,
				opPrintContentOfEachApp);
		// メニューの表示名を取得する
		String applicationName = Strings.EMPTY;
		List<ListOfAppTypesCmd> appNameList = appPrintQuery.appNameList;
		switch (application.getAppType()) {
		case STAMP_APPLICATION:
			List<ListOfAppTypesCmd> stampAppLst = appNameList.stream()
					.filter(x -> x.getAppType()==ApplicationType.STAMP_APPLICATION.value).collect(Collectors.toList());
			applicationName = stampAppLst.stream()
				.filter(x -> {
					boolean condition1 = x.getAppType()==ApplicationType.STAMP_APPLICATION.value;
					ApplicationTypeDisplay applicationTypeDisplay = null;
					if(application.getOpStampRequestMode().get() == StampRequestMode.STAMP_ADDITIONAL) {
						applicationTypeDisplay = ApplicationTypeDisplay.STAMP_ADDITIONAL;
					} else {
						applicationTypeDisplay = ApplicationTypeDisplay.STAMP_ONLINE_RECORD;
					}
					boolean condition2 = x.getOpApplicationTypeDisplay()==applicationTypeDisplay.value;
					return condition1 && condition2;
				}).findAny().map(x -> x.getAppName()).orElse(null);
			break;
		case OVER_TIME_APPLICATION:
			List<ListOfAppTypesCmd> overtimeAppLst = appNameList.stream()
					.filter(x -> x.getAppType()==ApplicationType.OVER_TIME_APPLICATION.value).collect(Collectors.toList());
			applicationName = overtimeAppLst.stream()
					.filter(x -> {
						boolean condition1 = x.getAppType()==ApplicationType.OVER_TIME_APPLICATION.value;
						boolean condition2 = false;
						if(printContentOfApp.getOpDetailOutput().isPresent()) {
							ApplicationTypeDisplay applicationTypeDisplay = null;
							OvertimeAppAtr overtimeAppAtr = printContentOfApp.getOpDetailOutput().get().getAppOverTime().getOverTimeClf();
							if(overtimeAppAtr==OvertimeAppAtr.EARLY_OVERTIME) {
								applicationTypeDisplay = ApplicationTypeDisplay.EARLY_OVERTIME;
							} else if(overtimeAppAtr==OvertimeAppAtr.NORMAL_OVERTIME) {
								applicationTypeDisplay = ApplicationTypeDisplay.NORMAL_OVERTIME;
							}else if(overtimeAppAtr == OvertimeAppAtr.MULTIPLE_OVERTIME ){
								applicationTypeDisplay = ApplicationTypeDisplay.OVERTIME_MULTIPLE_TIME;
							}
							else {
								applicationTypeDisplay = ApplicationTypeDisplay.EARLY_NORMAL_OVERTIME;
							}
							condition2 = x.getOpApplicationTypeDisplay()==applicationTypeDisplay.value;
						}
						return condition1 && condition2;
					}).findAny().map(x -> x.getAppName()).orElse(null);
			break;
		default:
			applicationName = appNameList.stream().filter(x -> x.getAppType()==application.getAppType().value).findAny().map(x -> x.getAppName()).orElse(null);
			break;
		}
		printContentOfApp.setApplicationName(applicationName);

		applicationGenerator.generate(
				context.getGeneratorContext(),
				printContentOfApp,
				application.getAppType());
	}

}
