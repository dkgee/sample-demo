package com.example.demo.util;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述：文本工具类
 * 作者：JinHuaTao
 * 时间：2018/6/21 17:14
 */
public class TextUtil {

    public static final String WWW_START_HEAD = "www.";

    public static final String HTTP_START_HEAD = "http://";

    public static final String HTTPS_START_HEAD = "https://";

    private static Pattern patt = Pattern.compile("[`~!@#$%^&*()\\-+={}\\[\\]/?￥%\\n(\\ud83c[\\udf00-\\udfff])|(\\ud83d[\\udc00-\\ude4f])|(\\ud83d[\\ude80-\\udeff])]");

    private static Pattern pattern = Pattern.compile("([\u4e00-\u9fa5])+",Pattern.MULTILINE | Pattern.DOTALL);

    private static Pattern ipNumber = Pattern.compile("(?=(\\b|\\D))(((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))(?=(\\b|\\D))");

    /**
     * 匹配包含  xx://xxx 模式的的正则表达式
     * 例如：
     *      www.cp.dawangidc.com/
     *      http://cp.u9un.com/
     *      https://fqjsjl.tumblr.com/
     *      http://www.cp.dawangidc.com/
     *      https://www.cp.dawangidc.com/
     *      ss://xxx
     *      ssr://xxx
     *      vmess://xxxx
     * */
    private static String urlRegex = "(([\\w-]+://?|www[.])[^\\s()<>,，\u4e00-\u9fa5]+(?:[\\w\\d]+|([^[:punct:]\\s]|/)))";

    private static Pattern urlPattern = Pattern.compile(urlRegex);


    /**
     * 通用正则匹配
     * */
    public static List<String> commonReg(final String reg, final String text){
        Pattern regPattern = Pattern.compile(reg);
        Matcher m = regPattern.matcher(text);
        List<String> list = new ArrayList<>();
        while (m.find()){
            list.add(m.group(1));
        }
        return list;
    }

    /**
     * 提取文本中的常见的URL
     * */
    public static List<String> getCommonUrl(final String text){
        Matcher m = urlPattern.matcher(text);
        List<String> list = new ArrayList<>();
        while (m.find()){
            list.add(m.group(1));
        }
        return list;
    }

    /**
     * 去掉特殊字符
     * */
    public static String trimeSpecialCharacter(String str) {
        Matcher m = patt.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 去除所有的空格
     */
    public String trimAllBlank(final String source) {
        String tmpStr = source.replaceAll("\\s+", "");
        return tmpStr;
    }

    /**
     * 去除中文字符
     * */
    public static String trimeChinese(String str) {
        Matcher m = pattern.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 判断是否有中文字符
     * */
    public static boolean hasChinese(String str) {
        Matcher m = pattern.matcher(str);
        if(m.find()){
            return true;
        }
        return false;
    }

    /**
     * 判断是否正确
     * */
    public static boolean correctIP(String str) {
        Matcher m = ipNumber.matcher(str);
        if(m.find()){
            return true;
        }
        return false;
    }

    /**
     * 获取集合文本
     * */
    private static String getListText(List<String> list){
        String linkText = null;
        if(list != null){
            linkText = Arrays.deepToString(list.toArray());
        }
        return linkText;
    }




    /**
     * 抽取所有css链接
     * */
    public static List<String> extractLinkHref(String text) {
        if(StringUtils.isBlank(text)){
            return null;
        }
        String linkReg = "(<link[\\s]+[^>]+>)";
        String hrefReg = "href *= *['\"]*(\\S+)[\"']";
        List<String> linkList = commonReg(linkReg, text);
        String linkText = Arrays.deepToString(linkList.toArray());
        return commonReg(hrefReg, linkText);
    }

    /**
     * 抽取所有css链接的文本
     * */
    public static String extractLinkHrefText(String text) {
        List<String> result = extractLinkHref(text);
        return getListText(result);
    }

    /**
     * 抽取所有js链接
     * */
    public static List<String> extractScriptSrc(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
//        String scriptReg = "(<script[^>]*>(.*?)<\\/script>)";
        String scriptReg = "(<script[^>]*><\\/script>)";
        String srcReg = "src *= *['\"]*(\\S+)[\"']";
        List<String> scriptList = commonReg(scriptReg, text);
        String scriptText = Arrays.deepToString(scriptList.toArray());
        return commonReg(srcReg, scriptText);
    }

    /**
     * 抽取所有js链接文本
     * */
    public static String extractScriptSrcText(String text){
        List<String> result = extractScriptSrc(text);
        return getListText(result);
    }

    /**
     * 抽取文本中所有a标签链接
     * */
    public static List<String> extractTagAHref(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        String aReg = "(<a[\\s]+[^>]+>)";
        String hrefReg = "href *= *['\"]*(\\S+)[\"']";
        List<String> scriptList = commonReg(aReg, text);
        String scriptText = Arrays.deepToString(scriptList.toArray());
        return commonReg(hrefReg, scriptText);
    }

    /**
     * 抽取文本中所有a标签链接文本
     * */
    public static String extractTagAHrefText(String text){
        List<String> result = extractTagAHref(text);
        return getListText(result);
    }

    /**
     * @Author jiabo
     * @Description 取出字符串中的 中文英文数字
     * @Date
     * @Param
     * @return
     **/
    public static String formartText(String text){
        StringBuffer sbf = new StringBuffer();
        if(text.contains("\\")){
            String[] split = text.split("\\\\");
            text=split[0];
        }
        char[] charArray = text.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if ((charArray[i] >= 0x4e00) && (charArray[i] <= 0x9fbb)) {
                sbf.append(charArray[i]);
            }
            if ((charArray[i] >= 65) && (charArray[i] <= 122)) {
                sbf.append(charArray[i]);
            }
            if (charArray[i] >= 48 && charArray[i] <= 57) {
                sbf.append(charArray[i]);
            }
        }
        return sbf.toString();

    }
/**
 * @Author jiabo
 * @Description 文本中是否包含中文
 **/
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 抽取所有的标签的CSS
     * */
    public static List<String> extractTagCss(String text) {
        if(StringUtils.isBlank(text)){
            return null;
        }
        String cssReg = "class *= *['\"]*([^\"']+)[\"']";
        return  commonReg(cssReg, text);
    }

    /**
     * 抽取所有的标签的CSS文本【已去重】
     * */
    public static String extractTagCssText(String text) {
        List<String> result = extractTagCss(text);
        Set<String> allTag = new HashSet<>();
        if(result != null){
            for(String key: result){
                allTag.add(key);
            }
        }
        return allTag.toString();
    }

    /**
     * 抽取所有标签
     * */
    public static List<String> extractAllTag(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        String tagReg = "<([A-Za-z]+)\\s{1}";
        return commonReg(tagReg, text);
    }

    /**
     * 抽取所有标签[已去重]
     * */
    public static String extractAllTagText(String text){
        List<String> result = extractAllTag(text);
        Set<String> allTag = new HashSet<>();
        if(result != null){
            for(String key: result){
                allTag.add(key);
            }
        }
        return allTag.toString();
    }

    /**
     * 去除所有标签
     * */
    public static String trimAllTag(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        String tagReg = "(<[^>]+>)";
        String specialReg = "\\s{4}";
        String blank = "&nbsp";
        String bracket = "-->";
        String scriptReg = "(<script[^>]*>[\\s\\S]*</script>)";
        String styleReg = "(<style[^>]*>[\\s\\S]*</style>)";
        return text.replaceAll(styleReg, "").replaceAll(scriptReg, "")
                .replaceAll(tagReg, "")
                .replaceAll(specialReg, "")
                .replaceAll(blank, "")
                .replaceAll(bracket, "");
    }


    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 检查是否中文乱码
     * */
    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = 0 ;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
                chLength++;
            }
        }
        float result = count / chLength ;
        if (result > 0.4) {
            return true;
        } else {
            return false;
        }
    }

    public static String getStandardTitle(final String title) {
        String tmpTitle = title;
        if(title.contains("|")){
            tmpTitle = title.replaceAll("\\|","\\s");
            if(isMessyCode(tmpTitle)){
                tmpTitle = "-";
            }
        }
        return tmpTitle;
    }

    private static Pattern metaEncodingPattern = Pattern.compile("<[meta|META][^>]*?charset\\s*=\\s*['\"]?([^'\"\\s]+)['\"]?[^>]*?");
    private static Pattern htmlEncodingPattern = Pattern.compile("charset\\s*=\\s*['\"]?([\\w-]*)");
    private static Pattern xmlEncodingPattern = Pattern.compile("encoding\\s*=\\s*['\"]?([\\w-]*)");
    private static String ENCODE_ISO_8859_1 = "ISO-8859-1";
    private static String ENCODE_UTF_8 = "UTF-8";
    private static String ENCODE_BIG_5 = "BIG5"; // 繁体中文

    private static String ENCODE_GBK = "GBK"; // GB13000 (1995 年): 16 位字符集, 收录有 21003 个汉字, 883 个符号, 共 21886 个字符, 不属于官方标准.
    private static String ENCODE_GB_2312 = "GB2312"; // (1980 年): 16 位字符集, 收录有 6763 个简体汉字, 682 个符号, 共 7445 个字符, 不兼容繁体中文.
    private static String ENCODE_GB_18030 = "GB18030"; // 2000 年): 32 位字符集, 收录了 27484 个汉字, 同时收录了藏文、蒙文、维吾尔文等主要的少数民族文字. 属于中国最新的国家标准.

    private static String ENCODE_EUC_KR = "euc-kr";

    /**
     * 针对网页进行解析, 并页面编码自动识别
     *
     * @param content String
     * @return String
     */
    public static String getPageEncoding(String content) {
        content = content.toLowerCase();
        String encoding = "";
        Matcher metaMatcher = metaEncodingPattern.matcher(content);
        if (metaMatcher.find()) {
            encoding = metaMatcher.group(1);
            if (encoding.startsWith("gb") || encoding.startsWith("GB")) {
                if (encoding.equalsIgnoreCase(ENCODE_GB_2312)) {
                    return ENCODE_GB_2312;
                } else if(encoding.equalsIgnoreCase(ENCODE_GB_18030)) {
                    return ENCODE_GB_18030;
                } else {
                    return ENCODE_GBK;
                }
            } else {
                return getEncode(encoding);
            }
        }

        Matcher htmlMatcher = htmlEncodingPattern.matcher(content);
        if (htmlMatcher.find()) {
            encoding = htmlMatcher.group(1);
            return getEncode(encoding);
        }

        // 针对 xml 文件进行解析
        Matcher xmlMatcher = xmlEncodingPattern.matcher(content);
        if (xmlMatcher.find()) {
            encoding = xmlMatcher.group(1);
            return getEncode(encoding);
        }
        return "";
    }

    private static String getEncode(String encoding){
        if (encoding.equalsIgnoreCase(ENCODE_UTF_8)) {
            return ENCODE_UTF_8;
        } else if (encoding.equalsIgnoreCase(ENCODE_BIG_5)) {
            return ENCODE_BIG_5;
        } else if (encoding.equalsIgnoreCase(ENCODE_GB_2312)) {
            return ENCODE_GB_2312;
        } else if(encoding.equalsIgnoreCase(ENCODE_GB_18030)) {
            return ENCODE_GB_18030;
        } else if (encoding.equalsIgnoreCase(ENCODE_GBK)) {
            return ENCODE_GBK;
        } else if (encoding.equalsIgnoreCase(ENCODE_ISO_8859_1)) {
            return ENCODE_ISO_8859_1;
        }else if(encoding.equalsIgnoreCase(ENCODE_EUC_KR)) {
            return ENCODE_EUC_KR;
        }
        return "";
    }

    /**
     * 采用 Mozilla 自动识别编码
     *
     * @param bytes byte[]
     * @return String
     */
    public static String getEncodingByMozilla(byte[] bytes) {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();

        if(StringUtils.isNotBlank(encoding)) {
            return encoding;
        }
        return "";
    }

    /**
     * 页面编码自动识别
     *
     * @param content String
     * @return String
     */
    public static String getPageEncoding(String content, byte[] date) {
        String code = getPageEncoding(content);
        if (code == null || code.equals("")) {
            content = new String(date);
            code = getPageEncoding(content);
        }
        return code;
    }

    /**
     * 获取字节编码
     * */
    public static String getPageEncoding(final byte[] htmlByte) {
        String encode = "";
        // 1. 采用 Mozilla 自动识别编码
        if (StringUtils.isBlank(encode) && htmlByte.length > 0) {
            try {
                encode = getEncodingByMozilla(htmlByte);
            } catch (Exception e) {
            }
        }

        // 2. 从内容中识别编码
        if (StringUtils.isBlank(encode) && htmlByte.length > 0) {
            String encStr = new String(htmlByte, 0, htmlByte.length <= 5000 ? htmlByte.length : 5000);
            encode = getPageEncoding(encStr, htmlByte);
        }

        // 3. 搞不定
        if (encode.trim().equals("")) {
            encode = "UTF-8";
        }

        return encode;
    }



    public static void main(String[] args) {
       /* String tt = "hk3.brasilia.me";
        System.out.println(correctIP(tt));
        if(correctIP(tt)){
            System.out.println(tt);
        }else {
        }*/

//        String url = "http://sqlingquan.xyz";
//        String url = "https://wljs.info/";
        String url = "http://ss.aiz5.com/";
//        String url = "https://www.cnblogs.com/";
//        String html = HttpUtil.getHtml(url);
//        System.out.println(html);
//        List<String> outerLinks = extractInnerLink(url, html);
//        System.out.println(Arrays.deepToString(outerLinks.toArray()));

//        System.out.println(extractScriptSrcText(html));
//        String txt = "�����йܡ����\uD946\uDCB4�����������VPS����,VPN��DDNר�\u07FD��롢������������ȫ���ܵ���IDCƷ�Ʒ���";
//        System.out.println(isMessyCode(txt));
    }
}
