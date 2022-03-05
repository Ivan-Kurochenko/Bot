package telegram.currencyMethods;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Currency {

    public Currency(){}

    public String getExchange(String text) {
        StringBuilder sb = new StringBuilder();
        Document doc = getDoc(text);
        if(doc == null) {
            return "\uD83D\uDEABIncorrect input\uD83D\uDEAB\n";
        }

        Element moneyFrom = doc.getElementsByAttributeValue("class", "result__ConvertedText-sc-1bsijpp-0 gwvOOF").get(0);
        Element moneyTo = doc.getElementsByAttributeValue("class", "result__BigRate-sc-1bsijpp-1 iGrAod").get(0);

        sb.append(moneyFrom.text())
                .append(" ")
                .append(moneyTo.text())
                .append('\n');
        return sb.toString();
    }

    public String getExchangeRate(String text) {
        StringBuilder sb = new StringBuilder();
        Document doc = getDoc(text);
        if(doc == null) {
            return "";
        }
        Element element = doc.getElementsByAttributeValue("class", "unit-rates___StyledDiv-sc-1dk593y-0 dEqdnx").get(0);
        for (int i = 0; i < element.childNodeSize(); i++) {
            sb.append(element.child(i).text()).append('\n');
        }
        return sb.toString();
    }

    private Document getDoc(String text){
        String[] words = text.split(" ");
        Document doc;
        try {
            doc = Jsoup.connect("https://www.xe.com/currencyconverter/convert/?Amount=" + words[0]+ "&From=" + words[1].toUpperCase() + "&To=" + words[2].toUpperCase()).get();
        } catch (IOException e) {
            return null;
        }
        return doc;
    }

    public boolean checkForInput(String text){
        Pattern p = Pattern.compile("^([0-9]+[\\.]?[0-9]*)[\\s]+([a-zA-Z]{3})[\\s]+([a-zA-Z]{3})$");
        Matcher m = p.matcher(text);
        return m.find();
    }

    public String getTopOfCurrencies() {
        StringBuilder sb = new StringBuilder();
        Document doc;
        try {
            doc = Jsoup.connect("https://www.xe.com/currencycharts").get();
        } catch (IOException e) {
            return "\uD83D\uDEABIncorrect input\uD83D\uDEAB";
        }
        Elements topList = doc.getElementsByAttributeValue("class", "table__TableBase-sc-1j0jd5l-0 pTERB");

        for (Element element : topList) {
            sb.append(element.child(0).text())
                    .append("\n");
            for (int j = 0; j < element.child(1).children().size(); j++) {
                sb.append(element.child(1).child(j).text()).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
