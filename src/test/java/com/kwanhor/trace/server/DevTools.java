package com.kwanhor.trace.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Set;

import org.springframework.util.ResourceUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

public class DevTools {
	private JSONObject json;
	private String className;//%1$s
	private String serviceName;//%2$s
	private String codeName;//%4$s
	
	public DevTools(String className,String codeName) throws FileNotFoundException {
		this.className = className;
		this.codeName=codeName;
		this.serviceName=className.substring(0,1).toLowerCase()+className.substring(1);
		this.json=JSON.parseObject(ResourceUtils.getURL("classpath:"+serviceName+".json"));
	}
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		String className="CartoonBox";
		className="Pallet";
		String codeName="SNCode";
		DevTools tools=new DevTools(className,codeName);
		tools.printEntity();
//		tools.printRepo();
//		tools.printAPI();
		
	}
	
	void printAPI() throws IOException, URISyntaxException {
		printTemplate("api");
	}
	private void printTemplate(String name) throws IOException, URISyntaxException {
		URL temp=ResourceUtils.getURL("classpath:"+name+".txt");
		try(BufferedReader reader=new BufferedReader(new FileReader(new File(temp.toURI()),Charset.forName("UTF-8")));){
			String line=null;
			boolean hasLine=false;
			while((line=reader.readLine())!=null) {
				if(hasLine)
					System.out.println();
				else
					hasLine=true;
				System.out.printf(line, className,serviceName,"%",codeName);
			}
		}
	}
	void printRepo() throws IOException, URISyntaxException {
		printTemplate("repo");
	}
	
	void printEntity() {
		System.out.println("package com.kwanhor.trace.server.model;\r\n"
				+ "\r\n"
				+ "import java.util.Date;\r\n"
				+ "\r\n"
				+ "import javax.persistence.Column;\r\n"
				+ "import javax.persistence.Entity;\r\n"
				+ "import javax.persistence.EntityListeners;\r\n"
				+ "import javax.persistence.GeneratedValue;\r\n"
				+ "import javax.persistence.GenerationType;\r\n"
				+ "import javax.persistence.Id;\r\n"
				+ "import javax.persistence.Table;\r\n"
				+ "import javax.persistence.UniqueConstraint;\r\n"
				+ "\r\n"
				+ "import org.springframework.data.annotation.CreatedDate;\r\n"
				+ "import org.springframework.data.jpa.domain.support.AuditingEntityListener;\r\n"
				+ "\r\n"
				+ "import com.fasterxml.jackson.annotation.JsonFormat;\r\n"
				+ "import com.fasterxml.jackson.annotation.JsonProperty;\r\n"
				+ "\r\n"
				+ "import lombok.Data;");
		System.out.println("@Data\r\n"
				+ "@Entity\r\n"
				+ "@Table(uniqueConstraints = @UniqueConstraint(columnNames = \"SNCode\"))\r\n"
				+ "@EntityListeners(AuditingEntityListener.class)");
		System.out.println("public class "+className+" {");
		System.out.println("	@Id\r\n"
				+ "	@GeneratedValue(strategy = GenerationType.IDENTITY)\r\n"
				+ "	private Long id;");
		final Set<String> keySet=json.keySet();
		for (String keyName : keySet) {
			JSONObject vJson=json.getJSONObject(keyName);
			Object value=vJson.get("value");
			String type="String";
			if(value instanceof Integer) {
				type="int";
			}
			if("createDate".equals(keyName)) {
				System.out.println("	@CreatedDate\r\n"
						+ "	@Column(updatable = false,nullable = false)\r\n"
						+ "	@JsonFormat(pattern = \"yyyyMMdd\")\r\n"
						+ "	private Date createDate;"+vJson.getString("comment")+";例如:"+value);
			}else {
				if(vJson.getBooleanValue("originName")) {
					System.out.println("	@JsonProperty(\""+keyName+"\")");
				}
				if(vJson.getBooleanValue("uk")) {
					System.out.println("	@Column(unique = true,nullable = false)");
				}
				System.out.println("	private "+type+" "+keyName+";"+vJson.getString("comment")+";例如:"+value);
			}
			
		}
		System.out.print("}");
	}
}
