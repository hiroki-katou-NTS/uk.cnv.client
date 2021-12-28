package nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.shr.infra.file.storage.stream.FileStoragePath;
import org.apache.commons.io.FileUtils;

import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.layer.infra.file.storage.StoredFileStreamService;
import nts.arc.layer.infra.file.temp.ApplicationTemporaryFileFactory;
import nts.arc.layer.infra.file.temp.ApplicationTemporaryFilesContainer;
import nts.arc.system.ServerSystemProperties;
import nts.arc.time.GeneralDateTime;
import nts.gul.file.archive.ArchiveFormat;
import nts.gul.file.archive.ExtractStatus;
import nts.gul.file.archive.FileArchiver;
import nts.uk.ctx.sys.portal.app.screenquery.topppagepart.createflowmenu.ExtractionResponseDto;

@Stateless
public class FileExportService extends ExportService<FileExportCommand> {

	@Inject
	private ApplicationTemporaryFileFactory applicationTemporaryFileFactory;

	@Inject
	private HtmlFileGenerator htmlGenerator;

	@Inject
	private StoredFileStreamService fileStreamService;

	@Override
	protected void handle(ExportServiceContext<FileExportCommand> context) {
		// Get fileGeneratorContext
		FileGeneratorContext generator = context.getGeneratorContext();
		// Write html content into html file and store as temp
		htmlGenerator.generate(generator, context.getQuery().getHtmlContent(), "index.html");
		// Zip html file into a .zip and delete the temp html file
		ApplicationTemporaryFilesContainer applicationTemporaryFilesContainer = applicationTemporaryFileFactory
				.createContainer();
		String fileName = String.format("%s_%s%s.%s", "CCG034", context.getQuery().getFlowMenuCode(),

				GeneralDateTime.now().toString("yyyyMMddhhmmss"), "zip");
		applicationTemporaryFilesContainer.zipWithName(generator, fileName, false);
		applicationTemporaryFilesContainer.removeContainer();
	}

	public Optional<ExtractionResponseDto> extract(String fileId) throws IOException {
		try (InputStream inputStream = this.fileStreamService.takeOutFromFileId(fileId)) {
			Path destinationDirectory = Paths.get(new FileStoragePath().getPathOfCurrentTenant().toString() + "//packs" + "//" + fileId);
			ExtractStatus status = FileArchiver.create(ArchiveFormat.ZIP).extract(inputStream, destinationDirectory);
			if (!status.equals(ExtractStatus.SUCCESS)) {
				return Optional.empty();
			}

			File file = destinationDirectory.toFile().listFiles()[0];
			return Optional.of(new ExtractionResponseDto(FileUtils.readFileToString(file, StandardCharsets.UTF_8),
					file.getAbsolutePath()));
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public List<ExtractionResponseDto> extractByListFileId(List<String> lstFileId) throws IOException {
		List<ExtractionResponseDto> result = new ArrayList<>();
		for (String fileId : lstFileId) {
			InputStream inputStream = this.fileStreamService.takeOutFromFileId(fileId);
			Path destinationDirectory = Paths.get(new FileStoragePath().getPathOfCurrentTenant().toString() + "//packs" + "//" + fileId);
			ExtractStatus status = FileArchiver.create(ArchiveFormat.ZIP).extract(inputStream, destinationDirectory);
			if (!status.equals(ExtractStatus.SUCCESS)) {
				return new ArrayList<>();
			}
			File file = destinationDirectory.toFile().listFiles()[0];
			ExtractionResponseDto item = new ExtractionResponseDto(
					FileUtils.readFileToString(file, StandardCharsets.UTF_8), file.getAbsolutePath());
			result.add(item);
		}
		return result;
	}

	public ExtractionResponseDto extractFlowMenu(String fileId) throws IOException {
		InputStream inputStream = this.fileStreamService.takeOutFromFileId(fileId);
		Path destinationDirectory = Paths.get(new FileStoragePath().getPathOfCurrentTenant().toString() + "//packs" + "//" + fileId);
		ExtractStatus status = FileArchiver.create(ArchiveFormat.ZIP).extract(inputStream, destinationDirectory);
		if (!status.equals(ExtractStatus.SUCCESS)) {
			return null;
		}

		File file = destinationDirectory.toFile().listFiles()[0];
		return new ExtractionResponseDto(FileUtils.readFileToString(file, StandardCharsets.UTF_8),
				destinationDirectory.toString());
	}

}
