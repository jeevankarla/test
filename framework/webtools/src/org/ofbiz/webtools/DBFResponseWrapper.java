package org.ofbiz.webtools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;

public class DBFResponseWrapper extends HttpServletResponseWrapper {
	private CharArrayWriter output;
	private ServletOutputStream stream;
    public DBFResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        this.output = new CharArrayWriter();
    }
    
    public byte[] getData() { 
    	return  (toString()).getBytes();  
    }
    
   public String toString() {
        return output.toString();
    }
    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(output);
    }
    public ServletOutputStream getOutputStream() { 
        return stream; 
    } 
}
