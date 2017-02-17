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
 * The context is top level and creates the foundation like main-method and
 * basic Spring configuration.
 *
 * @author onkobu
 *
 */
public class SpringContextGenerator {
	private static final Logger logger = Logger.getLogger(SpringContextGenerator.class.getName());

	enum ConfigTemplate {
		Application,

		SpringWebConfig,

		SwaggerConfig;

		void processMustache(TargetStructure targetStructure, HashMap<String, Object> scopes) {
			logger.info(String.format("writing %s class", name()));
			try (Writer writer = new BufferedWriter(
					new FileWriter(new File(targetStructure.getDirectory(Directory.SOURCE), name() + ".java")));) {
				MustacheFactory mf = new DefaultMustacheFactory();
				Mustache mustache = mf.compile(name() + ".mustache");
				mustache.execute(writer, scopes);
				writer.flush();
			} catch (IOException e) {
				logger.throwing(getClass().getSimpleName(), "generate", e);
			}
		}
	}

	public void generate(TargetStructure targetStructure) {
		HashMap<String, Object> scopes = new HashMap<String, Object>();
		scopes.put("projectName", targetStructure.getName());
		scopes.put("rootPackage", targetStructure.getRootPackage());
		// scopes.put("feature", new Feature("Perfect!"));

		for (ConfigTemplate template : ConfigTemplate.values()) {
			template.processMustache(targetStructure, scopes);
		}
	}
}
