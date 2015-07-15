package circuitbreaker;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@SpringBootApplication
@EnableCircuitBreaker
public class CircuitBreakerApp {

    public static void main(String[] args) {
       SpringApplication.run(CircuitBreakerApp.class, args);
    }
    

    @Autowired
    private FlakeyService service;

    @RequestMapping("/")
    public String hello() {
        return service.hello();
    }

    @RequestMapping("/quote")
    public String getQuote() {
        return service.getQuote();     
    }

    @Component
    public static class FlakeyService {

        @HystrixCommand(fallbackMethod="goodbye")
        public String hello() {
            if (Calendar.getInstance().get(Calendar.MINUTE) % 2 == 0) {
                throw new RuntimeException();
            }
            return "hello!";
        }

        String goodbye() {
            return "goodbye.";
        }
        
        @HystrixCommand(fallbackMethod="sorry")
        public String getQuote() {
            if (Calendar.getInstance().get(Calendar.MINUTE) % 2 == 0) {
                throw new RuntimeException();
            }
            RestTemplate restTemplate = new RestTemplate();
            Quote quote = restTemplate.getForObject("http://gturnquist-quoters.cfapps.io/api/random", Quote.class);
            return quote.toString();
        }

        String sorry() {
            return "sorry, no quote\n";
        }


    }
}