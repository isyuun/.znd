package is.yuun.com.loopj.android.http;

import java.io.IOException;
import java.io.OutputStream;

import is.yuun.com.loopj.android.http.api.OutputStreamMonitored;
import is.yuun.com.loopj.android.http.api.ProgressListener;

public class SimpleMultipartEntityMonitored extends SimpleMultipartEntity {

    private OutputStreamMonitored m_outputstream = null;
    private ProgressListener m_listener = null;

    public SimpleMultipartEntityMonitored(ProgressListener listener) {
        super(listener);

        m_listener = listener;
    }

    //public SimpleMultipartEntityMonitored() {
    //	super();
    //}

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        if (m_outputstream == null) {
            // m_outputstream = new OutputStreamMonitored(outstream, getContentLength());
            m_outputstream = new OutputStreamMonitored(outstream, getContentLength(), m_listener);
        }
        super.writeTo(m_outputstream);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    public void release() throws Throwable {
        if (m_outputstream != null) {
            m_outputstream.flush();
            m_outputstream.close();
        }
        m_outputstream = null;
        m_listener = null;
    }
}
