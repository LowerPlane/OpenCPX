using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Routing;

namespace OpenCPX;

/// <summary>
/// Extension methods for adding OpenCPX endpoints to ASP.NET Core applications.
/// </summary>
public static class CpxEndpoints
{
    /// <summary>
    /// Maps the /cpx endpoint to the application.
    /// </summary>
    /// <param name="endpoints">The endpoint route builder.</param>
    /// <param name="postureProvider">Function that returns the compliance posture.</param>
    /// <returns>The endpoint convention builder.</returns>
    public static IEndpointConventionBuilder MapCpxEndpoint(
        this IEndpointRouteBuilder endpoints,
        Func<Posture> postureProvider)
    {
        return endpoints.MapGet("/cpx", (HttpContext context) =>
        {
            try
            {
                var posture = postureProvider();
                var json = posture.ToJsonPretty();

                context.Response.Headers.Append("X-CPX-Version", Posture.VERSION);
                context.Response.ContentType = "application/json";

                return Results.Content(json, "application/json");
            }
            catch (Exception)
            {
                return Results.Json(new { error = "Internal server error" }, statusCode: 500);
            }
        });
    }

    /// <summary>
    /// Maps the /cpx endpoint with async posture provider.
    /// </summary>
    /// <param name="endpoints">The endpoint route builder.</param>
    /// <param name="postureProvider">Async function that returns the compliance posture.</param>
    /// <returns>The endpoint convention builder.</returns>
    public static IEndpointConventionBuilder MapCpxEndpointAsync(
        this IEndpointRouteBuilder endpoints,
        Func<Task<Posture>> postureProvider)
    {
        return endpoints.MapGet("/cpx", async (HttpContext context) =>
        {
            try
            {
                var posture = await postureProvider();
                var json = posture.ToJsonPretty();

                context.Response.Headers.Append("X-CPX-Version", Posture.VERSION);
                context.Response.ContentType = "application/json";

                return Results.Content(json, "application/json");
            }
            catch (Exception)
            {
                return Results.Json(new { error = "Internal server error" }, statusCode: 500);
            }
        });
    }
}

/// <summary>
/// Middleware for adding the /cpx endpoint.
/// </summary>
public class CpxMiddleware
{
    private readonly RequestDelegate _next;
    private readonly Func<Posture> _postureProvider;

    public CpxMiddleware(RequestDelegate next, Func<Posture> postureProvider)
    {
        _next = next;
        _postureProvider = postureProvider;
    }

    public async Task InvokeAsync(HttpContext context)
    {
        if (context.Request.Path == "/cpx" && context.Request.Method == "GET")
        {
            try
            {
                var posture = _postureProvider();
                var json = posture.ToJsonPretty();

                context.Response.Headers.Append("X-CPX-Version", Posture.VERSION);
                context.Response.ContentType = "application/json";
                await context.Response.WriteAsync(json);
            }
            catch (Exception)
            {
                context.Response.StatusCode = 500;
                context.Response.ContentType = "application/json";
                await context.Response.WriteAsync("{\"error\": \"Internal server error\"}");
            }
            return;
        }

        await _next(context);
    }
}

/// <summary>
/// Extension methods for adding CPX middleware.
/// </summary>
public static class CpxMiddlewareExtensions
{
    /// <summary>
    /// Adds the CPX middleware to the application pipeline.
    /// </summary>
    public static IApplicationBuilder UseCpx(
        this IApplicationBuilder app,
        Func<Posture> postureProvider)
    {
        return app.UseMiddleware<CpxMiddleware>(postureProvider);
    }
}
