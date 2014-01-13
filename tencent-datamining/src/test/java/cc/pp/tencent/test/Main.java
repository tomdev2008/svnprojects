package cc.pp.tencent.test;
import java.io.File;
import java.io.IOException;

import net.sf.json.JSONObject;

import org.codehaus.jackson.map.ObjectMapper;

public class Main {

	public static void main(String[] args) throws IOException {

		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		TencentDDD userdata = mapper.readValue(new File("result.txt"), TencentDDD.class);
		Data result = new Data();
		result.setText(userdata.getData().getText());
		System.out.println(JSONObject.fromObject(result));
	}
}

