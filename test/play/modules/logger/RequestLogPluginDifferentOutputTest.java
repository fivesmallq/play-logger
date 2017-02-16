package play.modules.logger;

import org.junit.Before;
import org.junit.Test;
import play.Play;
import play.mvc.Http;
import play.mvc.results.*;

import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestLogPluginDifferentOutputTest {
  @SuppressWarnings("deprecation")
  Http.Request request = new Http.Request();

  @Before
  public void setUp() {
    if (Play.configuration == null) Play.configuration = new Properties();
    Play.configuration.setProperty("request.log.output", "play.modules.logger.EvilResultOutput");
    new RequestLogPlugin().onConfigurationRead();
    Http.Request.current.set(request);
  }


  @Test
  public void noResultMeansRenderingError() {
    assertEquals("RenderError", RequestLogPlugin.result(null));
  }

  @Test
  public void logsRedirectUrl() {
    Redirect result = new Redirect("/foo");
    assertEquals("Evil Redirect /foo", RequestLogPlugin.result(result));
  }

  @Test
  public void logsTemplateRenderingTime() {
    RenderTemplate result = mock(RenderTemplate.class);
    when(result.getRenderTime()).thenReturn(101L);
    when(result.getName()).thenReturn("Employees/registry.html");
    assertEquals("Evil RenderTemplate Employees/registry.html 101 ms", RequestLogPlugin.result(result));
  }

  @Test
  public void logsFileName_forRenderBinary() {
    RenderBinary result = new RenderBinary((InputStream) null, "statement.dbf");
    assertEquals("Evil RenderBinary statement.dbf", RequestLogPlugin.result(result));
  }

  @Test
  public void logsFileNameAndContentType_forRenderBinary() {
    RenderBinary result = new RenderBinary(null, "cert.pem", "application/pkix-cert", false);
    assertEquals("Evil RenderBinary cert.pem application/pkix-cert", RequestLogPlugin.result(result));
  }
  
  @Test
  public void logsReason_forError() {
    Forbidden forbidden = new Forbidden("User signature not valid!");
    assertEquals("Evil Forbidden \"User signature not valid!\"", RequestLogPlugin.result(forbidden));
    BadRequest badRequest = new BadRequest("input error!");
    assertEquals("Evil BadRequest \"input error!\"", RequestLogPlugin.result(badRequest));
    EvilRequest evilRequest=new EvilRequest("evil request");
    assertEquals("Evil EvilRequest \"evil request\"", RequestLogPlugin.result(evilRequest));
  }
}

