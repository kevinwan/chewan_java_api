//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.gongpingjia.carplay.common.util.DateUtil;
//import com.gongpingjia.carplay.common.util.HttpClientUtil;
//import com.gongpingjia.carplay.controller.BaseTest;
//import com.gongpingjia.carplay.controller.tool.AreaLocationThread;
//import com.gongpingjia.carplay.controller.tool.AreaRangeInfoThread;
//import com.gongpingjia.carplay.controller.tool.SnTool;
//import com.gongpingjia.carplay.dao.common.AreaDao;
//import com.gongpingjia.carplay.dao.common.AreaRangeDao;
//import com.gongpingjia.carplay.dao.test.TestInfoDao;
//import com.gongpingjia.carplay.entity.common.Area;
//import com.gongpingjia.carplay.entity.common.AreaRange;
//import com.gongpingjia.carplay.entity.test.TestInfo;
//import org.apache.http.Header;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.util.EntityUtils;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.core.query.Update;
//
//import java.io.*;
//import java.net.URLEncoder;
//import java.util.*;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * Created by Administrator on 2015/11/11.
// */
//public class AreaInfoTest extends BaseTest {
//
//    @Autowired
//    private AreaDao areaDao;
//
//    @Autowired
//    private AreaRangeDao areaRangeDao;
//
//    @Test
//    public void queryAreaInfo() {
//        int pageNum = 4;
//        List<Area> areas = areaDao.find(null);
//        System.out.println(areas.size());
//        int maxSize = areas.size();
//        int jumSize = areas.size() / pageNum;
//        Map<Integer, Area> areaMap = new HashMap<Integer, Area>(areas.size());
//        for (Area area : areas) {
//            areaMap.put(area.getCode(), area);
//        }
//        ExecutorService pool = Executors.newFixedThreadPool(pageNum);
//        for (int index = 0; index < pageNum; index++) {
//            List<Area> subList = null;
//            if (index == pageNum - 1) {
//                subList = areas.subList(jumSize * index, areas.size());
//            } else {
//                subList = areas.subList(jumSize * index, jumSize * (index + 1));
//            }
//            Runnable thread = new AreaLocationThread(index, subList, areaMap);
//            pool.execute(thread);
//        }
//        try {
//            Thread.sleep(1000);
//            pool.shutdown();
//            while (!pool.isTerminated()) {
//            }
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//    @Test
//    public void testSubList() {
//        int pageNum = 3;
//        List<String> stringList = Arrays.asList("0", "1", "2", "3");
//        int maxSize = stringList.size();
//        int jumSize = stringList.size() / pageNum;
//        for (int index = 0; index < pageNum; index++) {
//            List<String> subList = null;
//            if (index == pageNum - 1) {
//                subList = stringList.subList(jumSize * index, stringList.size());
//            } else {
//                subList = stringList.subList(jumSize * index, jumSize * (index + 1));
//            }
//            System.out.println(subList);
//        }
//
//    }
//
//
//    @Test
//    public void testItem() throws Exception {
//        String address = "仙林街道 ";
//        String cityName = "南京 ";
//        StringBuilder urlBuilder = new StringBuilder();
//        urlBuilder.append("http://api.map.baidu.com/geocoder/v2/?ak=OCmPbY5v697YHilgoCER3CYR&output=json&address=")
//                .append(URLEncoder.encode(address, "UTF-8")).append("&city=").append(URLEncoder.encode(cityName, "UTF-8")).append("&sn=").append(SnTool.getSn(address));
//        Map<String, String> paramMap = new HashMap<String, String>();
//        paramMap.put("city", cityName);
//        paramMap.put("sn", SnTool.getSn(address));
//        paramMap.put("output", "json");
//        paramMap.put("address", address);
//        paramMap.put("ak", "OCmPbY5v697YHilgoCER3CYR");
//        CloseableHttpResponse response = HttpClientUtil.get("http://api.map.baidu.com/geocoder/v2/"
//                , paramMap, new ArrayList<Header>(), "UTF-8");
//
//        String exceptStr = urlBuilder.toString();
//        String entityStr = EntityUtils.toString(response.getEntity());
//        System.out.println(entityStr);
//    }
//
//
//    @Test
//    public void initAreaRangeInfo() throws Exception {
//        try {
//            Map<Integer, AreaRange> areaRangeMap = new HashMap<Integer, AreaRange>();
//            List<LongitudeInfo> longitudeInfoList = new ArrayList<LongitudeInfo>();
//            List<LatitudeInfo> latitudeInfoList = new ArrayList<LatitudeInfo>();
//
//            initDataInfo(areaRangeMap, longitudeInfoList, latitudeInfoList);
//
//            System.out.println("sort starts");
//            Date startTime = new Date();
//            //排序
//            Collections.sort(longitudeInfoList);
//            Collections.sort(latitudeInfoList);
//
//            initAreaRangeInfo(areaRangeMap, longitudeInfoList, latitudeInfoList);
//
//
//            //存贮
//            saveAreaRangeToJson(areaRangeMap);
////            saveAreaRangeInfo(areaRangeMap);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void saveAreaRangeToJson(Map<Integer, AreaRange> areaRangeMap) {
//        System.out.println(areaRangeMap.size());
//        List<AreaRange> areaRangeList = new ArrayList<AreaRange>(areaRangeMap.size());
//        for (Map.Entry<Integer, AreaRange> entry : areaRangeMap.entrySet()) {
//            areaRangeList.add(entry.getValue());
//        }
//        Collections.sort(areaRangeList);
//        FileWriter fileWriter = null;
//        BufferedWriter bufferedWriter = null;
//        try {
//            fileWriter  = new FileWriter("E://areaRange.json");
//            bufferedWriter = new BufferedWriter(fileWriter);
//            for (AreaRange areaRange : areaRangeList) {
//                bufferedWriter.write(JSONObject.toJSONString(areaRange)+"\n");
//            }
//            bufferedWriter.flush();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                if (null != bufferedWriter) {
//                    bufferedWriter.close();
//                }
//                if (null != fileWriter) {
//                    fileWriter.close();
//
//                }
//            } catch (Exception e) {
//            }
//        }
//
//    }
//
//
//    private void saveAreaRangeInfo(Map<Integer, AreaRange> areaRangeMap) throws Exception {
//        System.out.println(areaRangeMap.size());
//        int threadNum = 4;
//        int pageSize = areaRangeMap.size() / threadNum;
//        ExecutorService pool = Executors.newFixedThreadPool(threadNum);
//        Iterator<Map.Entry<Integer, AreaRange>> entryIterator = areaRangeMap.entrySet().iterator();
//        for (int pageIndex = 0; pageIndex < threadNum; pageIndex++) {
//
//            int listSize = 0;
//            if (pageIndex == threadNum - 1) {
//                listSize = areaRangeMap.size() - (threadNum - 1) * pageSize;
//            } else {
//                listSize = pageSize;
//            }
//            List<AreaRange> areaRangeList = new ArrayList<AreaRange>(listSize);
//            for (int index = 0; index < listSize; index++) {
//                Map.Entry<Integer, AreaRange> item = entryIterator.next();
//                AreaRange value = item.getValue();
//                areaRangeList.add(value);
//            }
//            System.out.println(listSize);
//            pool.execute(new AreaRangeInfoThread(areaRangeList, areaRangeDao));
//        }
//        pool.shutdown();
//        Thread.sleep(2000);
//        while (!pool.isTerminated()) {
//        }
//    }
//
//    private void initAreaRangeInfo(Map<Integer, AreaRange> areaRangeMap, List<LongitudeInfo> longitudeInfoList, List<LatitudeInfo> latitudeInfoList) {
//        long startTime = new Date().getTime();
//        for (int index = 0; index < longitudeInfoList.size(); index++) {
//            double preLongitude = 0;
//            double nextLongitude = 0;
//            LongitudeInfo item = longitudeInfoList.get(index);
//            AreaRange areaRange = areaRangeMap.get(item.code);
//            if (index == 0) {
//                preLongitude = item.longitude - 1;
//                nextLongitude = longitudeInfoList.get(index + 1).longitude;
//            } else if (index == longitudeInfoList.size() - 1) {
//                preLongitude = longitudeInfoList.get(index - 1).longitude;
//                nextLongitude = item.longitude + 1;
//            } else {
//                preLongitude = longitudeInfoList.get(index - 1).longitude;
//                nextLongitude = longitudeInfoList.get(index + 1).longitude;
//            }
//
//            areaRange.setMinLongitude((preLongitude + item.longitude) / 2);
//            areaRange.setMaxLongitude((nextLongitude + item.longitude) / 2);
//        }
//
//        System.out.println("initAreaRangeInfo time is :" + (new Date().getTime() - startTime) / 1000.0);
//
//        for (int index = 0; index < latitudeInfoList.size(); index++) {
//            double preLatitude = 0;
//            double nextLatitude = 0;
//            LatitudeInfo item = latitudeInfoList.get(index);
//            AreaRange areaRange = areaRangeMap.get(item.code);
//            if (index == 0) {
//                preLatitude = item.latitude - 1;
//                nextLatitude = latitudeInfoList.get(index + 1).latitude;
//            } else if (index == latitudeInfoList.size() - 1) {
//                preLatitude = latitudeInfoList.get(index - 1).latitude;
//                nextLatitude = item.latitude + 1;
//            } else {
//                preLatitude = latitudeInfoList.get(index - 1).latitude;
//                nextLatitude = latitudeInfoList.get(index + 1).latitude;
//            }
//
//            areaRange.setMinLatitude((preLatitude + item.latitude) / 2);
//            areaRange.setMaxLatitude((nextLatitude + item.latitude) / 2);
//        }
//    }
//
//    private void initDataInfo(Map<Integer, AreaRange> areaRangeMap, List<LongitudeInfo> longitudeInfoList, List<LatitudeInfo> latitudeInfoList) throws IOException {
//        File rootF = new File("D://");
//        String regex = " :";
//        File[] files = rootF.listFiles();
//        FileReader fileReader = null;
//        BufferedReader bufReader = null;
//        for (File file : files) {
//            if (file.getName().endsWith(".txt")) {
//                try {
//                    fileReader = new FileReader(file);
//                    bufReader = new BufferedReader(fileReader);
//                    String itemString = null;
//                    while ((itemString = bufReader.readLine()) != null) {
//                        if (itemString.matches("[0-9].*")) {
//                            String[] strings = itemString.split(regex);
//                            if (strings.length < 4) {
//                                System.out.println(itemString);
//                            } else {
//                                int code = Integer.parseInt(strings[0]);
//                                double longitude = Double.parseDouble(strings[2]);
//                                double latitude = Double.parseDouble(strings[3]);
//                                AreaRange areaRange = new AreaRange();
//
//                                areaRange.setCode(code);
//                                areaRange.setMaxLongitude(longitude);
//                                areaRange.setMaxLongitude(longitude);
//                                areaRange.setMaxLatitude(latitude);
//                                areaRange.setMinLatitude(latitude);
//
//                                areaRangeMap.put(code, areaRange);
//
//                                longitudeInfoList.add(new LongitudeInfo(code, longitude));
//                                latitudeInfoList.add(new LatitudeInfo(code, latitude));
//                            }
//
//                        }
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (null != bufReader) {
//                        bufReader.close();
//                    }
//                    if (null != fileReader) {
//                        fileReader.close();
//                    }
//                }
//
//            }
//        }
//    }
//
//    private class LatitudeInfo implements Comparable<LatitudeInfo> {
//        public int code;
//        public double latitude;
//
//        public LatitudeInfo(int code, double latitude) {
//            this.code = code;
//            this.latitude = latitude;
//        }
//
//        @Override
//        public int compareTo(LatitudeInfo o) {
//            return o.latitude > this.latitude ? 1 : -1;
//        }
//    }
//
//    private class LongitudeInfo implements Comparable<LongitudeInfo> {
//        public int code;
//        public double longitude;
//
//        public LongitudeInfo(int code, double longitude) {
//            this.code = code;
//            this.longitude = longitude;
//        }
//
//        @Override
//        public int compareTo(LongitudeInfo o) {
//            return o.longitude > this.longitude ? 1 : -1;
//        }
//    }
//
//
//}
