package antlr.struct;

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

import antlr.struct.TargetStructure.Directory;

/**
 * The context is top level and creates the foundation like main-method and
 * basic Spring configuration.
 * 
 * @author onkobu
 *
 */
public class SpringContextGenerator {
	private static final Logger logger = Logger.getLogger(SpringContextGenerator.class.getName());

	public void generate(TargetStructure targetStructure) {
		HashMap<String, Object> scopes = new HashMap<String, Object>();
		scopes.put("projectName", targetStructure.getName());
		scopes.put("rootPackage", targetStructure.getRootPackage());
		// scopes.put("feature", new Feature("Perfect!"));

		writeApplicationClass(targetStructure, scopes);
		writeSpringConfigClass(targetStructure, scopes);
	}

	private void writeApplicationClass(TargetStructure targetStructure, HashMap<String, Object> scopes) {
		logger.info("writing Application class");
		try (Writer writer = new BufferedWriter(
				new FileWriter(new File(targetStructure.getDirectory(Directory.SOURCE), "Application.java")));) {
			MustacheFactory mf = new DefaultMustacheFactory();
			Mustache mustache = mf.compile("ApplicationClass.mustache");
			mustache.execute(writer, scopes);
			writer.flush();
		} catch (IOException e) {
			logger.throwing(getClass().getSimpleName(), "generate", e);
		}
	}

	private void writeSpringConfigClass(TargetStructure targetStructure, HashMap<String, Object> scopes) {
		logger.info("writing Configuration class");
		try (Writer writer = new BufferedWriter(
				new FileWriter(new File(targetStructure.getDirectory(Directory.SOURCE), "SpringWebConfig.java")));) {
			MustacheFactory mf = new DefaultMustacheFactory();
			Mustache mustache = mf.compile("SpringWebConfig.mustache");
			mustache.execute(writer, scopes);
			writer.flush();
		} catch (IOException e) {
			logger.throwing(getClass().getSimpleName(), "generate", e);
		}
	}

}
