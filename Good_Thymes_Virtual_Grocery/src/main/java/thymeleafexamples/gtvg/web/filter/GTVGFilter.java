/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package thymeleafexamples.gtvg.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebRequest;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import thymeleafexamples.gtvg.business.entities.User;
import thymeleafexamples.gtvg.web.controller.IGTVGController;
import thymeleafexamples.gtvg.web.mapping.ControllerMappings;

import java.io.IOException;
import java.io.Writer;


// 把过滤器当servlet用了
public class GTVGFilter implements Filter {

    private ITemplateEngine templateEngine;
    private JakartaServletWebApplication application;

    
    public GTVGFilter() {
        super();
    }
    
    private static void addUserToSession(final HttpServletRequest request) {
        // Simulate a real user session by adding a user object
        request.getSession(true).setAttribute("user", new User("John", "Apricot", "Antarctica", null));
    }

    // 初始化模板引擎
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.application =
                JakartaServletWebApplication.buildApplication(filterConfig.getServletContext());
        this.templateEngine = buildTemplateEngine(this.application);
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
                         final FilterChain chain) throws IOException, ServletException {
        addUserToSession((HttpServletRequest)request);
        // 将请求委托给 process 方法
        // 如果正常处理，则返回true，则不传给下一个过滤器了
        // 如果不是正常处理，则返回false，传给下一个过滤器，但是不存在能处理的下一个过滤器，所以报错
        if (!process((HttpServletRequest)request, (HttpServletResponse)response)) {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // nothing to do
    }

    // 正常处理返回 true，处理过程有问题返回false。
    private boolean process(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException {
        
        try {

            final IWebExchange webExchange = this.application.buildExchange(request, response);
            final IWebRequest webRequest = webExchange.getRequest();

            // This prevents triggering engine executions for resource URLs
            if (webRequest.getPathWithinApplication().startsWith("/css") ||
                    webRequest.getPathWithinApplication().startsWith("/images") ||
                    webRequest.getPathWithinApplication().startsWith("/favicon")) {
                return false;
            }

            // 路由到具体的控制器
            /*
             * Query controller/URL mapping and obtain the controller
             * that will process the request. If no controller is available,
             * return false and let other filters/servlets process the request.
             */
            final IGTVGController controller = ControllerMappings.resolveControllerForRequest(webRequest);
            if (controller == null) {
                return false;
            }

            /*
             * Write the response headers
             */
            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            /*
             * Obtain the response writer
             */
            final Writer writer = response.getWriter();

            // 控制器处理
            /*
             * Execute the controller and process view template,
             * writing the results to the response writer. 
             */
            controller.process(webExchange, this.templateEngine, writer);
            
            return true;
            
        } catch (final Exception e) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (final IOException ignored) {
                // Just ignore this
            }
            throw new ServletException(e);
        }
        
    }
    

    private static ITemplateEngine buildTemplateEngine(final IWebApplication application) {

        final WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);

        // 设置 模板模式，也就是这个 TemplateEngine  要用来渲染什么拓展名的文件，这里我们指定的是 HTML（默认就是HTML）
        templateResolver.setTemplateMode(TemplateMode.HTML);
        // 设置模板名称的前缀和后缀。
        // 我们在调用 ITemplateEngine#process方法的时候都要指定模板名称，这里配置的前缀和后缀就是用来确定模板名称对应的文件的实际路径的
        // 根据下面的配置，视图名称为"home"的视图会去 "/WEB-INF/templates/home.html" 这个位置查找
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        // 模板名称跟实际路径的信息会进行缓存，这里设置缓存时间，如果没有设置，会根据LRU算法进行移除
        // TTL (Time To Live) ；LRU (Least Recently Used，谁被缓存的最早，谁被消除)
        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));

        // 默认进行缓存，
        templateResolver.setCacheable(true);

        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine;

    }
    
}
