package io.opencpx;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Supplier;

/**
 * Spring MVC controller for the /cpx endpoint.
 *
 * Usage:
 * <pre>
 * {@code
 * @Bean
 * public CpxController cpxController() {
 *     return new CpxController(() -> {
 *         Posture posture = new Posture(CompliancePosture.COMPLIANT);
 *         posture.addFramework(new Framework("SOC2", FrameworkStatus.COMPLIANT, 1.0));
 *         return posture;
 *     });
 * }
 * }
 * </pre>
 */
@RestController
public class CpxController {

    private final Supplier<Posture> postureProvider;

    public CpxController(Supplier<Posture> postureProvider) {
        this.postureProvider = postureProvider;
    }

    @GetMapping(value = "/cpx", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCompliancePosture() {
        try {
            Posture posture = postureProvider.get();
            String json = posture.toJsonPretty();

            return ResponseEntity.ok()
                    .header("X-CPX-Version", Posture.VERSION)
                    .body(json);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"Internal server error\"}");
        }
    }
}
