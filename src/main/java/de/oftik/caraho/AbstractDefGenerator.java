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
import de.oftik.caraho.TargetStructure.ModelClassProperty;

public class AbstractDefGenerator {
	private static final Logger logger = Logger.getLogger(AbstractDefGenerator.class.getName());

	public void generate(TargetStructure targetStructure, HashMap<String, Object> modelMap) {
		writeInterface(targetStructure, modelMap);
	}

	private void writeInterface(TargetStructure targetStructure, HashMap<String, Object> modelMap) {
		final String modelClassName = String.valueOf(modelMap.get(ModelClassProperty.modelName.name()));
		logger.info("writing model interface " + modelClassName);
		try (Writer writer = new BufferedWriter(new FileWriter(
				new File(targetStructure.getDirectory(Directory.MODEL_CLASS), modelClassName + ".java")));) {
			MustacheFactory mf = new DefaultMustacheFactory();
			Mustache mustache = mf.compile("AbstractDef.mustache");
			mustache.execute(writer, modelMap);
			writer.flush();
		} catch (IOException e) {
			logger.throwing(PomGenerator.class.getSimpleName(), "generate", e);
			throw new IllegalStateException("Failed to write model class");
		}
	}
}
