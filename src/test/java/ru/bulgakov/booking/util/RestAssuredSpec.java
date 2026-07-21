package ru.bulgakov.booking.util;

import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.with;
import static ru.bulgakov.booking.util.CustomAllureListener.withCustomTemplates;

public class RestAssuredSpec {
    public static RequestSpecification requestSpec = with()
            .filter(withCustomTemplates())
            .log().uri()
            .log().headers()
            .log().body();

    public static ResponseSpecification responseSpec = new ResponseSpecBuilder()
            .log(LogDetail.BODY)
            .build();

    public static void setupRestAssured() {
        RestAssured.requestSpecification = requestSpec;
        RestAssured.responseSpecification = responseSpec;
    }
}
