package org.jgroups.tools.pinger.transports;

import java.io.IOException;
import java.text.ParseException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cometd.bayeux.server.ServerMessage;
import org.cometd.server.BayeuxServerImpl;
import org.cometd.server.ServerSessionImpl;
import org.cometd.server.transport.LongPollingTransport;


public class CrossOriginLongPollingTransport extends LongPollingTransport {
    public final static String PREFIX = "long-polling-cross-origin.json";
    public final static String NAME = "cross-origin-long-polling";
    public final static String MIME_TYPE_OPTION = "mimeType";

    private boolean _jsonDebug = false;
    private String _mimeType = "application/json;charset=UTF-8";

    public CrossOriginLongPollingTransport(BayeuxServerImpl bayeux) {
        super(bayeux, NAME);
        setOptionPrefix(PREFIX);
    }

    @Override
    protected boolean isAlwaysFlushingAfterHandle() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        _jsonDebug = getOption(JSON_DEBUG_OPTION, _jsonDebug);
        _mimeType = getOption(MIME_TYPE_OPTION, _mimeType);
    }

    @Override
    public boolean accept(HttpServletRequest request) {
        return "POST".equals(request.getMethod());
    }

    @Override
    protected ServerMessage.Mutable[] parseMessages(HttpServletRequest request) throws IOException, ParseException {
        String charset = request.getCharacterEncoding();
        if (charset == null)
            request.setCharacterEncoding("UTF-8");
        String contentType = request.getContentType();
        if (contentType == null || contentType.startsWith("application/json")) {
            return parseMessages(request.getReader(), _jsonDebug);
        } else if (contentType.startsWith("application/x-www-form-urlencoded")) {
            return parseMessages(request.getParameterValues(MESSAGE_PARAM));
        } else {
            throw new IOException("Invalid Content-Type " + contentType);
        }
    }

    @Override
    protected ServletOutputStream beginWrite(HttpServletRequest request, HttpServletResponse response, ServerSessionImpl session) throws IOException {
        response.setContentType(_mimeType);
        ServletOutputStream output = response.getOutputStream();
        output.write('[');
        return output;
    }

    @Override
    protected void endWrite(ServletOutputStream output, ServerSessionImpl session) throws IOException {
        output.write(']');
        output.close();
    }
}