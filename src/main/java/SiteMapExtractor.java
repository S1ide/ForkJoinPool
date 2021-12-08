import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;

public class SiteMapExtractor extends RecursiveAction {

    private static final TreeSet<String> urls = new TreeSet<>();
    private final String pageUrl;

    public SiteMapExtractor(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    @Override
    protected void compute() {
        urls.add(pageUrl);
        List<SiteMapExtractor> tasks = new ArrayList<>();

        for (String childUrl : getChildUrls()) {
            if (!urls.contains(childUrl)) {
                SiteMapExtractor task = new SiteMapExtractor(childUrl);
                task.fork();
                tasks.add(task);
            }
        }
        tasks.forEach(ForkJoinTask::join);
    }

    private Set<String> getChildUrls() {
        Set<String> urls = new HashSet<>();
        try {
            Document doc = Jsoup
                    .connect(pageUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36")
                    .ignoreHttpErrors(true)
                    .get();
            URL baseUrl = new URL(pageUrl);
            urls = doc.select("a").stream()
                    .map(e -> getChildUrl(baseUrl, e.attr("href")))
                    .filter(u -> u.startsWith(pageUrl))
                    .collect(Collectors.toSet());

        } catch (Exception ex) {
            System.out.printf("Ошибка парсинга страницы '%s': %s%n", pageUrl, ex.getMessage());
        }

        return urls;
    }

    /**
     * Возвращает абсолютную ссылку на основе содержимого href,
     * отбрасывает часть ссылки после символа '#' (anchor)
     */
    private String getChildUrl(URL baseUrl, String href) {
        try {
            String childUrl = new URL(baseUrl, href).toString();
            int anchorIndex = childUrl.indexOf('#');
            if (anchorIndex > 0) {
                childUrl = childUrl.substring(0, anchorIndex);
            }
            return childUrl;
        } catch (MalformedURLException ex) {
            // just ignore incorrect urls
            return "";
        }
    }

    public static Set<String> getUrls() {
        return urls;
    }
}
