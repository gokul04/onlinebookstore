package servlets;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** Shared Mockito wiring for servlet tests (not itself a test class). */
final class ServletTestSupport {

    private ServletTestSupport() {
    }

    static HttpServletRequest mockRequest(HttpSession session) {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession()).thenReturn(session);
        RequestDispatcher rd = mock(RequestDispatcher.class);
        when(req.getRequestDispatcher(anyString())).thenReturn(rd);
        return req;
    }

    static HttpServletResponse mockResponse(StringWriter sink) throws Exception {
        HttpServletResponse res = mock(HttpServletResponse.class);
        when(res.getWriter()).thenReturn(new PrintWriter(sink));
        return res;
    }
}
