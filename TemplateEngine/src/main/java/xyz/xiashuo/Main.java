package xyz.xiashuo;

import org.thymeleaf.IThrottledTemplateProcessor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.StringWriter;

/**
 * @author xiashuo
 * @date ${DATE} ${TIME}
 */
public class Main {
    public static void main(String[] args) {
        rederFromClasspath();
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
    IThrottledTemplateProcessor processThrottled = templateEngine.processThrottled("simpleText", ctx);
    StringWriter stringWriter = new StringWriter();
    // 一次只读一个字节的内容
    while (processThrottled.process(1, stringWriter) != 0) {
        // 一个字节一个字节地累计输出
        System.out.println(stringWriter.toString());
    }
}
}