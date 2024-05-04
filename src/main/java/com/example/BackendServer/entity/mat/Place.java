package com.example.BackendServer.entity.mat;

import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.common.response.BaseResponseStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum Place {
    TTUKSEOM_HAN_RIVER1("뚝섬 한강1", 37.5293507, 127.0699562),
    TTUKSEOM_HAN_RIVER2("뚝섬 한강2", 37.5300000, 127.0690000),
    TTUKSEOM_HAN_RIVER3("뚝섬 한강3", 37.5290000, 127.0720000);


    private final String location;
    private final double lantitude;
    private final double logitude;

    public static Place getLocation(double lantitude, double logitude){

        return Arrays
                .stream(Place.values()).filter(place -> place.getLogitude() == logitude && place.getLantitude() == lantitude)
                .collect(Collectors.toList()).stream().findFirst().orElseThrow(
                        () -> new BaseException(BaseResponseStatus.NOT_EXIST_PLACE)
                );
    }
}
