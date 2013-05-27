package play.modules.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Play;
import play.PlayPlugin;
import play.mvc.Http;
import play.mvc.Scope;
import play.mvc.results.Result;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class RequestLogPlugin extends PlayPlugin {
  static final String REQUEST_ID_PREFIX = Integer.toHexString((int)(Math.random() * 0x1000));
  static final AtomicInteger counter = new AtomicInteger(1);
  static final Logger logger = LoggerFactory.getLogger("request");

  static final String LOG_AS_PATH = Play.configuration.getProperty("request.log.pathForAction", "Web.");
  static final Pattern EXCLUDE = Pattern.compile("(authenticityToken|action|controller|x-http-method-override)=.*?(\t|$)");
  static final Pattern MASK = Pattern.compile("(?i)(.*?(?=" + Play.configuration.getProperty("request.log.maskParams", "password|card\\.cvv|card\\.number") + ").*?)=.*?(\t|$)");

  @Override public void routeRequest(Http.Request request) {
    request.args.put("startTime", currentTimeMillis());
    request.args.put("requestId", REQUEST_ID_PREFIX + "-" + counter.incrementAndGet());
  }

  @Override public void onActionInvocationResult(Result result) {
    logRequestInfo(result);
  }

  static void logRequestInfo(Result result) {
    Http.Request request = Http.Request.current();
    Scope.Session session = Scope.Session.current();
    long start = (Long)request.args.get("startTime");

    StringBuilder line = new StringBuilder()
      .append(request.action.startsWith(LOG_AS_PATH) ? request.path : request.action).append(' ')
      .append(request.remoteAddress).append(' ')
      .append(session.getId()).append(' ')
      .append(extractParams(request))
      .append(" -> ").append(result.getClass().getSimpleName())
      .append(' ').append(currentTimeMillis() - start).append(" ms");

    logger.info(line.toString());
  }

  static String extractParams(Http.Request request) {
    try {
      String params = request.querystring;
      if (params.startsWith("?")) params = params.substring(1);
      if ("application/x-www-form-urlencoded".equals(request.contentType))
        params += (isNotEmpty(params) ? "\t" : "") + request.params.get("body");
      params = params.replace("&", "\t");
      params = EXCLUDE.matcher(params).replaceAll("");
      params = MASK.matcher(params).replaceAll("$1=*$2");
      return URLDecoder.decode(params, "UTF-8");
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
