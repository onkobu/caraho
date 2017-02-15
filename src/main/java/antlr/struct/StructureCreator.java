package antlr.struct;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.logging.Logger;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import antlr.struct.MetaStructureParser.DocumentContext;

public class StructureCreator {
	static final Logger logger = Logger.getLogger(StructureCreator.class.getName());

	/**
	 * Tries various syntaxes:
	 *
	 * <pre>
	 * <code>
	 * A machine has attributes: name as shortText, manufactured as date.
	 *
	 * A customer has attributes: name as shortText, street as shortText, zipCode as shortText.
	 *
	 *
	 * </code>
	 * </pre>
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		final File root = new File("/home/onkobu/projekte/structure/trunk/generated/");
		// TODO: inherit attributes from template

		try (InputStream strIn=Thread.currentThread().getContextClassLoader().getResourceAsStream("marep.structure");) {
			ANTLRInputStream in = new ANTLRInputStream(strIn);
			MetaStructureLexer lxr = new MetaStructureLexer(in);
			CommonTokenStream tokens = new CommonTokenStream(lxr);
			MetaStructureParser parser = new MetaStructureParser(tokens);
			DocumentContext docCtx = parser.document();
			ParseTreeWalker wlkr = new ParseTreeWalker();
			MetaStructureListener lstnr = new StructureListener(root);
			wlkr.walk(lstnr, docCtx);
		} catch (IOException e) {
			logger.throwing(StructureCreator.class.getName(), "main", e);
		}
	}

	static String toClassName(TerminalNode name) {
		return toClassName(name.getText());
	}

	public static String toClassName(String name) {
		if (name.charAt(0) >= 'A' && name.charAt(0) <= 'Z') {
			return name;
		}
		return name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
	}
}
