package com.company.project.filter;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.web.bind.annotation.RequestMethod;

public class InputStreamHttpServletRequestWrapper extends HttpServletRequestWrapper{

    private byte[] streamBody;
    private static final int BUFFER_SIZE = 4096;

    //对请求数据判断，getInputStream()为空的数据对key和value值进行拼接
    public InputStreamHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        byte[] bytes = inputStream2Byte(request.getInputStream());
        if (bytes.length != 0 && RequestMethod.POST.name().equals(request.getMethod())) { //post 请求并且body长度非0
            //获取数据，并保存以便多次获取
            streamBody = bytes;
        }
        //System.err.println(new String(bytes));
    }

    private byte[] inputStream2Byte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[BUFFER_SIZE];
        int length;
        while ((length = inputStream.read(bytes, 0, BUFFER_SIZE)) != -1) {
            outputStream.write(bytes, 0, length);
        }

        return outputStream.toByteArray();
    }


    @Override
    public ServletInputStream getInputStream() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(streamBody);

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }

            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }
}


