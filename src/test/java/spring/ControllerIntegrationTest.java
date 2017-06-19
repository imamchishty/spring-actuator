package spring;

import com.shedhack.exception.controller.spring.ExceptionController;
import com.shedhack.exception.core.ExceptionModel;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URL;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public class ControllerIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
	private TestRestTemplate template;


	@Test
	public void getProblem() throws Exception {

		ResponseEntity<ExceptionModel> response = template.getForEntity(makeURL("problem").toString(), ExceptionModel.class);

		// Check that the response is the exception model
		assertThat(response.getStatusCode().value(), equalTo(400));

		// body
		assertThat(response.getBody().getApplicationName(), equalTo("demo"));
		assertThat(response.getBody().getSpanId(), isEmptyOrNullString());
		assertThat(response.getBody().getExceptionId(), notNullValue());
		assertThat(response.getBody().getHttpStatusDescription(), equalTo("Bad Request"));
		assertThat(response.getBody().getPath(), equalTo("/problem"));
		assertThat(response.getBody().getSessionId(), notNullValue());
		assertThat(response.getBody().getHelpLink(), equalTo("http://link"));
		assertThat(response.getBody().getMessage(), equalTo("Something horrible happened"));
		assertThat(response.getBody().getExceptionClass(), equalTo("com.shedhack.exception.core.BusinessException"));
		assertThat(response.getBody().getMetadata(), equalTo("exception-core-model"));
		assertThat(response.getBody().getHttpStatusCode(), equalTo(400));
		assertThat(response.getBody().getParams(), hasKey("user"));
		assertThat(response.getBody().getParams().get("user"), Matchers.<Object>equalTo("imam"));
		assertThat(response.getBody().getBusinessCodes(), hasKey("E100"));
		assertThat(response.getBody().getContext(), hasKey("threadName"));
		assertThat(response.getBody().getDateTime(), notNullValue());
		assertThat(response.getBody().getExceptionChain(), notNullValue());

		// header
		assertThat(response.getHeaders().get("exceptionId"), notNullValue());
		assertThat(response.getHeaders().get("exceptionId").toString(), equalTo("["+response.getBody().getExceptionId()+"]"));
		assertThat(response.getHeaders().get("exceptionType"), notNullValue());

		// check the exception count
		assertThat(ExceptionController.getExceptionCount(), equalTo(1));

	}

	private URL makeURL(String path) throws Exception {
		return new URL("http://localhost:" + port + "/" + path);
	}
}
