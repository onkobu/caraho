package de.oftik.caraho;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.logging.Logger;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import de.oftik.caraho.TargetStructure.Directory;

/**
 * Generates POM and directories.
 * 
 * @author onkobu
 *
 */
public class PomGenerator {
	private static final Logger logger = Logger.getLogger(PomGenerator.class.getName());

	public PomGenerator() {
	}

	public void generate(TargetStructure targetStructure) {
		HashMap<String, Object> scopes = new HashMap<String, Object>();
		scopes.put("projectName", targetStructure.getName());
		scopes.put("projectVersion", targetStructure.getVersion());
		scopes.put("projectGroup", targetStructure.getProjectGroup());
		// scopes.put("feature", new Feature("Perfect!"));

		logger.info("writing POM");
		try (Writer writer = new BufferedWriter(
				new FileWriter(new File(targetStructure.getProjectRoot(), "pom.xml")));) {
			MustacheFactory mf = new DefaultMustacheFactory();
			Mustache mustache = mf.compile("pom.mustache");
			mustache.execute(writer, scopes);
			writer.flush();
		} catch (IOException e) {
			logger.throwing(PomGenerator.class.getSimpleName(), "generate", e);
			throw new IllegalStateException("Failed to write POM");
		}

		String rootPackDir = targetStructure.getRootPackage().replace(".", "/") + "/" + targetStructure.getName();
		createDirectories(TargetStructure.Directory.SOURCE, targetStructure, "src/main/java/" + rootPackDir);
		createDirectories(TargetStructure.Directory.RESOURCE, targetStructure, "src/main/resources/" + rootPackDir);
		createDirectories(TargetStructure.Directory.WEBAPP, targetStructure, "src/main/resources/public/");
		createDirectories(TargetStructure.Directory.TEST_SOURCE, targetStructure, "src/test/java/" + rootPackDir);
		createDirectories(TargetStructure.Directory.TEST_RESOURCE, targetStructure,
				"src/test/resources/" + rootPackDir);
		createDirectories(TargetStructure.Directory.MODEL_CLASS, targetStructure,
				"src/main/java/" + rootPackDir + "/model");
	}

	private void createDirectories(Directory dir, TargetStructure tStruct, String subDir) {
		final File projectRoot = tStruct.getProjectRoot();
		logger.info("Creating " + subDir);
		final File javaSrcDir = new File(projectRoot, subDir);
		javaSrcDir.mkdirs();
		tStruct.registerDirectory(dir, javaSrcDir);
	}
}
