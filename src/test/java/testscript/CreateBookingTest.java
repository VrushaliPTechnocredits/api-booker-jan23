package testscript;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import constants.Status_Code;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import pojo.request.createbooking.BookingDates;
import pojo.request.createbooking.CreateBookingRequest;

//given - all input details[URI, headers, path/query parameters, payload]
//when - submit api [headerType, endpoint]
//then - validate the response

public class CreateBookingTest {
	String token;
	int bookingId;
	CreateBookingRequest payload;

	@BeforeMethod
	public void generateToken() {
		RestAssured.baseURI = "https://restful-booker.herokuapp.com";

		Response res = RestAssured.given().log().all().headers("Content-Type", "application/json")
				.body("{\r\n" + "    \"username\" : \"admin\",\r\n" + "    \"password\" : \"password123\"\r\n" + "}")
				.when().post("/auth");
		// System.out.println(res.statusCode());
		Assert.assertEquals(res.statusCode(), 200);
		// System.out.println(res.asPrettyString());
		token = res.jsonPath().getString("token");
		System.out.println(token);

	}

	@Test(enabled = false)
	public void createBookingTest() {
		Response res = RestAssured.given().header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.body("{\r\n" + "    \"firstname\" : \"Vrushali\",\r\n" + "    \"lastname\" : \"Kanani\",\r\n"
						+ "    \"totalprice\" : 111,\r\n" + "    \"depositpaid\" : true,\r\n"
						+ "    \"bookingdates\" : {\r\n" + "        \"checkin\" : \"2023-02-02\",\r\n"
						+ "        \"checkout\" : \"2019-20-02\"\r\n" + "    },\r\n"
						+ "    \"additionalneeds\" : \"Breakfast\"\r\n" + "}")
				.when().post("/booking");

		System.out.println(res.statusCode());
		System.out.println(res.statusLine());
		Assert.assertEquals(res.getStatusCode(), Status_Code.OK);
		validateResponse(res, payload, "");

	}

	@Test
	public void createBookingTestWithPojo() {
		// RestAssured.baseURI =
		// "https://48c65d2a-5322-475d-ad64-d4f3093e6860.mock.pstmn.io";
		BookingDates bookingdates = new BookingDates();
		bookingdates.setCheckin("2023-05-02");
		bookingdates.setCheckout("2023-06-02");

		payload = new CreateBookingRequest();
		payload.setFirstname("Vrushali");
		payload.setLastname("Ladole");
		payload.setTotalprice(123);
		payload.setDepositpaid(true);
		payload.setAdditionalneeds("Breakfast");
		payload.setBookingdates(bookingdates);

		Response res = RestAssured.given().header("Content-Type", "application/json")
				.header("Accept", "application/json").body(payload).log().all().when().post("/booking");

		Assert.assertEquals(res.getStatusCode(), Status_Code.OK);
		// Assert.assertTrue(Integer.valueOf(res.jsonPath().getInt("bookingid"))instanceof
		// Integer);

		bookingId = res.jsonPath().getInt("bookingid");
		System.out.println(bookingId);
		Assert.assertTrue(bookingId > 0);
		validateResponse(res, payload, "booking.");
		
	}

	@Test (priority = 1, enabled = false)
	public void getAllBookingsTest() {
		
		Response res = RestAssured.given()
				.headers	("Accept", "application/json")
				.log().all()
				.when()
				.get("/booking");
		
		Assert.assertEquals(res.getStatusCode(), Status_Code.OK);
		System.out.println(res.asPrettyString());
		List<Integer> listOfBookingIds = res.jsonPath().getList("bookingid");
		System.out.println(listOfBookingIds.size());
		Assert.assertTrue(listOfBookingIds.contains(bookingId));

	}
	
	@Test (priority = 2, enabled = false)
	public void geBookingIdTest() {
		Response res = RestAssured.given()
				.headers	("Accept", "application/json")
				.when()
				.get("/booking/" + bookingId);
		
		Assert.assertEquals(res.getStatusCode(), Status_Code.OK);
		System.out.println(res.asPrettyString());
		validateResponse(res, payload, "");
		

	}
	
	@Test (priority = 2, enabled = false)
	public void geBookingIdDeserializedTest() {
		//int bookingId = 4507;
		Response res = RestAssured.given()
				.headers	("Accept", "application/json")
				.log().all()
				.when()
				.get("/booking/" + bookingId);
		
		Assert.assertEquals(res.getStatusCode(), Status_Code.OK);
		System.out.println(res.asPrettyString());
		
		CreateBookingRequest responseBody = res.as(CreateBookingRequest.class);
		//payload - all details of request
		//responseBody - all details from getBooking
		/*Assert.assertEquals(payload.firstname, responseBody.firstname);
		Assert.assertEquals(payload.lastname, responseBody.lastname);*/
		
		Assert.assertTrue(responseBody.equals(payload));
		

	}
	
	@Test (priority = 3)
	public void updateBookingIdTest() {
		//int bookingId = 4507;
		payload.setFirstname("Sagar");
		Response res = RestAssured.given()
				.header("Content-Type", "application/json")
				.headers("Accept", "application/json")
				.headers("Cookie", "token"+token)
				.log().all()
				.body(payload)
				.when()
				.put("/booking/" + bookingId);
		
		Assert.assertEquals(res.getStatusCode(), Status_Code.OK);
		System.out.println(res.asPrettyString());
		
		CreateBookingRequest responseBody = res.as(CreateBookingRequest.class);
		//payload - all details of request
		//responseBody - all details from getBooking
		/*Assert.assertEquals(payload.firstname, responseBody.firstname);
		Assert.assertEquals(payload.lastname, responseBody.lastname);*/
		
		Assert.assertTrue(responseBody.equals(payload));
		

	}
	
	public void validateResponse(Response res,CreateBookingRequest payload, String object ) {
		Assert.assertEquals(res.jsonPath().getString(object+ "firstname"), payload.getFirstname());
		Assert.assertEquals(res.jsonPath().getString(object+"lastname"), payload.getLastname());
		Assert.assertEquals(res.jsonPath().getInt(object+"totalprice"), payload.getTotalprice());
		Assert.assertEquals(res.jsonPath().getBoolean(object+ "depositpaid"), payload.isDepositpaid());
		Assert.assertEquals(res.jsonPath().getString(object+"additionalneeds"), payload.getAdditionalneeds());
		Assert.assertEquals(res.jsonPath().getString(object+"bookingdates.checkin"),
				payload.bookingdates.getCheckin());
		Assert.assertEquals(res.jsonPath().getString(object+"bookingdates.checkout"),
				payload.bookingdates.getCheckout());
	}

	
}
