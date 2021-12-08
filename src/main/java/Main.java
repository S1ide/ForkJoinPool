import org.apache.commons.lang3.StringUtils;
import org.jsoup.internal.StringUtil;

import java.io.*;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static final String ROOT_URL = "https://skillbox.ru/";

    public static void main(String[] args) {
        SiteMapExtractor siteMapExtractor = new SiteMapExtractor(ROOT_URL);
        new ForkJoinPool().invoke(siteMapExtractor);
        TreeSet<String> urls = (TreeSet<String>) SiteMapExtractor.getUrls();
        try {
            Helper.write("out.txt", urls);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
