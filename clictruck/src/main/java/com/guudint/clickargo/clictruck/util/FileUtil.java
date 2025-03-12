package com.guudint.clickargo.clictruck.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.config.model.TCoreSysparam;

@Service
public class FileUtil {

	@Autowired
	@Qualifier("coreSysparamDao")
	protected GenericDao<TCoreSysparam, String> coreSysparamDao;

	private static String baseAttachmentLocation = null;

	@PostConstruct
	public void initSysParam() throws Exception {
		TCoreSysparam sysParam = coreSysparamDao.find(CtConstant.KEY_ATTCH_BASE_LOCATION);
		if (sysParam != null) {
			baseAttachmentLocation = sysParam.getSysVal();
		}
	}

	public static String getBaseAttachmentLocation() {
		return baseAttachmentLocation;
	}

	protected String getSysParam(String key) throws Exception {
		if (StringUtils.isBlank(key))
			throw new ParameterException("param key null or empty");

		TCoreSysparam sysParam = coreSysparamDao.find(key);
		if (sysParam != null) {
			return sysParam.getSysVal();
		}

		throw new EntityNotFoundException("sys param config " + key + " not set");
	}

	public static String saveAttachment(String filePath, byte[] data) throws Exception {
		if (StringUtils.isBlank(filePath)) {
			throw new ParameterException("filePath null");
		}
		FileOutputStream fileOutputStream = new FileOutputStream(filePath);
		fileOutputStream.write(data);
		fileOutputStream.close();
		return filePath;
	}

	/**
	 * Creates a file in a folder name after jobId value.
	 */
	public static String saveAttachment(String jobId, String basePath, String filename, byte[] data) throws Exception {
		if (StringUtils.isBlank(filename))
			throw new ParameterException("param filename null or empty");

		if (data == null)
			throw new ParameterException("param data null or empty");

		if (StringUtils.isBlank(basePath))
			throw new ProcessingException("basePath is not configured");

		Path dir = Paths.get(basePath.concat(jobId));
		if (!Files.exists(dir)) {
			Files.createDirectories(dir);
		}

		File jobDir = new File(basePath.concat(jobId));
		File file = new File(jobDir.getAbsolutePath(), filename);
		FileOutputStream output = new FileOutputStream(file);
		output.write(data);
		output.close();
		return file.getAbsolutePath();
	}

	public static String saveAttachment(String jobId, String filename, byte[] data) throws Exception {
		if (StringUtils.isBlank(filename))
			throw new ParameterException("param filename null or empty");

		if (data == null)
			throw new ParameterException("param data null or empty");

		if (StringUtils.isBlank(baseAttachmentLocation))
			throw new ProcessingException("basePath is not configured");

		Path dir = Paths.get(baseAttachmentLocation.concat(jobId));
		if (!Files.exists(dir)) {
			Files.createDirectories(dir);
		}

		File jobDir = new File(baseAttachmentLocation.concat(jobId));
		File file = new File(jobDir.getAbsolutePath(), filename);
		FileOutputStream output = new FileOutputStream(file);
		output.write(data);
		output.close();
		return file.getAbsolutePath();
	}

	public static String toBase64(String filepath) throws IOException {
		if (StringUtils.isBlank(filepath)) {
			return null;
		}
		byte[] bytes = Files.readAllBytes(Paths.get(filepath));
		return Base64.getEncoder().encodeToString(bytes);
	}

	public static String zipFiles(List<File> files, String zipFileName) throws Exception {
		if (StringUtils.isBlank(baseAttachmentLocation)) {
			throw new ProcessingException("basePath is not configured");
		}
		String filepath = baseAttachmentLocation + "/" + zipFileName;
		FileOutputStream fileOutputStream = new FileOutputStream(filepath);
		ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
		for (File file : files) {
			if (file.isDirectory()) {
				zipOutputStream.putNextEntry(new ZipEntry(file.getName() + "/"));
				for (File child : file.listFiles()) {
					zipOutputStream.putNextEntry(new ZipEntry(file.getName() + "/" + child.getName()));
					insertFileinZip(zipOutputStream, child);
				}
			} else {
				zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
				insertFileinZip(zipOutputStream, file);
			}
		}
		zipOutputStream.close();
		fileOutputStream.close();
		return filepath;
	}

	private static void insertFileinZip(ZipOutputStream zipOutputStream, File file) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fileInputStream.read(bytes)) >= 0) {
			zipOutputStream.write(bytes, 0, length);
		}
		fileInputStream.close();
	}

	public static boolean delete(String file) throws IOException {
		if (StringUtils.isBlank(file)) {
			return false;
		}
		File path = new File(file);
		if(path.isDirectory()){
			FileUtils.deleteDirectory(path);
		}
		return new File(file).delete();
	}

	public static String copy(String src, String dst) throws IOException {
		if (StringUtils.isBlank(src) || StringUtils.isBlank(dst)) {
			return "";
		}
		return Files.copy(Paths.get(src), Paths.get(dst), StandardCopyOption.REPLACE_EXISTING).toString();
	}

	public static String createDirectory(String dir) throws IOException {
		File directory = new File(dir);
		if (!directory.exists()) {
			Files.createDirectories(Paths.get(dir));
		}
		return directory.getAbsolutePath();
	}
}
