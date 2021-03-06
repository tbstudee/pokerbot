package org.poker.irc.messagehandler;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.MessageEventHandler;

public class UptimeMessageEventHandler implements MessageEventHandler {
  private static final DateTime startTime = new DateTime();
  @Override
  public String[] getMessagePrefixes() {
    return new String[] { ".uptime", "!uptime" };
  }

  @Override
  public String getDescription() {
    return "!uptime or .uptime: shows how long the bot has been active.";
  }

  @Override
  public void onMessage(MessageEvent event) {
    DateTime now = new DateTime();
    Period period = new Period(startTime, now);
    PeriodFormatter formatter = new PeriodFormatterBuilder()
        .appendYears()
        .appendSeparator(" years, ")
        .appendMonths()
        .appendSeparator(" months, ")
        .appendDays()
        .appendSeparator(" days, ")
        .minimumPrintedDigits(2)
        .appendHours().appendSeparator(":")
        .printZeroAlways()
        .appendMinutes().appendSeparator(":")
        .appendSeconds()
        .toFormatter();
    String message = formatter.print(period);
    event.getChannel().send().message("up " + message);
  }
}
