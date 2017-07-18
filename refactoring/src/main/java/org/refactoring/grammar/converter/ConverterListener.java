package org.refactoring.grammar.converter;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.refactoring.grammar.parser.JavaParser.ClassDeclarationContext;
import org.refactoring.grammar.parser.JavaParser.CompilationUnitContext;
import org.refactoring.grammar.parser.JavaParser.EnumDeclarationContext;
import org.refactoring.grammar.parser.JavaParser.ImportDeclarationContext;
import org.refactoring.grammar.parser.JavaParser.PackageDeclarationContext;
import org.refactoring.grammar.parser.JavaParser.StatementExpressionContext;
import org.refactoring.grammar.parser.JavaParser.TypeDeclarationContext;

public class ConverterListener extends AbstractJavaListener {
	TokenStreamRewriter rewriter;
	Token startImportToken;
	Token startDeclToken;
	boolean flagImportDecDone = false;
	private TerminalNode name;

	public ConverterListener(TokenStream tokens) {
		rewriter = new TokenStreamRewriter(tokens);
	}

	public void enterImportDeclaration(ImportDeclarationContext ctx) {
		if(ctx.getText().contains("org.simplity.kernel.Tracer")){
			rewriter.delete(ctx.start,ctx.stop);
		};
	};

	@Override
	public void enterStatementExpression(StatementExpressionContext ctx) {
		if (ctx.getText().startsWith("Tracer.trace")) {
			rewriter.delete(ctx.getParent().start,ctx.getParent().stop);
		}
	}

}
