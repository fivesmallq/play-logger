package play.modules.logger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class CardNumberFilteringLayoutTest {
  CardNumberFilteringLayout cardNumberFilteringLayout = spy(new CardNumberFilteringLayout());

  @Test
  public void cardNumbersAreMasked() throws Exception {
    LoggingEvent event = new LoggingEvent("", Logger.getRootLogger(), Level.ALL, "Hello card 1234123412341234 and longer card 1234123412341234123, but not account DE123412341234123412341234", null);

    assertThat(cardNumberFilteringLayout.format(event), is("Hello card 123412******1234 and longer card 123412******4123, but not account DE123412341234123412341234\n"));
  }

  @Test
  public void doNotCreateNewEventIfNoCardNumbersWereFound() throws Exception {
    LoggingEvent event = new LoggingEvent("", Logger.getRootLogger(), Level.ALL, "foo", null);
    ArgumentCaptor<LoggingEvent> eventCaptor = ArgumentCaptor.forClass(LoggingEvent.class);

    assertThat(cardNumberFilteringLayout.format(event), is("foo\n"));

    verify(cardNumberFilteringLayout).superFormat(eventCaptor.capture());
    assertThat(eventCaptor.getValue(), is(sameInstance(event)));
  }
}