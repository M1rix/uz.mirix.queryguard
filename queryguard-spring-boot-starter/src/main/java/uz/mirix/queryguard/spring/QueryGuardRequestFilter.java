package uz.mirix.queryguard.spring;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.mirix.queryguard.*;
import java.io.IOException;
import java.util.Locale;

final class QueryGuardRequestFilter extends OncePerRequestFilter {
    private final QueryGuardProperties properties; private final QueryGuardDiagnostics diagnostics;
    QueryGuardRequestFilter(QueryGuardProperties properties,QueryGuardDiagnostics diagnostics){this.properties=properties;this.diagnostics=diagnostics;}
    @Override protected boolean shouldNotFilter(HttpServletRequest request){String uri=request.getRequestURI();return properties.getRequest().getExcludedPathPrefixes().stream().anyMatch(uri::startsWith);}
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain)throws ServletException,IOException{
        String scopeName=request.getMethod()+" "+request.getRequestURI(); QueryScope scope=QueryGuard.openScope(scopeName,properties.analysisOptions(),QueryBudgetSpec.unlimited());
        try{filterChain.doFilter(request,response);}finally{QueryReport report=scope.closeAndReport();addHeaders(response,report);diagnostics.report(report);}
    }
    private void addHeaders(HttpServletResponse response,QueryReport report){if(!properties.getHeaders().isEnabled()||response.isCommitted())return;if(properties.getHeaders().isQueryCount())response.setHeader("X-QueryGuard-Queries",Integer.toString(report.totalQueries()));if(properties.getHeaders().isServerTiming())response.addHeader("Server-Timing","db;dur="+String.format(Locale.ROOT,"%.2f",report.totalDurationMillis())+";desc=\""+report.totalQueries()+" SQL queries\"");}
}
