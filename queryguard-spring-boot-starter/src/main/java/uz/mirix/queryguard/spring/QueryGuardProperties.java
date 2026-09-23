package uz.mirix.queryguard.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import uz.mirix.queryguard.QueryAnalysisOptions;
import java.util.*;

@ConfigurationProperties(prefix="queryguard")
public class QueryGuardProperties {
    private boolean enabled=true; private final DataSource dataSource=new DataSource(); private final Request request=new Request(); private final Detection detection=new Detection(); private final Headers headers=new Headers(); private final Logging logging=new Logging();
    public boolean isEnabled(){return enabled;} public void setEnabled(boolean enabled){this.enabled=enabled;} public DataSource getDataSource(){return dataSource;} public Request getRequest(){return request;} public Detection getDetection(){return detection;} public Headers getHeaders(){return headers;} public Logging getLogging(){return logging;}
    public QueryAnalysisOptions analysisOptions(){return new QueryAnalysisOptions(detection.nPlusOne,detection.nPlusOneThreshold,detection.minDistinctExecutions,detection.includeFailedQueries);}
    public static class DataSource { private boolean enabled=true; public boolean isEnabled(){return enabled;} public void setEnabled(boolean enabled){this.enabled=enabled;} }
    public static class Request { private boolean enabled=true; private List<String> excludedPathPrefixes=new ArrayList<>(List.of("/actuator","/favicon.ico")); public boolean isEnabled(){return enabled;} public void setEnabled(boolean enabled){this.enabled=enabled;} public List<String> getExcludedPathPrefixes(){return excludedPathPrefixes;} public void setExcludedPathPrefixes(List<String> v){excludedPathPrefixes=v==null?new ArrayList<>():new ArrayList<>(v);} }
    public static class Detection { private boolean nPlusOne=true; private int nPlusOneThreshold=5; private int minDistinctExecutions=2; private boolean includeFailedQueries=false; public boolean isNPlusOne(){return nPlusOne;} public void setNPlusOne(boolean v){nPlusOne=v;} public int getNPlusOneThreshold(){return nPlusOneThreshold;} public void setNPlusOneThreshold(int v){nPlusOneThreshold=v;} public int getMinDistinctExecutions(){return minDistinctExecutions;} public void setMinDistinctExecutions(int v){minDistinctExecutions=v;} public boolean isIncludeFailedQueries(){return includeFailedQueries;} public void setIncludeFailedQueries(boolean v){includeFailedQueries=v;} }
    public static class Headers { private boolean enabled=false; private boolean serverTiming=true; private boolean queryCount=true; public boolean isEnabled(){return enabled;} public void setEnabled(boolean v){enabled=v;} public boolean isServerTiming(){return serverTiming;} public void setServerTiming(boolean v){serverTiming=v;} public boolean isQueryCount(){return queryCount;} public void setQueryCount(boolean v){queryCount=v;} }
    public static class Logging { private boolean logAllRequests=false; private boolean warnOnNPlusOne=true; private int maxSqlLength=500; public boolean isLogAllRequests(){return logAllRequests;} public void setLogAllRequests(boolean v){logAllRequests=v;} public boolean isWarnOnNPlusOne(){return warnOnNPlusOne;} public void setWarnOnNPlusOne(boolean v){warnOnNPlusOne=v;} public int getMaxSqlLength(){return maxSqlLength;} public void setMaxSqlLength(int v){maxSqlLength=v;} }
}
