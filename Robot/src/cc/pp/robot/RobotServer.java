package cc.pp.robot;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RobotServer extends HttpServlet {

	//	private static Logger logger = Logger.getLogger(RobotServer.class);
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String key = request.getParameter("key");
		Robot robot = new Robot();
		String answer = robot.getAnswers(key);
		response.getWriter().write(answer);
		//		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		//		response.getWriter().write("nouser");

	}

}
