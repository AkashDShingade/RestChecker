package restchecker;

import io.restassured.RestAssured;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.response.Response;

import org.hamcrest.Matcher;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.util.Map;

public class RestChecker {

    public static void runTest(String endpointFile) throws Exception {
        // Parse the JSON file
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(new FileReader("src/resources/endpoints/" + endpointFile));

        // Extract API details from JSON
        String method = (String) jsonObject.get("method");
        String endpoint = (String) jsonObject.get("endpoint");
        JSONObject request = (JSONObject) jsonObject.get("request");

        // Set up RestAssured request
        Response response = null;
        if (method.equalsIgnoreCase("GET")) {
            response = RestAssured.given()
                    .headers((Map<String, Object>) request.get("headers"))
                    .queryParams((Map<String, Object>) request.get("queryParams"))
                    .get(endpoint);
        } else if (method.equalsIgnoreCase("POST")) {
            response = RestAssured.given()
                    .headers((Map<String, Object>) request.get("headers"))
                    .body(request.get("body").toString())
                    .post(endpoint);
        }

        // Validate the response based on assertions in JSON
        JSONObject assertions = (JSONObject) jsonObject.get("assertions");
        validateResponse(response, assertions);
    }

    private static void validateResponse(Response response, JSONObject assertions) {
        // Validate status code
        response.then().statusCode(((Long) assertions.get("statusCode")).intValue());

        // Validate response time
        if (assertions.containsKey("responseTime")) {
            response.then().time(lessThan((Long) assertions.get("responseTime")));
        }

        // Validate response fields
        if (assertions.containsKey("responseFields")) {
            JSONObject fields = (JSONObject) assertions.get("responseFields");
            for (Object key : fields.keySet()) {
                response.then().body((String) key, equalTo(fields.get(key)));
            }
        }
    }

	private static Matcher<Long> lessThan(Long long1) {
		// TODO Auto-generated method stub
		return null;
	}

	private static ResponseAwareMatcher<Response> equalTo(Object object) {
		// TODO Auto-generated method stub
		return null;
	}
}
