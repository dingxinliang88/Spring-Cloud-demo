package cn.itcast.hotel.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class HotelDoc {
    private Long id;
    private String name;
    private String address;
    private Integer price;
    private Integer score;
    private String brand;
    private String city;
    private String starName;
    private String business;
    private String location;
    private String pic;
    /**
     * 排序时的距离
     */
    private Object distance;

    /**
     * 广告标志
     */
    private Boolean isAD;

    /**
     * 搜索框自动补全字段
     */
    private List<String> suggestion;

    public HotelDoc(Hotel hotel) {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.address = hotel.getAddress();
        this.price = hotel.getPrice();
        this.score = hotel.getScore();
        this.brand = hotel.getBrand();
        this.city = hotel.getCity();
        this.starName = hotel.getStarName();
        this.business = hotel.getBusiness();
        this.location = hotel.getLatitude() + ", " + hotel.getLongitude();
        this.pic = hotel.getPic();
        // 组装suggestion
        if(this.business.contains("、")) {
            // business有多个值，需要切割
            String[] splitBusiness = this.business.split("、");
            this.suggestion = new ArrayList<>(1 + splitBusiness.length);
            this.suggestion.add(this.brand);
            Collections.addAll(this.suggestion, splitBusiness);
        } else {
            this.suggestion = Arrays.asList(this.brand, this.business);
        }
    }
}
