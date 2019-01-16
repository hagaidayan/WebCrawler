import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLprocessing {

    private static Pattern hrefPat = Pattern.compile("(?i)<a([^>]+)>"); //(.+?)</a>
    private static Pattern linkPat = Pattern.compile("\\s*(?i)( href|'href|\"href|<ahref)\\s*=\\s*(\\\"([^\"]*\\\")|'[^']*'|([^'\">\\s]+))");
    private static Pattern linkCheck = Pattern.compile("\\s*(\\p{Alnum}+)\\s*=\\s*('[^']*'|\\\"[^\\\"]*\\\")\\s*");
    private static Matcher matcherHREF, matcherLink, matcherCheck;

    public interface URLhandler {
        void takeUrl(String url);
    }

    public static URLhandler handler = new URLhandler() {
        public void takeUrl(String url) {
            System.out.println(url);
        }
    };


    /**
     * Parse the given buffer to fetch embedded links and call the handler to
     * process these links.
     *
     * @param data the buffer containing the http document
     */
    public static void parseDocument(CharSequence data) {
        // call handler.takeUrl for each matched url
        matcherHREF = hrefPat.matcher(data);
        while (matcherHREF.find()) {
            String href = matcherHREF.group(1);
            matcherCheck = linkCheck.matcher(href);
            while(matcherCheck.find()) {
                String key = matcherCheck.group(1);
                String value = matcherCheck.group(2);
                if(key.equalsIgnoreCase("href")){
                    value = value.substring(1, value.length() - 1);
                    try {
                        MyURL myUrl = new MyURL(value);
                        if (myUrl.getProtocol().equals("http")) {
                            handler.takeUrl(value);
                        }
                    } catch (IllegalArgumentException e) {

                    }
                }
            }
        }
    }
}
