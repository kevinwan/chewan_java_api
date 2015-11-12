package com.gongpingjia.carplay.controller.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gongpingjia.carplay.common.util.HttpClientUtil;
import com.gongpingjia.carplay.entity.common.Area;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/11.
 */
public class AreaLocationThread implements Runnable {

    private static final String FILE_STORE_DIR = "D:\\location_";

    private static final String MAP_URL_PREFIX = "http://api.map.baidu.com/geocoder/v2/";

    private static final String AK_CODE = "OCmPbY5v697YHilgoCER3CYR";


    private static int CITY_DIVISION = 100000;

    private static int PROVINCE_DIVISION = 10000000;


    private int index;

    private List<Area> areas;

    private Map<Integer, Area> areaMap;

    public AreaLocationThread(int index, List<Area> areas, Map<Integer, Area> areaMap) {
        this.index = index;
        this.areas = areas;
        this.areaMap = areaMap;
    }

    @Override
    public void run() {
        initAreaInfo();
    }


    private void initAreaInfo() {
        OutputStream outputStream = null;
        try {
            StringBuilder fileBuilder = new StringBuilder();
            fileBuilder.append(FILE_STORE_DIR).append(index).append(".txt");
            File file = new File(fileBuilder.toString());
            outputStream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);

            for (Area area : areas) {
                System.out.println(area.getName());

                if (area.getLevel() == 4) {
                    Area cityArea = getCityArea(area, areaMap);
                    if (null == cityArea) {
                        writer.write("NULL: levelProvince is NULL:" + area.getName() + "\n");
                        continue;
                    }

                    Map<String, String> paramMap = new HashMap<String, String>();
                    paramMap.put("city", cityArea.getName());
                    paramMap.put("sn", SnTool.getSn(area.getName()));
                    paramMap.put("output", "json");
                    paramMap.put("address", area.getName());
                    paramMap.put("ak", AK_CODE);
                    CloseableHttpResponse response = HttpClientUtil.get(MAP_URL_PREFIX, paramMap, new ArrayList<Header>(), "UTF-8");
                    JSONObject json = JSON.parseObject(EntityUtils.toString(response.getEntity()));

                    if (json == null || !json.containsKey("status")) {
                        writer.write("NULL:empty response" + area.getName() + ":" + "\n");
                        continue;
                    } else if (json.getInteger("status") == 0) {
                        writeLocationStr(writer, area, json);
                    } else {
                        //未能读取出 location 信息；
                        writer.write("NULL: status is not 0" + area.getName() + ":" + "\n");
                    }
                    HttpClientUtil.close(response);
                }
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeOutputStream(outputStream);
        }

    }


    //获取街道对应的城市信息；如果是直辖市城市为空，取出省一级信息；
    private Area getCityArea(Area area, Map<Integer, Area> areaMap) {
        return areaMap.get(area.getCode() / CITY_DIVISION) != null ? areaMap.get(area.getCode()) : (areaMap.get(area.getCode() / PROVINCE_DIVISION) != null ? areaMap.get(area.getCode() / PROVINCE_DIVISION) : null);
    }

     //将街道经纬度信息写入到本地文件
    private void writeLocationStr(OutputStreamWriter writer, Area area, JSONObject json) throws IOException {
        JSONObject resultObj = (JSONObject) json.get("result");
        JSONObject location = (JSONObject) resultObj.get("location");
        String longitude = location.getString("lng");
        String latitude = location.getString("lat");
        StringBuilder resultBuild = new StringBuilder();
        resultBuild.append(area.getCode()).append(" :").append(area.getName()).append(" :").append(longitude).append(" :").append(latitude).append("\n");
        writer.write(resultBuild.toString());
    }

    private void closeOutputStream(OutputStream outputStream) {
        if (null != outputStream) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
