package Servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ppcc.KeyWord;

public class WordSegment extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8865504000549180434L;
	private KeyWord keyWord = null;

	@Override
	public void init() throws ServletException {
		try {
			keyWord = KeyWord.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		response.setContentType("application/Json;charset=utf-8");
		PrintWriter out = response.getWriter();
		String token = request.getParameter("token");
		//		String str = new String(request.getParameter("words").getBytes("iso-8859-1"),"UTF-8");

		if (token == null) {
			out.println("token can not be null.");
		} else if ("mnvrjierrdqdiefxanjp".equals(token)) {
			String[] param = new String[3];
			param[0] = request.getParameter("words");
			param[1] = request.getParameter("character");
			param[2] = request.getParameter("wdcount");
			out.println(keyWord.extractKeyword(param));
		} else {
			out.println("token error");
		}
		out.flush();
		out.close();
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
