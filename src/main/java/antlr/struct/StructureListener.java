package antlr.struct;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import antlr.struct.MetaStructureParser.AbstractDefContext;
import antlr.struct.MetaStructureParser.AggregateFunctionContext;
import antlr.struct.MetaStructureParser.AttributeContext;
import antlr.struct.MetaStructureParser.AttributeListContext;
import antlr.struct.MetaStructureParser.AttributeOrSetContext;
import antlr.struct.MetaStructureParser.BelongsToContext;
import antlr.struct.MetaStructureParser.ClassDefContext;
import antlr.struct.MetaStructureParser.ContextContext;
import antlr.struct.MetaStructureParser.DocumentContext;
import antlr.struct.MetaStructureParser.FunctionContext;
import antlr.struct.MetaStructureParser.FunctionListContext;
import antlr.struct.MetaStructureParser.InterfaceAliasContext;
import antlr.struct.MetaStructureParser.ScalarFunctionContext;
import antlr.struct.MetaStructureParser.SetContext;
import antlr.struct.types.Currency;

final class StructureListener implements MetaStructureListener {
	private final File root;
	private TargetStructure targetStructure;

	StructureListener(File root) {
		this.root = root;
	}

	@Override
	public void enterEveryRule(ParserRuleContext arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exitEveryRule(ParserRuleContext arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitErrorNode(ErrorNode arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitTerminal(TerminalNode arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterClassDef(ClassDefContext ctx) {
		targetStructure.startModelClass(ctx.NAME().getText(), StructureCreator.toClassName(ctx.NAME()));
	}

	@Override
	public void exitClassDef(ClassDefContext ctx) {
		targetStructure.flushModelClass();
	}

	@Override
	public void enterAttribute(AttributeContext ctx) {
		targetStructure.addAttribute(JavaTypeMapper.map(ctx.TYPE()), ctx.NAME().getText());
	}

	@Override
	public void exitAttribute(AttributeContext ctx) {

	}

	@Override
	public void enterAttributeList(AttributeListContext ctx) {
	}

	@Override
	public void exitAttributeList(AttributeListContext ctx) {
	}

	@Override
	public void enterDocument(DocumentContext ctx) {
	}

	@Override
	public void exitDocument(DocumentContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterContext(ContextContext ctx) {
		targetStructure = new TargetStructure(ctx.NAME().getText(), root);
		final PomGenerator pomGen = new PomGenerator();
		pomGen.generate(targetStructure);
		final SpringContextGenerator ctxGen = new SpringContextGenerator();
		ctxGen.generate(targetStructure);
		System.out.format("package %s;%n", ctx.NAME());
	}

	@Override
	public void exitContext(ContextContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterBelongsTo(BelongsToContext ctx) {
		System.out.format("\t%s %s; //ref%n", StructureCreator.toClassName(ctx.NAME()), ctx.NAME());
	}

	@Override
	public void exitBelongsTo(BelongsToContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterAttributeOrSet(AttributeOrSetContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exitAttributeOrSet(AttributeOrSetContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterSet(SetContext ctx) {
		final String name = ctx.NAME().getText();
		final String singleName = name.substring(0, name.length() - 1);
		targetStructure.addSetAttribute(StructureCreator.toClassName(singleName), name);
	}

	@Override
	public void exitSet(SetContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterInterfaceAlias(InterfaceAliasContext ctx) {
		System.out.format(" implements %s ", StructureCreator.toClassName(ctx.NAME()));
	}

	@Override
	public void exitInterfaceAlias(InterfaceAliasContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterAbstractDef(AbstractDefContext ctx) {
		targetStructure.startAbstractDef(ctx.NAME().getText(), StructureCreator.toClassName(ctx.NAME()));
	}

	@Override
	public void exitAbstractDef(AbstractDefContext ctx) {
		targetStructure.flushAbstractDef();
	}

	@Override
	public void enterFunctionList(FunctionListContext ctx) {
	}

	@Override
	public void exitFunctionList(FunctionListContext ctx) {
	}

	@Override
	public void enterFunction(FunctionContext ctx) {

	}

	@Override
	public void exitFunction(FunctionContext ctx) {
	}

	@Override
	public void enterAggregateFunction(AggregateFunctionContext ctx) {
		targetStructure.addAggregateFunction(ctx.NAME().getText(), ctx.AGGREGATE_OPERATOR().getText(),
				ctx.REFERENCE().getText());
		// System.out.format("\tpublic Object %s() {%n \t\treturn
		// %s(%s);%n\t}%n", );
	}

	@Override
	public void exitAggregateFunction(AggregateFunctionContext ctx) {
	}

	@Override
	public void enterScalarFunction(ScalarFunctionContext ctx) {
		List<TerminalNode> names = ctx.NAME();
		targetStructure.addScalarFunction(names.get(0).getText(), ctx.SCALAR_OPERATOR().getText(),
				names.subList(1, names.size()).stream().map(TerminalNode::getText).collect(Collectors.toList()));
		// System.out.format("\tpublic Object %s() {%n \t\treturn
		// %s(%s);%n\t}%n", names.get(0), ctx.SCALAR_OPERATOR(),
		// names.subList(1, names.size()));
	}

	@Override
	public void exitScalarFunction(ScalarFunctionContext ctx) {
		// TODO Auto-generated method stub

	}

	static class JavaTypeMapper {
		static Class<?> map(TerminalNode type) {
			switch (type.getText()) {
			case "number":
				return Integer.TYPE;
			case "shortText":
				return String.class;
			case "boolean":
				return Boolean.TYPE;
			case "currency":
				return Currency.class;
			case "dateTime":
				return Date.class;
			default: {
				throw new IllegalArgumentException("Type unknown: " + type);
			}
			}
		}
	}
}