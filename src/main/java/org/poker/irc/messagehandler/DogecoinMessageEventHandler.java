package org.poker.irc.messagehandler;

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.joda.money.BigMoney;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.BotUtils;
import org.poker.irc.MessageEventHandler;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DogecoinMessageEventHandler implements MessageEventHandler {

    private MoneyFormatter moneyFormatter = new MoneyFormatterBuilder()
          .appendCurrencyCode()
          .appendAmount()
          .toFormatter();

    @Override
    public String getDescription() {
      return "!doge or .doge: gives you the latest info on DogeCoin";
    }

    @Override
    public String[] getMessagePrefixes() {
      return new String[] { ".doge", "!doge" };
    }

    @Override
    public void onMessage(MessageEvent event) {

      String url = "http://www.coinwarz.com/cryptocurrency/coins/dogecoin";
      Document document;

      com.xeiam.xchange.dto.marketdata.Ticker btcTicker = org.poker.irc.mtGox.TickerFactory.CreateBtcTicker();

      BigMoney thousandDogeUSD;
      BigDecimal cryptsyPrice;
      BigDecimal vircurexPrice;
      BigDecimal coinexPrice;

      DecimalFormat satoshi = new DecimalFormat("0.00000000");
      StringBuilder sb = new StringBuilder();

      try {

        document = Jsoup.connect(url).get();
        cryptsyPrice = new BigDecimal(document.select("td a[href*=cryptsy] b").text());
        vircurexPrice = new BigDecimal(document.select("td a[href*=vircurex] b").text());
        coinexPrice = new BigDecimal(document.select("td a[href*=coinex] b").text());

        thousandDogeUSD = btcTicker.getLast().multipliedBy(cryptsyPrice).multipliedBy(1000);

        String[] commandParts = event.getMessage().split(" ");

        BigDecimal dogeAmount;
        if(commandParts.length == 2) {
          dogeAmount = new BigDecimal(commandParts[1]);
        }
        else {
          dogeAmount = new BigDecimal("0");
        }

        if(dogeAmount.intValue() > 0) {
          sb.append(dogeAmount.toString());
          sb.append(" DOGE = ");
          BotUtils.appendMoney(btcTicker.getLast().multipliedBy(cryptsyPrice).multipliedBy(dogeAmount), sb);
        }
        else {
          sb.append("DOGE/BTC - Cryptsy: ");
          sb.append(satoshi.format(cryptsyPrice).toString());
          sb.append(" | Vircurex: ");
          sb.append(satoshi.format(vircurexPrice).toString());
          sb.append(" | CoinEx: ");
          sb.append(satoshi.format(coinexPrice).toString());
          sb.append(" | 1000 DOGE = ");
          BotUtils.appendMoney(thousandDogeUSD, sb);
        }

      } catch (IOException e) {
          throw new RuntimeException(e);
      }
      //sb.append(" | w.avg: ");
      //sb.append(ticker.get());
      event.getChannel().send().message(sb.toString());
  }

}
