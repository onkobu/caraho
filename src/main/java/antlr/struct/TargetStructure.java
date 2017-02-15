package antlr.struct;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import antlr.struct.types.Currency;

/**
 * Adapter between the listener/ parser-world and the file system/ application.
 *
 * @author onkobu
 *
 */
public class TargetStructure {
	private static final Logger logger = Logger.getLogger(TargetStructure.class.getName());
	private final String name;
	private final File baseDir;

	private final Map<Directory, File> directories = new HashMap<>();
	private final File projectRoot;
	private HashMap<String, Object> modelMap;

	public enum Directory {
		SOURCE, RESOURCE, TEST_SOURCE, TEST_RESOURCE, MODEL_CLASS, WEBAPP;
	}

	public TargetStructure(String name, File baseDir) {
		super();
		this.name = name;
		this.baseDir = baseDir;
		projectRoot = new File(baseDir, name);
		if (projectRoot.exists()) {
			deleteRootRecursive();
		}
		StructureCreator.logger.info("Populating " + projectRoot);
		projectRoot.mkdirs();
	}

	// TODO Keep it from deleting .project-files so that an Eclipse import stays
	// healthy.
	private void deleteRootRecursive() {
		StructureCreator.logger.info("Cleaning " + projectRoot);
		Path directory = projectRoot.toPath();
		try {
			Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (".project".equals(file.getFileName())) {
						return FileVisitResult.CONTINUE;
					}
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			logger.throwing(TargetStructure.class.getSimpleName(), "deleteRootRecursive", e);
			throw new IllegalStateException("cannot delete previous project structure", e);
		}
	}

	public String getName() {
		return name;
	}

	public File getBaseDir() {
		return baseDir;
	}

	public File getProjectRoot() {
		return projectRoot;
	}

	public String getRootPackage() {
		return "caraho";
	}

	public String getProjectPackage() {
		return getRootPackage() + "." + getName();
	}

	public void registerDirectory(Directory dir, File f) {
		if (directories.containsKey(dir)) {
			throw new IllegalStateException("Cannot overwrite present value for " + dir);
		}
		if (!f.isDirectory()) {
			throw new IllegalArgumentException("Is not a directory: " + f.getAbsolutePath());
		}
		directories.put(dir, f);
	}

	public File getDirectory(Directory dir) {
		return directories.get(dir);
	}

	public String getVersion() {
		return "1.0.0";
	}

	public String getProjectGroup() {
		return "caraho";
	}

	public void startModelClass(String name, String className) {
		modelMap = new HashMap<>();
		modelMap.put(ModelClassProperty.modelNameL.name(), name);
		modelMap.put(ModelClassProperty.modelName.name(), className);
		modelMap.put(ModelClassProperty.modelPackage.name(), getProjectPackage() + ".model");
	}

	public void flushModelClass() {
		ModelClassGenerator mcg = new ModelClassGenerator();
		mcg.generate(this, modelMap);
	}

	public void addAttribute(Class<?> type, String name) {
		List<Attribute> attrLst = (List<Attribute>) modelMap.get(ModelClassProperty.attributeList.name());
		if (attrLst == null) {
			attrLst = new ArrayList<>();
			modelMap.put(ModelClassProperty.attributeList.name(), attrLst);
		}
		attrLst.add(new Attribute(type, name));
	}

	public void addSetAttribute(String className, String name) {
		List<Attribute> attrLst = (List<Attribute>) modelMap.get(ModelClassProperty.setAttributeList.name());
		if (attrLst == null) {
			attrLst = new ArrayList<>();
			modelMap.put(ModelClassProperty.setAttributeList.name(), attrLst);
		}
		attrLst.add(new Attribute(className, name));
	}

	public void startAbstractDef(String name, String className) {
		modelMap = new HashMap<>();
		modelMap.put(ModelClassProperty.modelNameL.name(), name);
		modelMap.put(ModelClassProperty.modelName.name(), className);
		modelMap.put(ModelClassProperty.modelPackage.name(), getProjectPackage() + ".model");
	}

	public void flushAbstractDef() {
		AbstractDefGenerator mcg = new AbstractDefGenerator();
		mcg.generate(this, modelMap);
	}

	public void addScalarFunction(String name, String operator, List<String> arguments) {
		List<Function> funcLst = (List<Function>) modelMap.get(ModelClassProperty.functionList.name());
		if (funcLst == null) {
			funcLst = new ArrayList<>();
			modelMap.put(ModelClassProperty.functionList.name(), funcLst);
		}
		funcLst.add(new Function(name, operator, arguments));
	}

	public void addAggregateFunction(String name, String operator, String reference) {
		List<Function> funcLst = (List<Function>) modelMap.get(ModelClassProperty.functionList.name());
		if (funcLst == null) {
			funcLst = new ArrayList<>();
			modelMap.put(ModelClassProperty.functionList.name(), funcLst);
		}
		funcLst.add(new Function(name, operator, reference));
	}

	static class Function {
		private final String name;
		private final String operator;
		private final String reference;
		private final List<String> arguments;

		public Function(String name, String operator, String reference) {
			this.name = name;
			this.operator = operator;
			this.reference = reference;
			this.arguments = null;
		}

		public Function(String name, String operator, List<String> arguments) {
			this.name = name;
			this.operator = operator;
			this.reference = null;
			this.arguments = arguments;
		}

		public String getName() {
			return name;
		}

		public String getOperator() {
			return operator;
		}

		public String getReference() {
			return reference;
		}

		public List<String> getArguments() {
			return arguments;
		}
	}

	static class Attribute {
		private final String attributeType;
		private final String attributeName;
		private final String attributeImport;
		private final String aattributeName;

		public Attribute(Class<?> attributeType, String attributeName) {
			super();
			this.attributeName = attributeName;
			this.aattributeName = attributeName.substring(0, 1).toUpperCase(Locale.ROOT) + attributeName.substring(1);
			if (attributeType == Currency.class) {
				this.attributeType = Integer.TYPE.getSimpleName();
				attributeImport = null;
			} else {
				this.attributeType = attributeType.getSimpleName();
				if (attributeType.getName().startsWith("java.lang") || attributeType.isPrimitive()) {
					attributeImport = null;
				} else {
					attributeImport = attributeType.getName();
				}
			}
		}

		public Attribute(String className, String name) {
			attributeType = className;
			attributeName = name;
			attributeImport = null;
			aattributeName = className;
		}

		public String getAttributeType() {
			return attributeType;
		}

		public String getAttributeName() {
			return attributeName;
		}

		public String getAattributeName() {
			return aattributeName;
		}

		public String getAttributeImport() {
			return attributeImport;
		}
	}

	enum ModelClassProperty {
		/**
		 * Class name/ starting with upper case letter.
		 */
		modelName,

		modelPackage,

		attributeList,

		/**
		 * model name, unchanged, starting with lower case letter.
		 */
		modelNameL,

		functionList,

		setAttributeList;
	}

}
