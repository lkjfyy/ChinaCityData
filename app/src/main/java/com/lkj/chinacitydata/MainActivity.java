package com.lkj.chinacitydata;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lkj.chinacitydata.bean.Area;
import com.lkj.chinacitydata.bean.City;
import com.lkj.chinacitydata.bean.Province;
import com.lkj.chinacitydata.runtimepermissions.PermissionsManager;
import com.lkj.chinacitydata.runtimepermissions.PermissionsUtils;
import com.lkj.chinacitydata.utils.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button createJson;
    private TextView showJson;
    List<List<String>> cityDataList;
    List<Province> provinceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityDataList = new ArrayList<>();
        provinceList = new ArrayList<>();
        createJson = findViewById(R.id.bt_create_json);
        showJson = findViewById(R.id.tv_show_json);
        PermissionsUtils.requestPermissions(this);//申请权限
        createJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showJson.setText(formatCityData());
            }
        });
    }

    /**
     * 格式化从assets中取出的字符串集合
     *
     * @return json字符串
     */
    @SuppressLint("LongLogTag")
    private String formatCityData() {
        List<String> strings = FileUtils.readFile(MainActivity.this, "province.txt");
        Log.e("字符串行数", strings.size() + "");
        for (int i = 0; i < strings.size(); i++) {
            //每一行根据空格分割，便于取出有用的值
            List<String> list = Arrays.asList(strings.get(i).split(" "));
            cityDataList.add(list);
        }

        for (int i = 0; i < cityDataList.size(); i++) {

            String provinceName = cityDataList.get(i).get(1);
            String provinceCode = cityDataList.get(i).get(0);
            //遍历获取省级单位
            if (provinceCode.endsWith("0000")) {
                Province province = new Province();
                provinceList.add(province);
                province.setCode(provinceCode);
                province.setName(provinceName);
                List<City> cities = new ArrayList<>();
                province.setCityList(cities);
                //香港，澳门，只有两级（如果想设成三级，只需自己增加一级即可）
                if (provinceName.contains("香港") || provinceName.contains("澳门")) {
                    for (int k = 0; k < cityDataList.size(); k++) {
                        String cityName = cityDataList.get(k).get(1);
                        String cityCode = cityDataList.get(k).get(0);
                        if (!provinceCode.equals(cityCode) && cityCode.startsWith(provinceCode.substring(0, 2))) {
                            City city = new City();
                            city.setName(cityName);
                            city.setCode(cityCode);
                            cities.add(city);
                        }
                    }
                }
                //直辖市 城市和省份名称一样
                if (provinceName.contains("北京") || provinceName.contains("上海") ||
                        provinceName.contains("天津") || provinceName.contains("重庆")) {
                    City city = new City();
                    List<Area> areas = new ArrayList<>();
                    city.setName(provinceName + "市");
                    city.setCode(provinceCode);
                    city.setAreaList(areas);
                    cities.add(city);
                    //县区
                    for (int k = 0; k < cityDataList.size(); k++) {
                        String areaName = cityDataList.get(k).get(1);
                        String areaCode = cityDataList.get(k).get(0);
                        if (!provinceCode.equals(areaCode) && areaCode.startsWith(provinceCode.substring(0, 2))) {
                            Area area = new Area();
                            area.setName(areaName);
                            area.setCode(areaCode);
                            areas.add(area);
                        }
                    }
                }
                for (int j = 0; j < cityDataList.size(); j++) {
                    String cityName = cityDataList.get(j).get(1).trim();
                    String cityCode = cityDataList.get(j).get(0).trim();
                    //遍历获取地级市
                    if (!cityCode.equals(provinceCode) && cityCode.startsWith(provinceCode.substring(0, 2)) && cityCode.endsWith("00")) {
                        City city = new City();
                        List<Area> areas = new ArrayList<>();
                        city.setName(cityName);
                        city.setCode(cityCode);
                        city.setAreaList(areas);
                        cities.add(city);
                        //遍历获取县区
                        for (int k = 0; k < cityDataList.size(); k++) {
                            String areaName = cityDataList.get(k).get(1);
                            String areaCode = cityDataList.get(k).get(0);
                            if (!areaCode.equals(cityCode) && areaCode.startsWith(cityCode.substring(0, 4))) {
                                Area area = new Area();
                                area.setName(areaName);
                                area.setCode(areaCode);
                                areas.add(area);
                            }
                        }
                    }
                }
            }
        }
        //转化成JSON数据
        String jsonStrings = new Gson().toJson(provinceList);
        //写入文件
        boolean b = FileUtils.createJsonFile(jsonStrings, "/province.json");
        Toast.makeText(MainActivity.this, b ? "创建文件成功" : "创建文件失败", Toast.LENGTH_SHORT).show();
        return jsonStrings;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
