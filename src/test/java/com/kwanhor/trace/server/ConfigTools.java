package com.kwanhor.trace.server;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.util.ResourceUtils;

public class ConfigTools {
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		String name="cartoonBox";
		name="pallet";
		URL url=ResourceUtils.getURL("classpath:"+name+".txt");
		List<String> list=Files.readAllLines(Paths.get(url.toURI()));
		System.out.println("{");
		for(int i=0;i<list.size();i++) {
			final String line=list.get(i);
			int firstColon=line.indexOf(":");
			int firstDot=line.indexOf(",",firstColon+1);
			int firstDash=line.indexOf("//",firstDot+1);
			if(i>0)
				System.out.println(",");
			String newLine=line.substring(0, firstColon)+":{\"value\":"+line.substring(firstColon+1, firstDot)+",\"comment\":\""+line.substring(firstDash)+"\"}";
			System.out.print(newLine);
		}
		System.out.print("\r\n}");
	}
}	
