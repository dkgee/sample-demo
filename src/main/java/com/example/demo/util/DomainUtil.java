package com.example.demo.util;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述：域名获取工具
 */
public class DomainUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DomainUtil.class);

    private static final String PROTOCOL = "://";

    private static final String W3 = "www.";

    private static final String QUESTION = "?";

    private static final String SLASH = "/";

    private static final String PORT = ":";

    private static final String DOT = "\\.";

    private static final String DOT2 = ".";

    //private static String DOMAIN_CHAXUN_PREFIX = "http://ip.chinaz.com/?IP=";

    // ip地址验证
    public static final Pattern IPPattern = Pattern.compile("[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}");

    public static Map<String, String> provinceMap = new HashMap<String, String>();

    static{
        initProvinceMap();
    }

    public static void initProvinceMap() {
        provinceMap.put("粤", "广东");
        provinceMap.put("苏", "江苏");
        provinceMap.put("京", "北京");
        provinceMap.put("沪", "上海");
        provinceMap.put("浙", "浙江");
        provinceMap.put("鲁", "山东");
        provinceMap.put("川", "四川");
        provinceMap.put("蜀", "四川");
        provinceMap.put("闽", "福建");
        provinceMap.put("豫", "河南");
        provinceMap.put("冀", "河北");
        provinceMap.put("鄂", "湖北");
        provinceMap.put("皖", "安徽");
        provinceMap.put("辽", "辽宁");
        provinceMap.put("秦", "陕西");
        provinceMap.put("陕", "陕西");
        provinceMap.put("湘", "湖南");
        provinceMap.put("渝", "重庆");
        provinceMap.put("赣", "江西");
        provinceMap.put("桂", "广西");
        provinceMap.put("晋", "山西");
        provinceMap.put("云", "云南");
        provinceMap.put("滇", "云南");
        provinceMap.put("津", "天津");
        provinceMap.put("黑", "黑龙江");
        provinceMap.put("吉", "吉林");
        provinceMap.put("黔", "贵州");
        provinceMap.put("贵", "贵州");
        provinceMap.put("蒙", "内蒙古");
        provinceMap.put("琼", "海南");
        provinceMap.put("新", "新疆");
        provinceMap.put("甘", "甘肃");
        provinceMap.put("陇", "甘肃");
        provinceMap.put("宁", "宁夏");
        provinceMap.put("青", "青海");
        provinceMap.put("藏", "西藏");
    }

    public static String getIcpProvince(String icp) {
        String province = "";
        if (StringUtils.isNotBlank(icp)) {
            String shortName = icp.substring(0, 1);
            province = provinceMap.get(shortName);
        }
        return province;
    }



    public static class DomainPostfixs {

        // 通用顶级域名
        public static final Pattern POSTFIXS = Pattern.compile("^(com)+|(org)+|(net)+|(edu)+|(mil)+|(int)+|(pro)+|(idv)+"
                + "|(biz)+|(museum)+|(coop)+|(aero)+|(info)+|(name)+|(cc)+|(tv)+|(gov)+"
                + "|(travel)+|(guide)+|(blog)+|(community)+|(asia)+|(jobs)+|(mobi)+|(tel)+|(cat)+$");

        // 国家地理域名
        public static final Pattern LOCATION_POSTFIXS = Pattern
                .compile("^(ac)+|(ad)+|(ae)+|(af)+|(ag)+|(ai)+|(al)+|(am)+|(an)+|(ao)+|(aq)+|(ar)+|(as)+|(at)+|(au)"
                        + "+|(aw)+|(ax)+|(az)+|(ba)+|(bb)+|(bd)+|(be)+|(bf)+|(bg)+|(bh)+|(bi)+|(bj)+|(bm)+|(bn)+|(bo)"
                        + "+|(br)+|(bs)+|(bt)+|(bv)+|(bw)+|(by)+|(bz)+|(ca)+|(cc)+|(cd)+|(cf)+|(cg)+|(ch)+|(ci)+|(ck)"
                        + "+|(cl)+|(cm)+|(cn)+|(co)+|(cr)+|(cu)+|(cv)+|(cx)+|(cy)+|(cz)+|(de)+|(dj)+|(dk)+|(dm)+|(do)"
                        + "+|(dz)+|(ec)+|(ee)+|(eg)+|(eh)+|(er)+|(es)+|(et)+|(eu)+|(fi)+|(fj)+|(fk)+|(fm)+|(fo)+|(fr)+|(ga)"
                        + "+|(gb)+|(gd)+|(ge)+|(gf)+|(gg)+|(gh)+|(gi)+|(gl)+|(gm)+|(gn)+|(gp)+|(gq)+|(gr)+|(gs)+|(gt)"
                        + "+|(gu)+|(gw)+|(gy)+|(hk)+|(hm)+|(hn)+|(hr)+|(ht)+|(hu)+|(id)+|(ie)+|(il)+|(im)+|(in)+|(io)"
                        + "+|(iq)+|(ir)+|(is)+|(it)+|(je)+|(jm)+|(jo)+|(jp)+|(ke)+|(kg)+|(kh)+|(ki)+|(km)+|(kn)+|(kp)"
                        + "+|(kr)+|(kw)+|(ky)+|(kz)+|(la)+|(lb)+|(lc)+|(li)+|(lk)+|(lr)+|(ls)+|(lt)+|(lu)+|(lv)+|(ly)"
                        + "+|(ma)+|(mc)+|(md)+|(me)+|(mg)+|(mh)+|(mk)+|(ml)+|(mm)+|(mn)+|(mo)+|(mp)+|(mq)+|(mr)+|(ms)"
                        + "+|(mt)+|(mu)+|(mv)+|(mw)+|(mx)+|(my)+|(mz)+|(na)+|(nc)+|(ne)+|(nf)+|(ng)+|(ni)+|(nl)+|(no)"
                        + "+|(np)+|(nr)+|(nu)+|(nz)+|(om)+|(pa)+|(pe)+|(pf)+|(pg)+|(ph)+|(pk)+|(pl)+|(pm)+|(pn)+|(pr)"
                        + "+|(ps)+|(pt)+|(pw)+|(py)+|(qa)+|(re)+|(ro)+|(rs)+|(ru)+|(rw)+|(sa)+|(sb)+|(sc)+|(sd)+|(se)"
                        + "+|(sg)+|(sh)+|(si)+|(sj)+|(sk)+|(sl)+|(sm)+|(sn)+|(so)+|(sr)+|(st)+|(su)+|(sv)+|(sy)+|(sz)"
                        + "+|(tc)+|(td)+|(tf)+|(tg)+|(th)+|(tj)+|(tk)+|(tl)+|(tm)+|(tn)+|(to)+|(tp)+|(tr)+|(tt)+|(tv)"
                        + "+|(tw)+|(tz)+|(ua)+|(ug)+|(uk)+|(um)+|(us)+|(uy)+|(uz)+|(va)+|(vc)+|(ve)+|(vg)+|(vi)+|(vn)"
                        + "+|(vu)+|(wf)+|(ws)+|(ye)+|(yt)+|(yu)+|(za)+|(zm)+|(zw)+|(jd)+|(zj)+|(cq)+|(yn)+|(xz)+|(sx)"
                        + "+|(nx)+|(nm)+|(ln)+|(jx)+|(js)+|(jl)+|(hb)+|(hl)+|(hb)+|(gz)+|(gx)+|(zj)+$");

        /**
         * 检测该域名是否属于通用顶级域名列表
         *
         * @param domainSegment
         * @return
         */
        public static boolean findPostfixs(String domainSegment) {
            return POSTFIXS.matcher(domainSegment).matches();
        }

        /**
         * 检测该域名是否属于国家地理域名
         *
         * @param domainSegment
         * @return
         */
        public static boolean isLocaionPostfixs(String domainSegment) {
            return LOCATION_POSTFIXS.matcher(domainSegment).matches();
        }
    }

    /**
     * 把所有传过来的链接转换为小写, 然后去掉 url 中的协议，端口号，子链
     *
     * @param url
     * @return
     */
    public static String removeUrlProtocol(String url) {
        StringBuilder sb = new StringBuilder(url.toLowerCase());

        if (sb.indexOf(PROTOCOL) >= 0) {
            sb.delete(0, sb.indexOf(PROTOCOL) + PROTOCOL.length());
        }

        if (sb.indexOf(QUESTION) >= 0) {
            sb.delete(sb.indexOf(QUESTION), sb.length());
        }

        if (sb.indexOf(SLASH) >= 0) {
            sb.delete(sb.indexOf(SLASH), sb.length());
        }
        return sb.toString();
    }

    public static String getUrlProtocol(String url) {
        String protocol = "";
        StringBuilder sb = new StringBuilder(url.toLowerCase());
        if (sb.indexOf(PROTOCOL) >= 0) {
            protocol = sb.substring(0, sb.indexOf(PROTOCOL));
        }
        return protocol;
    }

    /**
     * 把所有传过来的链接转换为小写, 然后去掉url中的协议，端口号，子链 子域名获取
     *
     * @param url
     * @return
     */
    public static String removeUrlProtocolAndPort(String url) {
        StringBuilder sb = new StringBuilder(url.toLowerCase());

        if (sb.indexOf(PROTOCOL) >= 0) {
            sb.delete(0, sb.indexOf(PROTOCOL) + PROTOCOL.length());
        }

        if (sb.indexOf(W3) >= 0) {
            sb.delete(0, sb.indexOf(W3) + W3.length());
        }

        if (sb.indexOf(QUESTION) >= 0) {
            sb.delete(sb.indexOf(QUESTION), sb.length());
        }

        if (sb.indexOf(PORT) >= 0) {
            sb.delete(sb.indexOf(PORT), sb.length());
        }

        if (sb.indexOf(SLASH) >= 0) {
            sb.delete(sb.indexOf(SLASH), sb.length());
        }
        return sb.toString();
    }

    /**
     * 把所有传过来的链接转换为小写, 然后去掉端口号，子链 子域名获取
     *
     * @param url
     * @return
     */
    public static String removeUrlPort(String url) {
        StringBuilder sb = new StringBuilder(url.toLowerCase());
        if (sb.indexOf(QUESTION) >= 0) {
            sb.delete(sb.indexOf(QUESTION), sb.length());
        }

        if (sb.indexOf(PORT) >= 0) {
            sb.delete(sb.indexOf(PORT), sb.length());
        }

        if (sb.indexOf(SLASH) >= 0) {
            sb.delete(sb.indexOf(SLASH), sb.length());
        }
        return sb.toString();
    }

    /**
     * 获取网站地址
     *
     * @param url
     *            非顶级域名链接
     * @return
     */
    public static String getSite(String url) {
        if (StringUtils.isNotBlank(url)) {
            if (isIp(url)) {
                return removeUrlProtocol(url);
            }
            return removeUrlProtocolAndPort(url);
        } else {
            return null;
        }
    }

    /**
     * 顶级域名获取, 移除协议头，端口，等只取出网站地址, 划分. 为数组,
     * 如果是ip地址并且没有端口号, 如果是 ip 地址并且有端口号，则返回ip: 端口
     *
     * @param url
     * @return
     */
    public static String getTopDomain(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }

        String tmp1 = removeUrlProtocolAndPort(url);
        if (isIp(tmp1)) {
            String tmp2 = removeUrlProtocol(url);
            if (tmp1.equals(tmp2)) {
                return tmp1;
            } else {
                return tmp2;
            }
        }

        String domain_tmp = tmp1;
        removeUrlProtocolAndPort(url);
        if (isIp(domain_tmp)) {
            return domain_tmp;
        }
        String[] domain_segments = domain_tmp.split(DOT);
        String domainReturn = null;

        if (domain_segments.length <= 1) {
            return null;
        }
        int lastIndex = domain_segments.length - 1;

        if (domain_segments.length >= 2) {
            // 通用顶级域名结尾的域名 .com
            if (DomainPostfixs.findPostfixs(domain_segments[lastIndex])) {
                domainReturn = domain_segments[lastIndex - 1] + DOT2 + domain_segments[lastIndex];
                return domainReturn;
            }

            // 国家地理域名结尾的处理 .cn
            if (DomainPostfixs.isLocaionPostfixs(domain_segments[lastIndex])) {
                // 如果下一级域名属于通用顶级域名 sina.com.cn
                if (DomainPostfixs.findPostfixs(domain_segments[lastIndex - 1])) {
                    if (domain_segments.length >= 3) {
                        if (domain_segments.length >= 4 && DomainPostfixs.isLocaionPostfixs(domain_segments[lastIndex - 2])) {
                            domainReturn = domain_segments[lastIndex - 3] + DOT2 + domain_segments[lastIndex - 2] + DOT2 + domain_segments[lastIndex - 1]
                                    + DOT2 + domain_segments[lastIndex];
                        } else {
                            domainReturn = domain_segments[lastIndex - 2] + DOT2 + domain_segments[lastIndex - 1] + DOT2 + domain_segments[lastIndex];
                        }
                    } else {
                        domainReturn = domain_segments[lastIndex - 1] + DOT2 + domain_segments[lastIndex];
                    }

                    return domainReturn;
                }
                // sina.cn
                domainReturn = domain_segments[lastIndex - 1] + DOT2 + domain_segments[lastIndex];
                return domainReturn;
            }
            return domainReturn;
        }
        return null;
    }

    public static boolean isSite(String url) {
        if (url == null || "".equals(url)) {
            return false;
        }

        if (url.endsWith("/")) {
            return false;
        }

        StringBuffer sb = new StringBuffer(url);
        boolean flag = true;
        if (StringUtils.isNotBlank(getTopDomain(url))) {
            if (sb.indexOf(PROTOCOL) >= 0) {
                sb.delete(0, sb.indexOf(PROTOCOL) + PROTOCOL.length());
            }
            if (url.endsWith(SLASH)) {
                sb.delete(sb.lastIndexOf(SLASH), sb.lastIndexOf(SLASH) + 1);
            }
            if (!sb.toString().trim().equalsIgnoreCase(getSite(url))) {
                if (sb.indexOf("/index") >= 0) {
                    sb.delete(sb.indexOf("/index"), sb.length());
                }
                if (sb.indexOf("/default") >= 0) {
                    sb.delete(sb.indexOf("/default"), sb.length());
                }
                if (sb.indexOf("/home") >= 0) {
                    sb.delete(sb.indexOf("/home"), sb.length());
                }
                flag = sb.toString().trim().equalsIgnoreCase(getSite(url));
            }
        } else {
            flag = StringUtils.countMatches(url, "/") <= 1
                    && (url.indexOf("index") >= 0 || url.indexOf("default") >= 0 || url.indexOf("home") >= 0);
        }
        return flag;
    }

    /**
     * 判断是否是ip地址
     *
     * @param url
     * @return
     */
    public static boolean isIp(String url) {
        return IPPattern.matcher(url).matches();
    }

    /**
     * 获取ip地址
     * @param url
     * @return
     */
    public static String getIp(String url) {
        try {
            return InetAddress.getByName(getSite(url)).getHostAddress();
        } catch (Exception ignore) {
            return null;
        }
    }

    public static String getPageTitle(String html){
        String result = "";
        try{
            result = Jsoup.parse(html).title();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * //豫ICP备13020827号  京ICP证030173号 京ICP备11024134号-1 沪B2-20120099 粤B2-20030128号-10 辽B-2-4-20100129 皖B1.B2-20100007 合字B2-20090003
     * @param html 苏ICP备 05001937
     * @return
     */
    public static String getPageICP(String html){
        html = html.replaceAll(" ", "");
        String regex = "([\\S]{1}ICP[备|证][0-9]{6,8}[号]?(-[0-9]{1,2})?)|([合字]*?[\\S]{1}[A-Z]{1}[[0-9]{1}]?(-[\\S]{1,2})?(-[\\S]{1,2})?(\\.[\\S]{1,2})?-[0-9]{8}[号]?(-[0-9]{1,3})?)";
        String icp = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String s = html.substring(start, end);
            if(StringUtils.isNotBlank(s)){
                if(StringUtils.isNotBlank(getIcpProvince(s))){
                    icp += s+";";
                }
            }
        }
        return icp;
    }

    public boolean isIP(String addr)
    {
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
        {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(addr);

        boolean ipAddress = mat.find();

        return ipAddress;
    }

    public static boolean ipExistsInRange(String ip,String ipSection) {
        ipSection = ipSection.trim();
        ip = ip.trim();
        int idx = ipSection.indexOf('-');
        String beginIP = ipSection.substring(0, idx);
        String endIP = ipSection.substring(idx + 1);
        return getIp2long(beginIP)<=getIp2long(ip) &&getIp2long(ip)<=getIp2long(endIP);
    }

    public static long getIp2long(String ip) {
        ip = ip.trim();
        String[] ips = ip.split("\\.");
        long ip2long = 0L;
        for (int i = 0; i < 4; ++i) {
            ip2long = ip2long << 8 | Integer.parseInt(ips[i]);
        }
        return ip2long;
    }

    public static long getIp2long2(String ip) {
        ip = ip.trim();
        String[] ips = ip.split("\\.");
        long ip1 = Integer.parseInt(ips[0]);
        long ip2 = Integer.parseInt(ips[1]);
        long ip3 = Integer.parseInt(ips[2]);
        long ip4 = Integer.parseInt(ips[3]);
        long ip2long =1L* ip1 * 256 * 256 * 256 + ip2 * 256 * 256 + ip3 * 256 + ip4;
        return ip2long;
    }

    public static boolean ipIsValid(String ipSection, String ip) {
        if (ipSection == null)
            throw new NullPointerException("IP段不能为空！");
        if (ip == null)
            throw new NullPointerException("IP不能为空！");
        ipSection = ipSection.trim();
        ip = ip.trim();
        final String REGX_IP = "((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
        final String REGX_IPB = REGX_IP + "\\-" + REGX_IP;
        if (!ipSection.matches(REGX_IPB) || !ip.matches(REGX_IP))
            return false;
        int idx = ipSection.indexOf('-');
        String[] sips = ipSection.substring(0, idx).split("\\.");
        String[] sipe = ipSection.substring(idx + 1).split("\\.");
        String[] sipt = ip.split("\\.");
        long ips = 0L, ipe = 0L, ipt = 0L;
        for (int i = 0; i < 4; ++i) {
            ips = ips << 8 | Integer.parseInt(sips[i]);
            ipe = ipe << 8 | Integer.parseInt(sipe[i]);
            ipt = ipt << 8 | Integer.parseInt(sipt[i]);
        }
        if (ips > ipe) {
            long t = ips;
            ips = ipe;
            ipe = t;
        }
        return ips <= ipt && ipt <= ipe;
    }


    public static void main(String[] args) {
        String url = "http://20p6q2.hjyt.huayi.com.my/n-18";
        System.out.println(getSite(url));
    }
}
