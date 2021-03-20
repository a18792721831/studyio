package com.studyio.hellosocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jiayq
 * @Date 2021-03-11
 */
public class Main3 {

    private static final String fileName = "./HelloSocket/省市县三级联动.json";

    public static void main(String[] args) {
        // 使用jdk 7 的自动关闭资源的写法，将数据从硬盘中读取进来
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // 通过一行一行的读取，获取全部的文件
            String message = reader.lines().reduce("", (v1, v2) -> v1 + v2).trim();
            Map<Integer, List<CityInfo>> listMap = arrayParsing(message).stream().map(x -> fillName("", x, 1)).flatMap(x -> flat(x).stream()).
                    collect(Collectors.groupingBy(x -> x.getLevel()));
            // 输出格式调整
            System.out.println(listMap.get(1).stream().map(x -> "“" + x.getName() + "“").
                    reduce(new StringJoiner("，", "{", "}"), StringJoiner::add, StringJoiner::merge));
            System.out.println(listMap.get(1).stream().map(x -> "“" + x.getName() + "“").
                    reduce(new StringJoiner("，"), StringJoiner::add, StringJoiner::merge));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 通过回调，将树形结构扁平化
     *
     * @param cityInfo
     * @return
     */
    private static List<CityInfo> flat(CityInfo cityInfo) {
        List<CityInfo> result = new ArrayList<>();
        result.add(cityInfo);
        if (Optional.ofNullable(cityInfo.getChildren()).isPresent()) {
            result.addAll(cityInfo.getChildren().stream().flatMap(x -> flat(x).stream()).collect(Collectors.toList()));
        }
        return result;
    }

    /**
     * 通过回调，将名字扁平化，并设置层数
     *
     * @param parentName
     * @param cityInfo
     * @param level
     */
    private static CityInfo fillName(String parentName, CityInfo cityInfo, int level) {
        if (parentName.isEmpty()) {
            cityInfo.setName(parentName + cityInfo.getName());
        } else {
            cityInfo.setName(parentName + "-" + cityInfo.getName());
        }
        cityInfo.setLevel(level);
        if (Optional.ofNullable(cityInfo.getChildren()).isPresent()) {
            cityInfo.getChildren().stream().forEach(x -> fillName(cityInfo.getName(), x, cityInfo.getLevel() + 1));
        }
        return cityInfo;
    }

    /**
     * 解析对象
     *
     * @param jsonObject
     * @return
     */
    private static CityInfo objectParsing(JSONObject jsonObject) {
        CityInfo cityInfo = new CityInfo();
        if (jsonObject.containsKey("code")) {
            cityInfo.setCode(jsonObject.getString("code"));
        }
        if (jsonObject.containsKey("name")) {
            cityInfo.setName(jsonObject.getString("name"));
        }
        if (jsonObject.containsKey("children")) {
            cityInfo.setChildren(arrayParsing(jsonObject.getString("children")));
        }
        return cityInfo;
    }

    /**
     * 解析数组
     *
     * @param str
     * @return
     */
    private static List<CityInfo> arrayParsing(String str) {
        // 使用fastjson进行解析字符串
        JSONArray jsonArray = JSON.parseArray(str);
        return jsonArray.stream().map(x -> (JSONObject) x).
                map(x -> objectParsing(x)).
                collect(Collectors.toList());
    }

}
