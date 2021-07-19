package com.ylw.annotation_compiler;


import com.google.auto.service.AutoService;
import com.ylw.annotation.SafeRun;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;


/**
 * author : liwen15
 * date : 2021/7/18
 * description : safeRun 注解处理器
 */
@AutoService(Processor.class)
public class SafeRunProcessor extends AbstractProcessor {

    private Messager messager;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        Context context = ((JavacProcessingEnvironment) processingEnvironment).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            //scan class
            for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(SafeRun.class)) {
                if (annotatedElement.getKind() != ElementKind.METHOD) {
                    error(annotatedElement, "Only normal method can be annotated with @%s", SafeRun.class.getSimpleName());
                    continue;
                }
                final JavacElements elementUtils = (JavacElements) processingEnv.getElementUtils();
                JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) elementUtils.getTree(annotatedElement);
                treeMaker.pos = jcMethodDecl.pos;
                enhanceMethod(jcMethodDecl);
            }
        } catch (Throwable throwable) {
            error(throwable.getMessage(), "process error");
        }
        return false;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        info("CanonicalName is %s", SafeRun.class.getCanonicalName());
        annotations.add(SafeRun.class.getCanonicalName());
        return annotations;
    }


    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private void error(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    private void info(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }

    /**
     * 方法增强，增加try catch
     *
     * @return JCMethodDecl
     */
    private JCTree.JCMethodDecl enhanceMethod(JCTree jcTree) {
        JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) jcTree;
        messager.printMessage(Diagnostic.Kind.NOTE, "before:" + jcMethodDecl.body.toString());

        ListBuffer<JCStatement> statements = new ListBuffer<JCStatement>();
        statements.append(getJcTry(jcMethodDecl));
        JCTree.JCReturn jcReturn = getJcReturn(jcMethodDecl);
        if (jcReturn != null) {
            statements.append(jcReturn);
        }
        jcMethodDecl.body = treeMaker.Block(0, statements.toList());

        messager.printMessage(Diagnostic.Kind.NOTE, "after:" + jcMethodDecl.body.toString());
        return jcMethodDecl;
    }

    private JCTree.JCReturn getJcReturn(JCTree.JCMethodDecl jcMethodDecl) {
        JCTree.JCReturn jcReturn = null;
        if (jcMethodDecl.getReturnType().toString().equals("boolean")) {
            jcReturn = treeMaker.Return(treeMaker.Literal(TypeTag.BOOLEAN, 0));
        } else if (jcMethodDecl.getReturnType().toString().equals("int")) {
            jcReturn = treeMaker.Return(treeMaker.Literal(TypeTag.INT, -1));
        } else if (jcMethodDecl.getReturnType().toString().equals("void")) {

        } else {
            jcReturn = treeMaker.Return(treeMaker.Literal(TypeTag.BOT, null));
        }
        return jcReturn;
    }

    private JCTree.JCStatement getJcTry(JCTree.JCMethodDecl jcMethod) {
        JCTree.JCBlock catchBlock = treeMaker.Block(0, List.<JCStatement>nil());
        return treeMaker.Try(jcMethod.body,
                List.of(treeMaker.Catch(createVarDef(treeMaker.Modifiers(0), "e", memberAccess("java.lang.Exception"),
                        null), catchBlock)), null);
    }

    private JCTree.JCVariableDecl createVarDef(JCTree.JCModifiers modifiers, String name, JCTree.JCExpression varType, JCTree.JCExpression init) {
        return treeMaker.VarDef(
                modifiers,
                getNameFromString(name),
                varType,
                init
        );
    }

    private com.sun.tools.javac.util.Name getNameFromString(String s) {
        return names.fromString(s);
    }

    private JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(getNameFromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, getNameFromString(componentArray[i]));
        }
        return expr;
    }

}
