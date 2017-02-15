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
import antlr.struct.TargetStructure.ModelClassProperty;

/**
 * Generates the model class and all necessary (service-) classes.
 * 
 * @author onkobu
 *
 */
public class ModelClassGenerator {
	private static final Logger logger = Logger.getLogger(ModelClassGenerator.class.getName());

	public void generate(TargetStructure targetStructure, HashMap<String, Object> modelMap) {
		writeModelClass(targetStructure, modelMap);
		writeModelRepo(targetStructure, modelMap);
		writeModelEndpoint(targetStructure, modelMap);
		writeModelAngular(targetStructure, modelMap);
	}

	private void writeModelRepo(TargetStructure targetStructure, HashMap<String, Object> modelMap) {
		final String modelClassName = String.valueOf(modelMap.get(ModelClassProperty.modelName.name()));
		logger.info("writing model repository " + modelClassName);
		try (Writer writer = new BufferedWriter(new FileWriter(
				new File(targetStructure.getDirectory(Directory.MODEL_CLASS), modelClassName + "Repository.java")));) {
			MustacheFactory mf = new DefaultMustacheFactory();
			Mustache mustache = mf.compile("ModelRepository.mustache");
			mustache.execute(writer, modelMap);
			writer.flush();
		} catch (IOException e) {
			logger.throwing(PomGenerator.class.getSimpleName(), "generate", e);
			throw new IllegalStateException("Failed to write repository");
		}
	}

	private void writeModelClass(TargetStructure targetStructure, HashMap<String, Object> modelMap) {
		final String modelClassName = String.valueOf(modelMap.get(ModelClassProperty.modelName.name()));
		logger.info("writing model class " + modelClassName);
		try (Writer writer = new BufferedWriter(new FileWriter(
				new File(targetStructure.getDirectory(Directory.MODEL_CLASS), modelClassName + ".java")));) {
			MustacheFactory mf = new DefaultMustacheFactory();
			Mustache mustache = mf.compile("ModelClass.mustache");
			mustache.execute(writer, modelMap);
			writer.flush();
		} catch (IOException e) {
			logger.throwing(PomGenerator.class.getSimpleName(), "generate", e);
			throw new IllegalStateException("Failed to write model class");
		}
	}

	private void writeModelEndpoint(TargetStructure targetStructure, HashMap<String, Object> modelMap) {
		final String modelClassName = String.valueOf(modelMap.get(ModelClassProperty.modelName.name()));
		logger.info("writing model endpoint " + modelClassName);
		try (Writer writer = new BufferedWriter(new FileWriter(new File(
				targetStructure.getDirectory(Directory.MODEL_CLASS), modelClassName + "RestController.java")));) {
			MustacheFactory mf = new DefaultMustacheFactory();
			Mustache mustache = mf.compile("ModelRestController.mustache");
			mustache.execute(writer, modelMap);
			writer.flush();
		} catch (IOException e) {
			logger.throwing(PomGenerator.class.getSimpleName(), "generate", e);
			throw new IllegalStateException("Failed to write endpoint");
		}
	}

	private void writeModelAngular(TargetStructure targetStructure, HashMap<String, Object> modelMap) {
		final String modelClassName = String.valueOf(modelMap.get(ModelClassProperty.modelName.name()));
		logger.info("writing model HTML " + modelClassName);
		try (Writer writer = new BufferedWriter(new FileWriter(new File(targetStructure.getDirectory(Directory.WEBAPP),
				modelMap.get(ModelClassProperty.modelNameL.name()) + ".html")));) {
			MustacheFactory mf = new DefaultMustacheFactory();
			Mustache mustache = mf.compile("model_angular.html.mustache");
			mustache.execute(writer, modelMap);
			writer.flush();
		} catch (IOException e) {
			logger.throwing(PomGenerator.class.getSimpleName(), "generate", e);
			throw new IllegalStateException("Failed to write HTML");
		}

		try (Writer writer = new BufferedWriter(new FileWriter(new File(targetStructure.getDirectory(Directory.WEBAPP),
				modelMap.get(ModelClassProperty.modelNameL.name()) + ".js")));) {
			MustacheFactory mf = new DefaultMustacheFactory();
			Mustache mustache = mf.compile("model_angular.js.mustache");
			mustache.execute(writer, modelMap);
			writer.flush();
		} catch (IOException e) {
			logger.throwing(PomGenerator.class.getSimpleName(), "generate", e);
			throw new IllegalStateException("Failed to write JS");
		}
	}

}
