package com.oss.java.springdemo;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oss")
public class OssController {

	@Autowired
	OssService ossService;

	@PostMapping("/createPackage")
	public Map<String, String> createPackage() throws IOException {
		return ossService.createPackage();
	}
	
	@PostMapping("/createSession1")
	public Map<String, String> createSession1(@RequestBody Map<String, String> payload) throws IOException {
		String packageId = payload.get("pkgId");
		return ossService.createSession1(packageId);
	}
	
	@PostMapping("/createSession2")
	public Map<String, String> createSession2(@RequestBody Map<String, String> payload) throws IOException {
		String packageId = payload.get("pkgId");
		return ossService.createSession2(packageId);
	}

}
