package io.opencpx;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Jakarta Servlet for the /cpx endpoint.
 *
 * Usage with Jakarta EE / Servlet containers:
 * <pre>
 * {@code
 * @WebServlet("/cpx")
 * public class MyCpxServlet extends CpxServlet {
 *     public MyCpxServlet() {
 *         super(() -> {
 *             Posture posture = new Posture(CompliancePosture.COMPLIANT);
 *             posture.addFramework(new Framework("SOC2", FrameworkStatus.COMPLIANT, 1.0));
 *             return posture;
 *         });
 *     }
 * }
 * }
 * </pre>
 */
public class CpxServlet extends HttpServlet {

    private final Supplier<Posture> postureProvider;

    public CpxServlet(Supplier<Posture> postureProvider) {
        this.postureProvider = postureProvider;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Posture posture = postureProvider.get();
            String json = posture.toJsonPretty();

            resp.setContentType("application/json");
            resp.setHeader("X-CPX-Version", Posture.VERSION);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(json);
        } catch (Exception e) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Internal server error\"}");
        }
    }
}
