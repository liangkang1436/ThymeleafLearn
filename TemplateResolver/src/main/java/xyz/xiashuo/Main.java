package xyz.xiashuo;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

/**
 * @author xiashuo
 * @date ${DATE} ${TIME}
 */
public class Main {

    public static void main(String[] args) {
        rederFromClasspath();
        rederFromFile();
        readFromString();
    }

    private static void rederFromClasspath() {
        // 在类路径中加载模板
        ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
        classLoaderTemplateResolver.setTemplateMode(TemplateMode.TEXT);
        classLoaderTemplateResolver.setPrefix("/templates/");
        classLoaderTemplateResolver.setSuffix(".txt");
        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(classLoaderTemplateResolver);

        Context ctx = new Context();
        ctx.setVariable("name", "xiashuo");
        ctx.setVariable("from", "renderFromClasspath");
        String simpleText = templateEngine.process("simpleText", ctx);
        System.out.println(simpleText);
    }

    private static void rederFromFile() {
        // 从绝对路径中加载模板
        FileTemplateResolver fileTemplateResolver = new FileTemplateResolver();
        fileTemplateResolver.setTemplateMode(TemplateMode.TEXT);
        // 默认的工作目录是工程路径，而不是模板路径
        // 当前工程路径是 E:\IDEAProject\Thymeleaf
        String prefix = System.getProperty("user.dir") + "/TemplateResolver/src/main/resources/templates/";
        fileTemplateResolver.setPrefix(prefix);
        fileTemplateResolver.setSuffix(".txt");
        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(fileTemplateResolver);

        Context ctx = new Context();
        ctx.setVariable("name", "xiashuo");
        ctx.setVariable("from", "renderFromFile");
        String simpleText = templateEngine.process("simpleText", ctx);
        System.out.println(simpleText);
    }

    private static void readFromString(){
        // 不设置 TemplateResolver ，默认会使用 StringTemplateResolver
        final TemplateEngine templateEngine = new TemplateEngine();

        Context ctx = new Context();
        ctx.setVariable("name", "xiashuo");
        ctx.setVariable("from", "renderFromString");
        String simpleText = templateEngine.process("Hello World ,I'm [(${name})] from: [(${from})]", ctx);
        System.out.println(simpleText);
    }


}