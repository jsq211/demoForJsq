package com.jsq.demo.mapstruct;

import org.checkerframework.checker.units.qual.C;

/**
 * @author jsq
 * date: 2021/3/15 10:08
 **/
public class CarTest {
    public static void main(String[] args) {
        Car car = new Car();
        car.setMake("test");
        car.setNumberOfSeats("testSet");
        CarDto carDto = CarMapper.INSTANCE.carToCarDto(car);
        System.out.println(carDto.toString());
    }
}
