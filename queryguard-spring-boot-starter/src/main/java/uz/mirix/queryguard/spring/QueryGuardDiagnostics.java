package uz.mirix.queryguard.spring;

import org.slf4j.*;
import uz.mirix.queryguard.*;

final class QueryGuardDiagnostics {
    private static final Logger log=LoggerFactory.getLogger("uz.mirix.queryguard.QueryGuard"); private final QueryGuardProperties properties;
    QueryGuardDiagnostics(QueryGuardProperties properties){this.properties=properties;}
    void report(QueryReport report){
        if(report.budgetExceeded()){log.warn(QueryReportFormatter.format(report));return;}
        if(report.hasNPlusOne()&&properties.getLogging().isWarnOnNPlusOne()){
            StringBuilder message=new StringBuilder("QueryGuard possible N+1 in '").append(report.scopeName()).append("': ");
            for(int i=0;i<report.nPlusOneFindings().size();i++){NPlusOneFinding finding=report.nPlusOneFindings().get(i);if(i>0)message.append(" | ");message.append(finding.executions()).append("x ").append(QueryReportFormatter.abbreviate(finding.normalizedSql(),properties.getLogging().getMaxSqlLength()));}
            log.warn(message.toString());
        } else if(properties.getLogging().isLogAllRequests()) log.info(QueryReportFormatter.format(report)); else if(log.isDebugEnabled()) log.debug(QueryReportFormatter.format(report));
    }
}
