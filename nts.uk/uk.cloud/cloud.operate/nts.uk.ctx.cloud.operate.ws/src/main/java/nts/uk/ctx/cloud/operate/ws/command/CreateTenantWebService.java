package nts.uk.ctx.cloud.operate.ws.command;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import lombok.SneakyThrows;
import nts.arc.layer.ws.WebService;
import nts.arc.task.AsyncTaskInfo;
import nts.arc.task.AsyncTaskInfoRepository;
import nts.arc.task.AsyncTaskInfoService;
import nts.uk.ctx.cloud.operate.app.command.*;
import nts.uk.ctx.sys.assist.app.command.mastercopy.MasterCopyCategoryDto;
import nts.uk.ctx.sys.assist.app.command.mastercopy.MasterCopyDataCommand;
import nts.uk.ctx.sys.assist.app.command.mastercopy.MasterCopyDataCommandHanlder;
import nts.uk.ctx.sys.assist.app.find.mastercopy.MasterCopyCategoryFindDto;
import nts.uk.ctx.sys.assist.app.find.mastercopy.MasterCopyCategoryFinder;
import nts.uk.ctx.sys.assist.dom.mastercopy.CopyMethod;
import nts.uk.shr.com.company.CompanyId;
import nts.uk.shr.com.system.property.UKServerSystemProperties;
import nts.uk.shr.infra.data.TenantLocatorService;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Path("ctx/cld/operate/tenant")
@Produces("application/json")
public class CreateTenantWebService extends WebService{

	@Inject
	private CreateTenantOnCloudCommandHandler handler;

	@Inject
	private GeneratePasswordCommandHandler generatePasswordHandler;

	@Inject
	private MasterCopyCommandHandler masterCopyHandler;

	@Inject
	private AsyncTaskInfoService infoService;

	@POST
	@Path("regist")
	public void registTenant(CreateTenantOnCloudCommand command) {
		onTenant(command.getTenanteCode(), () -> {
			this.handler.handle(command);
			return null;
		});
	}

	@POST
	@Path("generatePassword")
	public void generatePassword(GeneratePasswordCommand command) {
		this.generatePasswordHandler.handle(command);
	}

	@POST
	@Path("mastercopy/execute")
	public AsyncTaskInfo executeMasterCopy(MasterCopyCommand command){
		return onTenant(command.getTenantCode(), () -> {
			AsyncTaskInfo result = this.masterCopyHandler.handle(command);
			return result;
		});
	}

	@Path("taskInfo/{tenantCode}/{id}")
	@POST
	public AsyncTaskInfo get(@PathParam("tenantCode") String tenantCode, @PathParam("id") String taskId)
	{
		return onTenant(tenantCode, () -> {
			AsyncTaskInfo result = this.infoService.find(taskId);
			return result;
		});
	}

	/**
	 * TenantLocatorを要する処理を実行する
	 * @param tenantCode
	 * @param task
	 */
	private static void onTenant(String tenantCode, Runnable task) {
		// テナントロケーターを使用するかどうかチェック
		if (UKServerSystemProperties.usesTenantLocator()){
			// 使用する
			try {
				TenantLocatorService.connect(tenantCode);
				task.run();
			} finally {
				TenantLocatorService.disconnect();
			}
		}else{
			// 使用しない
			task.run();
		}
	}

	@SneakyThrows
	private static AsyncTaskInfo onTenant(String tenantCode, Callable<AsyncTaskInfo> task) {
		// テナントロケーターを使用するかどうかチェック
		if (UKServerSystemProperties.usesTenantLocator()){
			// 使用する
			try {
				TenantLocatorService.connect(tenantCode);
				return task.call();
			} finally {
				TenantLocatorService.disconnect();
			}
		}else{
			// 使用しない
			return task.call();
		}
	}
}
