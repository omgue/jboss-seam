package org.jboss.seam.servlet;

import java.io.IOException;
import java.util.Map;

import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Pages;

/**
 * Propagates the conversation context across a browser redirect
 * 
 * @author Gavin King
 */
public class SeamRedirectFilter implements Filter 
{

   public void init(FilterConfig config) throws ServletException {}

   public void doFilter(ServletRequest request, ServletResponse response,
         FilterChain chain) throws IOException, ServletException 
   {
      chain.doFilter( request, wrapResponse( (HttpServletResponse) response ) );
   }
   
   private static ServletResponse wrapResponse(HttpServletResponse response) 
   {
      return new HttpServletResponseWrapper(response)
      {
         @Override
         public void sendRedirect(String url) throws IOException
         {
            if ( Contexts.isEventContextActive() )
            {
               String viewId = getViewId(url);
               if (viewId!=null)
               {
                  Map<String, Object> parameters = Pages.instance().getParameters(viewId);
                  url = Manager.instance().encodeParameters(url, parameters);
               }
               url = Manager.instance().appendConversationIdFromRedirectFilter(url);
            }
            super.sendRedirect(url);
         }
      };
   }

   public void destroy() {}  

   public static String getViewId(String url)
   {
      String servletPath = FacesContext.getCurrentInstance().getExternalContext().getRequestServletPath();
      String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
      String pathInfo = FacesContext.getCurrentInstance().getExternalContext().getRequestPathInfo();
      if ( url.startsWith(contextPath) )
      {
         String extension = servletPath.substring( servletPath.indexOf('.') );
         if ( url.endsWith(extension) || url.contains(extension + '?') )
         {
            int loc = url.indexOf('?');
            if (loc<0) loc = url.length();
            String suffix = getSuffix();
            return url.substring(contextPath.length(), loc - suffix.length() + 1) + suffix;
         }
         else
         {
            return null;
         }
      }
      else
      {
         return null;
      }
   }
   
   public static String getSuffix()
   {
      String defaultSuffix = FacesContext.getCurrentInstance().getExternalContext()
            .getInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME);
      return defaultSuffix == null ? ViewHandler.DEFAULT_SUFFIX : defaultSuffix;

   }
}
